package Chess.Gui;

import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.BoardUtils;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Board.Move.MoveFactory;
import Chess.ProblemDomain.Board.Tile;
import Chess.ProblemDomain.Piece.Piece;
import Chess.ProblemDomain.Piece.Piece.PieceType;
import Chess.ProblemDomain.Player.AI.Minimax;
import Chess.ProblemDomain.Player.AI.MoveStrategy;
import Chess.ProblemDomain.Player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final JFrame gameFrame;
    private PawnPromotionFrame pawnPromotionFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;

    private final BoardPanel boardPanel;
    private Board chessBoard;
    private BoardDirection boardDirection;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Tile sourceTile;
    private Tile destTile;
    private Piece humanMovePiece;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static final Dimension PROMOTION_OPTION_DIMENSION = new Dimension(200, 45);
    private static final String defaultPiecePath = "holywarriors/";
    private static final Color lightTileColour = new Color(239, 239, 146);
    private static final Color darkTileColour = new Color(70, 18, 138);
    private static boolean highlightLegalMoves;
    private static final Table INSTANCE = new Table();
    private Move computerMove;

    public static Table get(){
        return INSTANCE;
    }

    public GameSetup getGameSetup() {
        return gameSetup;
    }

    private Table() {
        this.chessBoard = Board.createStandardBoard();
        this.gameFrame = new JFrame("JChess");
        final JMenuBar tableMenuBar = createMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;
        this.gameHistoryPanel = new GameHistoryPanel();
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.moveLog = new MoveLog();
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.addObserver(new TableGameAIWatcher());
        this.gameFrame.setVisible(true);
        this.pawnPromotionFrame = null;
    }

    private JMenuBar createMenuBar() {

        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoard = Board.createStandardBoard();
                boardDirection = BoardDirection.NORMAL;
                highlightLegalMoves = true;
                moveLog.clear();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        boardPanel.drawBoard(chessBoard);
                        gameHistoryPanel.redo(chessBoard, moveLog);
                        takenPiecesPanel.redo(moveLog);

                        if (gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                            Table.get().moveMadeUpdate(PlayerType.HUMAN);
                        }
                    }
                });
            }
        });
        fileMenu.add(newGame);

        /*final JMenuItem openPGN = new JMenuItem("Load PGN");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open the pgn file");
            }
        });
        fileMenu.add(openPGN);*/

        final JMenuItem exitMenuItem =  new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem =  new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighter = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        legalMoveHighlighter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = !highlightLegalMoves;
            }
        });
        preferencesMenu.add(legalMoveHighlighter);

        return preferencesMenu;
    }

    private JMenu createOptionsMenu(){
        final JMenu optionsMenu = new JMenu("Options");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer{

        @Override
        public void update(final Observable o, final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
            !Table.get().getGameBoard().currentPlayer().isCheckmate() &&
                    !Table.get().getGameBoard().currentPlayer().isCheckmate()){
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            if(Table.get().getGameBoard().currentPlayer().isCheckmate()){
                System.out.println("Checkmate!");
            }

            if(Table.get().getGameBoard().currentPlayer().isStalemate()){
                System.out.println("Stalemate!");
            }
        }
    }

    private static class AIThinkTank extends SwingWorker<Move, String>{
        private AIThinkTank(){

        }

        @Override
        protected Move doInBackground() throws Exception {
            System.out.println("thinking");
            final MoveStrategy minimax = new Minimax(Table.get().getGameSetup().getSearchDepth());
            final Move bestMove = minimax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done(){
            try {
                final Move bestMove = get();

                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private void updateComputerMove(Move move) {
        this.computerMove = move;
    }

    private void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }


    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            setBackground(new Color(100,40,40));
            validate();
        }

        public void drawBoard(Board chessBoard) {
            removeAll();
            for(final TilePanel tilePanel: boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(chessBoard);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;

        public List<Move> getMoves() {
            return moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }

        public MoveLog() {
            this.moves = new ArrayList<>();
        }
    }

    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColour();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)){
                        sourceTile = null;
                        destTile = null;
                        humanMovePiece = null;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    } else if (isLeftMouseButton(e)){
                        if (sourceTile == null && !gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovePiece = sourceTile.getPiece();

                            if (humanMovePiece == null ){
                                sourceTile = null;
                            }

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    boardPanel.drawBoard(chessBoard);
                                    gameHistoryPanel.redo(chessBoard, moveLog);
                                    takenPiecesPanel.redo(moveLog);

                                    if (gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                        Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                    }
                                }
                            });
                        } else {
                            destTile = chessBoard.getTile(tileId);
                            if(sourceTile.getPiece().getPieceType() == PieceType.PAWN &&
                                    chessBoard.currentPlayer().getAlliance().isPawnPromotionSquare(tileId)){
                                pawnPromotionFrame = new PawnPromotionFrame();
                            } else {
                                final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinates(),
                                        destTile.getTileCoordinates());

                                final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                                if(transition.getMoveStatus().isDone()){
                                    chessBoard = transition.getBoard();
                                    moveLog.addMove(move);
                                }

                                sourceTile = null;
                                destTile = null;
                                humanMovePiece = null;

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        boardPanel.drawBoard(chessBoard);
                                        gameHistoryPanel.redo(chessBoard, moveLog);
                                        takenPiecesPanel.redo(moveLog);

                                        if (gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                            Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                        }
                                    }
                                });
                            }
                        }


                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPiecePath +
                            board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColour() {
            if ((BoardUtils.EIGHT_RANK[this.tileId]) || (BoardUtils.SIXTH_RANK[this.tileId]) ||
            (BoardUtils.FOURTH_RANK[this.tileId]) || (BoardUtils.SECOND_RANK[this.tileId])) {
                setBackground(this.tileId % 2 == 0? lightTileColour : darkTileColour);
            } else {
                setBackground(this.tileId % 2 == 1? lightTileColour : darkTileColour);
            }
        }

        public void drawTile(Board chessBoard) {
            assignTileColour();
            assignTilePieceIcon(chessBoard);
            highlightLegalMoves(chessBoard);

            validate();
            repaint();
        }

        private void highlightLegalMoves(final Board board){
            if(highlightLegalMoves){
                for(final Move move: pieceLegalMoves(board)){
                    MoveTransition transition = board.currentPlayer().makeMove(move);
                    if(move.getDestCoordinate() == this.tileId && transition.getMoveStatus().isDone()){
                        System.out.println(move.toString());
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("misc/green_dot.png")))));
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(Board board) {
            if (humanMovePiece != null && humanMovePiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                List<Move> legalMove = new ArrayList<Move>();
                legalMove.addAll(humanMovePiece.calculateLegalMoves(board));
                if (humanMovePiece.getPieceType() == PieceType.KING) {
                    legalMove.addAll(board.currentPlayer().KingCastles(board.currentPlayer().getLegalMoves(),
                            board.currentPlayer().getOpponent().getLegalMoves()));
                }
                return legalMove;
            }
            return Collections.emptyList();
        }
    }

    public enum BoardDirection {
        NORMAL{
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    };


    public class PawnPromotionFrame {
        private final JFrame promotionFrame;
        private final PawnPromotionOptions pawnPromotionOptions;
        public PawnPromotionFrame(){
            this.promotionFrame = new JFrame("Pawn Promotion");
            this.pawnPromotionOptions = new PawnPromotionOptions();
            promotionFrame.add(pawnPromotionOptions);
            promotionFrame.setVisible(true);
        }
        private class PawnPromotionOptions extends JPanel {
            final List<PiecePromotionSelectTile> boardTiles;

            public PawnPromotionOptions() {
                super(new GridLayout(1,4));
                this.setSize(PROMOTION_OPTION_DIMENSION);
                setVisible(true);

                this.boardTiles = new ArrayList<>();
                this.boardTiles.add(new PiecePromotionSelectTile(PieceType.QUEEN));
                this.boardTiles.add(new PiecePromotionSelectTile(PieceType.ROOK));
                this.boardTiles.add(new PiecePromotionSelectTile(PieceType.BISHOP));
                this.boardTiles.add(new PiecePromotionSelectTile(PieceType.KNIGHT));

                for (int i = 0; i < 4; i++) {
                    add(boardTiles.get(i), i);
                }

                validate();
                repaint();

            }

        }

        private class PiecePromotionSelectTile extends JPanel {
            public PiecePromotionSelectTile(PieceType piece) {
                super(new GridLayout());
                setPreferredSize(TILE_PANEL_DIMENSION);
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPiecePath +
                            humanMovePiece.getPieceAlliance().toString().substring(0, 1) +
                            piece.toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e){
                    e.printStackTrace();
                }

                addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(final MouseEvent e) {
                        if (isLeftMouseButton(e)) {
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinates(),
                                    destTile.getTileCoordinates(), piece);
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destTile = null;
                            humanMovePiece = null;

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    boardPanel.drawBoard(chessBoard);
                                    gameHistoryPanel.redo(chessBoard, moveLog);
                                    takenPiecesPanel.redo(moveLog);
                                }
                            });
                        }
                    }

                    @Override
                    public void mousePressed(final MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(final MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(final MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(final MouseEvent e) {

                    }
                });
                validate();
            }
        }
    }
}
