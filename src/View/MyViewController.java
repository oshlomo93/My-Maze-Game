package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import java.io.File;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    public Button load;
    @FXML
    private MyViewModel myViewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button save;

    private boolean isMuted = false;
    private int musicPlaying = 0;

    public void setMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
        bindProperties(myViewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            displayMaze(myViewModel.getMaze());
            btn_generateMaze.setDisable(false);
            close();
        }
    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = myViewModel.getCharacterPositionRow();
        int characterPositionColumn = myViewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        mazeDisplayer.drawCharacter();
    }

    public void generateMaze() {
        int row = Integer.valueOf(txtfld_rowsNum.getText());
        int col = Integer.valueOf(txtfld_columnsNum.getText());
        if (row > 0 && col > 0) {
            btn_generateMaze.setDisable(true);
            btn_solveMaze.setDisable(false);
            save.setVisible(true);
            save.setDisable((false));
            myViewModel.generateMaze(row, col);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("please insert numbers greater than 0");
            alert.setTitle("ERROR");
            alert.setHeaderText("");
            alert.showAndWait();
        }
    }

    private void close() {
        int row = mazeDisplayer.getRows();
        int col = mazeDisplayer.getColumns();
        Maze maze = new Maze(row, col);
        if (myViewModel.checkVictory()) {
            mazeDisplayer.setMaze(maze);
            characterPositionColumn.setValue("0");
            characterPositionRow.setValue("0");
            mazeDisplayer.setCharacterPosition(0,0);
            mazeDisplayer.redrawAfterFinish();
            btn_solveMaze.setDisable(true);
            save.setDisable((true));
        }
    }

    public void solveMaze() {
        btn_solveMaze.setDisable(true);
        myViewModel.solveMaze();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        if (!myViewModel.checkVictory()) {
            myViewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    private StringProperty characterPositionRow = new SimpleStringProperty();

    private StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            mazeDisplayer.setWidth( mazeDisplayer.getWidth() + ( newSceneWidth.doubleValue() - oldSceneWidth.doubleValue() ));
            mazeDisplayer.drawNew();
        });
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            mazeDisplayer.setHeight( mazeDisplayer.getHeight() + ( newSceneHeight.doubleValue() - oldSceneHeight.doubleValue() ));
            mazeDisplayer.drawNew();
        });
    }

    public void scrollEvent(ScrollEvent scroll) {
        if(!scroll.isControlDown()) {
            return;
        }
        double zoom_fac = 1.2;
        double delta_y = scroll.getDeltaY();
        if (delta_y < 0) {
            zoom_fac = 2.0 - zoom_fac;
        }
        Scale myScale = new Scale();
        myScale.setPivotX(scroll.getSceneX());
        myScale.setPivotY(scroll.getSceneY());
        myScale.setX(mazeDisplayer.getScaleX() * zoom_fac);
        myScale.setY(mazeDisplayer.getScaleY() * zoom_fac);
        mazeDisplayer.getTransforms().add(myScale);
        scroll.consume();
    }
    public void mouseClicked() {
        this.mazeDisplayer.requestFocus();
    }

    public void saveGame() {
        FileChooser fc = new FileChooser();
        File filePath = new File("./Mazes/");
        int counter = Objects.requireNonNull(filePath.list()).length;
        if (!filePath.exists())
            filePath.mkdir();
        fc.setTitle("Saving maze");
        fc.setInitialFileName("Maze Number " + counter + "");
        fc.setInitialDirectory(filePath);
        File file = fc.showSaveDialog(mazeDisplayer.getScene().getWindow());
        if (file != null)
            myViewModel.save(file);
    }

    /////// Menu Bar Events////////
    public void sound(){
        Stage window= new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Sound");
        CheckBox b1=new CheckBox("Mute");
        b1.setSelected(isMuted);
        window.setMinWidth(350);
        window.setMinHeight(100);
        Button DoneButton=new Button("Done");
        DoneButton.setOnAction(e->{
            isMuted=myViewModel.HandleChoice(b1);
            window.close();
        });
        if(musicPlaying==1) {
            b1.setSelected(isMuted);
            musicPlaying=1;
        }
        VBox layout=new VBox(10);
        layout.getChildren().addAll(b1,DoneButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene=new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public void loadGame() {
        boolean isSolve = false;
        FileChooser fc = new FileChooser();
        fc.setTitle("Loading maze");
        File filePath = new File("./Mazes/");
        if (!filePath.exists())
            filePath.mkdir();
        fc.setInitialDirectory(filePath);
        File file = fc.showOpenDialog(new PopupWindow() {
        });
        if (file != null && file.exists() && !file.isDirectory()) {
            myViewModel.load(file);
            Maze maze = mazeDisplayer.getMaze();
            for (int i = 0; i< maze.grid.length; i++) {
                for (int j=0; j<maze.grid[0].length; j++) {
                    if (maze.grid[i][j] == 2) {
                        isSolve = true;
                    }
                }
            }
            mazeDisplayer.drawNew();
        }
        btn_solveMaze.setDisable(isSolve);
        save.setVisible(true);
    }
    public void About() {
        Stage stage = new Stage();
        stage.setTitle("About");
        Label text=new Label("This game and graphics was made by:\n" +
                                  "Omer Shlomo & Gonen Davidi.\n" +
                                  "music: Pokemon Theme Song (hebrew)\n");
        StackPane layout=new StackPane();
        Scene scene = new Scene(layout, 400, 150);
        layout.getChildren().addAll(text);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        stage.show();
    }
    public void Help() {
        Stage stage = new Stage();
        stage.setTitle("Help");
        Label text=new Label("  Greetings Dear player!\n" +
                "   Our main goal in this game is to help Pikachu get back to Ash. \n" +
                "   To stat a new game you need to choose your maze height and depth,\n" +
                "   doing so by fill in the 'rows' & 'Columns' on the upper left of the screen.\n" +
                "   Then press on the 'Generate maze' button.\n" +
                "   If you already played the game you you can load you latest save by pressing the load button.\n" +
                "   We wish you good luck and hope you can bring back Pikachu to Ash :) ");
        StackPane layout=new StackPane();
        Scene scene = new Scene(layout, 550, 250);
        layout.getChildren().addAll(text);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        stage.show();

    }

    public void Exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("are you sure you want to leave the game?");
        alert.setTitle("Exit?");
        alert.setHeaderText("");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) System.exit(0);
    }
}
