package Chess.ProblemDomain.Board;
import Chess.ProblemDomain.Piece.Pawn;
import Chess.ProblemDomain.Piece.Piece;
import Chess.ProblemDomain.Piece.Rook;

import static Chess.ProblemDomain.Board.Board.*;
import static Chess.ProblemDomain.Piece.Piece.*;

public abstract class Move {

    protected final Board board;
    protected final Piece movePiece;
    protected final int destCoordinate;
    protected final boolean isFirstMove;

    public static Move NULL_MOVE = new NullMove();

    private Move(Board board, Piece movePiece, int destCoordinate) {
        this.board = board;
        this.movePiece = movePiece;
        this.destCoordinate = destCoordinate;
        this.isFirstMove = movePiece.isFirstMove();
    }

    private Move(Board board, int destCoordinate) {
        this.board = board;
        this.movePiece = null;
        this.destCoordinate = destCoordinate;
        this.isFirstMove = false;
    }


    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + this.movePiece.getPiecePosition();
        result = prime * result + this.destCoordinate;
        result = prime * result + movePiece.hashCode();
        result = prime * result + movePiece.getPiecePosition();

        return result;
    }

    @Override
    public boolean equals(final Object other){
        if (this == other){
            return true;
        } else if (!(other instanceof Move)){
            return false;
        }
        final Move otherMove = (Move) other;
        return getDestCoordinate() == otherMove.getDestCoordinate() &&
                getMovePiece().equals(otherMove.getMovePiece()) &&
                getCurrentCoordinate() == otherMove.getCurrentCoordinate();
    }

    public int getDestCoordinate() {
        return this.destCoordinate;
    }

    public int getCurrentCoordinate(){
        return this.getMovePiece().getPiecePosition();
    }

    public Board execute() {
        final Builder builder = new Builder();
        for(final Piece piece: this.board.currentPlayer().getActivePiece()){
            if(!this.movePiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePiece()){
            builder.setPiece(piece);
        }

        builder.setPiece(this.movePiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public Piece getMovePiece() {
        return movePiece;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastling(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public static final class MajorMove extends Move {
        public MajorMove(Board board, Piece movePiece, int destinationCoordinate) {
            super(board, movePiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other){
            return this == other || (other instanceof MajorMove && super.equals(other));
        }

        @Override
        public String toString(){
            return movePiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destCoordinate);
        }
    }

    public static class MajorAttackMove extends Move {
        final Piece attackedPiece;
        public MajorAttackMove(Board board, Piece movePiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movePiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if (this == other){
                return true;
            } else if (!(other instanceof MajorAttackMove)){
                return false;
            }
            MajorAttackMove otherAttackMove = (MajorAttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public String toString(){
            return movePiece.getPieceType().toString() + "x" + BoardUtils.getPositionAtCoordinate(this.destCoordinate);
        }

    }

    public static class PawnMove extends Move {
        public PawnMove(Board board, Piece movePiece, int destinationCoordinate) {
            super(board, movePiece, destinationCoordinate);
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return (this == other) || (other instanceof PawnMove && super.equals(other));
        }

    }

    public static class PawnAttackMove extends MajorAttackMove {

        public PawnAttackMove(Board board, Piece movePiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movePiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.getCurrentCoordinate()).substring(0,1) +
                    "x" + BoardUtils.getPositionAtCoordinate(this.destCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return (this == other) || (other instanceof PawnAttackMove && super.equals(other));
        }
    }

    public static final class PawnJump extends PawnMove {
        public PawnJump(Board board, Piece movePiece, int destinationCoordinate) {
            super(board, movePiece, destinationCoordinate);
        }
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePiece()){
                if(!this.movePiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePiece()){
                builder.setPiece(piece);
            }

            final Pawn movedPawn = (Pawn) this.movePiece.movePiece(this);

            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(Board board, Piece movePiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movePiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return (this == other) || (other instanceof PawnEnPassantAttackMove && super.equals(other));
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePiece()) {
                if (!this.movePiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePiece()) {
                if (!piece.equals(getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movePiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }
    
    public static class PawnPromotionMove extends Move {
        final Move decoratedMove;
        final Pawn promotedPawn;
        final PieceType promotedTo;
        public PawnPromotionMove(final Move decoratedMove, PieceType promotedTo) {
            super(decoratedMove.getBoard(), decoratedMove.getMovePiece(), decoratedMove.getDestCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovePiece();
            this.promotedTo = promotedTo;
        }

        @Override
        public Board execute() {

            final Board pawnMoveBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();

            for(final Piece piece: pawnMoveBoard.currentPlayer().getOpponent().getActivePiece()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: pawnMoveBoard.currentPlayer().getActivePiece()){
                builder.setPiece(piece);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece(promotedTo).movePiece(this));
            builder.setMoveMaker(pawnMoveBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack(){
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece(){
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            return "";
        }

        @Override
        public int hashCode(){
            return decoratedMove.hashCode() + ( 31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(Object other){
            return this == other || other instanceof PawnPromotionMove && (super.equals(other));
        }
    }

    private Board getBoard() {
        return this.board;
    }

    static abstract class CastleMove extends Move {


        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public boolean isCastling(){
            return true;
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePiece()){
                if(!this.movePiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePiece()){
                builder.setPiece(piece);
            }

            builder.setPiece(this.movePiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(Object other){
            if (this == other){
                return true;
            } else if (!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove);
        }

        public CastleMove(Board board, Piece movePiece,
                          int destinationCoordinate, Rook castleRook,
                          int castleRookStart, int castleRookDestination) {
            super(board, movePiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }
    }

    public static final class KingSideCastle extends CastleMove {
        public KingSideCastle(Board board, Piece movePiece, int destinationCoordinate, Rook castleRook,
                              int castleRookStart, int castleRookDestination) {
            super(board, movePiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "0-0";
        }

        @Override
        public boolean equals(Object other){
            return (this == other) || (other instanceof KingSideCastle && super.equals(other));
        }
    }

    public static final class QueenSideCastle extends CastleMove {
        public QueenSideCastle(Board board, Piece movePiece, int destinationCoordinate, Rook castleRook,
                               int castleRookStart, int castleRookDestination) {
            super(board, movePiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "0-0-0";
        }

        @Override
        public boolean equals(Object other){
            return (this == other) || (other instanceof QueenSideCastle && super.equals(other));
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null, -1);
        }

        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute null move");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }

    
    public static class MoveFactory {
        
        private MoveFactory(){
            throw new RuntimeException(("Not instantiable"));
        }
        
        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate){
            for(final Move move: board.getAllLegalMoves()){
                if(move.getDestCoordinate() == destinationCoordinate &&
                    move.getCurrentCoordinate() == currentCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate,
                                      PieceType promotionPiece){
            for(final Move move: board.getAllLegalMoves()){
                if(move.getDestCoordinate() == destinationCoordinate &&
                        move.getCurrentCoordinate() == currentCoordinate){

                    if (!(move instanceof PawnPromotionMove) || ((PawnPromotionMove) move).promotedTo.equals(promotionPiece)) {
                        return move;
                    }
                }
            }
            return NULL_MOVE;
        }

    }


}
