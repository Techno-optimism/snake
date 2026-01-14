package Audio;

import java.net.URL;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;

public class Sound {
    
    Clip clip;
    URL soundURL[] = new URL[30];
    public double previousVolume = 0;
    public double currentVolume = 0;
    public boolean mute = false;
    public static FloatControl backgroundFC;

    public Sound() {

        soundURL[0] = getClass().getResource("BackgroundSound.wav");
        soundURL[1] = getClass().getResource("EatApple.wav");

    }

    public void setFile(int i) {

        try {

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

            backgroundFC = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch(Exception e) {

        }
    }
    public void play() {

        clip.start();

    }
    public void loop() {

        clip.loop(Clip.LOOP_CONTINUOUSLY);

    }
    public void stop() {

        clip.stop();

    }
}
