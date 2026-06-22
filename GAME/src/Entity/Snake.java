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
    int actionCounter = 0; // Timer 
    public BufferedImage snakeImg; 

    public Snake(GamePanel gp, int col, int row) {
        this.gp = gp; 
        this.col = col; 
        this.row = row;
        getSnakeImage(); 
    }

    public void getSnakeImage() {
        try { 
            snakeImg = ImageIO.read(getClass().getResourceAsStream("/Asset/snake.png")); 
        } catch (Exception e){ 
            e.printStackTrace(); 
        }
    }

    public void update() {
        actionCounter++;
        if(actionCounter == 60) {
            int dir = (int)(Math.random() * 4); 
            int nCol = col, nRow = row;
            if(dir == 0) nRow--; 
            if(dir == 1) nRow++; 
            if(dir == 2) nCol--; 
            if(dir == 3) nCol++; 

            // cek valid move ular
            if(nCol >= 0 && nRow >= 0 && nCol < gp.maxScreenCol && nRow < gp.maxScreenRow){
                if(!gp.tileM.isCollision(nCol, nRow) && gp.tileM.mapTileNum[nCol][nRow] == 0){
                    col = nCol; 
                    row = nRow;
                }
            }
            actionCounter = 0;  
        }
    }

    public void draw(Graphics2D g2) {
        if(snakeImg != null){ 
            g2.drawImage(snakeImg, col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize, null); 
        } 
        else{ 
            g2.setColor(Color.BLACK); g2.fillRect(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize); 
        }
    }
}