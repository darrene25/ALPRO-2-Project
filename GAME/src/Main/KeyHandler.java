package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    // Bendera penanda tombol apa yang sedang ditahan
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean autoPressed, ePressed, enterPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // KETIKA TOMBOL DITEKAN TAHAN
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(gp.gameState == gp.titleState) { // Kontrol saat di Menu
            if(code == KeyEvent.VK_W) { gp.ui.commandNum--; if(gp.ui.commandNum < 0) gp.ui.commandNum = 1; }
            if(code == KeyEvent.VK_S) { gp.ui.commandNum++; if(gp.ui.commandNum > 1) gp.ui.commandNum = 0; }
            if(code == KeyEvent.VK_ENTER) {
                if(gp.ui.commandNum == 0) { // Pilih "PLAY GAME"
                    gp.stopMusic(); gp.playMusic(1); gp.gameState = gp.playState;
                } else { // Pilih "EXIT"
                    System.exit(0);
                }
            }
        } 
        else if(gp.gameState == gp.playState) { // Kontrol saat Main
            if(code == KeyEvent.VK_W) upPressed = true;
            if(code == KeyEvent.VK_S) downPressed = true;
            if(code == KeyEvent.VK_A) leftPressed = true;
            if(code == KeyEvent.VK_D) rightPressed = true;
            if(code == KeyEvent.VK_E) ePressed = true;      // Interaksi manual
            if(code == KeyEvent.VK_B) autoPressed = true;   // Trigger Bot
            if(code == KeyEvent.VK_ENTER) enterPressed = true;
        }
    }

    // KETIKA TOMBOL DILEPAS (Kembalikan ke status false)
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W) upPressed = false;
        if(code == KeyEvent.VK_S) downPressed = false;
        if(code == KeyEvent.VK_A) leftPressed = false;
        if(code == KeyEvent.VK_D) rightPressed = false;
        if(code == KeyEvent.VK_E) ePressed = false;
        if(code == KeyEvent.VK_ENTER) enterPressed = false;
    }
}