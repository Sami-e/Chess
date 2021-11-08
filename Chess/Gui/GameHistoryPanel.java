package Chess.Gui;

import Chess.ProblemDomain.Board.Board;
import Chess.ProblemDomain.Board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

import static Chess.Gui.Table.*;

public class GameHistoryPanel extends JPanel {
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,400);
    private final DataModel model;
    private final JScrollPane scrollPane;
    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board,
              final MoveLog moveLog){
        int currentRow = 0;
        this.model.clear();
        for(final Move move: moveLog.getMoves()){
            final String moveText = move.toString();
            if(move.getMovePiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText, currentRow, 0);
            } else {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveLog.getMoves().size() > 0){
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();
            if(lastMove.getMovePiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText + calculateCheckAndCheckmatePath(board), currentRow, 0);
            } else {
                this.model.setValueAt(moveText + calculateCheckAndCheckmatePath(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckmatePath(final Board board) {
        if (board.currentPlayer().isCheckmate()){
            return "*";
        } else if (board.currentPlayer().isCheck()){
            return "+";
        } else {
            return "";
        }
    }


    private class DataModel extends DefaultTableModel {

        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};
        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(this.values == null){
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return this.NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            final Row currentRow = this.values.get(row);
            if(column == 0){
                return currentRow.getWhiteMove();
            } else if(column == 1){
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column){
            final Row currentRow;
            if(this.values.size() == row){
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }

            if (column == 0){
                currentRow.setWhiteMove((String) value);
                fireTableRowsInserted(row, row);
            } else {
                currentRow.setBlackMove((String) value);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column){
            return Move.class;
        }

        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row(){
        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }

    }
}
