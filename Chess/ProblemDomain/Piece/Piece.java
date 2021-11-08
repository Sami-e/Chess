package Chess.ProblemDomain.Piece;

import Chess.ProblemDomain.Alliance;
import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.Move;


import java.util.List;

public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    public PieceType getPieceType() {
        return pieceType;
    }



    public Piece(final int piecePosition, final Alliance pieceAlliance, PieceType pieceType) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
        this.isFirstMove = true;
    }

    public Piece(final int piecePosition, final Alliance pieceAlliance, PieceType pieceType, boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
        this.isFirstMove = isFirstMove;
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }else if (!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }


    @Override
    public int hashCode(){
        return this.cachedHashCode;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public abstract List<Move> calculateLegalMoves(final Board board);

    public boolean isFirstMove(){
       return this.isFirstMove;
    }

    public abstract Piece movePiece(Move move);

    public int getPieceValue(){
        return this.pieceType.pieceValue();
    }


    public enum PieceType {
        PAWN("P"){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int pieceValue() {
                return 100;
            }

        },
        KNIGHT("N"){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int pieceValue() {
                return 300;
            }
        },
        BISHOP("B"){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int pieceValue() {
                return 300;
            }
        },
        ROOK("R"){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public int pieceValue() {
                return 500;
            }
        },
        QUEEN("Q"){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int pieceValue() {
                return 900;
            }
        },
        KING("K"){
            @Override
            public boolean isKing(){
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int pieceValue() {
                return 10000;
            }
        };

        private String pieceName;

        PieceType(final String pieceName){
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();

        public abstract int pieceValue();
    }

}
