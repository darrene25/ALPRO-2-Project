package Main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[8]; // Array menampung alamat file audio

    public Sound() {
        // Melakukan mapping path audio ke array
        soundURL[0] = getClass().getResource("/Sound/MENU.wav");
        soundURL[1] = getClass().getResource("/Sound/MAP1.wav");
        soundURL[2] = getClass().getResource("/Sound/MAP2.wav");
        soundURL[3] = getClass().getResource("/Sound/GAMEOVER.wav");
        soundURL[4] = getClass().getResource("/Sound/WALKING.wav"); 
        soundURL[5] = getClass().getResource("/Sound/NYAWAH.wav");  
        soundURL[6] = getClass().getResource("/Sound/FISHING.wav"); 
        soundURL[7] = getClass().getResource("/Sound/CUTWOOD.wav"); 
    }

    // Memasukkan audio ke dalam "CD Player" (Clip)
    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) { System.out.println("Gagal memuat suara di index " + i + ": " + e.getMessage()); }
    }

    // Perintah jalankan Audio
    public void play() { if(clip != null) clip.start(); }
    public void loop() { if(clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY); }
    public void stop() { if(clip != null) clip.stop(); }
}