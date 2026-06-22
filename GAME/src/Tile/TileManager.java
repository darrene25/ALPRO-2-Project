package Tile;

import Main.GamePanel;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class TileManager {
    GamePanel gp;
    public Tile[] tile; 
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[15];
        getTileImage();
        loadMap("/Maps/map01.txt"); 
    }

    // atur asset bisa dilewati atau ga
    public void getTileImage() {
        try {
            for(int i = 0; i < 15; i++) 
                tile[i] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/Asset/rumput.png"));
            
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/Asset/batu.png"));
            tile[1].collision = true; 
            
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/Asset/pohon.png"));
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/Asset/desa.png"));
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/Asset/jamu.png"));
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/Asset/exit.png"));
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("/Asset/pasar.png"));
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("/Asset/kolam.png"));
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/Asset/sawah.png")); // a
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("/Asset/kades.png")); // c
        } catch (Exception e){ 
            e.printStackTrace(); 
        }
    }

    // ubah txt jadi map
    public void loadMap(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];

            for(int row = 0; row < gp.maxScreenRow; row++) {
                String line = br.readLine();
                for(int col = 0; col < gp.maxScreenCol; col++) {
                    char c = line.charAt(col);

                    mapTileNum[col][row] = Character.getNumericValue(c);
                }
            }
            br.close();
        } catch(Exception e){ 
            e.printStackTrace(); 
        }
    }

    public boolean isCollision(int c, int r) {
        if(c < 0 || r < 0 || c >= gp.maxScreenCol || r >= gp.maxScreenRow) 
            return true; 
        
        return tile[mapTileNum[c][r]].collision;
    }

    public void draw(Graphics2D g2) {
        for(int row = 0; row < gp.maxScreenRow; row++) {
            for(int col = 0; col < gp.maxScreenCol; col++) {
                int tileNum = mapTileNum[col][row];
                if(tile[tileNum] != null)
                    g2.drawImage(tile[tileNum].image, col*gp.tileSize, row*gp.tileSize, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}