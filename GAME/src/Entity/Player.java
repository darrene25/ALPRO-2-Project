package Entity;

import Main.GamePanel;
import Main.KeyHandler;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;


public class Player {
    public GamePanel gp;
    public KeyHandler keyH;

    // Stats player
    public int col, row, hp, money, wood, steps, snakeCooldown;
    public boolean isDay = true, gameOver = false, gameWin = false, autoMode = false;
    public String direction = "down";

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // backtrack
    private List<int[]> autoPath = new ArrayList<>(); // Rute langkah yang harus diambil
    private boolean[][] deadEndMemo; // Dynamic Programming (Mengingat jalan buntu)
    public boolean hasRecognition = false;
    private int moveTick = 0; // Kecepatan gerak bot

    // KONFIGURASI GAME & EKONOMI
    private final int dayLimit = 10; // JUMLAH STEP DIPERLUKAN BUAT MALEM / PAGI
    private final int uangNaekLvl2 = 600;
    private final int uangMenang = 1200;
    private final int hpMinimum = 75;
    private final int hpHeal = 35;
    private final int hargaWood = 1000;
    private final int hargaFish = 30;
    private final int hargaWorms = 60;
    private final int hargaCorn = 15;
    private final int hargaJamu = 20;
    private final int jumlahCornDiperlukan = 100; // buat recognition
    
    public int fish = 0, worms = 0, seeds = 0, corn = 0;
    public int lastCornCol = -1, lastCornRow = -1; 
    public int lastFishCol = -1, lastFishRow = -1;

    // Stats akhir
    public int totalWoodSold = 0, totalFishSold = 0, totalCornCollected = 0;
    public int moneyMap1 = 0, moneyMap2 = 0;
    public int stepsMap1 = 0, stepsMap2 = 0, totalStepsAllMap = 0; 

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp; 
        this.keyH = keyH;
        this.col = 1; 
        this.row = 1; 
        this.hp = 60; 
        this.money = 0;
        getPlayerImage();
    }

    public void getPlayerImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/Asset/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/Asset/up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/Asset/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/Asset/down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/Asset/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/Asset/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/Asset/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/Asset/right2.png"));
        } catch (Exception e){ e.printStackTrace(); }
    }


    public void update(){
        if(gameOver || gameWin){
            handleMenu(); 
            return; 
        }

        // automode
        if(keyH.autoPressed){
            autoMode = !autoMode;
            autoPath.clear(); // reset rute
            keyH.autoPressed = false;
            gp.ui.addMessage(autoMode ? "Bot Active" : "Manual Mode");
        }

        if(autoMode) 
            playAuto();
        else 
            handleManual();

        if(snakeCooldown > 0) 
            snakeCooldown--;
    }

    // NAVIGASI POP-UP SAAT MATI / MENANG
    private void handleMenu(){
        if(keyH.upPressed){ gp.ui.commandNum = 0; keyH.upPressed = false; }
        if(keyH.downPressed){ gp.ui.commandNum = 1; keyH.downPressed = false; }
        if(keyH.enterPressed){
            keyH.enterPressed = false;
            if(gameWin){
                resetGame(); 
                gp.gameState = gp.titleState;
            } else if(gameOver){
                if(gp.ui.commandNum == 0) 
                    resetGame(); 
                else 
                    System.exit(0);
            }
        }
    }

    // Gerak Manual
    private void handleManual(){
        boolean moved = false;
        // Pengecekan collision sebelum bergerak
        if(keyH.upPressed && !gp.tileM.isCollision(col, row-1)){ 
            row--; 
            moved = true; 
            direction = "up"; 
            keyH.upPressed = false; 
        }
        if(keyH.downPressed && !gp.tileM.isCollision(col, row+1)){ 
            row++; 
            moved = true; 
            direction = "down"; 
            keyH.downPressed = false; 
        }
        if(keyH.leftPressed && !gp.tileM.isCollision(col-1, row)){ 
            col--; 
            moved = true; 
            direction = "left"; 
            keyH.leftPressed = false; 
        }
        if(keyH.rightPressed && !gp.tileM.isCollision(col+1, row)){ 
            col++; 
            moved = true; 
            direction = "right"; 
            keyH.rightPressed = false; 
        }
        
        // jalan
        if(moved) 
            onMove(); 
        
        // interact
        if(keyH.ePressed){ 
            interact(); 
            keyH.ePressed = false; 
        }
    }

    // Logika jalan
    private void onMove(){
        steps++; 
        totalStepsAllMap++; 
        gp.playSE(4);
        
        // ganti hari
        if(steps % dayLimit == 0) 
            isDay = !isDay;
        
        int tile = gp.tileM.mapTileNum[col][row];

        // potong pohon
        if(tile == 2 && gp.currentLevel == 1){ 
            gp.tileM.mapTileNum[col][row] = 0; // Hapus pohon
            wood++; 
            gp.ui.addMessage("Wood +1"); 
            gp.playSE(7); 
        }
        
        // Syarat next level
        if(gp.currentLevel == 1 && tile == 5 && money >= uangNaekLvl2 && hp >= hpMinimum) 
            gp.nextLevel();
        if(gp.currentLevel == 2 && tile == 5 && money >= uangMenang && hasRecognition){
            stepsMap2 = steps; 
            moneyMap2 = money; 
            gameWin = true;
        }

        // Mode malam
        if(!isDay) {
            for(Snake s : gp.snakes){
                if(Math.abs(col - s.col) + Math.abs(row - s.row) <= 1 && snakeCooldown == 0){
                    hp -= 10; snakeCooldown = 60; // biar ga sekali kena snake matihuy
                    gp.ui.addMessage("Snake Bite!");
                    if(hp <= 0){
                        gameOver = true; gp.stopMusic(); 
                        gp.playSE(3);      
                    }
                }
            }
        }

        spriteCounter++;
        if(spriteCounter > 2) { // Kecepatan ganti kaki
            if(spriteNum == 1) spriteNum = 2;
            else if(spriteNum == 2) spriteNum = 1;
            spriteCounter = 0;
        }
    }


    private void playAuto(){
        int kecepatanBot = 7; 
        moveTick++;
        if(moveTick < kecepatanBot) 
            return;
        moveTick = 0;

        int target = getTarget(); 
        
        // cari jalur
        if(autoPath.isEmpty()){
            deadEndMemo = new boolean[gp.maxScreenCol][gp.maxScreenRow]; // tandai mana jalan buntu
            
            boolean[][] visited = new boolean[gp.maxScreenCol][gp.maxScreenRow]; // tandai mana yg sudah dilewati
            
            // cek seluruh map cari targetC dan targetR
            int targetC = -1, targetR = -1;
            outer: for(int i=0; i<gp.maxScreenCol; i++){
                for(int j=0 ; j<gp.maxScreenRow ; j++){
                    if(gp.tileM.mapTileNum[i][j] == target){
                        // Gaboleh mancing atau nyawah di tempat sama
                        if(target == 10 && i == lastCornCol && j == lastCornRow) continue;
                        if(target == 9 && i == lastFishCol && j == lastFishRow) continue;
                        
                        targetC = i; // tandai column
                        targetR = j; // tandai row
                        break outer; // break semua loop
                    }
                }
            }

            // jalanin aja
            if (backtrack(col, row, target, targetC, targetR, visited, 0)){
                if (autoPath.isEmpty()){ 
                    interact(); // tidak ada rute karna sudah dilokasi
                    return; 
                }
            } else{
                // klo map error alias gaada jalan maka begitu
                autoMode = false;
                gp.ui.addMessage("Path Not Found!");
                return;
            }
        }

        if (!autoPath.isEmpty()){
            int[] next = autoPath.remove(0); // simpen langkah pertama terus hapus dari rute
            
            if(next[0] > col) 
                direction = "right"; 
            else if(next[0] < col) 
                direction = "left";
            else if(next[1] > row) 
                direction = "down"; 
            else if(next[1] < row) 
                direction = "up";
            
            col = next[0]; 
            row = next[1];
            onMove();
            
            if (gp.tileM.mapTileNum[col][row] == target){ 
                // sudah di target -> lgsg sinteract
                interact();
                autoPath.clear(); // reset rute
            }
        }
    }

    // cari jalan
    private boolean backtrack(int c, int r, int target, int tC, int tR, boolean[][] visited, int depth){
        // Kondisi Gagal (Batas kedalaman rekursi, keluar layar, menabrak tembok/jalan buntu)
        if (depth > 400) 
            return false; 
        if (c < 0 || r < 0 || c >= gp.maxScreenCol || r >= gp.maxScreenRow) 
            return false;
        if (deadEndMemo[c][r] || visited[c][r] || gp.tileM.isCollision(c, r)) 
            return false;

        // base case
        if (gp.tileM.mapTileNum[c][r] == target) {
            boolean isLastTarget = false;
            if (target == 10 && c == lastCornCol && r == lastCornRow) 
                isLastTarget = true;
            if (target == 9 && c == lastFishCol && r == lastFishRow) 
                isLastTarget = true;
            
            if (!isLastTarget) return true;
        }

        visited[c][r] = true; // posisi sudah lewat itu true

        // (Atas, Bawah, Kiri, Kanan)
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        
        // CEK MANA DEKET 
        Arrays.sort(dirs, (a, b) -> {
            int distA = Math.abs((c + a[0]) - tC) + Math.abs((r + a[1]) - tR);
            int distB = Math.abs((c + b[0]) - tC) + Math.abs((r + b[1]) - tR);
            return distA - distB;
        });

        // 
        for(int[] d : dirs){
            int testC = c + d[0];
            int testR = r + d[1];
            if (backtrack(testC, testR, target, tC, tR, visited, depth + 1)){
                autoPath.add(0, new int[]{testC, testR}); // klo berhasil ketemu rutenya, simpen di autopath
                return true;
            }
        }

        // Semua jalan buntu, balik
        visited[c][r] = false;
        deadEndMemo[c][r] = true; // simpen di jalan buntu 
        return false; 
    }

    // arah gerak
    private int getTarget(){
        int jumlahWoodDiperlukan = uangNaekLvl2 / hargaWood;

        // darah sekarat ke jamu
        if(hp <= 30 && money >= hargaJamu) return 4;
        
        if(gp.currentLevel == 1){
            if(money < uangNaekLvl2){
                // Punya kayu cukup + sisa uang buat jamu = Pulang ke Desa
                if((uangNaekLvl2 + (3 * hargaJamu)) - money <= hargaWood * wood)
                    return 3;
                
                // kumpul kayu
                return (wood >= jumlahWoodDiperlukan || (wood > 0 && hp < 40)) ? 3 : 2;
            }
            // Syarat done
            return (hp < hpMinimum) ? 4 : 5;
        }
        else{
            if(money < uangMenang){
                if(uangMenang + (3 * hargaJamu) - money <= fish * hargaFish)
                    return 8;

                if(worms > 0) return 9; // punya worm -> mancing
                if(fish > 0) return 8; // worm abis dan punya ikan -> jual ikan
                if(money >= hargaWorms) return 8; // uang cukup -> beli worm
                return 10;                         // gaisa beli worm -> panen n jual jagung
            }
            if(!hasRecognition) 
                return (corn < jumlahCornDiperlukan) ? 10 : 12;

            return 5; // syarat done -> ke exit
        }
    }

    // interact
    private void interact(){
        int t = gp.tileM.mapTileNum[col][row];

        if(t == 4 && money >= hargaJamu && hp < 100){
            money -= hargaJamu; 
            hp = Math.min(100, hp + hpHeal); 
            gp.ui.addMessage("Healed!");
        }
        else if(gp.currentLevel == 1){
            if(t == 3 && wood > 0){
                totalWoodSold += wood; 
                money += wood * hargaWood; 
                wood = 0;
                gp.ui.addMessage("Wood Sold!");
            }
        }
        else{
            if(t == 8){
                if(fish > 0){
                    totalFishSold += fish; 
                    money += fish * hargaFish; 
                    fish = 0;
                    gp.ui.addMessage("Fish Sold!");
                }
                else if(worms == 0 && money >= hargaWorms){
                    money -= hargaWorms; 
                    worms += 15;
                    gp.ui.addMessage("Bought Worms");
                }
                else if(money < hargaWorms && corn > 0){
                    corn--; 
                    money += hargaCorn;
                    gp.ui.addMessage("Sold Corn");
                }
            }
            else if(t == 9){
                if(worms > 0){
                    if(col != lastFishCol || row != lastFishRow){
                        fish++; 
                        worms--;
                        lastFishCol = col; 
                        lastFishRow = row;
                        gp.ui.addMessage("Caught Fish!"); 
                        gp.playSE(6);
                    }
                }
            }
            else if(t == 10 && corn < 100){
                corn++; 
                totalCornCollected++; 
                lastCornCol = col; lastCornRow = row;
                gp.ui.addMessage("Corn: " + corn); 
                gp.playSE(5);
            }
            else if(t == 12 && corn >= 100){
                corn -= 100; 
                hasRecognition = true; 
                gp.ui.addMessage("Recognition OK!");
            }
        }
        autoPath.clear(); // reset rute
    }

    // RESET GAME
    public void resetGame(){
        gp.currentLevel = 1; 
        gp.maxScreenCol = 25;
        gp.maxScreenRow = 25;
        gp.tileM.loadMap("/Maps/map01.txt");
        col = 1; 
        row = 1; 
        hp = 30; 
        money = 0; 
        wood = 0; 
        steps = 0; 
        corn = 0; 
        fish = 0; 
        worms = 0;
        totalStepsAllMap = 0; 
        totalWoodSold = 0; 
        totalFishSold = 0; 
        totalCornCollected = 0;
        moneyMap1 = 0; 
        moneyMap2 = 0; 
        stepsMap1 = 0; 
        stepsMap2 = 0;
        hasRecognition = false; gameOver = false; gameWin = false; autoMode = false; autoPath.clear();
        lastFishCol = -1; lastFishRow = -1; lastCornCol = -1; lastCornRow = -1;
    }

    public void resetForMap2(){
        this.col = 1; this.row = 1; this.steps = 0; this.autoPath.clear(); this.autoMode = false;
        lastFishCol = -1; lastFishRow = -1; lastCornCol = -1; lastCornRow = -1;
    }

    public void draw(Graphics2D g2){
        BufferedImage img = null;
        
        switch(direction) {
            case "up":
                if(spriteNum == 1) img = up1;
                if(spriteNum == 2) img = up2;
                break;
            case "down":
                if(spriteNum == 1) img = down1;
                if(spriteNum == 2) img = down2;
                break;
            case "left":
                if(spriteNum == 1) img = left1;
                if(spriteNum == 2) img = left2;
                break;
            case "right":
                if(spriteNum == 1) img = right1;
                if(spriteNum == 2) img = right2;
                break;
        }
        
        if(autoMode){ 
            g2.setColor(new Color(0, 255, 255, 60)); 
            g2.fillRect(col*gp.tileSize, row*gp.tileSize, gp.tileSize, gp.tileSize); 
        }
        g2.drawImage(img, col*gp.tileSize, row*gp.tileSize, gp.tileSize, gp.tileSize, null);
    }
}