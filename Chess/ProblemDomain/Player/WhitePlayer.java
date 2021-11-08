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

public class WhitePlayer extends Player{

    public WhitePlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePiece() {
        return this.board.getWhitePieces();

    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.BLackPlayer();
    }

    @Override
    public Collection<Move> KingCastles(final Collection<Move> playerMoves, final Collection<Move> opponentMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isCheck()){

            if(!this.board.getTile(61).isTileOccupied() &&
                    !this.board.getTile(62).isTileOccupied()){
                final Tile rookTile = this.board.getTile(63);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        rookTile.getPiece().getPieceType().isRook()){

                    if(Player.calculateAttackOnTile(61,opponentMoves).isEmpty() &&
                            Player.calculateAttackOnTile(62,opponentMoves).isEmpty()) {
                        kingCastles.add(new Move.KingSideCastle(this.board, this.playerKing, 62,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinates(), 61));
                    }
                }
            }

            if(!this.board.getTile(59).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(57).isTileOccupied()){
                final Tile rookTile = this.board.getTile(56);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        rookTile.getPiece().getPieceType().isRook()){
                    if(Player.calculateAttackOnTile(59,opponentMoves).isEmpty() &&
                            Player.calculateAttackOnTile(58,opponentMoves).isEmpty()){
                        kingCastles.add(new Move.QueenSideCastle(this.board, this.playerKing, 58,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinates(), 59));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
