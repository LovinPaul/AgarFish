package Fish;

import Enviroment.Camera;
import Game.Server;
import Game.GraphicsPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Fish implements Serializable{
    private static final long serialVersionUID = 1L;
    //CONSTANTS
    private transient static final float BONES_SPEED=1;
    private transient static final int Charge_Timer_Value = 40;
    private transient static final int Charge_CoolDown_Value=150;
    private transient static final int Charge_Multiplication = 3;
    private transient static final float MAX_Angle = 20;
    
//    private transient static ArrayList<Fish> fishList = new ArrayList<>();
    private transient ArrayList<SprintBubble> sprintBubbleList = new ArrayList<>();
    private transient int newGain;
    
    private transient BufferedImage image;
    private transient BufferedImage restFacingLeft[];
    private transient BufferedImage restFacingRight[];
    private transient BufferedImage swimToLeft[];
    private transient BufferedImage swimToRight[];
    private transient BufferedImage fishBones[];
    private transient static int animationIndex;
//    private int animationCount;
    private transient boolean facingLeft;
    
    private float x;
    private float y;
    private transient float speedX;
    private transient float speedY;
    private float speedRateY;
    private float speedRateX;
    private int mass;
    private transient static float massDecayRate = 0.001f;
    private transient boolean moveY;
    private transient boolean moveX;
    private int agilityX;
    private int agilityY;
    private transient float angle=0;
    private transient boolean inCharge;
    private transient int chargeTimer;
    private transient int chargeCoolDown;

    private transient boolean showLocation;
    private transient int showLocationSize=200;

    private byte playerID = -1;
    private String name = "Random Boot";
    private transient boolean isHuman;
    
    public transient boolean up;
    public transient boolean down;
    public transient boolean left;
    public transient boolean right;
    
    public transient boolean clientUp;
    public transient boolean clientDown;
    public transient boolean clientLeft;
    public transient boolean clientRight;    
    
    //Statics
    public static void oncePerFrameUpdates(){
        animationIndex++;
        if(animationIndex==6){animationIndex=0;}
    }

    //Getters
    public String getName(){
        return name;
    }
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getSpeedX(){
        return speedX;
    }
    public float getSpeedY(){
        return speedY;
    }
    public int getAgilityX(){
        return agilityX;
    }
    public int getAgilityY(){
        return agilityY;
    }
    public float getSpeedRateX(){
        return speedRateX;
    }
    public float getSpeedRateY(){
        return speedRateY;
    }
    public int getMass(){
        return mass;
    }
    public Rectangle getRectangle(){
        try{
            if(swimToLeft==null || swimToRight==null || restFacingLeft==null || restFacingRight==null){
                identity();
            }
            if(image!=null){
                return new Rectangle((int)x, (int)y, (int) (image.getWidth()*Camera.getZoom()), (int) (image.getHeight()*Camera.getZoom()));
            }else{
                if(speedX!=0){
                    if(speedX>0){
                        return new Rectangle((int)x, (int)y, (int) (swimToRight[animationIndex].getWidth()*Camera.getZoom()), (int) (swimToRight[animationIndex].getHeight()*Camera.getZoom()));
                    }else{
                        return new Rectangle((int)x, (int)y, (int) (swimToLeft[animationIndex].getWidth()*Camera.getZoom()), (int) (swimToLeft[animationIndex].getHeight()*Camera.getZoom()));
                    }             
                }else{
                    if(facingLeft){
                        return new Rectangle((int)x, (int)y, (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()));
                    }else{
                        return new Rectangle((int)x, (int)y, (int) (restFacingRight[animationIndex].getWidth()*Camera.getZoom()), (int) (restFacingRight[animationIndex].getHeight()*Camera.getZoom()));
                    }

                }
            }
        }catch(Exception ex){
            System.out.println("getCurentRectangle() NullPointerException");
            return new Rectangle(0, 0, 0, 0);
        }
    }
    public int getChargeTimer(){
        return chargeTimer;
    }
    public int getChargeTimerPercent(){
        return (chargeTimer*100)/Charge_Timer_Value;
    }
    public int getChargeCoolDown(){
        return chargeCoolDown;
    }
    public BufferedImage[] getSwimToLeft(){
        if(swimToLeft==null || swimToRight==null || restFacingLeft==null || restFacingRight==null){
            identity();
        }
        return swimToLeft;
    }
    public short getNewGain(){
        int gain = newGain;
        newGain=0;
        return (short)gain;
    }
    public byte getID(){
        return playerID;
    }
    public byte getAngle(){
        return (byte)angle;
    }
    public byte getFacingLeft(){
        return facingLeft ? (byte)1 : (byte)0;
    }
//    public byte getInstance(){
//        if(this instanceof AngelFish){
//            return (byte) 0;
//            
//        }else if(this instanceof GuppieFish){
//            return (byte) 1;
//            
//        }else{
//            return (byte) 2;
//        }
//    }
    //Setters
    public void setName(String name){
        this.name = name;
    }
    public void setImage(BufferedImage img){
        image = img;
    }
    public void setRestFacingLeft(BufferedImage img[]){
        restFacingLeft=img;
    }
    public void setRestFacingRight(BufferedImage img[]){
        restFacingRight=img;
    }
    public void setSwimToLeft(BufferedImage img[]){
        swimToLeft=img;
    }
    public void setSwimToRight(BufferedImage img[]){
        swimToRight=img;
    }
    public void setFishBones(BufferedImage img[]){
        fishBones = img;
    }
    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public void setSpeedX(float speedX){
        this.speedX = speedX;
    }
    public void setSpeedY(float speedY){
        this.speedY = speedY;
    }
    public void setAgilityX(int agi){
        agilityX = agi;
    }
    public void setAgilityY(int agi){
        agilityY = agi;
    }
    public void setSpeedRateX(float rate){
        speedRateX=rate;
    }
    public void setSpeedRateY(float rate){
        speedRateY=rate;
    }
    public void setMass(int mass){
        this.mass=mass;
    }
    public void setToBot(){
        isHuman=false;
    }
    public void setID(byte id){
        playerID = id;
    }
    public void setHuman(boolean human){
        isHuman=human;
    }
    //IS
    public boolean isAlive(){
        return mass>0;
    }
    public byte isAliveIsSprinting(){
        
//        if(inCharge && Math.abs(speedX) > agilityX/Charge_Multiplication && mass>0){
//            return (byte) 2;
//        }else if(mass>0){
//            return (byte) 1;
//        }else{
//            return (byte) 0;
//        }
        return mass>0 ? (byte) (1+chargeTimer): (byte) 0;
    }
    public boolean isInCharge(){
        return inCharge;
    }
    public boolean isHuman(){
        return isHuman;
    }
    //Add/Give
    public void addMass(int mass){
        if(isHuman()){
            newGain = mass;
        }
        this.mass+=mass;
    }
    public int giveMass(){
        int x = mass;
        mass=0;
        return 100+x;
    }
    //Movement
    public void showLocation(){
        showLocation=true;
    }
    public void charge(){
        if(chargeTimer==0 && chargeCoolDown==0){
            chargeTimer=Charge_Timer_Value;
            inCharge=true;
            agilityX=Charge_Multiplication*agilityX;
        }
    }
    public void goUp(){
        moveY=true;
        up=true;
        if(angle<MAX_Angle){angle++;}
        if(angle>0){
            if(Math.abs(speedY) < agilityY){
                speedY-=speedRateY;
            }else{
                speedY=-agilityY;
            }
        }else{
            angle++;
        }
    }
    public void goDown(){
        moveY=true;
        down=true;
        if(angle>-MAX_Angle){angle--;}
        if(angle<0){
            if(Math.abs(speedY) < agilityY){
                speedY+=speedRateY;
            }else{
                speedY=agilityY;
            }
        }else{
            angle--;
        }
    }
    public void goLeft(){
        moveX=true;
        left=true;
        if(speedX > -agilityX){
            speedX-=speedRateX;
            if(speedX<-agilityX){speedX=-agilityX;}
        }else{
            if(chargeCoolDown>0 && speedX!=-agilityX){
                speedX+=speedRateX;
            }
        }
    }
    public void goRight(){
        moveX=true;
        right=true;
        if(speedX < agilityX){
            speedX+=speedRateX;
            if(speedX>agilityX){speedX=agilityX;}
        }else{
            if(chargeCoolDown>0 && speedX!=agilityX){
                speedX-=speedRateX;
            }
        }
    }
    //Update
    public void identity(){
        if(this instanceof AngelFish){
            restFacingLeft = GraphicsPanel.getAngelFishRestLeft();
            restFacingRight = GraphicsPanel.getAngelFishRestRight();
            swimToLeft = (GraphicsPanel.getAngelFishSwimLeft());
            swimToRight = GraphicsPanel.getAngelFishSwimRight();
            fishBones = (GraphicsPanel.getAngelFishBones());
        }
        if(this instanceof JaiFish){
            restFacingLeft = (GraphicsPanel.getJaiFishRestLeft());
            restFacingRight = (GraphicsPanel.getJaiFishRestRight());
            swimToLeft = (GraphicsPanel.getJaiFishSwimLeft());
            swimToRight = (GraphicsPanel.getJaiFishSwimRight());
            fishBones = (GraphicsPanel.getJaiFishBones());
        }
        
        if(this instanceof GuppieFish){
            restFacingLeft = (GraphicsPanel.getGuppieFishRestLeft());
            restFacingRight = (GraphicsPanel.getGuppieFishRestRight());
            swimToLeft = (GraphicsPanel.getGuppieFishSwimLeft());
            swimToRight = (GraphicsPanel.getGuppieFishSwimRight());
            fishBones = (GraphicsPanel.getGuppieFishBones());
        }
    }
    public void massDecay(){
        if(mass!=0){
            mass-= massDecayRate * mass;
        }
    }
    public void updateCoord() {
        
        if(clientUp){goUp();}
        if(clientDown){goDown();}
        if(clientLeft){goLeft();}
        if(clientRight){goRight();}
        
        if(swimToLeft==null || swimToRight==null || restFacingLeft==null || restFacingRight==null){
            identity();
        }
        if(mass!=0){
            //Charge
            if(inCharge){
                chargeTimer--;
                if(chargeTimer==0){
                    inCharge=false;
                    agilityX=agilityX/Charge_Multiplication;
                    chargeCoolDown=Charge_CoolDown_Value;
                }
            }else if(chargeCoolDown>0){
                chargeCoolDown--;
            }
            //Facing
            if(speedX>0){
                facingLeft=false;
            }else if(speedX<0){
                facingLeft=true;
            }
            //
            if(moveY){
            }else{
                
                if(angle!=0){
                    if(angle<2 && angle>-2){angle=0;}
                    if(angle>0){angle-=2;}else{angle+=2;}
                }
                if(speedY<0 ){
                    speedY+=speedRateY/2;
                }
                if(speedY>0 ){
                    speedY-=speedRateY/2;
                }
                if(Math.abs(speedY)<speedRateY){
                    speedY = 0;
                }
            }
            //
            if(moveX){
            }else{
                if(speedX<0){
                    speedX+=speedRateX/10;//
                }
                if(speedX>0){
                    speedX-=speedRateX/10;//
                }
                if(Math.abs(speedX)<speedRateX){
                    speedX = 0;
                }
            }
            
            moveY=false;
            moveX=false;
            x+=speedX;
            y+=speedY;
            if(y>(GraphicsPanel.getBackground()[0].getHeight()-swimToRight[0].getHeight()-50)){y=(GraphicsPanel.getBackground()[0].getHeight()-swimToRight[0].getHeight()-50);}
            if(y<10){y=10;}
            
            if(x>GraphicsPanel.getBackground()[0].getWidth()-restFacingLeft[0].getWidth()){
                goLeft();
                goLeft();
                goLeft();
                goLeft();
                goLeft();
            }
            if(x<0){
                goRight();
                goRight();
                goRight();
                goRight();
                goRight();
            }
            
        }else{
            //DEATH
            y+=BONES_SPEED;
        }
    }
    //Draw
    public void draw(Graphics g){

        if(swimToLeft==null || swimToRight==null || restFacingLeft==null || restFacingRight==null){
            identity();
        }
        //if the fish is in the frame then draw
        if((!(x>(Camera.getX()-swimToRight[0].getWidth()) && x<(Camera.getX()+Server.getWIDTH()))) || 
            (!(y>(Camera.getY()-swimToRight[0].getHeight()) && y<(Camera.getY()+Server.getHEIGHT())))){
            return;
        }

        float drawX = this.x - Camera.x;
        float drawY = this.y - Camera.y;
        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        if(name!=null){
            g.drawString(name, (int)drawX, (int)drawY);
        }else{
            g.drawString("*NULL*", (int)drawX, (int)drawY);
        }
        
//        g.drawString(this.playerID+"", (int)drawX, (int)drawY);
        if(mass!=0){
            if(showLocation){
                if(showLocationSize>10){
                    showLocationSize-=9;
                    Graphics2D g2t = (Graphics2D) g;
                    g2t.setStroke(new BasicStroke(10));
                    g2t.setColor(Color.ORANGE);
                    g2t.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.drawArc((int)drawX, (int)drawY, showLocationSize, showLocationSize, 0+showLocationSize, 45+showLocationSize);
                }else{
                    showLocationSize = 200;
                    showLocation=false;
                }
            }

            //SprintBubble
            if(sprintBubbleList==null){sprintBubbleList = new ArrayList<>();}
            if(inCharge && Math.abs(speedX) > agilityX/Charge_Multiplication){
                sprintBubbleList.add(new SprintBubble(x, y));
            }
            synchronized(sprintBubbleList){
                Iterator<SprintBubble> sBubleIterator = sprintBubbleList.iterator();
                while ( sBubleIterator.hasNext() ) {
                    SprintBubble bubb = sBubleIterator.next();
                    if(bubb.isAlive()){
                        bubb.draw(g);
                    }else{
                        sBubleIterator.remove();
                    }
                }
            }
            
//            g.drawRect((int) (drawX), (int) (drawY), (int) (getRectangle().width), (int) (getRectangle().height));
            //Swim
            if(speedX!=0 || speedY!=0 || angle!=0){
                Graphics2D g2 = (Graphics2D) g.create();

                if(angle>0 && facingLeft==false){
                    g2.rotate(Math.toRadians(360-Math.abs(angle)), (int)drawX+swimToRight[animationIndex].getWidth()/2, (int)drawY+swimToRight[animationIndex].getHeight()/2);
                }
                if(angle<0 && facingLeft==false){
                    g2.rotate(Math.toRadians(Math.abs(angle)), (int)drawX+swimToRight[animationIndex].getWidth()/2, (int)drawY+swimToRight[animationIndex].getHeight()/2);
                }
                if(angle>0 && facingLeft==true){
                    g2.rotate(Math.toRadians(Math.abs(angle)), (int)drawX+swimToRight[animationIndex].getWidth()/2, (int)drawY+swimToRight[animationIndex].getHeight()/2);
                }
                if(angle<0 && facingLeft==true){
                    g2.rotate(Math.toRadians(360-Math.abs(angle)), (int)drawX+swimToRight[animationIndex].getWidth()/2, (int)drawY+swimToRight[animationIndex].getHeight()/2);
                }

                if(speedX==0){
                    if(facingLeft){
                        g2.drawImage(swimToLeft[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()), null);
                    }else{
                        g2.drawImage(swimToRight[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()), null);
                    }
                }else if(speedX>0){
                    g2.drawImage(swimToRight[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()), null);
//                    facingLeft=false;
                }else{
                    g2.drawImage(swimToLeft[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()), null);
//                    facingLeft=true;
                }
            //Rest
            }else{
                if(facingLeft){
                    g.drawImage(restFacingLeft[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()), null);
                }else{
                    g.drawImage(restFacingRight[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()), null);
                }
            }
        }else{
            if(fishBones!=null){
                if(facingLeft){
                    g.drawImage(fishBones[0], (int)drawX, (int)drawY, null);
                }else{
                    g.drawImage(fishBones[1], (int)drawX, (int)drawY, null);
                }
            }
        }
    }
    
    //innerClass
    class Gain implements Serializable{
        private static final long serialVersionUID = 2L;
        private boolean isAlive=true;
        private final float xGain;
        private final float yGain;
        private final int massGain;
        private float showGain;

        public Gain(float xGain, float yGain, int massGain) {
            this.xGain = xGain;
            this.yGain = yGain;
            this.massGain = massGain;
        }
    }
    class SprintBubble {
        float index;
        BufferedImage image;
        float x;
        float y;

        public SprintBubble(float x, float y) {
            this.x = x;
            this.y = y + (float) (Math.random()*restFacingLeft[0].getHeight()/2);
            image = GraphicsPanel.getSprintBubles();
        }
        
        public boolean isAlive(){
            return index<9;
        }
        
        public void draw(Graphics g){
            float drawX = this.x - Camera.x;
            float drawY = this.y - Camera.y;
//            System.out.println(64*index + " - " + 64*index + " - " + 64 + " - " + 64);*index
            g.drawImage(image.getSubimage(32*(int)index, 0, 32, 32),(int)drawX, (int)drawY, null);
            y-=3;
            index+=0.25;
        }
    }
}
