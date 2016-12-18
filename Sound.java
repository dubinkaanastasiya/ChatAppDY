import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

class Sound {
    private boolean released;
    private Clip clip;

    private Sound(File f) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(f);
            clip = AudioSystem.getClip();
            clip.open(stream);
            clip.addLineListener(new Listener());
            released = true;
        } catch (Exception ex) {
            released = false;
            ex.printStackTrace();
        }
    }

    private void play () {
        if (released) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    static Sound playSound() {
        File f = new File("message.wav");
        Sound snd = new Sound(f);
        snd.play();
        return snd;
    }

    private class Listener implements LineListener {
        public void update(LineEvent ev) {
            if (ev.getType() == LineEvent.Type.STOP) {
                synchronized (clip) {
                    clip.notify();
                }
            }
        }
    }
}