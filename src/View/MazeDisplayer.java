package View;

import algorithms.mazeGenerators.Maze;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int characterPositionRow = 0;
    private int characterPositionColumn = 0;
    private Image wallImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(("images/wall.jpg"))));
    private Image characterImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(("images/character2.jpg"))));
    private Image solImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(("images/sol.jpg"))));
    private Image endImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(("images/endImage.jpg"))));
    private double cellHeight;
    private double cellWidth;
    private GraphicsContext gc = getGraphicsContext2D();

    void setMaze(Maze maze) {
        this.maze = maze;
        drawNew();
    }

    void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        drawNew();
    }

    Maze getMaze() {
        return maze;
    }

    void drawCharacter() {
        //gc.clearRect(0, 0, getWidth(), getHeight());
        gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
    }

    void drawNew() {
        if (maze != null) {
            gc.clearRect(0, 0, getWidth(), getHeight());
            double canvasWidth = getHeight();
            double canvasHeight = getWidth();
            double cellHeight = canvasHeight / maze.grid[0].length;
            double cellWidth = canvasWidth / maze.grid.length;
            //Draw Maze
            for (int i = 0; i < maze.grid.length; i++) {
                for (int j = 0; j < maze.grid[i].length; j++) {
                    if (maze.grid[i][j] == 1)
                        gc.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                    else if (maze.grid[i][j] == 2)
                        gc.drawImage(solImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                }
            }
            //draw end point
            gc.drawImage(endImage, maze.endPosition.column * cellHeight, maze.endPosition.row * cellWidth, cellHeight, cellWidth);
            //Draw Character
            gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
        }
    }

    void redrawAfterFinish() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.grid[0].length;
            double cellWidth = canvasWidth / maze.grid.length;
            Image wallImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(("images/wall.jpg"))));
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            //Draw Maze
            for (int i = 0; i < maze.grid.length; i++) {
                for (int j = 0; j < maze.grid[i].length; j++) {
                    if (maze.grid[i][j] == 1) {
                        gc.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                    }
                }
            }
        }
    }

    int getRows() {
        return maze.grid.length;
    }

    int getColumns() {
        return maze.grid[0].length;
    }

}
