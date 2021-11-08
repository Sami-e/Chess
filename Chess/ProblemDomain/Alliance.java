package Chess.ProblemDomain;

import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.BoardUtils;
import Chess.ProblemDomain.Player.BlackPlayer;
import Chess.ProblemDomain.Player.Player;
import Chess.ProblemDomain.Player.WhitePlayer;

public enum Alliance {
    WHITE{
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isBlack(){
            return false;
        }

        @Override
        public boolean isWhite(){
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.EIGHT_RANK[position];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }
        @Override
        public boolean isBlack(){
            return true;
        }

        @Override
        public boolean isWhite(){
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.FIRST_RANK[position];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    public abstract int getDirection();
    public abstract boolean isBlack();
    public abstract boolean isWhite();
    public abstract boolean isPawnPromotionSquare(int position);

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
