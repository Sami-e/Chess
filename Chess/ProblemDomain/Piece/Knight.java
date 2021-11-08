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

public class Knight extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.KNIGHT);
    }

    public Knight( int piecePosition, Alliance pieceAlliance, boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.QUEEN, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        int candidateDestCoordinate;
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES){
            candidateDestCoordinate = this.piecePosition + currentCandidateOffset;
            if (isValidTileCoordinate(candidateDestCoordinate)){

                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    continue;
                }

                final Tile candidateDestTile = board.getTile(candidateDestCoordinate);

                if (!candidateDestTile.isTileOccupied()){
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestCoordinate));
                } else {
                    final Piece pieceAtDest = candidateDestTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDest.getPieceAlliance();

                    if(this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestCoordinate, pieceAtDest));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    @Override
    public Piece movePiece(Move move) {
        return new Knight(move.getDestCoordinate(), move.getMovePiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10) ||
                (candidateOffset == 6) || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private boolean isSeventhColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((candidateOffset == 10) || (candidateOffset == -6));
    }

    private boolean isEighthColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 17) || (candidateOffset == 10) ||
                (candidateOffset == -6) || (candidateOffset == -15));
    }

}
