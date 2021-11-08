package Chess.Gui;

import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Piece.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static Chess.Gui.Table.*;

public class TakenPiecesPanel extends JPanel {
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOUR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSIONS = new Dimension(60, 80);

    private final JPanel northPanel;
    private final JPanel southPanel;


    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOUR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8,2));
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOUR);
        this.southPanel.setBackground(PANEL_COLOUR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSIONS);
    }

    public void redo(final MoveLog moveLog) {
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move: moveLog.getMoves()){
            if(move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                } else {
                    blackTakenPieces.add(takenPiece);
                }
            }
        }
        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                if (o1.getPieceValue() > o2.getPieceValue()){
                    return 1;
                } else if (o1.getPieceValue() < o2.getPieceValue()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (final Piece takenPiece : whiteTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File("holywarriors/" +
                        takenPiece.getPieceAlliance().toString().substring(0, 1) + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(icon);
                this.southPanel.add(imageLabel);
            } catch (final IOException e){
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece : blackTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File("holywarriors/" +
                        takenPiece.getPieceAlliance().toString().substring(0, 1) + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(icon);
                this.southPanel.add(imageLabel);
            } catch (final IOException e){
                e.printStackTrace();
            }
        }
        validate();
    }
}
