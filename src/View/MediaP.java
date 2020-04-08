package View;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaP {
    private MediaPlayer player;

    public MediaP(String song) {
        Media media = new Media(song);
        player = new MediaPlayer(media);
    }
    public void loop(){
        player.setCycleCount(MediaPlayer.INDEFINITE);
    }
    public void play(){
        player.play();
    }
    public void pause(){
        player.pause();
    }
    public void stop(){ player.stop(); }
}
