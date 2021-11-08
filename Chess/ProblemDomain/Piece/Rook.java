package Chess.ProblemDomain.Piece;

import Chess.ProblemDomain.Alliance;
import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.BoardUtils;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Board.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rook extends Piece {
    private final static int[] CANDIDATE_VECTOR_COORDINATES = {-8,-1,1,8};

    public Rook(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.ROOK);
    }

    public Rook (int piecePosition, Alliance pieceAlliance, boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestCoordinate(), move.getMovePiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(final int candidateDestOffset :CANDIDATE_VECTOR_COORDINATES){
            int candidateDestCoordinate = this.piecePosition;


            while(BoardUtils.isValidTileCoordinate(candidateDestCoordinate + candidateDestOffset)){

                if(isFirstColumnExclusion(candidateDestCoordinate, candidateDestOffset) ||
                        isEighthColumnExclusion(candidateDestCoordinate, candidateDestOffset)){
                    break;
                }
                candidateDestCoordinate += candidateDestOffset;

                final Tile candidateDestTile = board.getTile(candidateDestCoordinate);
                if (!candidateDestTile.isTileOccupied()){
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestCoordinate));
                } else {
                    final Piece pieceAtDest = candidateDestTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDest.getPieceAlliance();

                    if(this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestCoordinate, pieceAtDest));
                    }
                    break;
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
    }
}

