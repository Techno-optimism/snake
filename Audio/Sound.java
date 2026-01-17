package Audio;

import java.net.URL;
import javax.sound.sampled.*;

public class Sound {
    Clip clip;
    URL[] soundURL = new URL[30];
    FloatControl volumeControl; 
    float currentVolume = -5.0f;

    public Sound() {
        soundURL[0] = getClass().getResource("background_music.wav"); 
        soundURL[1] = getClass().getResource("eat_apple_red.wav");
        soundURL[2] = getClass().getResource("eat_apple_blue.wav");
        soundURL[3] = getClass().getResource("eat_apple_purple.wav");
        soundURL[4] = getClass().getResource("explosion.wav");
        soundURL[5] = getClass().getResource("death.wav");
        soundURL[6] = getClass().getResource("playback.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                 volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                 volumeControl.setValue(currentVolume); 
            }
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    public void play() {
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (clip == null) return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void setVolume(float volume) {
        this.currentVolume = volume;

        if (volumeControl != null) {
            volumeControl.setValue(volume);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    // public void toggleMute() {
    //     if (volumeControl == null) return;
        
    //     if (!mute) {
    //         previousVolume = currentVolume;
    //         currentVolume = -80.0f;
    //         volumeControl.setValue(currentVolume);
    //         mute = true;
    //     } else {
    //         currentVolume = previousVolume;
    //         volumeControl.setValue(currentVolume);
    //         mute = false;
    //     }
    // }
}