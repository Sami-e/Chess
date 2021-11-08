package Chess.ProblemDomain.Player.AI;

import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Piece.Piece;
import Chess.ProblemDomain.Player.Player;

final public class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 100000;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final Board board, final int depth) {
        return isStalemate(board.currentPlayer()) ? 0 :
                scorePlayer(board.WhitePlayer(), depth) -
                    scorePlayer(board.BLackPlayer(), depth);
    }

    private int scorePlayer(final Player player,
                            final int depth){
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth)
                + castleBonus(player);
    }




    private int pieceValue(Player player) {
        int pieceValueScore = 0;
        for (final Piece piece: player.getActivePiece()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }

    private int mobility(Player player) {
        return player.getLegalMoves().size();
    }

    private int check(Player player) {
        return player.getOpponent().isCheck() ? CHECK_BONUS : 0;
    }

    private int checkmate(Player player, int depth) {
        return player.getOpponent().isCheckmate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    private boolean isStalemate(Player player){
        return player.isStalemate();
    }

    private int depthBonus(int depth) {
        return (int) Math.pow(100, depth + 1);
    }

    private int castleBonus(Player player) {
        return player.isCastle() ? CASTLE_BONUS : 0;
    }
}
