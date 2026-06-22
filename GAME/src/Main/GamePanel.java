package Main;

import Entity.Player;
import Entity.Snake;
import Tile.TileManager;
import UI.UI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // Frame Layar n Grid
    public final int tileSize = 32; // Ukuran 1 kotak adalah 32x32 pixel
    public int maxScreenCol = 25;   // Lebar map 25 kotak
    public int maxScreenRow = 25;   // Tinggi map 25 kotak
    public int currentLevel = 1;
    public final int uiWidth = 180; // Lebar khusus untuk Sidebar UI

    // Total ukuran layar = (Map) + (Sidebar UI)
    public int screenWidth = (tileSize * maxScreenCol) + uiWidth;
    public int screenHeight = tileSize * maxScreenRow;

    // STATE SYSTEM (Pengatur Halaman Game)
    public int gameState;
    public final int titleState = 0; // Mode Menu
    public final int playState = 1;  // Mode Bermain

    Thread gameThread; // Jantung permainan agar loop terus berjalan
    
    // INSTANSIASI OBJEK KOMPONEN GAME
    public KeyHandler keyH = new KeyHandler(this);
    public TileManager tileM = new TileManager(this);
    public Player player = new Player(this, keyH);
    public ArrayList<Snake> snakes = new ArrayList<>();
    public UI ui = new UI(this);
    public Sound music = new Sound();
    public Sound se = new Sound();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // render grafik
        this.addKeyListener(keyH);    // KeyListener 
        this.setFocusable(true);  // input ke GamePanel

        this.gameState = titleState;  // start di Menu Utama
        playMusic(0);

        // Posisi Ular awal
        snakes.add(new Snake(this, 15, 15));
        snakes.add(new Snake(this, 5, 5));
        snakes.add(new Snake(this, 21, 4));
        snakes.add(new Snake(this, 10, 20));
    }

    // Play Music 
    public void playMusic(int i){ 
        music.setFile(i); 
        music.play(); 
        music.loop(); 
    }
    public void stopMusic(){ 
        music.stop(); 

    }
    public void playSE(int i){ 
        se.setFile(i); 
        se.play(); 
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Loop Utama
    @Override
    public void run() {
        while(gameThread != null) {
            update();  
            repaint(); 
            try{ 
                Thread.sleep(16); 
            } catch(Exception e){}
        }
    }

    public void update() {
        if(gameState == playState) {
            player.update();
            
            // cek klo malam, ular baru gerak
            if(!player.isDay) {
                for(Snake s : snakes) s.update();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if(gameState == titleState) {
            ui.draw(g2); // Gambar menu
        } 
        else if(gameState == playState) {
            ui.draw(g2); // 1. Gambar Sidebar

            g2.translate(uiWidth, 0);
            tileM.draw(g2); // Gambar tanah, batu, air, dll

            if(!player.isDay) {
                for(Snake s : snakes) s.draw(g2); // Gambar ular
            }

            player.draw(g2); 

            // efek gelap malam
            if(!player.isDay) {
                g2.setColor(new Color(0, 0, 50, 110));
                g2.fillRect(0, 0, tileSize * maxScreenCol, tileSize * maxScreenRow);
            }

            g2.translate(-uiWidth, 0);

            if(player.gameOver || player.gameWin) {
                ui.drawResultScreens(g2);
            }
        }
        g2.dispose(); // ben ga lemot kata GPT
    }

    // next level
    public void nextLevel() {
        if(currentLevel == 1) {
            // save data
            player.moneyMap1 = player.money;
            player.stepsMap1 = player.steps;

            currentLevel = 2;
            player.money = 300;
            stopMusic(); playMusic(2); 
            tileM.loadMap("/Maps/map02.txt");
            player.resetForMap2();
            ui.addMessage("Level 2: Success!");
        }
    }
}