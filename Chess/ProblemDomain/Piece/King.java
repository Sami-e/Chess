package Chess.ProblemDomain.Piece;

import Chess.ProblemDomain.Alliance;
import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.BoardUtils;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Board.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Chess.ProblemDomain.Board.BoardUtils.isValidTileCoordinate;

public class King extends Piece {
    public King(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.KING);
    }

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        int candidateDestCoordinate;
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            candidateDestCoordinate = this.piecePosition + currentCandidateOffset;
            if (isValidTileCoordinate(candidateDestCoordinate)) {

                if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isEIGHTHColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    continue;
                }

                final Tile candidateDestTile = board.getTile(candidateDestCoordinate);

                if (!candidateDestTile.isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestCoordinate));
                } else {
                    final Piece pieceAtDest = candidateDestTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDest.getPieceAlliance();

                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestCoordinate, pieceAtDest));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestCoordinate(), move.getMovePiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    private boolean isFirstColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) ||
                (candidateOffset == -1) || (candidateOffset == 7));
    }

    private boolean isEIGHTHColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 9) ||
                (candidateOffset == 1) || (candidateOffset == -7));
    }

}
