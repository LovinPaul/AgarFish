package Game;


import AI.AI;
import Enviroment.*;
import Fish.*;
import SeaFood.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.DefaultCaret;




public class Server extends JPanel implements Runnable {

    //TEMPORARY
    public static float wheel;
    int lag;
    
    //CONSTANTS
    public static int WIDTH = 1366;
    private static int HEIGHT = 600;
    ////Game
    private static final int ANGEL_FISH_PERCENT=10;
    private static final int GUPPIE_FISH_PERCENT=35;
    private static final int JAI_FISH_PERCENT=55;
    private static final int MAX_NR_OF_FISH=30;
    
    //ServerSide
    //TCP
    private Socket sock;
    private ServerSocket serversock;
    private Thread connectionThread;
//    private ArrayList<Thread> threadsList;
    private int fishListCheck=MAX_NR_OF_FISH;
    //UDP
    DatagramSocket UDPsock;
    ArrayList<UDPack> UDPList;
    int bytesIn;
    
    
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
    Fish player;
//    Fish killerFish = null;
    private ArrayList<Fish> fishList;
    private ArrayList<SeaFood> seaFoodList;
    
    private ArrayList<String> chatList;
    
    //swing
    JFrame frame;
    JTextArea chatArea;
    JTextField chatText;
    JScrollPane jsp;
//    private int frameCounter;
    
    ArrayList<Connection> connectionList;
    
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
            
//            threadsList = new ArrayList<>();
            UDPList = new ArrayList<>();
            connectionList = new ArrayList<>();
            
            serversock = new ServerSocket(4242);
            
            UDPsock = new DatagramSocket(4243);
            UDPsock.setSoTimeout(1);
            
            manageConnections();
        } catch (IOException ex) {
            System.out.println("initSocket() :" + ex.getMessage());
        }        
    }
    private void manageConnections(){
        System.out.println("manageConnections()");
        Thread listen = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    while(true){
                        sock = serversock.accept();
                        sock.setTcpNoDelay(true);
                        
                        UDPack pack = new UDPack(sock.getInetAddress());
                        UDPList.add(pack);
                        
                        System.out.println("New connection...");
                        System.out.println("IP/port : "+sock.getInetAddress().getHostAddress() + ":" + sock.getPort());


                        Connection connection = new Connection(sock, pack);
                        synchronized(connectionList){
                            connectionList.add(connection);
                        }

                        connectionThread = new Thread(connection);
                        connectionThread.start();
//                        synchronized(connectionList){
//                            threadsList.add(connectionThread);                    
//                        }
                    }
                } catch (IOException ex) {
                    System.out.println("manageConnections() :" + ex.getMessage());
                }
            }
        });
        listen.start();
    }
    private int getNrOfConnections(){
        int nrOfTh=0;
//        synchronized(connectionList){
//            Iterator<Thread> threadIterator = connectionList.iterator();
//            while ( threadIterator.hasNext() ) {
//                Thread thread = threadIterator.next();
//                if(thread.isAlive()){
//                    nrOfTh++;
//                }else{
//                    threadIterator.remove();
//                }
//            }
//        }
        return nrOfTh;
    }

    private void getUpdate(){
        byte[] receiveData = new byte[10];
        byte[] data;
//        int timeMilis;
        byte sesionID;
        
        try {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            bytesIn=0;
            try{
                do{
                    UDPsock.receive(receivePacket);
                    
                    data = receivePacket.getData();
//                    timeMilis = data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
                    sesionID  = data[4];
//                    byte up = data[8];
//                    byte down = data[9];
//                    byte left = data[10];
//                    byte right = data[11];
                    
//                    System.out.println(timeMilis  + " - " + sesionID + " - " + up + " - " + down + " - " + left + " - " + right);
                    
                    for(UDPack pack : UDPList){
                        if(pack.isThisID(sesionID)){
                            pack.update(data);
                            break;
                        }
                    }
                    
                }while(true);

            }catch(SocketTimeoutException stex){
                for(UDPack udp : UDPList){
                    udp.consumeData();
                }
            }
        } catch (IOException ex) {
            System.out.println("getData() IOException :" +ex.getMessage());
        }
    }
    private void sendUpdate(){
        
//        synchronized(connectionList){
//            for(Connection conn : connectionList){
//                conn.sendFishUpdate();
//            }
//        }
//        for(Fish fish : fishList){
//            fish.up=false;
//            fish.down=false;
//            fish.left=false;
//            fish.right=false;
//        }
        
//        lag++;
////        if(lag<=2){return;}
//        lag=0;
        
        synchronized(fishList){
            byte[] sendData = new byte[4 + 14 * fishList.size()];
//            byte[][] sendData2 = new byte[fishList.size()-1][];
            
            int timeMilis = (int)(System.currentTimeMillis()%1000000000);// 4 bytes
            sendData[0] = (byte)(timeMilis >>> 24);
            sendData[1] = (byte)(timeMilis >>> 16);
            sendData[2] = (byte)(timeMilis >>> 8);
            sendData[3] = (byte)(timeMilis);
            
            
            int i = 4;
            for(Fish fish : fishList){
                // 1 bytes
                sendData[i++] = fish.getID();
                //getX 2 bytes
                sendData[i++] = (byte)((short)fish.getX() >>> 8);
                sendData[i++] = (byte)((short)fish.getX());
                //getY 2 bytes
                sendData[i++] = (byte)((short)fish.getY() >>> 8);
                sendData[i++] = (byte)((short)fish.getY());
                //facing 1 bytes
                sendData[i++] = fish.getFacingLeft();
                //angle 1 bytes
                sendData[i++] = fish.getAngle();
                //isAlive 1 bytes
                sendData[i++] = fish.isAliveIsSprinting();
                //gain 2 bytes
                sendData[i++] = (byte)(fish.getNewGain() >>> 8);
                sendData[i++] = (byte)(fish.getNewGain());

                //experimental
                sendData[i++] = fish.up ? (byte) 1:0;
                sendData[i++] = fish.down ? (byte) 1:0;
                sendData[i++] = fish.left ? (byte) 1:0;
                sendData[i++] = fish.right ? (byte) 1:0;

                fish.up=false;
                fish.down=false;
                fish.left=false;
                fish.right=false;
            }
            
            
//            SendData
            try {
                for(UDPack pack : UDPList){
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, pack.address, 4244);                
                    UDPsock.send(sendPacket);
                }
            } catch (IOException ex) {
                System.out.println("sendUpdate() :" + ex.getMessage());
                ex.printStackTrace();
            }
            
            
        }
        
    }
    
    
        private void sendUpdate2(){
            
        lag++;
        if(lag<=100){return;}
        lag=0;
            
            synchronized(fishList){
                byte[][] dataNames = new byte[2*(fishList.size())][];


                int i=0;
                for(Fish fish : fishList){
                    dataNames[i] = new byte[1];
                    dataNames[i][0] = (byte)fish.getName().getBytes().length;
                    i++;
                    dataNames[i] = new byte[fish.getName().getBytes().length];
                    dataNames[i] = fish.getName().getBytes();
                    
//                    String s = new String(dataNames[i]);
//                    System.out.println(s);
                    
                    i++;
                }

                int f=0;
                for(int l=0; l<dataNames.length; l++){
                    f+=dataNames[l].length;
                }
                
                
                byte[] dataToSend =new byte[f];
                int z=0;
                for(int j=0; j<dataNames.length;j++){
                    for(int k=0; k<dataNames[j].length; k++){
                        dataToSend[z] = dataNames[j][k];
                        z++;
                    }
                }
                
                String s = new String(dataToSend);
                System.out.println(s);
            }

            

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

        chatList=new ArrayList<>();
        
        chatArea = new JTextArea();
        chatArea.setLocation(0, 0);
        chatArea.setSize((int) (0.3 * WIDTH), (int) (0.33 * HEIGHT));
        chatArea.setEditable(false);
        chatArea.setOpaque(false);
        chatArea.setVisible(true);
        chatArea.setFocusable(false);
        
        chatText = new JTextField();        
        chatText.setLocation(50, 50+(int) (0.33 * HEIGHT));
        chatText.setSize((int) (0.3 * WIDTH), 25);
        chatText.setVisible(true);
        chatText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    chatArea.append("*server: \t" +chatText.getText()+" *\n");
                    synchronized(chatList){
                        chatList.add("*server: \t" +chatText.getText()+" *");
                    }                    
                    chatText.setText("");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());

                }
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    frame.requestFocus();
                }
                
            }
        });
        chatText.setOpaque(false);
        
        jsp = new JScrollPane(chatArea) {
            @Override
            protected void paintComponent(Graphics g) {
                try {
                    Composite composite = ((Graphics2D)g).getComposite();

                    ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());

                    ((Graphics2D)g).setComposite(composite);
                    paintChildren(g);
                }
                catch(IndexOutOfBoundsException e) {
                    super.paintComponent(g);
                }
            }       
        };
        jsp.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        jsp.setLocation(50, 50);
        jsp.setSize((int) (0.3 * WIDTH), (int) (0.33 * HEIGHT));
        jsp.setEnabled(true);
        jsp.setVisible(true);
        jsp.getViewport().setOpaque(false);
        jsp.setOpaque(false);
        
        this.setLayout(null);
        this.add(jsp);
        this.add(chatText);
        frame.add(this);
        
        
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
            seaFoodList.add(new SmlSquid((float) (Math.random()*5000),(float)(Math.random()*800),20,10));
        }        
    }
    private void randomBuble(){
        if(Bubles.getNrOfBubles()<15){
            new Bubles((float)Math.random()*10000, GraphicsPanel.getBackground()[0].getHeight()+100);
        }
    }
    private byte generateID(byte instace){
        byte id=0;
        boolean notFound;
        switch(instace){
            case 0:
                synchronized(fishList){
                    id=0;
                    do{
                        id++;
                        notFound=false;
                        for(Fish fish : fishList){
                            if(fish.getID()==id){
                                notFound=true;
                                break;
                            }
                        }
                    }while(notFound);
                }
                break;
            case 1:
                synchronized(fishList){
                    id=30;
                    do{
                        id++;
                        notFound=false;
                        for(Fish fish : fishList){
                            if(fish.getID()==id){
                                notFound=true;
                                break;
                            }
                        }
                    }while(notFound);
                }
                break;
            case 2:
                synchronized(fishList){
                    id=60;
                    do{
                        id++;
                        notFound=false;
                        for(Fish fish : fishList){
                            if(fish.getID()==id){
                                notFound=true;
                                break;
                            }
                        }
                    }while(notFound);
                }
                break;
        }
        
        return id;
        
    }
    private void randomFish(){
        synchronized(fishList){
            Fish fishToAdd;
            if(fishList.size()<MAX_NR_OF_FISH){
                if(fishList.isEmpty()){
                    fishToAdd = new JaiFish((float)Math.random()*5000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    fishToAdd.setID(generateID((byte)2));
                    fishList.add(fishToAdd);
                    fishToAdd = new GuppieFish((float)Math.random()*5000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    fishToAdd.setID(generateID((byte)1));
                    fishList.add(fishToAdd);
                    fishToAdd = new AngelFish((float)Math.random()*5000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    fishToAdd.setID(generateID((byte)0));
                    fishList.add(fishToAdd);
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
                    fishToAdd = new AngelFish((float)Math.random()*5000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    fishToAdd.setID(generateID((byte)0));
                    fishList.add(fishToAdd);
                }
                if(JAI_FISH_PERCENT/100.0*MAX_NR_OF_FISH>nrOfJai){
                    fishToAdd = new JaiFish((float)Math.random()*5000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    fishToAdd.setID(generateID((byte)2));
                    fishList.add(fishToAdd);
                }
                if(GUPPIE_FISH_PERCENT/100.0*MAX_NR_OF_FISH>nrOfGuppie){
                    fishToAdd = new GuppieFish((float)Math.random()*5000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10));
                    fishToAdd.setID(generateID((byte)1));
                    fishList.add(fishToAdd);
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
        
//        if(shift && (left || right)){
//            player.charge();
//        }
        
    }
    private void updateGameMechanics(){
        if(inGame){
            
            randomBuble();
            randomFish();
            randomSeaFood();
            
            Fish.oncePerFrameUpdates();
            
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
            
            if(!UDPList.isEmpty()){
                getUpdate();
            }
            
            Iterator<Connection> connectionIterator = connectionList.iterator();
            while ( connectionIterator.hasNext() ) {
                Connection conn = connectionIterator.next();
                if(!conn.isAlive()){
                    connectionIterator.remove();                    
                }
            }

            
            synchronized(fishList){
                Iterator<Fish> fishIterator = fishList.iterator();
                while (fishIterator.hasNext()) {
                    Fish fish = fishIterator.next();
//                    fish.massDecay();
                    
                    if(fish.isHuman()){
                        Camera.follow(fish);
                    }else{
                        
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
//                            if(fish2!=fish && fish2.isAlive() && fish.getRectangle().contains(fish2.getRectangle())){
//                                fish.addMass(fish2.giveMass());
//    //                            if(fish2==player){killerFish=fish;}
//                            }
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
                    
                    fish.updateCoord();
                }
            }
            if(!UDPList.isEmpty()){
                sendUpdate();
            }
            
        }
    }
    private void renderGame() {
        
        this.repaint();
        
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        HEIGHT = this.getHeight();
        WIDTH = this.getWidth();
        
//        jsp.setSize((int) (0.3 * WIDTH), (int) (0.33 * HEIGHT));
        
        
        if(inGame){
            //
//            g.drawImage(GraphicsPanel.getBackground()[0].getSubimage(
//                    (int)Camera.x < 0 ? 0 : (int)Camera.x,
//                    (int)Camera.y < 0 ? 0 : (int)Camera.y,
//                    (int)Camera.x < 0 ? WIDTH+(int)Camera.x : WIDTH, 
//                    (int)Camera.y < 0 ? HEIGHT+(int)Camera.y : HEIGHT), 
//                    (int)Camera.x < 0 ? -(int)Camera.x : 0, 
//                    (int)Camera.y < 0 ? -(int)Camera.y : 0, 
//                    null);
            g.drawImage(GraphicsPanel.getBackground()[0].getSubimage(
                    (int)Camera.x,
                    (int)Camera.y,
                    WIDTH,
                    HEIGHT), 
                    0,
                    0,
                    null);
            //Background Tiles
//            int tileX = (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth());
//            tileX = GraphicsPanel.getBackground()[0].getWidth() * tileX;
//            g.drawImage(GraphicsPanel.getBackground()[0].getSubimage((int)Camera.x, (int)Camera.y, WIDTH, HEIGHT), (int) -Camera.x + tileX, (int) -Camera.y, null);
            
//            tileX+=GraphicsPanel.getBackground()[0].getWidth();
//            g.drawImage(GraphicsPanel.getBackground()[0].getSubimage((int)Camera.x, (int)Camera.y, WIDTH, HEIGHT), (int) -Camera.x + tileX, (int) -Camera.y, null);
            
            
//            Level Limits
//            if(Camera.x<1600){
//                g.drawImage(GraphicsPanel.getBackground()[1], (int) -Camera.x, (int) -Camera.y, null);
//            }
//            if(Camera.x>9500){
//                g.drawImage(GraphicsPanel.getBackground()[2], (int) -Camera.x + 12000 - GraphicsPanel.getBackground()[2].getWidth(), (int) -Camera.y, null);
//            }
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

//            Player
            if(player!=null){
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
            }
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
            
//            g.drawString("NrOfConnections :"+ getNrOfConnections(), 890, 50);
//            if(connectionList.get(0).sendData!=null){
//                g.drawString("Send :" +connectionList.get(0).sendData.length, 890, 50);
//            }
            if(UDPList!=null && !UDPList.isEmpty()){
                g.drawString("Lantency :" + UDPList.get(0).latency, 890, 70);
            }
            
        }
    }    
    @Override
    public void run() {
        initGame();
        initSockets();
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
    ////Runnable
    public class Connection implements Runnable {// 
//        private boolean isAlive;
        //TCP
        private Socket sockTCP;
        private ObjectInputStream objInputStream;
        private ObjectOutputStream objOutputStream;
        
        private UDPack pack;
        Fish player;
        private int playerID;
        private String playerName;
//        private ArrayList<Fish> tempFishList = new ArrayList<Fish>();
        
        private int sleep;
        private byte fishNamerequest=-1;
        
        private int chatListsize = chatList.size();

        private int clientWidth = 1366;
        private int clientHeight = 600;
        //UDP
//        public void sendFishUpdate(){
//            if(player==null){return;}
//            synchronized(fishList){
////                tempFishList.clear();
////                tempFishList.add(player);
//                
//                byte[] sendData = new byte[4 + 14 * fishList.size()];
//                int timeMilis = (int)(System.currentTimeMillis()%1000000000);// 4 bytes
//                sendData[0] = (byte)(timeMilis >>> 24);
//                sendData[1] = (byte)(timeMilis >>> 16);
//                sendData[2] = (byte)(timeMilis >>> 8);
//                sendData[3] = (byte)(timeMilis);    
//
//                int i = 4;//(fish!=player) && 
//                for(Fish fish : fishList){
//                    if((fish.getX()<clientWidth/2+player.getX() && fish.getX()>player.getX()-clientWidth/2)
//                            && (fish.getY()<clientHeight/2+player.getY() && fish.getY()>player.getY()-clientWidth/2)){
////                        tempFishList.add(fish);
//
//                        // 1 bytes
//                        sendData[i++] = fish.getID();
//                        //getX 2 bytes
//                        sendData[i++] = (byte)((short)fish.getX() >>> 8);
//                        sendData[i++] = (byte)((short)fish.getX());
//                        //getY 2 bytes
//                        sendData[i++] = (byte)((short)fish.getY() >>> 8);
//                        sendData[i++] = (byte)((short)fish.getY());
//                        //facing 1 bytes
//                        sendData[i++] = fish.getFacingLeft();
//                        //angle 1 bytes
//                        sendData[i++] = fish.getAngle();
//                        //isAlive 1 bytes
//                        sendData[i++] = fish.isAliveIsSprinting();
//                        //gain 2 bytes
//                        sendData[i++] = (byte)(fish.getNewGain() >>> 8);
//                        sendData[i++] = (byte)(fish.getNewGain());
//                        //movement 4 bytes
//                        sendData[i++] = fish.up ? (byte) 1:0;
//                        sendData[i++] = fish.down ? (byte) 1:0;
//                        sendData[i++] = fish.left ? (byte) 1:0;
//                        sendData[i++] = fish.right ? (byte) 1:0;                        
//
//                    }
//                }
//                
//
//                
//                
////                for(Fish fish : tempFishList){
////
////                }
//                
//                //SendData
//                try {
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sockTCP.getInetAddress(), 4244);                
//                    UDPsock.send(sendPacket);
//                } catch (IOException ex) {
//                    System.out.println("sendUpdate() :" + ex.getMessage());
//                    ex.printStackTrace();
//                }
//                
//            }
//            
//        }
        
        
        
        
        public Connection(Socket sock, UDPack pack) {
            try {
                this.sockTCP = sock;
                this.pack = pack;
                sockTCP.setKeepAlive(true);

//                outputStream = sockTCP.getOutputStream();
//                dataOutputStream = new DataOutputStream(outputStream);
//                
//                inputStream = sockTCP.getInputStream();
//                dataInputStream = new DataInputStream(inputStream);               
                objOutputStream = new ObjectOutputStream(sock.getOutputStream());
                objInputStream = new ObjectInputStream(sock.getInputStream());
                

//                inputUDP = new DatagramSocket(4243);
//                
//                outputUDP = new DatagramSocket();
//                inputUDP.setSoTimeout(1);
                
            } catch (IOException ex) {
                System.out.println("Connection constructor :" +ex.getMessage());
                ex.printStackTrace();
            }
        }
        
        private void addPlayer(){
            try {
                player = (Fish) objInputStream.readObject();
                
//                Server.this.player = player;
                if(player!=null){
//                    playerID = player.getID();
                    playerName = player.getName();
                    player.setHuman(true);
                    pack.setFish(player);
                    synchronized(fishList){
                        fishList.add(player);
                    }
                    synchronized(chatList){
                        chatList.add("* New Join : " + playerName + " *");
                    }
                }else{
                    //null fish... what to do?!
                }
            } catch (IOException ex) {
                System.out.println("IOException in getFish :" + ex.getMessage());
                System.out.println("Closing Connection");
                closeConnection();
            } catch (ClassNotFoundException ex) {
                System.out.println("ClassNotFoundException in getFish :" + ex.getMessage());
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
//                inputUDP.close();
//                outputUDP.close();
                objInputStream.close();
                objOutputStream.close();
                sockTCP.close();
                synchronized(chatList){
                    chatList.add("* " + playerName + " : Left the game *");
                }
//                isAlive=false;
            } catch (IOException ex) {
                System.out.println("closeConnection : " + ex.getMessage());
//                ex.printStackTrace();
            }
        }
        private void setToBot(){
            synchronized(fishList){
                for(Fish fish : fishList){
                    if(fish.getID()==playerID){
                        fish.setName(fish.getName()+ " (Bot_"+playerID +")");
                        fish.setToBot();
                        
                        fish.setAgilityX(7);
                        fish.setAgilityY(5);
                        fish.setSpeedRateX(1);
                        fish.setSpeedRateY(0.5f);
                    }
                }
            }
        }
        private boolean isAlive(){
            return sockTCP.isConnected() && !sockTCP.isClosed();
        }
        
        private void getChatUpdate(){
            synchronized(chatList){
                try {
                    if(objInputStream.available()>0){ /// ocazie de crash! (citeste tot indiferent daca e type 1 2 sau X)
                        byte type = objInputStream.readByte();
                        byte[] in = new byte[objInputStream.available()];
                        switch(type){
                            case 0:
                                objInputStream.read(in);
                                chatArea.append(playerName + ": \t" + new String(in) + "\n");
                                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                                chatList.add(playerName + ": \t" + new String(in));
                            break;
                            case 1:
                                fishNamerequest = objInputStream.readByte();
                            break;
                        }
                            


                    }

                } catch (IOException ex) {
                    System.out.println("getChatUpdate() :" + ex.getMessage());
                }
            }
        }
        private void sendChatUpdate(){
            
            try {
                //Send Chat
                synchronized(chatList){
                    if(chatListsize!=chatList.size()){

                            for(int i=chatListsize; i<chatList.size(); i++){
                                    objOutputStream.writeByte(0);
                                    objOutputStream.write(chatList.get(i).getBytes());
                            }

                            objOutputStream.flush();
                            objOutputStream.reset();
                            chatListsize = chatList.size();


                    }
                }
                //Name request
                if(fishNamerequest!=-1){
                    synchronized(fishList){
                        for(Fish fish : fishList){
                            if(fish.getID()==fishNamerequest){
                                synchronized(objOutputStream){
                                    objOutputStream.writeByte(1);
                                    objOutputStream.writeByte(fishNamerequest);
                                    objOutputStream.write(fish.getName().getBytes());
                                    objOutputStream.flush();
                                    objOutputStream.reset();
                                }
                                break;
                            }
                        }
                    }

                }            
            } catch (IOException ex) {
                System.out.println("sendChatUpdate() :" + ex.getMessage());
                System.out.println("Closing Connection");
                closeConnection();
            }
        }
        
        @Override
        public void run() {
            addPlayer();
            while(inGame){
                
                getChatUpdate();
                sendChatUpdate();
                if(!isAlive()){break;}
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    System.out.println("GetData Sleep : " + ex.getMessage());
                }                    
            }
        }
    }
    //
    public class UDPack {
        int latency;
        InetAddress address;
        byte[] data;
        private Fish fish;
        
        public UDPack(InetAddress address) {
            this.address = address;
        }
        
        
        public void setFish(Fish fish){
            this.fish = fish;
        }
        public boolean isThisID(byte sesion){
            return fish!=null ? fish.getID()==sesion : false;
        }
        
        public void update(byte[] receiveData){
            int nowTimeMillis = (int)(System.currentTimeMillis() % 1000000000);
            int tempTimeMillis;
            int tempLatency;
            
            if(data==null){
                data = receiveData.clone();
                tempTimeMillis = data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
                latency = nowTimeMillis-tempTimeMillis;
                bytesIn += data.length;
            }else{
                tempTimeMillis = receiveData[0] << 24 | (receiveData[1] & 0xFF) << 16 | (receiveData[2] & 0xFF) << 8 | (receiveData[3] & 0xFF);
                tempLatency = nowTimeMillis-tempTimeMillis;

                if(latency > tempLatency){
                    data = receiveData.clone();
                    latency = tempLatency;
                    bytesIn += data.length;
                }                        

            }
        }
        public void consumeData(){
            if(data!=null){
//                if(data[5]==1){fish.goUp();}
//                if(data[6]==1){fish.goDown();}
//                if(data[7]==1){fish.goLeft();}
//                if(data[8]==1){fish.goRight();}
//                if(data[9]==1){fish.charge();}
                if(data[5]==1){fish.clientUp=true;}else{fish.clientUp=false;}
                if(data[6]==1){fish.clientDown=true;}else{fish.clientDown=false;}
                if(data[7]==1){fish.clientLeft=true;}else{fish.clientLeft=false;}
                if(data[8]==1){fish.clientRight=true;}else{fish.clientRight=false;}
                if(data[9]==1){fish.charge();}
                
//                System.out.println(data[4] + "     -     " + fish.getID());
                
                data = null;
            }
        }
    }
}
