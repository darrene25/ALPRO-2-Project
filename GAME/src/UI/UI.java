package UI;

import Main.GamePanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class UI {

    GamePanel gp;
    // Variasi Font yang digunakan sepanjang game
    Font arial_14, arial_16_bold, arial_20_bold, arial_12, arial_18_bold, arial_40_bold, arial_60_bold;
    
    // Sistem Notifikasi Log
    public String message = "Welcome, Player!";
    public int messageCounter = 0; // Timer hilangnya pesan log
    public int commandNum = 0; // Posisi panah ">" saat memilih menu

    BufferedImage titleBg, winBg, deathMap1, deathMap2;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_12 = new Font("Arial", Font.PLAIN, 12);
        arial_14 = new Font("Arial", Font.PLAIN, 14);
        arial_16_bold = new Font("Arial", Font.BOLD, 16);
        arial_18_bold = new Font("Arial", Font.BOLD, 18);
        arial_20_bold = new Font("Arial", Font.BOLD, 20);
        arial_40_bold = new Font("Arial", Font.BOLD, 40);
        arial_60_bold = new Font("Arial", Font.BOLD, 60);
        
        loadImages();
    }

    private void loadImages() {
        try {
            titleBg = ImageIO.read(getClass().getResourceAsStream("/Asset/background_menu_awal.jpeg"));
            winBg = ImageIO.read(getClass().getResourceAsStream("/Asset/finish.png"));
            deathMap1 = ImageIO.read(getClass().getResourceAsStream("/Asset/mati_map1.png"));
            deathMap2 = ImageIO.read(getClass().getResourceAsStream("/Asset/mati_map2.png"));
        } catch (Exception e) {}
    }

    public void addMessage(String text) {
        message = text;
        messageCounter = 180; // Teks tampil selama 180 frame (sekitar 3 detik)
    }

    // Fungsi canggih untuk selalu mencari titik tengah layar X berdasarkan panjang string
    private int getXforCenteredText(Graphics2D g2, String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }

    public void draw(Graphics2D g2) {
        // ANTI-ALIASING: Teknik grafis membuat ujung text/huruf menjadi sangat halus, tidak bergerigi
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (gp.gameState == gp.titleState) drawTitleScreen(g2);
        else drawGameHUD(g2);
    }

    private void drawTitleScreen(Graphics2D g2) {
        if(titleBg != null) g2.drawImage(titleBg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        else { g2.setColor(Color.DARK_GRAY); g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight); }

        // Kotak Transparan Background Menu
        int menuWidth = 220, menuHeight = 120;
        int menuX = gp.screenWidth / 2 - (menuWidth / 2);
        int menuY = gp.screenHeight - 180; 
        
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 25, 25);
        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 25, 25);

        g2.setFont(arial_20_bold);
        String menu1 = "PLAY GAME", menu2 = "EXIT";
        int textX1 = getXforCenteredText(g2, menu1), textX2 = getXforCenteredText(g2, menu2);
        int playTextY = menuY + 50, exitTextY = menuY + 95;

        // Visual Interaksi Kursor Menu
        g2.setColor(commandNum == 0 ? Color.YELLOW : Color.WHITE);
        g2.drawString(menu1, textX1, playTextY);
        if(commandNum == 0) g2.drawString(">", textX1 - 30, playTextY);

        g2.setColor(commandNum == 1 ? Color.YELLOW : Color.WHITE);
        g2.drawString(menu2, textX2, exitTextY);
        if(commandNum == 1) g2.drawString(">", textX2 - 30, exitTextY);
    }

    // DRAW GAME HUD (Tampilan Status Bar di kiri monitor)
    private void drawGameHUD(Graphics2D g2) {
        // Dasar Sidebar
        g2.setColor(new Color(35, 35, 35)); g2.fillRect(0, 0, gp.uiWidth, gp.screenHeight);
        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3)); g2.drawLine(gp.uiWidth, 0, gp.uiWidth, gp.screenHeight);

        int x = 20; int y = 35;
        g2.setFont(arial_16_bold); g2.setColor(Color.YELLOW);
        g2.drawString("MAZE TO RICH", x, y); y += 35;

        // PENGGAMBARAN HEALTH BAR (Persentase HP)
        g2.setFont(arial_14); g2.setColor(Color.WHITE); g2.drawString("HEALTH", x, y); y += 10;
        g2.setColor(new Color(60, 0, 0)); g2.fillRoundRect(x, y, 140, 20, 10, 10);
        g2.setColor(new Color(255, 50, 50));
        int hpWidth = (int)((double)gp.player.hp / 100 * 140);
        g2.fillRoundRect(x, y, Math.max(0, Math.min(140, hpWidth)), 20, 10, 10);
        g2.setColor(Color.WHITE); g2.drawRoundRect(x, y, 140, 20, 10, 10); y += 40;

        // KEUANGAN DAN INVENTORY
        g2.setColor(new Color(100, 255, 100));
        g2.drawString("Cash: $" + gp.player.money, x, y); y += 30;

        if(gp.currentLevel == 1) {
            g2.setColor(new Color(200, 150, 100)); g2.drawString("Wood: " + gp.player.wood, x, y); y += 25;
        } else {
            g2.setColor(Color.CYAN); g2.drawString("Worms: " + gp.player.worms, x, y); y += 25;
            g2.setColor(new Color(255, 200, 100)); g2.drawString("Corn: " + gp.player.corn + "/100", x, y); y += 25;
            g2.setColor(new Color(150, 150, 255)); g2.drawString("Fish: " + gp.player.fish, x, y); y += 25;
            
            // INDIKATOR MISI MAP 2
            if(gp.player.hasRecognition) { g2.setColor(new Color(50, 255, 50)); g2.drawString("Recog: ACHIEVED", x, y); } 
            else { g2.setColor(Color.LIGHT_GRAY); g2.drawString("Recog: Not Yet", x, y); } y += 25;
        }
        
        y += 10; g2.setColor(Color.WHITE); g2.drawString("Steps: " + gp.player.steps, x, y);
        
        // PENGGAMBARAN KOTAK ACTIVITY LOG
        y += 40; g2.setFont(arial_12); g2.setColor(Color.WHITE); g2.drawString("ACTIVITY LOG", x, y); y += 10;
        g2.setColor(new Color(0, 0, 0, 180)); g2.fillRoundRect(x, y, 140, 45, 10, 10);
        g2.setColor(Color.GRAY); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(x, y, 140, 45, 10, 10);

        if(messageCounter > 0) { 
            g2.setFont(arial_14); g2.setColor(Color.CYAN); g2.drawString(message, x + 10, y + 28); messageCounter--; 
        }
    }

    // TAMPILAN POP-UP MENANG / KALAH
    public void drawResultScreens(Graphics2D g2) {
        if(gp.player.gameWin) {
            if(winBg != null) g2.drawImage(winBg, 0, 0, gp.screenWidth, gp.screenHeight, null);
            
            int boxWidth = 400, boxHeight = 400;
            int boxX = gp.screenWidth/2 - (boxWidth/2);
            int boxY = 180;

            // Box Statistik Kemenangan
            g2.setColor(new Color(0, 0, 0, 220)); g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 30, 30);
            g2.setColor(Color.YELLOW); g2.setStroke(new BasicStroke(4)); g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 30, 30);

            g2.setFont(arial_20_bold); String title = "VICTORY STATISTICS";
            g2.drawString(title, getXforCenteredText(g2, title), boxY + 40);

            // DATA PENGGABUNGAN STATISTIK DARI PLAYER
            g2.setFont(arial_18_bold); g2.setColor(Color.WHITE);
            int startX = boxX + 40, startY = boxY + 80, gap = 32;

            g2.drawString("1. Total Wood Sold: " + gp.player.totalWoodSold, startX, startY);
            g2.drawString("2. Map 1 Coins: $" + gp.player.moneyMap1, startX, startY + gap);
            g2.drawString("3. Map 2 Coins: $" + gp.player.moneyMap2, startX, startY + gap*2);
            g2.drawString("4. Total Fish Sold: " + gp.player.totalFishSold, startX, startY + gap*3);
            g2.drawString("5. Total Corn Collected: " + gp.player.totalCornCollected, startX, startY + gap*4);
            g2.drawString("6. Steps Map 1: " + gp.player.stepsMap1, startX, startY + gap*5);
            g2.drawString("7. Steps Map 2: " + gp.player.stepsMap2, startX, startY + gap*6);
            
            g2.setColor(Color.YELLOW);
            g2.drawString("8. TOTAL STEPS: " + gp.player.totalStepsAllMap, startX, startY + gap*7);

            g2.setFont(arial_16_bold); g2.setColor(Color.WHITE);
            String msg = "Press ENTER to Return to Menu";
            g2.drawString(msg, getXforCenteredText(g2, msg), boxY + boxHeight - 20);

        } else if(gp.player.gameOver) {
            BufferedImage deathImg = (gp.currentLevel == 1) ? deathMap1 : deathMap2;
            if(deathImg != null) g2.drawImage(deathImg, 0, 0, gp.screenWidth, gp.screenHeight, null);
            
            int boxX = gp.screenWidth / 2 - 100;
            g2.setColor(new Color(0, 0, 0, 200)); g2.fillRoundRect(boxX, 450, 200, 120, 20, 20);
            g2.setColor(Color.WHITE); g2.drawRoundRect(boxX, 450, 200, 120, 20, 20);

            g2.setFont(arial_20_bold);
            String t1 = "RETRY", t2 = "EXIT";
            int tx1 = getXforCenteredText(g2, t1), tx2 = getXforCenteredText(g2, t2);

            g2.setColor(commandNum == 0 ? Color.YELLOW : Color.WHITE);
            g2.drawString(t1, tx1, 500);
            if(commandNum == 0) g2.drawString(">", tx1 - 30, 500);

            g2.setColor(commandNum == 1 ? Color.YELLOW : Color.WHITE);
            g2.drawString(t2, tx2, 550);
            if(commandNum == 1) g2.drawString(">", tx2 - 30, 550);
        }
    }
}