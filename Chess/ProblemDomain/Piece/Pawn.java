package Chess.ProblemDomain.Piece;

import Chess.ProblemDomain.Alliance;
import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.BoardUtils;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Board.Move.PawnEnPassantAttackMove;
import Chess.ProblemDomain.Board.Move.PawnPromotionMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece{
    private final static int[] CANDIDATE_MOVE_COORDINATE = {7, 8, 9, 16};
    private final static PieceType[] CANDIDATE_PAWN_PROMOTIONS = {PieceType.QUEEN, PieceType.ROOK,
            PieceType.BISHOP, PieceType.KNIGHT};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.PAWN);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestCoordinate(), move.getMovePiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE) {

            final int candidateDestCoordinate = this.piecePosition + (currentCandidateOffset * this.getPieceAlliance().getDirection());
            if (BoardUtils.isValidTileCoordinate(candidateDestCoordinate)) {

                if ((currentCandidateOffset == 8) && !board.getTile(candidateDestCoordinate).isTileOccupied()) {
                    if (this.pieceAlliance.isPawnPromotionSquare(candidateDestCoordinate)) {
                        for(final PieceType promotedTo : CANDIDATE_PAWN_PROMOTIONS) {
                            legalMoves.add(new PawnPromotionMove(new Move.PawnMove(board, this, candidateDestCoordinate), promotedTo));
                        }
                    } else {
                        legalMoves.add(new Move.PawnMove(board, this, candidateDestCoordinate));
                    }

                }

                if ((currentCandidateOffset == 16) && this.isFirstMove()
                        && ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack())
                        || (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))) {

                    final int behindDestCoordinate = candidateDestCoordinate - (8 * this.pieceAlliance.getDirection());
                    if (!board.getTile(behindDestCoordinate).isTileOccupied() &&
                            !board.getTile(candidateDestCoordinate).isTileOccupied()) {
                        legalMoves.add(new Move.PawnJump(board, this, candidateDestCoordinate));
                    }

                }

                if ((currentCandidateOffset == 7) &&
                        !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                                (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                    if (board.getTile(candidateDestCoordinate).isTileOccupied() &&
                            (board.getTile(candidateDestCoordinate).getPiece().getPieceAlliance() != this.pieceAlliance)) {

                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestCoordinate)) {
                            for(final PieceType promotedTo : CANDIDATE_PAWN_PROMOTIONS) {
                                legalMoves.add(new PawnPromotionMove(new Move.PawnAttackMove(board, this, candidateDestCoordinate,
                                        board.getTile(candidateDestCoordinate).getPiece()), promotedTo));
                            }
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestCoordinate,
                                    board.getTile(candidateDestCoordinate).getPiece()));
                        }

                    } else if (!board.getTile(candidateDestCoordinate).isTileOccupied() && board.getEnPassantPawn() != null) {
                        final Pawn pieceOnCandidate = board.getEnPassantPawn();
                        if (pieceOnCandidate.getPiecePosition() == this.piecePosition - (this.pieceAlliance.getDirection())) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestCoordinate, pieceOnCandidate));
                        }
                    }
                }
                if ((currentCandidateOffset == 9) &&
                        !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                                (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()))) {
                    if (board.getTile(candidateDestCoordinate).isTileOccupied() &&
                            (board.getTile(candidateDestCoordinate).getPiece().getPieceAlliance() != this.pieceAlliance)) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestCoordinate)) {
                            for(final PieceType promotedTo : CANDIDATE_PAWN_PROMOTIONS) {
                                legalMoves.add(new PawnPromotionMove(new Move.PawnAttackMove(board, this, candidateDestCoordinate,
                                        board.getTile(candidateDestCoordinate).getPiece()), promotedTo));
                            }
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestCoordinate,
                                    board.getTile(candidateDestCoordinate).getPiece()));
                        }

                    } else if (!board.getTile(candidateDestCoordinate).isTileOccupied() && board.getEnPassantPawn() != null) {
                        final Pawn pieceOnCandidate = board.getEnPassantPawn();
                        if (pieceOnCandidate.getPiecePosition() == this.piecePosition + (this.pieceAlliance.getDirection())) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    public Piece getPromotionPiece(PieceType promotedTo) {
        if (promotedTo == PieceType.QUEEN) {
            return new Queen(this.piecePosition, this.pieceAlliance,  false);
        } else if (promotedTo == PieceType.ROOK) {
            return new Rook(this.piecePosition, this.pieceAlliance,  false);
        } else if (promotedTo == PieceType.BISHOP) {
            return new Bishop(this.piecePosition, this.pieceAlliance,  false);
        } else {
            return new Knight(this.piecePosition, this.pieceAlliance,  false);
        }
    }
}
