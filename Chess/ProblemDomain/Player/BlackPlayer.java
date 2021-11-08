package Chess.ProblemDomain.Player;

import Chess.ProblemDomain.Alliance;
import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Board.Tile;
import Chess.ProblemDomain.Piece.Piece;
import Chess.ProblemDomain.Piece.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlackPlayer extends Player{
    public BlackPlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePiece() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.WhitePlayer();
    }

    @Override
    public Collection<Move> KingCastles(final Collection<Move> playerMoves, final Collection<Move> opponentMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isCheck()){

            if(!this.board.getTile(5).isTileOccupied() &&
                    !this.board.getTile(6).isTileOccupied()){
                final Tile rookTile = this.board.getTile(7);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        rookTile.getPiece().getPieceType().isRook()){

                    if(Player.calculateAttackOnTile(5,opponentMoves).isEmpty() &&
                            Player.calculateAttackOnTile(6,opponentMoves).isEmpty()) {
                        kingCastles.add(new Move.KingSideCastle(this.board, this.playerKing, 6,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinates(), 5));
                    }
                }
            }
            if(!this.board.getTile(3).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(1).isTileOccupied()){
                final Tile rookTile = this.board.getTile(0);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        rookTile.getPiece().getPieceType().isRook()){
                    if(Player.calculateAttackOnTile(3,opponentMoves).isEmpty() &&
                            Player.calculateAttackOnTile(2,opponentMoves).isEmpty()){
                        kingCastles.add(new Move.QueenSideCastle(this.board, this.playerKing, 2,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinates(), 3));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
