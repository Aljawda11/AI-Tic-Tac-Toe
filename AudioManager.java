import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {

    private static Clip clip = null;
    private static boolean muted = false;
    private static String currentFilePath = null;
    private static FloatControl volumeControl = null;

    /**
     * Load and play a WAV file on loop.
     * Place your WAV file in the same folder as the .java files
     * and pass the filename e.g. "music.wav"
     */
    public static void playMusic(String filePath) {
        stopMusic();
        currentFilePath = filePath;

        File musicFile = new File(filePath);
        if (!musicFile.exists()) {
            System.out.println("Music file not found: " + filePath + ". Skipping audio.");
            return;
        }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

            if (!muted) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format. Please use a WAV file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
        clip = null;
        volumeControl = null;
    }

    public static void toggleMute() {
        muted = !muted;
        if (clip == null) return;

        if (muted) {
            clip.stop();
        } else {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void setMuted(boolean val) {
        if (muted != val) {
            toggleMute();
        }
    }

    /**
     * Call this when switching screens to keep music going.
     * Music persists across screens unless explicitly stopped.
     */
    public static boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}
