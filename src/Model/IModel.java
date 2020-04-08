package Model;

import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyCode;
import java.io.File;

public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    boolean checkVictory();
    void solveMaze();
    void save(File file);
    void load(File file);
}
