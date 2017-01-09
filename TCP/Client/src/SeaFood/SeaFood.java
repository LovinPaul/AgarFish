package SeaFood;

import Enviroment.Camera;
import Game.Client;
import Game.GraphicsPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class SeaFood implements Serializable{
    private static final long serialVersionUID = 3L;
    private transient static ArrayList<SeaFood> seaFoodList = new ArrayList<>();
    
    private transient BufferedImage image[];
    private transient int animationIndex;
    private transient boolean facingLeft;
    
    
    private float x;
    private float y;
    private transient float speedX;
    private transient float speedY;
    private transient float speedRateY;
    private transient float speedRateX;
    private int mass;
    private transient boolean moveY;
    private transient boolean moveX;
    private transient int agilityX;
    private transient int agilityY;
    private transient float angle;
    
    public SeaFood() {
        seaFoodList.add(this);
    }
    
    //Statics
    public synchronized static ArrayList<SeaFood> getSeaFoodList(){
        return seaFoodList;
    }
    public synchronized static void setFishLit(ArrayList<SeaFood> foodList){
        SeaFood.seaFoodList = foodList;
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
    public Rectangle getRectangle(){
        try{
            if(image==null){identity();}
            return new Rectangle((int)x, (int)y, image[animationIndex].getWidth(), image[animationIndex].getHeight());

        }catch(Exception ex){
            System.out.println("getCurentRectangle() NullPointerException");
            return new Rectangle(0, 0, 0, 0);
        }
    }
    //Setters
    public void setImage(BufferedImage[] img){
        image = img;
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
    //IS
    public boolean isAlive(){
        return mass>0;
    }
    //Add/Give
    public void addMass(int mass){
        this.mass+=mass;
    }
    public int giveMass(){
        int x = mass;
        mass=0;
        return x;
    }
    //Movement
    public void goUp(){
        moveY=true;
        if(angle<20){angle++;}
        if(Math.abs(speedY) < agilityY){
            speedY-=speedRateY;
        }else{
            speedY=-agilityY;
        }
    }
    public void goDown(){
        moveY=true;
        if(angle<20){angle++;}
        if(Math.abs(speedY) < agilityY){
            speedY+=speedRateY;
        }else{
            speedY=agilityY;
        }
    }
    public void goLeft(){
        moveX=true;
        if(speedX > -agilityX){
            speedX-=speedRateX;
            if(speedX<-agilityX){speedX=-agilityX;}
        }else{
        }
    }
    public void goRight(){
        moveX=true;
        if(speedX < agilityX){
            speedX+=speedRateX;
            if(speedX>agilityX){speedX=agilityX;}
        }else{
        }
    }
    //Update
    public void updateCoord() {
        if(mass!=0){
            //Mass Decay
//            mass-= 0 * mass;
            //
            if(moveY){
            }else{
                if(angle>0){angle-=2;}else{angle=0;}
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
            if(y>(GraphicsPanel.getBackground()[0].getHeight()-image[0].getHeight()-50)){y=(GraphicsPanel.getBackground()[0].getHeight()-image[0].getHeight()-50);}
            if(y<10){y=10;}
            if(x>11300){
                x=11300;
            }
            if(x<500){
                x=500;
            }
            
        }else{
            //DEATH
//            y+=BONES_SPEED;
        }
    }
    public void identity(){
        if(this instanceof SmlSquid){
            image = GraphicsPanel.getSmlSquid();
        }
    }
    //Draw
    public void draw(Graphics g){
        
        if(image==null){identity();}
        
        //if the fish is in the frame then draw
        if((!(x>(Camera.getX()-image[0].getWidth()) && x<(Camera.getX()+Client.getWIDTH()))) || 
            (!(y>(Camera.getY()-image[0].getHeight()) && y<(Camera.getY()+Client.getHEIGHT())))){
            return;
        }
            
        float drawX = this.x - Camera.x;
        float drawY = this.y - Camera.y;
        if(mass!=0){
                //Swim
                if(speedX!=0 || speedY!=0 || angle!=0){

                    Graphics2D g2 = (Graphics2D) g.create();

                    if(speedY<0 && facingLeft==false){
                        g2.rotate(Math.toRadians(angle), (int)drawX+image[animationIndex].getWidth()/2, (int)drawY+image[animationIndex].getHeight()/2);
                    }
                    if(speedY>0 && facingLeft==false){
                        g2.rotate(Math.toRadians(360-angle), (int)drawX+image[animationIndex].getWidth()/2, (int)drawY+image[animationIndex].getHeight()/2);
                    }
                    if(speedY<0 && facingLeft==true){
                        g2.rotate(Math.toRadians(360-angle), (int)drawX+image[animationIndex].getWidth()/2, (int)drawY+image[animationIndex].getHeight()/2);
                    }
                    if(speedY>0 && facingLeft==true){
                        g2.rotate(Math.toRadians(angle), (int)drawX+image[animationIndex].getWidth()/2, (int)drawY+image[animationIndex].getHeight()/2);
                    }

                    if(speedX==0){
                        if(facingLeft){
                            g2.drawImage(image[animationIndex], (int)drawX, (int)drawY, null);
                        }else{
                            g2.drawImage(image[animationIndex], (int)drawX, (int)drawY, null);
                        }
                    }else if(speedX>0){
                        g2.drawImage(image[animationIndex], (int)drawX, (int)drawY, null);
                        facingLeft=false;
                    }else{
                        g2.drawImage(image[animationIndex], (int)drawX, (int)drawY, null);
                        facingLeft=true;
                    }

                //Rest
                }else{
                    if(facingLeft){
                        g.drawImage(image[animationIndex], (int)drawX, (int)drawY, null);
                    }else{
                        g.drawImage(image[animationIndex], (int)drawX, (int)drawY, null);
                    }
                }
                animationIndex++;
                if(animationIndex==8){animationIndex=0;}
            
        }else{
//            if(fishBones!=null){
//                if(facingLeft){
//                    g.drawImage(fishBones[0], (int)drawX, (int)drawY, null);
//                }else{
//                    g.drawImage(fishBones[1], (int)drawX, (int)drawY, null);
//                }
//            }else{
//                g.drawImage(GraphicsPanel.getFishBone(), (int)drawX, (int)drawY, null);
//            }
        }
    }
}
