package Fish;

import Enviroment.Camera;
import Game.Game;
import Game.GraphicsPanel;
import SeaFood.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Fish {

    //CONSTANTS
    private static final float BONES_SPEED=1;
    private static final int Charge_Timer_Value = 40;
    private static final int Charge_CoolDown_Value=150;
    private static final int Charge_Multiplication = 3;
    
    private static ArrayList<Fish> fishList = new ArrayList<>();
    private ArrayList<Gain> gainList = new ArrayList<>();
    
    private BufferedImage image;
    private BufferedImage restFacingLeft[];
    private BufferedImage restFacingRight[];
    private BufferedImage swimToLeft[];
    private BufferedImage swimToRight[];
    private BufferedImage fishBones[];
    private int animationIndex;
//    private int animationCount;
    private boolean facingLeft;
    
    private float x;
    private float y;
    private float speedX;
    private float speedY;
    private float speedRateY;
    private float speedRateX;
    private int mass;
    private float size = (float)mass/(float)5000;
    private float massDecayRate = 0.001f;
    private boolean moveY;
    private boolean moveX;
    private int agilityX;
    private int agilityY;
    private float angle;
    private boolean inCharge;
    private int chargeTimer;
    private int chargeCoolDown;

    private boolean showLocation;
    private int showLocationSize=200;
    private boolean isHuman;

    public Fish() {
        fishList.add(this);
    }
    public synchronized static ArrayList<Fish> getFishList(){
        return fishList;
    }
    
    //Getters
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
    public Rectangle getCurentRectangle(){
        try{
            
            if(image!=null){
                return new Rectangle((int)x, (int)y, (int) (image.getWidth()*Camera.getZoom()*size), (int) (image.getHeight()*Camera.getZoom()*size));
            }else{
                if(speedX!=0){
                    if(speedX>0){
                        return new Rectangle((int)x, (int)y, (int) (swimToRight[animationIndex].getWidth()*Camera.getZoom()*size), (int) (swimToRight[animationIndex].getHeight()*Camera.getZoom()*size));
                    }else{
                        return new Rectangle((int)x, (int)y, (int) (swimToLeft[animationIndex].getWidth()*Camera.getZoom()*size), (int) (swimToLeft[animationIndex].getHeight()*Camera.getZoom()*size));
                    }             
                }else{
                    if(facingLeft){
                        return new Rectangle((int)x, (int)y, (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size));
                    }else{
                        return new Rectangle((int)x, (int)y, (int) (restFacingRight[animationIndex].getWidth()*Camera.getZoom()*size), (int) (restFacingRight[animationIndex].getHeight()*Camera.getZoom()*size));
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
        return swimToLeft;
    }
    //Setters
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
    public void setIsHuman(boolean isHuman){
        this.isHuman=isHuman;
    }
    //IS
    public boolean isAlive(){
        return mass>0;
    }
    public boolean isInCharge(){
        return inCharge;
    }
    public boolean isHuman(){
        return isHuman;
    }
    //Add/Give
    public void addMass(int mass){
        if(isHuman){
            gainList.add(new Gain(x+restFacingLeft[0].getWidth()/2, y+restFacingLeft[0].getHeight()/2, mass));}
        this.mass+=mass;
    }
    public int giveMass(){
        int x = mass;
        mass=0;
        return x;
    }
    public void giveBait(){
        if(mass>500){
            mass-=100;
            if(facingLeft){
                new SmlSquid(x-20, y+swimToLeft[0].getHeight()/2, -15, 0);
            }else{
                new SmlSquid(x+swimToRight[0].getWidth()+20, y+swimToLeft[0].getHeight()/2, 15, 0);
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
        if(angle<20){angle++;}
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
        if(angle>-20){angle--;}
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
        if(mass!=0){
            //Mass Decay
            mass-= massDecayRate * mass;
//            size = (float)mass/(float)5000;
            size =1;
                
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
            
            if(x>11300){
                x=11300;
            }
            if(x<500){
                x=500;
            }
            
        }else{
            //DEATH
            y+=BONES_SPEED;
        }
    }
    //Draw
    public void draw(Graphics g){
        
        //if the fish is in the frame then draw
        if((!(x>(Camera.getX()-swimToRight[0].getWidth()) && x<(Camera.getX()+Game.getWIDTH()))) || 
            (!(y>(Camera.getY()-swimToRight[0].getHeight()) && y<(Camera.getY()+Game.getHEIGHT())))){
            return;
        }
        
        float drawX = this.x - Camera.x;
        float drawY = this.y - Camera.y;
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
//            g.drawRect((int) (drawX), (int) (drawY), (int) (getCurentRectangle().width), (int) (getCurentRectangle().height));
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
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size), null);
                    }else{
                        g2.drawImage(swimToRight[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size), null);
                    }
                }else if(speedX>0){
                    g2.drawImage(swimToRight[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size), null);
                    facingLeft=false;
                }else{
                    g2.drawImage(swimToLeft[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size), null);
                    facingLeft=true;
                }

            //Rest
            }else{
                if(facingLeft){
                    g.drawImage(restFacingLeft[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size), null);
                }else{
                    g.drawImage(restFacingRight[animationIndex], (int)drawX, (int)drawY, 
                            (int) (restFacingLeft[animationIndex].getWidth()*Camera.getZoom()*size), 
                            (int) (restFacingLeft[animationIndex].getHeight()*Camera.getZoom()*size), null);
                }
            }
            
            //ShowGain
            if(isHuman){
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


            animationIndex++;
            if(animationIndex==6){animationIndex=0;}
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
    class Gain {
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
            
            
            g.setColor(Color.red);
            if(showGain>7 && showGain <20){
                g.setFont(new Font("TimesRoman", Font.PLAIN, (int)(2*showGain)));
            }else if(showGain<=7){
                g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            }else if(showGain>=20){
                g.setFont(new Font("TimesRoman", Font.PLAIN, 40));
            }
            g.drawString("+ " + massGain, (int)(xGain-Camera.x), (int)(yGain-showGain-Camera.y));
            g.setColor(Color.black);                
        }
        
    }
}
