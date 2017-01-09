package Fish;

import Enviroment.Camera;
import Game.Client;
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

public class Fish implements Serializable{
    private static final long serialVersionUID = 1L;
    //CONSTANTS
    private transient static final float BONES_SPEED=1;
    private transient static final int Charge_Timer_Value = 40;
    private transient static final int Charge_CoolDown_Value=150;
    private transient static final int Charge_Multiplication = 3;
    private transient static final float MAX_Angle = 20;
    
    private transient ArrayList<Gain> gainList;
    private transient ArrayList<SprintBubble> sprintBubbleList = new ArrayList<>();
    
    
    private transient BufferedImage image;
    private transient BufferedImage restFacingLeft[];
    private transient BufferedImage restFacingRight[];
    private transient BufferedImage swimToLeft[];
    private transient BufferedImage swimToRight[];
    private transient BufferedImage fishBones[];
    private transient float animationIndex;
//    private int animationCount;
    private transient boolean facingLeft;
    
    private float x;
    private float y;
    private transient float speedX;
    private transient float speedY;
    private float speedRateY;
    private float speedRateX;
    private int mass;
//    private transient static float massDecayRate = 0.001f;
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
    private String name;
//    private transient byte setIdentity;
    
    public transient boolean up;
    public transient boolean down;
    public transient boolean left;
    public transient boolean right;

    
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
    public int getChargeTimer(){
        return chargeTimer;
    }
    public int getChargeTimerPercent(){
        return (chargeTimer*100)/Charge_Timer_Value;
    }
    public int getChargeCoolDown(){
        return chargeCoolDown;
    }
    public ArrayList<Gain> getGainList(){
        return gainList;
    }
//    public Gain getNewGain(){
//        Gain tmp = newGain;
//        newGain=null;
//        return tmp;
//    }
    public Rectangle getRectangle(){
        try{
            if(image!=null){
                return new Rectangle((int)x, (int)y, (int) (image.getWidth()*Camera.getZoom()), (int) (image.getHeight()*Camera.getZoom()));
            }else{
                if(speedX!=0){
                    if(speedX>0){
                        return new Rectangle((int)x, (int)y, (int) (swimToRight[(int)animationIndex].getWidth()*Camera.getZoom()), (int) (swimToRight[(int)animationIndex].getHeight()*Camera.getZoom()));
                    }else{
                        return new Rectangle((int)x, (int)y, (int) (swimToLeft[(int)animationIndex].getWidth()*Camera.getZoom()), (int) (swimToLeft[(int)animationIndex].getHeight()*Camera.getZoom()));
                    }             
                }else{
                    if(facingLeft){
                        return new Rectangle((int)x, (int)y, (int) (restFacingLeft[(int)animationIndex].getWidth()*Camera.getZoom()), (int) (restFacingLeft[(int)animationIndex].getHeight()*Camera.getZoom()));
                    }else{
                        return new Rectangle((int)x, (int)y, (int) (restFacingRight[(int)animationIndex].getWidth()*Camera.getZoom()), (int) (restFacingRight[(int)animationIndex].getHeight()*Camera.getZoom()));
                    }

                }
            }
        }catch(Exception ex){
            System.out.println("getCurentRectangle() NullPointerException");
            return new Rectangle(0, 0, 0, 0);
        }
    }
    public byte getAngle(){
        return (byte) angle;
    }
    public byte getID(){
        return playerID;
    }
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
    public void setID(byte id){
        playerID = id;
    }
    public void setAngle(float angle){
//        System.out.println(angle);
        this.angle = angle;
    }
    public void setFacingLeft(byte facing){
        facingLeft = facing==(byte)1;
    }
    public void setIsAliveIsSprinting(byte as){
        if(as>1){
//            chargeTimer=as-1;
            setMass(1);
            //SprintBubble
            charge();
        }else if(as==1){
            setMass(1);
        }else{
            setMass(0);
        }
    }
    public void setIdentity(byte identity){
        
        if(identity<30){
            restFacingLeft = GraphicsPanel.getAngelFishRestLeft();
            restFacingRight = GraphicsPanel.getAngelFishRestRight();
            swimToLeft = (GraphicsPanel.getAngelFishSwimLeft());
            swimToRight = GraphicsPanel.getAngelFishSwimRight();
            fishBones = (GraphicsPanel.getAngelFishBones());
            
            setAgilityX(7);
            setAgilityY(5);
            setSpeedRateX(1);
            setSpeedRateY(0.5f);
            setMass(1000);
            
        }else if(identity>=30 && identity<60){
            restFacingLeft = (GraphicsPanel.getGuppieFishRestLeft());
            restFacingRight = (GraphicsPanel.getGuppieFishRestRight());
            swimToLeft = (GraphicsPanel.getGuppieFishSwimLeft());
            swimToRight = (GraphicsPanel.getGuppieFishSwimRight());
            fishBones = (GraphicsPanel.getGuppieFishBones());
            
            setAgilityX(10);
            setAgilityY(7);
            setSpeedRateX(1);
            setSpeedRateY(0.5f);
            setMass(750);
            
        }else if(identity>=60){
            restFacingLeft = (GraphicsPanel.getJaiFishRestLeft());
            restFacingRight = (GraphicsPanel.getJaiFishRestRight());
            swimToLeft = (GraphicsPanel.getJaiFishSwimLeft());
            swimToRight = (GraphicsPanel.getJaiFishSwimRight());
            fishBones = (GraphicsPanel.getJaiFishBones()); 
            
            setAgilityX(10);
            setAgilityY(7);
            setSpeedRateX(1);
            setSpeedRateY(0.5f);
            setMass(500);
            
        }

    }    
    
    //IS
    public boolean isAlive(){
        return mass>0;
    }
    public boolean isInCharge(){
        return inCharge;
    }
    public boolean isPlayer(){
        return Client.sesionID==playerID;
    }
    //Add/Give
    public void addNewGain(int gain){
        if(gain>0){
            if(gainList==null){gainList = new ArrayList<>();}
            synchronized(gainList){
                gainList.add(new Gain(x, y, gain));
            }
        }
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
        if(speedX > -agilityX){
            speedX-=speedRateX;
            if(speedX<-agilityX){speedX=-agilityX;}
        }else{
            //Xneed easy slow down from charge
            if(chargeCoolDown>0 && speedX!=-agilityX){
                speedX+=speedRateX;
            }
        }
    }
    public void goRight(){
        moveX=true;
        if(speedX < agilityX){
            speedX+=speedRateX;
            if(speedX>agilityX){speedX=agilityX;}
        }else{
            //Xneed easy slow down from charge
            if(chargeCoolDown>0 && speedX!=agilityX){
                speedX-=speedRateX;
            }
        }
    }
    //Update

    public void updateCoord() {
        if(up){goUp();}
        if(down){goDown();}
        if(left){goLeft();}
        if(right){goRight();}
        
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
        
//        if(swimToLeft==null || swimToRight==null || restFacingLeft==null || restFacingRight==null){
//            setIdentity();
//        }

        //if the fish is in the frame then draw
        if((!(x>(Camera.getX()-swimToRight[0].getWidth()) && x<(Camera.getX()+Client.getWIDTH()))) || 
            (!(y>(Camera.getY()-swimToRight[0].getHeight()) && y<(Camera.getY()+Client.getHEIGHT())))){
            return;
        }
        
        float drawX = this.x - Camera.x;
        float drawY = this.y - Camera.y;
        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        if(name!=null){
            g.drawString(name, (int)drawX, (int)drawY);            
        }else{
            g.drawString("NAME_PLACEHOLDER", (int)drawX, (int)drawY);   
        }

        //g.drawString(playerID+"", (int)drawX, (int)drawY);
        if(mass!=0){
            
            
            animationIndex+=Math.abs(speedX)/agilityX;
            if(animationIndex>=6){animationIndex=0;}
            
            
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
            
            if(inCharge){
//                if(sprintBubbleList==null){sprintBubbleList = new ArrayList<>();}
                sprintBubbleList.add(new SprintBubble(x, y));
//                System.out.println(sprintBubbleList.size());
            }
            if(!sprintBubbleList.isEmpty()){
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


                

            //Swim
            if(speedX!=0 || speedY!=0 || angle!=0){
                Graphics2D g2 = (Graphics2D) g.create();
                if(angle>0 && facingLeft==false){
                    g2.rotate(Math.toRadians(360-Math.abs(angle)), (int)drawX+swimToRight[(int)animationIndex].getWidth()/2, (int)drawY+swimToRight[(int)animationIndex].getHeight()/2);
                }
                if(angle<0 && facingLeft==false){
                    g2.rotate(Math.toRadians(Math.abs(angle)), (int)drawX+swimToRight[(int)animationIndex].getWidth()/2, (int)drawY+swimToRight[(int)animationIndex].getHeight()/2);
                }
                if(angle>0 && facingLeft==true){
                    g2.rotate(Math.toRadians(Math.abs(angle)), (int)drawX+swimToRight[(int)animationIndex].getWidth()/2, (int)drawY+swimToRight[(int)animationIndex].getHeight()/2);
                }
                if(angle<0 && facingLeft==true){
                    g2.rotate(Math.toRadians(360-Math.abs(angle)), (int)drawX+swimToRight[(int)animationIndex].getWidth()/2, (int)drawY+swimToRight[(int)animationIndex].getHeight()/2);
                }

                if(speedX==0){
                    if(facingLeft){
                        g2.drawImage(swimToLeft[(int)animationIndex], (int)drawX, (int)drawY, 
                            (int) (swimToLeft[(int)animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (swimToLeft[(int)animationIndex].getHeight()*Camera.getZoom()), null);
                    }else{
                        g2.drawImage(swimToRight[(int)animationIndex], (int)drawX, (int)drawY, 
                            (int) (swimToRight[(int)animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (swimToRight[(int)animationIndex].getHeight()*Camera.getZoom()), null);
                    }
                }else if(speedX>0){
                    g2.drawImage(swimToRight[(int)animationIndex], (int)drawX, (int)drawY, 
                            (int) (swimToRight[(int)animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (swimToRight[(int)animationIndex].getHeight()*Camera.getZoom()), null);
                }else{
                    g2.drawImage(swimToLeft[(int)animationIndex], (int)drawX, (int)drawY, 
                            (int) (swimToLeft[(int)animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (swimToLeft[(int)animationIndex].getHeight()*Camera.getZoom()), null);
                }

            //Rest
            }else{
                if(facingLeft){
                    g.drawImage(restFacingLeft[(int)animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[(int)animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingLeft[(int)animationIndex].getHeight()*Camera.getZoom()), null);
                }else{
                    g.drawImage(restFacingRight[(int)animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingRight[(int)animationIndex].getWidth()*Camera.getZoom()), 
                            (int) (restFacingRight[(int)animationIndex].getHeight()*Camera.getZoom()), null);
                }
            }
            //ShowGain
            if(gainList==null){gainList = new ArrayList<>();}
            synchronized(gainList){
                if(isPlayer() && gainList!=null){
                    Iterator<Gain> gainIterator = gainList.iterator();
                    while ( gainIterator.hasNext() ) {
                        Gain gain = gainIterator.next();
                        if(gain.isAlive()){
                            gain.draw(g);
                        }else{
                            gainIterator.remove();
                        }
                    }
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
    public class Gain implements Serializable{
        private static final long serialVersionUID = 2L;
        public  boolean isAlive=true;
        public  final float xGain;
        public  final float yGain;
        public  final int massGain;
        public  float showGain;

        public Gain(float xGain, float yGain, int massGain) {
            this.xGain = xGain;
            this.yGain = yGain;
            this.massGain = massGain;
        }
        
        public boolean isAlive(){
            return isAlive;
        }
        
        //Draw
        public void draw(Graphics g){
            
            if(showGain>=80){
                isAlive=false;
                return;
            }else{
                showGain+=1;
            }
            
//            if(swimToLeft==null || swimToRight==null || restFacingLeft==null || restFacingRight==null){
//                setIdentity();
//            }
            
            g.setColor(Color.red);
            if(showGain>7 && showGain <20){
                g.setFont(new Font("TimesRoman", Font.PLAIN, (int)(2*showGain)));
            }else if(showGain<=7){
                g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            }else if(showGain>=20){
                g.setFont(new Font("TimesRoman", Font.PLAIN, 40));
            }
            g.drawString("+ " + massGain, (int)(xGain+(swimToLeft[0].getWidth()/2)-Camera.x), 
                                          (int)(yGain+(swimToLeft[0].getHeight()/2)-showGain-Camera.y));
            g.setColor(Color.black);
//            g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
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
