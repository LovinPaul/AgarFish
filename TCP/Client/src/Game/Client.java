package Game;


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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.swing.*;




public class Client extends JPanel implements Runnable {

    //TEMPORARY
    public static float wheel;
    
    //CONSTANTS
    private static int WIDTH = 1366;
    private static int HEIGHT = 600;
    
    //ClientSide
    private Socket sock;
    private ObjectInputStream objInputStream=null;
    private ObjectOutputStream objOutputStream=null;
    private int bytesReceived;
    
    private Thread getThread;
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
    private String playerName;
    private ArrayList<Fish> fishList = new ArrayList<>();
    private ArrayList<SeaFood> seaFoodList;
    public static final int SESION_ID = Integer.parseInt(//LocalDateTime.now().getDayOfYear()+ ""+
                                                            LocalDateTime.now().getHour()+""+
                                                            LocalDateTime.now().getMinute()+""+
                                                            LocalDateTime.now().getSecond());
    
    JFrame frame;
    
//    public int frameCounter;
    private boolean listConsumed=true;
    
    public static void main(String args[]){
        
        Client app = new Client();
        app.init();
        
    }
    
    public static int getWIDTH(){
        return WIDTH;
    }
    public static int getHEIGHT(){
        return HEIGHT;
    }
    
    private void init(){
        frame = new JFrame("ClientAgarFish");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
//        frame.setResizable(false);
        
        this.setLocation(0, 0);
        this.setSize(WIDTH, HEIGHT);
        this.setVisible(true);
        
        frame.add(this);
    }
    private void initSocket(){
        try {
            System.out.println("Init Socket");
            sock = new Socket("127.0.0.1", 4242);//78.97.157.147//192.168.0.171
            sock.setTcpNoDelay(true);
            
            objOutputStream = new ObjectOutputStream(sock.getOutputStream());
            objInputStream = new ObjectInputStream(sock.getInputStream());

            objOutputStream.writeInt(SESION_ID);
            
            System.out.println("Client connected with sesionID : " +SESION_ID);
            
            getThread = new Thread(new GetData());
            getThread.start();
            
        } catch (IOException ex) {
            System.out.println("initSocket() :" + ex.getMessage());
        }        
    }
    private void initGame(){
        
        GraphicsPanel.init();
        frame.addKeyListener(new InputKeyboard());
        frame.addMouseWheelListener(new InputMouseWheel());
        
        player = new AngelFish(1000f, 500f, 10, 10);
        player.setPlayerID(SESION_ID);
        player.setName(JOptionPane.showInputDialog(frame, "Please choose your display name :", "Lady Java"));//"The Pussy Destroyer"
        player.setMass(150000);
        player.showLocation();
        
        for(int i=0; i<15; i++){
            new Bubles((float)Math.random()*10000, (float) (Math.random()*(GraphicsPanel.getBackground()[0].getHeight()+100)));
        }
        
        inGame=true;
    }    
    
    //Socket
    public ArrayList[] getFishList(){
        try {
            return (ArrayList[]) objInputStream.readObject();
        } catch (IOException ex) {
            System.out.println("IOException in getFishList :" + ex.getMessage());
            return null;
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException in getFishList :" + ex.getMessage());
            return null;
        }
    }
    public void sendFish(Fish fish){
        
        try {
            
            objOutputStream.writeObject(fish);
            objOutputStream.flush();
            objOutputStream.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("sendFish : " + ex.getMessage());
        }
    }
    //Game
    private void randomBuble(){
        if(Bubles.getBublesList().size()<15){
            new Bubles((float)Math.random()*10000, GraphicsPanel.getBackground()[0].getHeight()+100);
        }
    }
    
    //Engine
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
            
            randomBuble();
            Iterator<Bubles> bubleIterator = Bubles.getBublesList().iterator();
            while ( bubleIterator.hasNext() ) {
                Bubles buble = bubleIterator.next();
                if(buble.getY()>-100){
                    buble.updateCoord();
                }else{
                    bubleIterator.remove();                    
                }
            }
            
            if(fishList.size()>0){
                for(Fish fish : fishList){
                    if(fish.isPlayer()){
                        player.setMass(fish.getMass());
                        if(fish.newGain!=null){
                            player.getGainList().add(fish.newGain);
                            player.newGain=null;
                        }
                    }else{
                        fish.updateCoord();
                    }
                }
                listConsumed=true;
            }

            player.updateCoord();
            Camera.follow(player);
            Fish.oncePerFrameUpdates();

        }
    }
    
    private void renderGame() {
        
        this.repaint();
        
    }
    
    @Override
    public void run() {
        initGame();
        initSocket();
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
            if(player==null){return;}
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
            for(int i=0; i< fishList.size();i++){
//                if(fishList.get(i).isPlayer()){
//                    player.draw(g);                    
//                }else{
                    fishList.get(i).draw(g);
//                }
            }
//            player.draw(g); 

            if(player.isAlive()){
                if(player.isInCharge()){
                    g.setColor(new Color(200, 0, 0, 127));
//                    g.setFont(new Font("Gabriola", Font.PLAIN, 15)); 
                    g.fillRect(Client.getWIDTH()/2 - player.getChargeTimerPercent(), 20, 2*player.getChargeTimerPercent(), 20);
//                    g.setColor(Color.WHITE);
//                    g.drawString(getChargeTimerPercent()+"%", Client.getWIDTH()/2, 35);

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
            g.drawString("NrOfFishes: "+fishList.size(), 750, 50);
            g.drawString("NrOfFood: "+SeaFood.getSeaFoodList().size(), 750, 70);
            g.drawString("Tile: " + (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth()), 750, 90);
            //Sock
            if(sock.isConnected() && !sock.isClosed()){
                g.drawString("Sock :-connected-", 890, 50);
            }else{
                g.drawString("Sock : disconected", 890, 50);
            }
            
            
        }
    }

    //InerCLass
    public class InputKeyboard implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar()=='x'){
                player.setMass(player.getMass()*2);
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
    ////Runnable
    public class GetData implements Runnable{
        @Override
        public void run(){
            while(inGame){
                if(sock!=null && sock.isConnected() && listConsumed){// 
                    //send
                    sendFish(player);
                    //get
                    ArrayList[] arrayList;
                    arrayList = getFishList();
                    fishList=arrayList[0];
//                    SeaFood.setFishLit(arrayList[1]);
                    listConsumed=false;
                }else{
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        System.out.println("GetData Sleep : " + ex.getMessage());
                    }
                }
            }
        }
    }
}
