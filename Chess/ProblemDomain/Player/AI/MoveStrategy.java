package Chess.ProblemDomain.Player.AI;

import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.Move;

public interface MoveStrategy {
    Move execute(Board board);
}
