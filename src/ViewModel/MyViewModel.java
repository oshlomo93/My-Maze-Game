package ViewModel;

import Model.IModel;
import View.AlertBox;
import View.MediaP;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;


public class MyViewModel extends Observable implements Observer {
    private  boolean musicPlaying= false;
    private  boolean isMute=false;
    private IModel model;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    private MediaP themeSong;
    private MediaP openPika;
    private MediaP endPika;
    public StringProperty characterPositionRow = new SimpleStringProperty("0"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("0"); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
        themeSong=new MediaP(Objects.requireNonNull(getClass().getClassLoader().getResource("music/pokemon.mp3")).toExternalForm());
        openPika =new MediaP(Objects.requireNonNull(getClass().getClassLoader().getResource("music/openPika.mp3")).toExternalForm());
        endPika=new MediaP(Objects.requireNonNull(getClass().getClassLoader().getResource("music/endPika.mp3")).toExternalForm());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers();
            if(checkVictory()) {
                themeSong.stop();
                openPika.stop();
                endPika.play();
                musicPlaying = false;
                AlertBox.display("Congratulations!","You made it!");
            }
        }
    }

    public boolean checkVictory() {
        return model.checkVictory();
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
        if(!getIsMuted()){
            if(!musicPlaying)
            {
                endPika.stop();
                openPika.play();
                themeSong.play();
                themeSong.loop();
                musicPlaying = true;
            }
        }
    }

    public void solveMaze(){ model.solveMaze();}

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void save(File file) {
        model.save(file);
    }

    public void load(File file) {
        if(!getIsMuted()){
            if(!musicPlaying)
            {
                openPika.play();
                themeSong.play();
                themeSong.loop();
                musicPlaying=true;
            }
        }
        model.load(file);
    }
    public boolean HandleChoice(CheckBox b1){
        if(b1.isSelected()) {
            musicPlaying = false;
            themeSong.pause();
            setIsMuted(true);
        }
        else{
            setIsMuted(false);
            if(model.getMaze()!=null && !checkVictory())
                themeSong.play();
        }
        return getIsMuted();
    }

    private void setIsMuted(boolean stat){
        isMute=stat;
    }
    private boolean getIsMuted(){
        return isMute;
    }



}
