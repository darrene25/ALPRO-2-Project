package Entity;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Snake {

    GamePanel gp;
    public int col;
    public int row;
    int actionCounter = 0; 
    
    // VARIABEL ANIMASI BARU
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction = "down"; // Arah default
    public int spriteNum = 1;
    public int spriteCounter = 0;

    public Snake(GamePanel gp, int col, int row) {
        this.gp = gp; 
        this.col = col; 
        this.row = row;
        getSnakeImage(); 
    }

    public void getSnakeImage() {
        try { 
            // Load 8 asset ghost baru sesuai nama file Anda
            up1 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/Asset/ghost_right2.png"));
        } catch (Exception e){ 
            e.printStackTrace(); 
        }
    }

    public void update() {
        // Logika Gerakan (Tetap seperti aslinya agar tidak merubah gameplay)
        actionCounter++;
        if(actionCounter == 60) {
            int dir = (int)(Math.random() * 4); 
            int nCol = col, nRow = row;
            
            if(dir == 0) { nRow--; direction = "up"; }
            if(dir == 1) { nRow++; direction = "down"; }
            if(dir == 2) { nCol--; direction = "left"; }
            if(dir == 3) { nCol++; direction = "right"; }

            if(nCol >= 0 && nRow >= 0 && nCol < gp.maxScreenCol && nRow < gp.maxScreenRow){
                if(!gp.tileM.isCollision(nCol, nRow) && gp.tileM.mapTileNum[nCol][nRow] == 0){
                    col = nCol; 
                    row = nRow;
                }
            }
            actionCounter = 0;  
        }

        // LOGIKA ANIMASI (Agar hantu berganti sprite 1 dan 2 setiap 15 frame)
        spriteCounter++;
        if(spriteCounter > 15) {
            if(spriteNum == 1) spriteNum = 2;
            else if(spriteNum == 2) spriteNum = 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage img = null;

        // Pilih gambar berdasarkan arah dan nomor sprite
        switch(direction) {
            case "up":
                img = (spriteNum == 1) ? up1 : up2; break;
            case "down":
                img = (spriteNum == 1) ? down1 : down2; break;
            case "left":
                img = (spriteNum == 1) ? left1 : left2; break;
            case "right":
                img = (spriteNum == 1) ? right1 : right2; break;
        }

        if(img != null){ 
            g2.drawImage(img, col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize, null); 
        } 
        else { 
            // Fallback jika gambar gagal load
            g2.setColor(Color.BLACK); 
            g2.fillRect(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize); 
        }
    }
}