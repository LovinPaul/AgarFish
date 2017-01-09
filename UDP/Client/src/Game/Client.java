package Game;


import Enviroment.*;
import Fish.*;
import SeaFood.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;




public class Client extends JPanel implements Runnable {

    //TEMPORARY
    public static float wheel;
    int lag;
    
    //CONSTANTS
    private static int WIDTH = 1366;
    private static int HEIGHT = 600;
    
    //ClientSide
    ////TCP
    private Socket sock;
    private ObjectInputStream objInputStream=null;
    private ObjectOutputStream objOutputStream=null;
    
    private OutputStream outputStream;
    private DataOutputStream  dataOutputStream;
    
    
    //UDP
//    DatagramSocket inputUDP;
    DatagramSocket UDPsock;
    private int latency = -1;
    private int bytesIn;
    private int bytesOut;
//    private ArrayList<UDPack> UDPList = new ArrayList<>();
    private ArrayList<UDPUpdate> udpUpdate = new ArrayList<>();
    
    private Thread updateList;
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
    public static byte sesionID;
    
    
    
    JFrame frame;
    JTextArea chatArea;
    JTextField chatText;
    JScrollPane jsp;
    
    public int frameCounter;
    
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
                    try {
                        synchronized(objOutputStream){
                            objOutputStream.writeByte(0);
                            objOutputStream.write(chatText.getText().getBytes());
                            objOutputStream.flush();
                        }
                    } catch (IOException ex) {
                        System.out.println("objOutputStream.write Chat :" + ex.getMessage());
                    }
                    
                    chatText.setText("");
                    
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
    private void initSocket(){
        System.out.println("initSocket()");
        try {
            System.out.println("TCP Socket");
            sock = new Socket("127.0.0.1", 4242);////192.168.0.171////78.97.157.147
            sock.setTcpNoDelay(true);
            sock.setKeepAlive(true);
            objOutputStream = new ObjectOutputStream(sock.getOutputStream());
            objInputStream = new ObjectInputStream(sock.getInputStream());

//            outputStream = sock.getOutputStream();
//            dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Client connected with sesionID : " +sesionID);
            
            updateList = new Thread(new TCPConnection());
            updateList.start();
            
            System.out.println("UDP Socket");
            UDPsock = new DatagramSocket(4244);
//            inputUDP = new DatagramSocket();
            UDPsock.setSoTimeout(1);
        } catch (IOException ex) {
            System.out.println("initSocket() :" + ex.getMessage());
            ex.printStackTrace();
        }        
    }
    private void initGame(){
        
        GraphicsPanel.init();
        frame.addKeyListener(new InputKeyboard());
        frame.addMouseWheelListener(new InputMouseWheel());

//        for(int i=0; i<25; i++){
//            fishList.add(
//                new GuppieFish((float)Math.random()*10000, (float)Math.random()*50, (int)Math.round(Math.random()*10), (int)Math.round(Math.random()*10)));
//        }

        for(int i=0; i<35; i++){
            
            if(i<15){new Bubles((float)Math.random()*10000, (float) (Math.random()*(GraphicsPanel.getBackground()[0].getHeight()+100)));}
            
            fishList.add(new Fish());
            udpUpdate.add(new UDPUpdate());
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
    private void sendUpdate(){
        try {
//            if(up || down || left || right){
                byte[] sendData = new byte[10];

                byte up;
                byte down;
                byte left;
                byte right;
                byte shift;

                up = this.up ? (byte)1 : (byte)0;
                down = this.down ? (byte)1 : (byte)0;
                left = this.left ? (byte)1 : (byte)0;
                right = this.right ? (byte)1 : (byte)0;
                shift = this.shift ? (byte)1 : (byte)0;

                int timeMilis = (int)(System.currentTimeMillis()%1000000000);

                sendData[0] = (byte)(timeMilis >>> 24);
                sendData[1] = (byte)(timeMilis >>> 16);
                sendData[2] = (byte)(timeMilis >>> 8);
                sendData[3] = (byte)(timeMilis);

//                sendData[4] = (byte)(sesionID >>> 24);
//                sendData[5] = (byte)(sesionID >>> 16);
//                sendData[6] = (byte)(sesionID >>> 8);
                sendData[4] = sesionID;                    

                sendData[5] = up;
                sendData[6] = down;
                sendData[7] = left;
                sendData[8] = right;
                sendData[9] = shift;

                bytesOut = sendData.length;

//                InetAddress IPAddress = InetAddress.getByName("78.97.157.147");IPAddress
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sock.getInetAddress(), 4243);
                UDPsock.send(sendPacket);
//            }

        } catch (IOException ex) {
            System.out.println("sendPlayerCoord() :" + ex.getMessage());
        }
    }
    private void getUpdate(){
        byte[] receiveData = new byte[512];
        byte[] data = null;
        int nowTimeMillis = (int)(System.currentTimeMillis() % 1000000000);
        
        byte[] tempData;
        int tempTimeMillis;
        int tempLatency;

        try {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try{
                
                do{
                    UDPsock.receive(receivePacket);
                    
                    if(data==null){
                        data = receivePacket.getData();
                        tempTimeMillis = data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
                        latency = nowTimeMillis-tempTimeMillis;
                        bytesIn = receivePacket.getLength();
                    }else{
                        tempData = receivePacket.getData();
                        tempTimeMillis = tempData[0] << 24 | (tempData[1] & 0xFF) << 16 | (tempData[2] & 0xFF) << 8 | (tempData[3] & 0xFF);
                        tempLatency = nowTimeMillis-tempTimeMillis;
                        
                        if(latency > tempLatency){
                            data = tempData;
                            latency = tempLatency;
                            bytesIn = receivePacket.getLength();
                        }                        
                        
                    }

                }while(true);

            }catch(SocketTimeoutException stex){
                
                if(data!=null){
                    synchronized(fishList){
                        
                        int i=4;
                        for(Fish fish : fishList){
                            if(i<bytesIn && i!=0){
                                if(data[i]== fish.getID()){
                                    i++;
                                    int serverX = (int)((data[i++] & 0xFF) << 8 | (data[i++] & 0xFF));
                                    int serverY = (int)((data[i++] & 0xFF) << 8 | (data[i++] & 0xFF));
                                    if(fish.getX()!=serverX){
                                        fish.setX((fish.getX()+serverX)/2);
                                    }
                                    if(fish.getY()!=serverY){
                                        fish.setY((fish.getY()+serverY)/2);
                                    }
                                    fish.setFacingLeft(data[i++]);
                                    int serverAngle = data[i++];
                                    if(fish.getAngle()!=serverX){
                                        fish.setAngle((fish.getAngle()+serverAngle)/2);
                                    }

                                }else{
                                    fish.setID(data[i]);
                                    fish.setIdentity(data[i++]);//data[i++] << 24 | (data[i++] & 0xFF) << 16 | 
                                    fish.setX((int)((data[i++] & 0xFF) << 8 | (data[i++] & 0xFF)));
                                    fish.setY((int)((data[i++] & 0xFF) << 8 | (data[i++] & 0xFF)));
                                    fish.setFacingLeft(data[i++]);
                                    fish.setAngle((int)data[i++]);
                                }



                                fish.setIsAliveIsSprinting(data[i++]);
                                fish.addNewGain((int)((data[i++] & 0xFF) << 8 | (data[i++] & 0xFF)));
                                //experimental
                                fish.up = data[i++] == (byte)1;
                                fish.down = data[i++] == (byte)1;
                                fish.left = data[i++] == (byte)1;
                                fish.right = data[i++] == (byte)1;

                            }else{
                                i+=14;
                                fish.setID((byte)-1);
                            }
                        }
                    }
                }else{
                    //data is null what to do ?
                }
            }
        } catch (IOException ex) {
            System.out.println("getData() IOException :" +ex.getMessage());
        }
        
    }

    
    private void getUpdateThread(){
        Thread update = new Thread(new Runnable(){
            @Override
            public void run() {
                byte[] receiveData = new byte[1024];
                byte[] data = null;
                int nowTimeMillis;                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                
                while(true){
                    
                    byte[] tempData = receivePacket.getData();
                    nowTimeMillis = (int)(System.currentTimeMillis() % 1000000000);
                    
                    
                }
            }
            
        });
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
            Iterator<Bubles> bubleIterator = Bubles.getBublesList().iterator();
            while ( bubleIterator.hasNext() ) {
                Bubles buble = bubleIterator.next();
                if(buble.getY()>-100){
                    buble.updateCoord();
                }else{
                    bubleIterator.remove();                    
                }
            }
            
            
            getUpdate();
            
            synchronized(fishList){
                for(Fish fish : fishList){
                    if(fish.getID()!=-1){
                        fish.updateCoord();
                    }
                    if(fish.getID()==sesionID){player=fish;}
                }
            }
            
//            player.updateCoord();
//            Fish.oncePerFrameUpdates();
            if(Camera.isFreeCam()){
                Camera.updateCoord();
            }else{
            }
            sendUpdate();
            
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
            
            g.drawImage(GraphicsPanel.getBackground()[0].getSubimage(
                    (int)Camera.x,
                    (int)Camera.y,
                    WIDTH,
                    HEIGHT), 
                    0,
                    0,
                    null);
            
//            if(player==null){return;}
            //Background Tiles
//            int tileX = (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth());
//            tileX = GraphicsPanel.getBackground()[0].getWidth() * tileX;
//            g.drawImage(GraphicsPanel.getBackground()[0], (int) -Camera.x + tileX, (int) -Camera.y, null);
//            tileX+=GraphicsPanel.getBackground()[0].getWidth();
//            g.drawImage(GraphicsPanel.getBackground()[0], (int) -Camera.x + tileX, (int) -Camera.y, null);
//            //Level Limits
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
            for(int i=0; i< SeaFood.getSeaFoodList().size();i++){
                SeaFood.getSeaFoodList().get(i).draw(g);
            }
            
            
            for(Fish fish : fishList){
                if(fish.getID()!=-1){
                    fish.draw(g);
                }
                
                if(fish.isPlayer()){
                    Camera.follow(fish);
                }
            }

//            if(player.isAlive()){
//                if(player.isInCharge()){
//                    g.setColor(new Color(200, 0, 0, 127));
////                    g.setFont(new Font("Gabriola", Font.PLAIN, 15)); 
//                    g.fillRect(Client.getWIDTH()/2 - player.getChargeTimerPercent(), 20, 2*player.getChargeTimerPercent(), 20);
////                    g.setColor(Color.WHITE);
////                    g.drawString(getChargeTimerPercent()+"%", Client.getWIDTH()/2, 35);
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
//            g.drawString("DifX: " + Camera.getDiferenceX(), 610, 90);
            g.drawString("Zoom: " + Camera.getZoom(), 610, 110);
            g.drawString("Wheel: " + wheel, 610, 130);
            //GameInfo
            g.drawString("NrOfFishes: "+fishList.size(), 750, 50);
            g.drawString("NrOfFood: "+SeaFood.getSeaFoodList().size(), 750, 70);
            g.drawString("Tile: " + (int) (Camera.x/GraphicsPanel.getBackground()[0].getWidth()), 750, 90);
            //Sock
//            if(sock.isConnected() && !sock.isClosed()){
//                g.drawString("Sock :-connected-", 890, 50);
//            }else{
//                g.drawString("Sock : disconected", 890, 50);
//            }
            g.drawString("Latency :"+latency, 890, 50);
            g.drawString("In :" + bytesIn +" bytes", 890, 70);
            g.drawString("Out :" + bytesOut +" bytes", 890, 90);
            bytesOut=0;
            
        }
    }

    //InerCLass
    public class InputKeyboard implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {
//            if(e.getKeyChar()=='x'){
//                player.setMass(player.getMass()*2);
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
    public class TCPConnection implements Runnable{// 
        
        boolean nameRequestSend;
        
        //return 10
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

//            return id;
            return 10;

        }
        
        private void getChatUpdate(){
            try {//objInputStream!=null && 
                if(objInputStream.available()>0){
                    byte[] in;
                    byte type = objInputStream.readByte();
                    switch(type){
                        case 0:
                            in = new byte[objInputStream.available()];
                            objInputStream.read(in);
                            chatArea.append(new String(in) + "\n");
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        break;
                        case 1:
                            nameRequestSend=false;
                            byte id =  objInputStream.readByte();
                            in = new byte[objInputStream.available()];
                            objInputStream.read(in);
                            synchronized(fishList){
                                for(Fish fish : fishList){
                                    if(fish.getID()==id){
                                        fish.setName(new String(in));
                                    }
                                }
                            }
                        break;
                    }


                }

            } catch (IOException ex) {
                System.out.println("getChatUpdate() :" + ex.getMessage());
            }
        }

        private void verifyNames(){
            if(!nameRequestSend){
                synchronized(fishList){
                    for(Fish fish : fishList){
                        if(fish.getName()==null && fish.getID()!=(byte)-1){
                            try {
                                synchronized(objOutputStream){
                                    fish.setName("name_request_send");
                                    nameRequestSend=true;
                                    objOutputStream.writeByte(1);
                                    objOutputStream.write(fish.getID());
                                    objOutputStream.flush();
                                }

                            } catch (IOException ex) {
                                System.out.println("verifyNames() :" + ex.getMessage());
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        @Override
        public void run(){
            Fish player;
            player = new AngelFish(1000f, 500f, 10, 10);
//            player.setID(sesionID);
            player.setName(JOptionPane.showInputDialog(frame, "Please choose your display name :", "Lady Java"));//"The Pussy Destroyer"
            player.setMass(150000);
            player.showLocation();
            sesionID = generateID((byte)0);
            player.setID(sesionID);
            sendFish(player);
            
            while(inGame){
                getChatUpdate();
                verifyNames();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    System.out.println("GetData Sleep : " + ex.getMessage());
                } 
            }
        }
    }
    public class UDPack {
        
        int latency;
        byte[] data;
        Fish fish;

        public UDPack(Fish fish) {
            this.fish = fish;
        }
        
        public boolean isAlive(){
            return fish.getY()<(GraphicsPanel.getBackground()[0].getHeight()+fish.getRectangle().getHeight()-5);
        }
        public void update(DatagramPacket receivePacket){
            int nowTimeMillis = (int)(System.currentTimeMillis() % 1000000000);
            
            byte[] tempData;
            int tempTimeMillis;
            int tempLatency;
            
            if(data==null){
                data = receivePacket.getData();
                tempTimeMillis = data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
                latency = nowTimeMillis-tempTimeMillis;
                bytesIn += receivePacket.getLength();
            }else{
                tempData = receivePacket.getData();
                tempTimeMillis = tempData[0] << 24 | (tempData[1] & 0xFF) << 16 | (tempData[2] & 0xFF) << 8 | (tempData[3] & 0xFF);
                tempLatency = nowTimeMillis-tempTimeMillis;

                if(latency > tempLatency){
                    data = tempData;
                    latency = tempLatency;
                    bytesIn += receivePacket.getLength();
                }                        

            }
        }
        
        public void consume(){
            synchronized(fishList){

                for(Fish fish : fishList){
                    if(fish.getID()!=-1){
//                            fish.setID(data[4]);
//                            fish.setIdentity(data[4]);//data[i++] << 24 | (data[i++] & 0xFF) << 16 | 
                            fish.setX((int)((data[5] & 0xFF) << 8 | (data[6] & 0xFF)));
                            fish.setY((int)((data[7] & 0xFF) << 8 | (data[8] & 0xFF)));
                            fish.setFacingLeft(data[9]);
                            fish.setAngle((int)data[10]);
                            fish.setIsAliveIsSprinting(data[11]);
                            fish.addNewGain((int)((data[12] & 0xFF) << 8 | (data[13] & 0xFF)));
                    }
                }
            }
        }
    }
    public class UDPUpdate{
        byte[] data;
        boolean used;
        
        
        
    }
}
