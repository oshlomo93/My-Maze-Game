package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;

public class MyModel extends Observable implements IModel {

    private static Maze maze;
    private int characterPositionRow = 0;
    private int characterPositionColumn = 0;
    private  static Solution sol;
    private static final Logger LOGGER = LogManager.getLogger();

    public MyModel() {
    }

    public void startServers() {
        Server mazeGeneratingServer = new Server(5400, 2000, new ServerStrategyGenerateMaze());
        mazeGeneratingServer.start();

        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
    }

    private void CommunicateWithServer_MazeGenerating(int width, int height ) {
        try {
            Client client;
            client = new Client(InetAddress.getLocalHost(), 5400, (inFromServer, outToServer) -> {
                try {
                    LOGGER.info("GenerateMaze client is started!");
                    LOGGER.info(String.format("GenerateMaze's port is %d", 5400));
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();
                    int[] mazeDimensions = new int[]{width, height};
                    toServer.writeObject(mazeDimensions); //send maze dimensions to server
                    toServer.flush();
                    byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[compressedMaze.length]; /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/ //allocating byte[] for the decompressed maze -
                    is.read(decompressedMaze); //Fill decompressedMaze with bytes
                    maze = new Maze(decompressedMaze);
                    LOGGER.info(String.format("Maze's size: %d X %d", width, height));
                    LOGGER.info(String.format("Start position (%d, %d) ", maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex()));
                    LOGGER.info(String.format("Goal position (%d, %d) ", maze.getGoalPosition().getRowIndex(), maze.getGoalPosition().getColumnIndex()));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Exception: GenerateMaze client strategy cannot be created", e);
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LOGGER.error("UnknownHostException: GenerateMaze client does not created", e);
        }
    }

    private static void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (inFromServer, outToServer) -> {
                try {
                    LOGGER.info("SolveMaze client is started!");
                    LOGGER.info(String.format("SolveMaze's port is %d", 5401));
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();
                    toServer.writeObject(maze); //send maze to server
                    toServer.flush();
                    sol = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                    //Print Maze Solution retrieved from the server
                    ArrayList<AState> mazeSolutionSteps = sol.getSolutionPath();
                    LOGGER.info(String.format("The solving algorithm is: %s", sol.toString()));
                    for (AState mazeSolutionStep : mazeSolutionSteps) {
                        int r = ((MazeState) mazeSolutionStep).getCurrent().row;
                        int c = ((MazeState) mazeSolutionStep).getCurrent().column;
                        maze.grid[r][c] = 2;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateMaze(int width, int height) {
        CommunicateWithServer_MazeGenerating(width,height);
        characterPositionRow=0;
        characterPositionColumn=0;
        setChanged();
        notifyObservers();
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    public boolean checkVictory(){
        return getCharacterPositionColumn() == maze.grid[0].length - 1 && getCharacterPositionRow() == maze.grid.length - 1;
    }

    @Override
    public void solveMaze() {
        CommunicateWithServer_SolveSearchProblem();
        setChanged();
        notifyObservers();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        int r =getCharacterPositionRow();
        int c = getCharacterPositionColumn();
        switch (movement) {
            case UP:
            case NUMPAD8:
                if (isValidUP(r, c)) {
                    characterPositionRow--;
                }
                break;
            case DOWN:
            case NUMPAD2:
                if (isValidDOWN(r, c)) {
                    characterPositionRow++;
                }
                break;
            case RIGHT:
            case NUMPAD6:
                if (isValidRIGHT(r, c)) {
                    characterPositionColumn++;
                }
                break;
            case LEFT:
            case NUMPAD4:
                if (isValidLEFT(r, c)) {
                    characterPositionColumn--;
                }
                break;
            case NUMPAD1:
                isValidNUM1(r, c);
                break;
            case NUMPAD3:
                isValidNUM3(r, c);
                break;
            case NUMPAD5:
                break;
            case NUMPAD7:
                isValidNUM7(r, c);
                break;
            case NUMPAD9:
                isValidNUM9(r, c);
                break;
        }
        setChanged();
        notifyObservers();
    }

    private boolean isValidUP(int r, int c) {
        boolean ans = false;
        if (r > 0 && maze.grid[r - 1][c] != 1) {
            ans = true;
        }
        return ans;
    }

    private boolean isValidDOWN(int r, int c) {
        boolean ans = false;
        if(r<maze.grid.length-1 && maze.grid[r+1][c]!=1) {
            ans = true;
        }
        return ans;
    }

    private boolean isValidRIGHT(int r, int c) {
        boolean ans = false;
        if(c<maze.grid[0].length && maze.grid[r][c+1]!=1) {
            ans = true;
        }
        return ans;
    }

    private boolean isValidLEFT(int r, int c) {
        boolean ans = false;
        if(c>0 && maze.grid[r][c-1]!=1) {
            ans = true;
        }
        return ans;
    }
    private void isValidNUM1(int r, int c) {
        if (isValidDOWN(r, c)) {
            if (isValidLEFT(r+1, c)) {
                characterPositionRow++;
                characterPositionColumn--;
            }
        }
        if (isValidLEFT(r, c)) {
            if (isValidDOWN(r, c-1)) {
                characterPositionRow++;
                characterPositionColumn--;
            }
        }
    }

    private void isValidNUM3(int r, int c) {
        if (isValidDOWN(r, c)) {
            if (isValidRIGHT(r+1, c)) {
                characterPositionRow++;
                characterPositionColumn++;
            }
        }
        if (isValidRIGHT(r,c)) {
            if (isValidDOWN(r, c+1)) {
                characterPositionRow++;
                characterPositionColumn++;
            }
        }
    }

    private void isValidNUM7(int r, int c) {
        if (isValidUP(r, c)) {
            if (isValidLEFT(r-1, c)) {
                characterPositionRow--;
                characterPositionColumn--;
            }
        }
        if (isValidLEFT(r, c)) {
            if (isValidUP(r, c-1)) {
                characterPositionRow--;
                characterPositionColumn--;
            }
        }
    }

    private void isValidNUM9(int r, int c) {
        if (isValidUP(r, c)) {
            if (isValidRIGHT(r-1, c)) {
                characterPositionRow--;
                characterPositionColumn++;
            }
        }
        if (isValidRIGHT(r, c)) {
            if (isValidUP(r, c+1)) {
                characterPositionRow--;
                characterPositionColumn++;
            }
        }
    }

    public void save(File file)
    {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fileOutputStream);
            maze.startPosition.row = getCharacterPositionRow();
            maze.startPosition.column = getCharacterPositionColumn();
            os.writeObject(maze);
            os.flush();
            os.close();
        } catch (IOException ignored) {

        }
    }

    public void load(File file)
    {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream os = new ObjectInputStream(fileInputStream);
            maze = (Maze)os.readObject();
            characterPositionRow = maze.startPosition.row;
            characterPositionColumn = maze.startPosition.column;
            os.close();
            setChanged();
            notifyObservers();
        } catch (IOException ignored) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
