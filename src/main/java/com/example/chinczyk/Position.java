package com.example.chinczyk;

import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

import java.util.*;

import com.example.chinczyk.Pawn.PawnColor;

public class Position {

	public enum CellType {YARD, START, DEFAULT, HOME}

	public static final Map<PawnColor, Circle> START_CELLS = Controller.getStartCells();
	public static final Set<Circle> HOME_CELLS = Controller.getHomeCells();

	private Point2D cell;
	private CellType cellType = CellType.YARD;
	private Direction dir = Direction.NONE;

	public void makeStep(PawnColor pawnColor) {
		if (cellType == CellType.YARD) {
			putOnStartCell(pawnColor);
		} else {
			cell = cell.add(dir.coordination);
		}
		changeCellType();
		changeDir(pawnColor);
	}

	private void changeCellType() {
		if (isAnyMatch(START_CELLS.values(), cell)) {
			cellType = CellType.START;
		} else if (isAnyMatch(HOME_CELLS, cell)) {
			cellType = CellType.HOME;
		} else {
			cellType = CellType.DEFAULT;
		}
	}

	/*
	 * if at home
	 *      then the direction doesn't change
	 * if the next cell in the given direction is a home
	 *      then change the direction counterclockwise
	 * if on the starting circle
	 *      then set the direction depending on the color
	 * if the next circle in the given direction is the starting circle
	 *  and the color of the pawn matches the color of the starting circle
	 *      then change the direction clockwise
	 * if two steps from start
	 *      then change the direction clockwise
	 */
	private void changeDir(PawnColor pawnColor) {
		if (isAnyMatch(HOME_CELLS, cell)) {
			return;
		} else if (isAnyMatch(HOME_CELLS, cell.add(dir.coordination))) {
			dir = dir.changeCounterClockwise();
		} else if (isAnyMatch(START_CELLS.values(), cell)) {
			dir = switch (getStartCellColor(cell)) {
				case BLUE -> Direction.DOWN;
				case GREEN -> Direction.LEFT;
				case RED -> Direction.RIGHT;
				case YELLOW -> Direction.UP;
			};
		} else if (isAnyMatch(START_CELLS.values(), cell.add(dir.coordination))
				&& getStartCellColor(cell.add(dir.coordination)) == pawnColor) {
			dir = dir.changeClockWise();
		} else if (isTwoStepsFromStart())
			dir = dir.changeClockWise();
	}

	private boolean isAnyMatch(Collection<Circle> cells, Point2D currentCell) {
		return cells
				.stream()
				.anyMatch(cell ->
						GridPane.getColumnIndex(cell) == (int) currentCell.getX()
						&& GridPane.getRowIndex(cell) == (int) currentCell.getY());
	}

	private PawnColor getStartCellColor(Point2D currentCell) {
		return START_CELLS
				.entrySet()
				.stream()
				.filter(entry ->
						GridPane.getColumnIndex(entry.getValue()) == (int) currentCell.getX()
						&& GridPane.getRowIndex(entry.getValue()) == (int) currentCell.getY())
				.findFirst()
				.get()
				.getKey();
	}

	private boolean isTwoStepsFromStart() {
		return isAnyMatch(START_CELLS.values(), cell.add(dir.changeClockWise().coordination.multiply(2)));
	}

	private void putOnStartCell(PawnColor pawnColor) {
		Circle startCell = START_CELLS.entrySet()
				.stream()
				.filter(entry -> entry.getKey() == pawnColor)
				.findFirst()
				.get()
				.getValue();
		cell = new Point2D(GridPane.getColumnIndex(startCell), GridPane.getRowIndex(startCell));
	}

	public Position(int col, int row) {
		this.cell = new Point2D(col, row);
	}

	public int getCol() {
		return (int) cell.getX();
	}

	public int getRow() {
		return (int) cell.getY();
	}
}
