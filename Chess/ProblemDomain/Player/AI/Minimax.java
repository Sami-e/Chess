package Chess.ProblemDomain.Player.AI;

import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.Move;
import Chess.ProblemDomain.Player.MoveTransition;

public class Minimax implements MoveStrategy{

    private final BoardEvaluator boardEvaluator;
    private final int depth;
    public Minimax(int depth) {
        this.depth = depth;
        this.boardEvaluator = new StandardBoardEvaluator();
    }

    @Override
    public String toString(){
        return "Minimax";
    }

    @Override
    public Move execute(Board board) {

        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue = 0;
        int numMoves = board.currentPlayer().getLegalMoves().size();
        for(final Move move: board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                System.out.println(board.currentPlayer().getAlliance().toString() + " " + move.toString());
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getBoard(), this.depth - 1, highestSeenValue) :
                        max(moveTransition.getBoard(), this.depth - 1, lowestSeenValue);

                if (board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }

        }

        final long executionTime = System.currentTimeMillis() - startTime;
        /*if (board.currentPlayer().getAlliance().isWhite()){
            System.out.println(highestSeenValue);
        } else{
            System.out.println(lowestSeenValue);
        }*/
        System.out.println(executionTime);
        return bestMove;
    }

    public int min(final Board board, final int depth, final int alpha){
        if (depth == 0 || isEndGameScenario(board)){
            int Value = this.boardEvaluator.evaluate(board, depth);
            return Value;
        }

        int lowestSeenValue = Integer.MAX_VALUE;
        for(final Move move: board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getBoard(), depth - 1, lowestSeenValue);
                if (currentValue < lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }

            if(lowestSeenValue < alpha){
                break;
            }
        }
        return lowestSeenValue;
    }

    public int max(final Board board, final int depth, final int beta){
        if (depth == 0 || isEndGameScenario(board)){
            int Value = this.boardEvaluator.evaluate(board, depth);
            return Value;
        }

        int highestSeenValue = Integer.MIN_VALUE;
        for(final Move move: board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getBoard(), depth - 1, highestSeenValue);
                if (currentValue > highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }

            if(highestSeenValue > beta){
                break;
            }
        }
        return highestSeenValue;
    }

    private boolean isEndGameScenario(Board board) {
        return board.currentPlayer().isCheckmate() ||
                board.currentPlayer().isStalemate();
    }
}
