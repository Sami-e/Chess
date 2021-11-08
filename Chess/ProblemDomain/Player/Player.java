package Chess.ProblemDomain.Player;

import Chess.ProblemDomain.Alliance;
import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Piece.King;
import Chess.ProblemDomain.Piece.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isCheck;

    protected Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.isCheck = !Player.calculateAttackOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();

        List<Move> totalLegalMoves = new ArrayList<Move>();
        totalLegalMoves.addAll(legalMoves);
        totalLegalMoves.addAll(KingCastles(legalMoves, opponentMoves));
        this.legalMoves = totalLegalMoves;
    }

    static Collection<Move> calculateAttackOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(final Move move: moves){
            if(piecePosition == move.getDestCoordinate()){
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    private King establishKing(){
        for(final Piece piece : getActivePiece()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("No King");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isCheck(){
        return this.isCheck;
    }

    public boolean isCheckmate(){
        return this.isCheck && !HasEscapeMoves();
    }

    protected boolean HasEscapeMoves(){
        for (final Move move: this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isStalemate(){
        return !(this.isCheck) && !HasEscapeMoves();
    }

    public boolean isCastle(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        transitionBoard.currentPlayer().getLegalMoves();

        if(transitionBoard.currentPlayer().getOpponent().isCheck()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }


    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public King getPlayerKing(){
        return this.playerKing;
    }

    public abstract Collection<Piece> getActivePiece();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract Collection<Move> KingCastles(Collection<Move> playerMoves, Collection<Move> opponentMoves);
}
