package Game;


import AI.AI;
import Enviroment.*;
import Fish.*;
import SeaFood.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import javax.swing.*;




public class Game extends JPanel implements Runnable {

    //TEMPORARY
    public static float wheel;
    
    //CONSTANTS
    private static int WIDTH = 1366;
    private static int HEIGHT = 600;
    
    
    private Thread thread;
    private boolean running;
    private final int FPS = 30;
    private final long targetTime = 1000 / FPS;
    private boolean inGame;
    
    //playerControls
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean shift;
    //
    Fish player;
    Fish killerFish = null;
    
    JFrame frame;
    
    public static void main(String args[]){
        
        Game app = new Game();
        app.init();
        
    }
    
    public static int getWIDTH(){
        return WIDTH;
    }
    public static int getHEIGHT(){
        return HEIGHT;
    }

    
    private void init(){
        frame = new JFrame("AgarFish");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
//        frame.setResizable(false);
        
        this.setLocation(0, 0);
        this.setSize(WIDTH, HEIGHT);
        this.setVisible(true);
        
        frame.add(this);

    }
    
    private void initGame(){        
        
        GraphicsPanel.init();
        frame.addKeyListener(new InputKeyboard());
        frame.addMouseWheelListener(new InputMouseWheel());
        
        player = new AngelFish(1000f, 500f, 10, 10);
        player.setIsHuman(true);
        player.setMass(2000);
        player.showLocation();
        
        for(int i=0; i<15; i++){
            new Bubles((float)Math.random()*10000, (float) (Math.random()*(GraphicsPanel.getBackground()[0].getHeight()+100)));
        }
        
        inGame=true;
    }
    private void randomSeaFood(){
        if(SeaFood.getSeaFoodList().size()<10){
            new SmlSquid((float) (Math.random()*11000),(float)(Math.random()*800),20,10);
        }        
    }
    private void randomBuble(){
        if(Bubles.getNrOfBubles()<15){
            new Bubles((float)Math.random()*10000, GraphicsPanel.getBackground()[0].getHeight()+100);
        }
    }
    private void randomFish(){

        if(Fish.getFishList().size()<25){
            switch((int)Math.round(Math.random()*12)){
                case 0:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;
                case 1:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;
                case 2:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;
                case 3:
                    new AngelFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("AngelFish spawn");
                    break;
                case 4:
                    new GuppieFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("GuppieFish spawn");
                    break;
                case 5:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;
                case 6:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;  
                case 7:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;  
                case 8:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;  
                case 9:
                    new JaiFish((float)Math.random()*10000, (float)Math.random()*500, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("JaiFish spawn");
                    break;  
                case 10:
                    new GuppieFish((float)Math.random()*10000, (float)Math.random()*500, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("GuppieFish spawn");
                    break;
                case 11:
                    new GuppieFish((float)Math.random()*10000, (float)Math.random()*500, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    System.out.println("GuppieFish spawn");
                    break;
            }
        }
    }
    private void getUserInput(){
        
        if(up){
            if(Camera.isFreeCam()){
                Camera.goUp();
            }else{
                player.goUp();
            }
        }
        if(down){
            if(Camera.isFreeCam()){
                Camera.goDown();
            }else{
                player.goDown();
            }
        }
        if(right){
            if(Camera.isFreeCam()){
                Camera.goRight();
            }else{
                player.goRight();
            }
        }
        if(left){
            if(Camera.isFreeCam()){
                Camera.goLeft();
            }else{
                player.goLeft();
            }
        }
        
        if(shift && (left || right)){
            player.charge();
        }
        
    }
    
    private void updateGameMechanics(){
        if(inGame){
            
            randomSeaFood();
            randomBuble();
            randomFish();
            
            if(Camera.isFreeCam()){Camera.updateCoord();}
            if(player.isAlive()){
                Camera.follow(player);
            }else{
                if(killerFish!=null){Camera.follow(killerFish);}
            }
            
            Iterator<Bubles> bubleIterator = Bubles.getBublesList().iterator();
            while ( bubleIterator.hasNext() ) {
                Bubles buble = bubleIterator.next();
                if(buble.getY()>-100){
                    buble.updateCoord();
                }else{
                    Bubles.nrOfBublesDecrement();
                    bubleIterator.remove();                    
                }
            }
            
            
            Iterator<SeaFood> foodIterator = SeaFood.getSeaFoodList().iterator();
            while ( foodIterator.hasNext() ) {
                SeaFood food = foodIterator.next();
                if(food.isAlive()){
                    AI.seaFoodMovement(food);
                    food.updateCoord();
                }else{
                    foodIterator.remove();                    
                }
            }
            
            
            Iterator<Fish> fishIterator = Fish.getFishList().iterator();
            while (fishIterator.hasNext()) {
                Fish fish = fishIterator.next();
                fish.updateCoord();
                
                if(fish.isAlive()){
                    double huntDist = 999999999;
                    SeaFood seafoodTarget=null;
                    Fish fishTarget=null;
                    
                    for(Fish fish2 : Fish.getFishList()){
                        //Hunt FishFood
                        if(fish!=player && fish2.isAlive() && 
                                (fish instanceof AngelFish) && //hunter
                                (fish2 instanceof JaiFish || fish2 instanceof GuppieFish)){
                            double newDist = AI.distanceBetween(fish.getX(), fish.getY(), fish2.getX(), fish2.getY());
                            if(huntDist > newDist){
                                huntDist = newDist;
                                fishTarget = fish2;
                            }
                        }
                        //Colision
                        if(fish2!=fish && fish2.isAlive() && fish.getCurentRectangle().contains(fish2.getCurentRectangle())){
                            fish.addMass(fish2.giveMass());
                            if(fish2==player){killerFish=fish;}
                        }
                    }
                    AI.huntFishFood(fish, fishTarget);

                    for(SeaFood food : SeaFood.getSeaFoodList()){
                        //Hunt SeaFood
                        if(fish!=player && (fish instanceof JaiFish || fish instanceof GuppieFish)){
                            double newDist = AI.distanceBetween(fish.getX(), fish.getY(), food.getX(), food.getY());
                            if(huntDist > newDist){
                                huntDist = newDist;
                                seafoodTarget = food;
                            }
                        }
                        //Colision
                        if(fish.getCurentRectangle().contains(food.getCurentRectangle())){
                            fish.addMass(food.giveMass());
                        }
                    }
                    AI.hundSeaFood(fish, seafoodTarget);
                }else{
                    if(fish.getY()>(GraphicsPanel.getBackground()[0].getHeight()+fish.getSwimToLeft()[0].getHeight())){
                        fishIterator.remove();                        
                    }
                }
            }
            
            //<editor-fold defaultstate="collapsed" desc="old interation">
//            for(Fish fish : Fish.getFishList()){
//                fish.updateCoord();
//                if(fish.isAlive()){
//                    double huntDist = 999999999;
//                    SeaFood seafoodTarget=null;
//                    Fish fishTarget=null;
//
//                    for(Fish fish2 : Fish.getFishList()){
//                        //Hunt FishFood
//                        if(fish!=player && fish2.isAlive() &&
//                                (fish instanceof AngelFish) && //hunter
//                                (fish2 instanceof JaiFish || fish2 instanceof GuppieFish)){
//                            double newDist = AI.distanceBetween(fish.getX(), fish.getY(), fish2.getX(), fish2.getY());
//                            if(huntDist > newDist){
//                                huntDist = newDist;
//                                fishTarget = fish2;
//                            }
//                        }
//                        //Colision
//                        if(fish2!=fish && fish2.isAlive() && fish.getCurentRectangle().contains(fish2.getCurentRectangle())){
//                            fish.addMass(fish2.giveMass());
//                            if(fish2==player){killerFish=fish;}
//                        }
//                    }
//                    AI.huntFishFood(fish, fishTarget);
//
//                    for(SeaFood food : SeaFood.getSeaFoodList()){
//                        //Hunt SeaFood
//                        if(fish!=player && (fish instanceof JaiFish || fish instanceof GuppieFish)){
//                            double newDist = AI.distanceBetween(fish.getX(), fish.getY(), food.getX(), food.getY());
//                            if(huntDist > newDist){
//                                huntDist = newDist;
//                                seafoodTarget = food;
//                            }
//                        }
//                        //Colision
//                        if(fish.getCurentRectangle().contains(food.getCurentRectangle())){
//                            fish.addMass(food.giveMass());
//                        }
//                    }
//                    AI.hundSeaFood(fish, seafoodTarget);
//                }
//            }
            //</editor-fold>
        }
    }
    
    private void renderGame() {
        
        this.repaint();
        
    }
    
    @Override
    public void run() {
        initGame();
        long start;
        long elapsed;
        long wait;
        
        while (running) {
            start = System.nanoTime();
            
            getUserInput();
            updateGameMechanics();
            renderGame();

            elapsed = System.nanoTime() - start;
            wait = targetTime - elapsed / 1000000;
            if (wait < 0) {wait = 5;}
            try {
                Thread.sleep(wait);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        
        
    }
    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        running = true;
    }
    
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        HEIGHT = this.getHeight();
        WIDTH = this.getWidth();
        
        if(inGame){
            
            //Background Tiles
            int tileX = (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth());
            tileX = GraphicsPanel.getBackground()[0].getWidth() * tileX;
            g.drawImage(GraphicsPanel.getBackground()[0], (int) -Camera.x + tileX, (int) -Camera.y, null);
            tileX+=GraphicsPanel.getBackground()[0].getWidth();
            g.drawImage(GraphicsPanel.getBackground()[0], (int) -Camera.x + tileX, (int) -Camera.y, null);
            //Level Limits
            if(Camera.x<1600){
                g.drawImage(GraphicsPanel.getBackground()[1], (int) -Camera.x, (int) -Camera.y, null);
            }
            if(Camera.x>9500){
                g.drawImage(GraphicsPanel.getBackground()[2], (int) -Camera.x + 12000 - GraphicsPanel.getBackground()[2].getWidth(), (int) -Camera.y, null);
            }
            //NO Enchanced For Loop because of uknown concurrentmodificationexception 
            for(int i=0; i< Bubles.getBublesList().size();i++){
                Bubles.getBublesList().get(i).draw(g);
            }
            
            //NO Enchanced For Loop because of uknown concurrentmodificationexception 
            for(int i=0; i< SeaFood.getSeaFoodList().size();i++){
                SeaFood.getSeaFoodList().get(i).draw(g);
            }
            
            //NO Enchanced For Loop because of uknown concurrentmodificationexception 
            for(int i=0; i< Fish.getFishList().size();i++){
                Fish.getFishList().get(i).draw(g);
            }

            if(player.isAlive()){
                if(player.isInCharge()){
                    g.setColor(new Color(200, 0, 0, 127));
//                    g.setFont(new Font("Gabriola", Font.PLAIN, 15)); 
                    g.fillRect(Game.getWIDTH()/2 - player.getChargeTimerPercent(), 20, 2*player.getChargeTimerPercent(), 20);
//                    g.setColor(Color.WHITE);
//                    g.drawString(getChargeTimerPercent()+"%", Game.getWIDTH()/2, 35);

                }
            }else{
                Color deathColor = new Color(200, 0, 0, 127);
                g.setColor(Color.RED);
                g.setFont(new Font("Gabriola", Font.BOLD, 40));
                
                if(killerFish!=null){
                    g.drawString("You're fish meat...", WIDTH/2, HEIGHT/2);
                }else{
                    g.drawString("Starved to DEATH!", WIDTH/2, HEIGHT/2);
                }
                g.setColor(deathColor);
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
            
            //Info
            g.setColor(Color.black);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 15)); 

            //Player
            g.setColor(Color.BLACK);
            g.drawString("player: " + player, 50, 50);
            g.drawString("playerX: " +player.getX(), 50, 70);
            g.drawString("playerY: " +player.getY(), 50, 90);
            g.drawString("Mass: " + player.getMass(), 50, 110);
            g.drawString("SpeedX: " + player.getSpeedX(), 50, 130);
            if(player.isInCharge()){g.setColor(Color.red);}
            g.drawString("InCharge: " + player.isInCharge(), 50, 150);
            g.drawString("ChargeTimer: " + player.getChargeTimer(), 50, 170);
            g.drawString("ChargeCoolDown: " + player.getChargeCoolDown(), 50, 190);
            g.setColor(Color.black);
            
            //KillerFish
            g.drawString("Killer: " +killerFish, 330, 50);
            if(killerFish!=null){
                g.drawString("KillerX: " +killerFish.getX(), 330, 70);
                g.drawString("KillerY: " +killerFish.getY(), 330, 90);
                g.drawString("Mass: " + killerFish.getMass(), 330, 110);
                g.drawString("SpeedX: " + killerFish.getSpeedX(), 330, 130);
                if(killerFish.isInCharge()){g.setColor(Color.red);}
                g.drawString("InCharge: " + killerFish.isInCharge(), 330, 150);
                g.drawString("ChargeTimer: " + killerFish.getChargeTimer(), 330, 170);
                g.drawString("ChargeCoolDown: " + killerFish.getChargeCoolDown(), 330, 190);
                g.setColor(Color.black);
            }            
            //Camera
            g.drawString("CameraX: " +Camera.x, 610, 50);
            g.drawString("CameraY: " +Camera.y, 610, 70);
            g.drawString("DifX: " + Camera.getDiferenceX(), 610, 90);
            g.drawString("Zoom: " + Camera.getZoom(), 610, 110);
            g.drawString("Wheel: " + wheel, 610, 130);
            //GameInfo
            g.drawString("NrOfFishes: "+Fish.getFishList().size(), 750, 50);
            g.drawString("NrOfFood: "+SeaFood.getSeaFoodList().size(), 750, 70);
            g.drawString("Tile: " + (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth()), 750, 90);
        }
    }

    
    //InerCLass
    public class InputKeyboard implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar()=='x'){
                
                Fish fish = Fish.getFishList().get((int)(Math.random()*Fish.getFishList().size()));
                if(fish.isAlive()){
                    if(player!=null){
                        player.setIsHuman(false);
                    }
                    player = fish;
                    player.setIsHuman(true);
                    killerFish=null;
                    player.showLocation();                    
                    
                }
//                if(player.isAlive()){
//                    Camera.x = player.getX()-HEIGHT/2;
//                    Camera.y = player.getY()-WIDTH/2;
//                    Camera.speedX =0;
//                    Camera.speedY =0;
//                }
                
            }
            if(e.getKeyChar()=='q'){
                player.giveBait();
            }
            
            if(e.getKeyChar()=='z'){
                Camera.setFreeCam(!Camera.isFreeCam());
            }
            
            if(e.isShiftDown()){
                player.charge();
            }
            
        }

        @Override
        public void keyPressed(KeyEvent e) {
            
            if(e.getKeyCode()==38){up=true;}
            if(e.getKeyCode()==40){down=true;}
            if(e.getKeyCode()==37){left=true;}
            if(e.getKeyCode()==39){right=true;}

            if(e.getKeyChar() == 'w'){up=true;}
            if(e.getKeyChar() == 's'){down=true;}
            if(e.getKeyChar() == 'a'){left=true;}
            if(e.getKeyChar() == 'd'){right=true;}
            shift = e.isShiftDown();

            
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode()==38){up=false;}
            if(e.getKeyCode()==40){down=false;}
            if(e.getKeyCode()==37){left=false;}
            if(e.getKeyCode()==39){right=false;}
            
            if(e.getKeyChar() == 'w'){up=false;}
            if(e.getKeyChar() == 's'){down=false;}
            if(e.getKeyChar() == 'a'){left=false;}
            if(e.getKeyChar() == 'd'){right=false;}
            shift = e.isShiftDown();
        }
        
    }
    public class InputMouseWheel implements MouseWheelListener{
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            
            wheel += (float) e.getWheelRotation()/100.0;
            
        }
        
    }
    
}
