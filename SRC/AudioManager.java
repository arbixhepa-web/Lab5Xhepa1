import javax.sound.sampled.*;
import java.io.File;

public class AudioManager {

    public void playFire() {
        play("src/sounds/fire.wav");
    }

    public void playHit() {
        play("src/sounds/hit.wav");
    }

    private void play(String path) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound error: " + e.getMessage());
        }
    }
}