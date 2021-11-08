package Chess.ProblemDomain.Player.AI;

import Chess.ProblemDomain.Board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}
