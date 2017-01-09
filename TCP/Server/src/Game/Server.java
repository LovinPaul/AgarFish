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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;




public class Server extends JPanel implements Runnable {

    //TEMPORARY
    public static float wheel;
    
    //CONSTANTS
    private static int WIDTH = 1366;
    private static int HEIGHT = 600;
    ////Game
    private static final int ANGEL_FISH_PERCENT=10;
    private static final int GUPPIE_FISH_PERCENT=35;
    private static final int JAI_FISH_PERCENT=55;
    private static final int MAX_NR_OF_FISH=25;
    
    //ServerSide
    private Socket sock;
    private ServerSocket serversock;
    private Thread connection;
    private ArrayList<Thread> connectionList;
    
//    private Thread getThread;
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
//    Fish killerFish = null;
    private ArrayList<Fish> fishList;
    private ArrayList<SeaFood> seaFoodList;
    JFrame frame;
    
//    private int frameCounter;
    
    public static void main(String args[]){
        
        Server app = new Server();
        app.init();
        
    }
    
    public static int getWIDTH(){
        return WIDTH;
    }
    public static int getHEIGHT(){
        return HEIGHT;
    }
    
    public void initSockets(){
        System.out.println("initSocket()");
        try {
            connectionList = new ArrayList<>();
            serversock = new ServerSocket(4242);
            manageConnections();
        } catch (IOException ex) {
            System.out.println("initSocket() :" + ex.getMessage());
        }        
    }
    private void manageConnections(){
        System.out.println("manageConnections()");
        try {
            while(true){
                sock = serversock.accept();
                sock.setTcpNoDelay(true);
                System.out.println("New connection...");
                System.out.println("IP/port :"+sock.getInetAddress() + ":" + sock.getPort());
                connection = new Thread(new Connection(sock));
                connection.start();
                synchronized(connectionList){
                    connectionList.add(connection);                    
                }
            }
            
//            objOutputStream = new ObjectOutputStream(sock.getOutputStream());
//            objInputStream = new ObjectInputStream(sock.getInputStream());
            
        } catch (IOException ex) {
            System.out.println("manageConnections() :" + ex.getMessage());
        }
    }
    private int getNrOfConnections(){
        int nrOfTh=0;
        synchronized(connectionList){
            Iterator<Thread> threadIterator = connectionList.iterator();
            while ( threadIterator.hasNext() ) {
                Thread thread = threadIterator.next();
                if(thread.isAlive()){
                    nrOfTh++;
                }else{
                    threadIterator.remove();
                }
            }
        }
        return nrOfTh;
    }
    
    private void init(){
        System.out.println("init()");
        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
//        frame.setResizable(false);
        
        this.setLocation(0, 0);
        this.setSize(WIDTH, HEIGHT);
        this.setVisible(true);
        
        frame.add(this);
        //Socket
        initSockets();
        
    }
    private void initGame(){        
        System.out.println("initGame()");
        GraphicsPanel.init();
        fishList = new ArrayList<>();
        seaFoodList = new ArrayList<>();
        frame.addKeyListener(new InputKeyboard());
        frame.addMouseWheelListener(new InputMouseWheel());

        for(int i=0; i<50; i++){
            if(i<15){
                new Bubles((float)Math.random()*10000, (float) (Math.random()*(GraphicsPanel.getBackground()[0].getHeight()+100)));
            }else if(i<25){
                randomFish();
            }
            randomSeaFood();
            
        }
        
        inGame=true;
    }

    private void randomSeaFood(){
        if(seaFoodList.size()<100){
            seaFoodList.add(new SmlSquid((float) (Math.random()*11000),(float)(Math.random()*800),20,10));
        }        
    }
    private void randomBuble(){
        if(Bubles.getNrOfBubles()<15){
            new Bubles((float)Math.random()*10000, GraphicsPanel.getBackground()[0].getHeight()+100);
        }
    }
    private void randomFish(){
        synchronized(fishList){
            if(fishList.size()<MAX_NR_OF_FISH){
                if(fishList.isEmpty()){
                    fishList.add(
                        new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
                    fishList.add(
                        new AngelFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
                    fishList.add(
                        new GuppieFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
                }
                int nrOfAngel = 0;
                int nrOfJai = 0;
                int nrOfGuppie = 0;

                for(Fish fish : fishList){
                    if(fish instanceof AngelFish){
                        nrOfAngel++;
                    }
                    if(fish instanceof JaiFish){
                        nrOfJai++;
                    }
                    if(fish instanceof GuppieFish){
                        nrOfGuppie++;
                    }
                }
                
                if(ANGEL_FISH_PERCENT/100.0*MAX_NR_OF_FISH>nrOfAngel){
                    fishList.add(
                        new AngelFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
                }
                if(JAI_FISH_PERCENT/100.0*MAX_NR_OF_FISH>nrOfJai){
                    fishList.add(
                        new JaiFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
                }
                if(GUPPIE_FISH_PERCENT/100.0*MAX_NR_OF_FISH>nrOfGuppie){
                    fishList.add(
                        new GuppieFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
                }            
            }
        }
    }

    private void getUserInput(){
        
        if(up){
            if(Camera.isFreeCam()){
                Camera.goUp();
            }else{
//                player.goUp();
            }
        }
        if(down){
            if(Camera.isFreeCam()){
                Camera.goDown();
            }else{
//                player.goDown();
            }
        }
        if(right){
            if(Camera.isFreeCam()){
                Camera.goRight();
            }else{
//                player.goRight();
            }
        }
        if(left){
            if(Camera.isFreeCam()){
                Camera.goLeft();
            }else{
//                player.goLeft();
            }
        }
        
        if(shift && (left || right)){
//            player.charge();
        }
        
    }
    
    private void updateGameMechanics(){
        if(inGame){
            
            randomBuble();
            randomFish();
            randomSeaFood();
            
            Fish.oncePerFrameUpdates();
            
//            if(player1==null || !player1.isAlive()){
//                int random = (int)Math.random()*40;
//                Fish.getFishList().get(random).setPlayer1(true);
//                player1=Fish.getFishList().get(random);
//            }
//            if(player2==null || !player2.isAlive()){
//                int random = (int)Math.random()*40;
//                Fish.getFishList().get(random).setPlayer2(true);
//                player2=Fish.getFishList().get(random);
//            }
            
            
            
            if(Camera.isFreeCam()){Camera.updateCoord();}
            
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
            
            
            Iterator<SeaFood> foodIterator = seaFoodList.iterator();
            while ( foodIterator.hasNext() ) {
                SeaFood food = foodIterator.next();
                if(food.isAlive()){
                    AI.seaFoodMovement(food);
                    food.updateCoord();
                }else{
                    foodIterator.remove();                    
                }
            }
            
            synchronized(fishList){
                Iterator<Fish> fishIterator = fishList.iterator();
                while (fishIterator.hasNext()) {
                    Fish fish = fishIterator.next();
                    fish.massDecay();

                    if(fish.isHuman()){
//                        Camera.follow(fish);
                    }else{
                        fish.updateCoord();
                    }

                    if(fish.isAlive()){
                        double huntDist = 999999999;
                        SeaFood seafoodTarget=null;
                        Fish fishTarget=null;

                        for(Fish fish2 : fishList){
                            //Hunt FishFood
                            if(!fish.isHuman() && fish2.isAlive() && 
                                    (fish instanceof AngelFish) && //hunter
                                    (fish2 instanceof JaiFish || fish2 instanceof GuppieFish)){
                                double newDist = AI.distanceBetween(fish.getX(), fish.getY(), fish2.getX(), fish2.getY());
                                if(huntDist > newDist){
                                    huntDist = newDist;
                                    fishTarget = fish2;
                                }
                            }
                            //Colision
                            if(fish2!=fish && fish2.isAlive() && fish.getRectangle().contains(fish2.getRectangle())){
                                fish.addMass(fish2.giveMass());
    //                            if(fish2==player){killerFish=fish;}
                            }
                        }
                        AI.huntFishFood(fish, fishTarget);

                        for(SeaFood food : seaFoodList){
                            //Hunt SeaFood
                            if(!fish.isHuman() && (fish instanceof JaiFish || fish instanceof GuppieFish)){
                                double newDist = AI.distanceBetween(fish.getX(), fish.getY(), food.getX(), food.getY());
                                if(huntDist > newDist){
                                    huntDist = newDist;
                                    seafoodTarget = food;
                                }
                            }
                            //Colision
                            if(fish.getRectangle().contains(food.getCurentRectangle())){
                                fish.addMass(food.giveMass());
                            }
                        }
                        AI.hundSeaFood(fish, seafoodTarget);
                    }else{
                        if(fish.getY()>(GraphicsPanel.getBackground()[0].getHeight()+fish.getRectangle().getHeight())){
                            fishIterator.remove();                        
                        }
                    }
                }
            }
//            if(sock!=null && sock.isConnected() && !sock.isClosed()){
////                if(frameCounter<10){
////                    frameCounter++;
////                }else{
//                    sendArrayList();
////                    frameCounter=0;
////                }
//               
//            }
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
                System.out.println("MAIN RUN : " +e.toString());
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
            for(int i=0; i< seaFoodList.size();i++){
                seaFoodList.get(i).draw(g);
            }
            
            //NO Enchanced For Loop because of uknown concurrentmodificationexception 
            synchronized(fishList){
                for(int i=0; i< fishList.size();i++){
                    fishList.get(i).draw(g);
                }
            }
//            if(player.isAlive()){
//                if(player.isInCharge()){
//                    g.setColor(new Color(200, 0, 0, 127));
////                    g.setFont(new Font("Gabriola", Font.PLAIN, 15)); 
//                    g.fillRect(Server.getWIDTH()/2 - player.getChargeTimerPercent(), 20, 2*player.getChargeTimerPercent(), 20);
////                    g.setColor(Color.WHITE);
////                    g.drawString(getChargeTimerPercent()+"%", Server.getWIDTH()/2, 35);
//
//                }
//            }else{
//                Color deathColor = new Color(200, 0, 0, 127);
//                g.setColor(Color.RED);
//                g.setFont(new Font("Gabriola", Font.BOLD, 40));
//                
//                if(killerFish!=null){
//                    g.drawString("You're fish meat...", WIDTH/2, HEIGHT/2);
//                }else{
//                    g.drawString("Starved to DEATH!", WIDTH/2, HEIGHT/2);
//                }
//                g.setColor(deathColor);
//                g.fillRect(0, 0, WIDTH, HEIGHT);
//            }
            
            //Info
            g.setColor(Color.black);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 15)); 

            //Player
//            g.setColor(Color.BLACK);
//            g.drawString("player: " + player, 50, 50);
//            g.drawString("playerX: " +player.getX(), 50, 70);
//            g.drawString("playerY: " +player.getY(), 50, 90);
//            g.drawString("Mass: " + player.getMass(), 50, 110);
//            g.drawString("SpeedX: " + player.getSpeedX(), 50, 130);
//            if(player.isInCharge()){g.setColor(Color.red);}
//            g.drawString("InCharge: " + player.isInCharge(), 50, 150);
//            g.drawString("ChargeTimer: " + player.getChargeTimer(), 50, 170);
//            g.drawString("ChargeCoolDown: " + player.getChargeCoolDown(), 50, 190);
//            g.setColor(Color.black);
            
//            //KillerFish
//            g.drawString("Killer: " +killerFish, 330, 50);
//            if(killerFish!=null){
//                g.drawString("KillerX: " +killerFish.getX(), 330, 70);
//                g.drawString("KillerY: " +killerFish.getY(), 330, 90);
//                g.drawString("Mass: " + killerFish.getMass(), 330, 110);
//                g.drawString("SpeedX: " + killerFish.getSpeedX(), 330, 130);
//                if(killerFish.isInCharge()){g.setColor(Color.red);}
//                g.drawString("InCharge: " + killerFish.isInCharge(), 330, 150);
//                g.drawString("ChargeTimer: " + killerFish.getChargeTimer(), 330, 170);
//                g.drawString("ChargeCoolDown: " + killerFish.getChargeCoolDown(), 330, 190);
//                g.setColor(Color.black);
//            }
            //Camera
            g.drawString("CameraX: " +Camera.x, 610, 50);
            g.drawString("CameraY: " +Camera.y, 610, 70);
//            g.drawString("DifX: " + Camera.getDiferenceX(), 610, 90);
            g.drawString("Zoom: " + Camera.getZoom(), 610, 110);
            g.drawString("Wheel: " + wheel, 610, 130);
            //GameInfo
            g.drawString("NrOfFishes: "+fishList.size(), 750, 50);
            g.drawString("NrOfFood: "+seaFoodList.size(), 750, 70);
            g.drawString("Tile: " + (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth()), 750, 90);
            //Sock
            g.drawString("NrOfConnections :"+ getNrOfConnections(), 890, 50);
        }
    }

    
    //InerCLass
    public class InputKeyboard implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {
//            if(e.getKeyChar()=='x'){
//                
//                player = Fish.getFishList().get((int)(Math.random()*Fish.getNrOfFishes()));
//                if(player.isAlive()){
//                    Camera.x = player.getX()-HEIGHT/2;
//                    Camera.y = player.getY()-WIDTH/2;
//                    Camera.speedX =0;
//                    Camera.speedY =0;
//                    killerFish=null;
//                    player.showLocation();
//                }
//                
//            }
            
            if(e.getKeyChar()=='z'){
                Camera.setFreeCam(!Camera.isFreeCam());
            }
            
//            if(e.isShiftDown()){
//                player.charge();
//            }
            
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
//    ////Runnable
    public class Connection implements Runnable {
        private Socket sock;
        private ObjectInputStream objInputStream;
        private ObjectOutputStream objOutputStream;
        private int playerID;
        private boolean sendConsumed;
        private boolean getConsumed;

        public Connection(Socket sock) {
            try {
                this.sock = sock;
                objOutputStream = new ObjectOutputStream(sock.getOutputStream());
                objInputStream = new ObjectInputStream(sock.getInputStream());
            } catch (IOException ex) {
                System.out.println("Connection constructor :" +ex.getMessage());
                ex.printStackTrace();
            }
        }

        private void sendLists(){
            try {
                synchronized(fishList){
                synchronized(seaFoodList){
                    if(fishList!=null && seaFoodList!=null && objOutputStream!=null){
                        ArrayList[] arrayList = new ArrayList[2];
                        arrayList[0] = fishList;
//                        arrayList[1] = seaFoodList;
                        objOutputStream.writeObject(arrayList);

                        objOutputStream.flush();
                        objOutputStream.reset();
                    }
                }
                }
            } catch (IOException ex) {
                System.out.println("sendArrayList :" +ex.getMessage());
            }
        }
        public Fish getFish(){
                try {
                    return (Fish) objInputStream.readObject();
                } catch (IOException ex) {
                    System.out.println("IOException in getFish :" + ex.getMessage());
                    System.out.println("Closing Connection");
                    closeConnection();
                    return null;
                } catch (ClassNotFoundException ex) {
                    System.out.println("ClassNotFoundException in getFish :" + ex.getMessage());
                    return null;
                }
        }
        private void getPlayerID(){
            try {
                playerID = objInputStream.readInt();
                System.out.println("playerID : " + playerID);
            } catch (IOException ex) {
                System.out.println("objInputStream read sesionIndex" + ex.getMessage());
                ex.printStackTrace();
            }            
        }
        private void closeConnection(){
            try {
                setToBot();
                objInputStream.close();
                objOutputStream.close();
                sock.close();
            } catch (IOException ex) {
                System.out.println("closeConnection : " + ex.getMessage());
//                ex.printStackTrace();
            }
        }
        private void setToBot(){
            synchronized(fishList){
                for(Fish fish : fishList){
                    if(fish.getPlayerID()==playerID){
                        fish.setName(fish.getName()+ "_Bot_"+playerID);
                        fish.setToBot();
                        
                        fish.setAgilityX(7);
                        fish.setAgilityY(5);
                        fish.setSpeedRateX(1);
                        fish.setSpeedRateY(0.5f);
                    }
                }
            }
        }
        
        @Override
        public void run() {
            getPlayerID();
            while(inGame){
                if(sock!=null && sock.isConnected() && !sock.isClosed()){
                    //Send
                    sendLists();
                    //Receive
                    synchronized(fishList){
                        int i;
                        boolean addToList;
                        Fish player = getFish();
                        if(player!=null){
                            addToList=true;
                            i=0;                            
                            for(Fish fish : fishList){
                                if(fish.getPlayerID()==playerID){
                                    fishList.set(i, player);
                                    addToList=false;
                                    break;                            
                                }
                                i++;
                            }
                            if(addToList){
                                fishList.add(player);
                            }                            
                        }else{
                            closeConnection();
                            break;
                        }
                    }
                    //Sleep
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        System.out.println("GetData : " + ex.getMessage());
                    }
                }else{
                    closeConnection();
                    break;
                }
            }
        }
        
    }
}
