package Chess.ProblemDomain.Board;

import Chess.ProblemDomain.Piece.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    protected final int tileCoordinates;

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i < 64; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }

        return Collections.unmodifiableMap(emptyTileMap); //Not Immutable
    }

    public static Tile createTile(final int tileCoordinates, final Piece piece){
        if (piece == null){
            return EMPTY_TILES_CACHE.get(tileCoordinates);
        } else {
            return new OccupiedTile(tileCoordinates, piece);
        }
    }

    Tile(int tileCoordinates) {
        this.tileCoordinates = tileCoordinates;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();
    public int getTileCoordinates(){
        return this.tileCoordinates;
    }
    private static final class EmptyTile extends Tile {

        EmptyTile(int coordinates){
            super(coordinates);
        }
        @Override
        public String toString(){
            return "-";
        }
        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    private static final class OccupiedTile extends Tile {
        private final Piece pieceOnTile;

        OccupiedTile(int tileCoordinate, Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString(){
            if (getPiece().getPieceAlliance().isBlack()){
                return getPiece().toString().toLowerCase();
            } else {
                return getPiece().toString().toUpperCase();
            }
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }


}
