package Enviroment;

import Fish.Fish;
import Game.Server;
import Game.GraphicsPanel;

public abstract class Camera {
    
    private static Fish fish;
    
    public static float x;
    public static float y;
    public static float speedX;
    public static float speedY;
    private static boolean freeCam=true;
    private static boolean moveY;
    private static boolean moveX;
     static int agilityX=30;
     static int agilityY=30;
     static float speedRateY=1;
     static float speedRateX=1;
    
     private static float zoom=1;
    

     
    public static void goUp(){
        moveY=true;
        if(isFreeCam()){
            if(Math.abs(speedY) < agilityY){
                speedY-=speedRateY;
            }
        }else{
            if(Math.abs(speedY) < fish.getAgilityY()){
                speedY-=fish.getSpeedRateY();
            }
        }
    }
    public static void goDown(){
        moveY=true;
        if(isFreeCam()){
            if(Math.abs(speedY) < agilityY){
                speedY+=speedRateY;
            }
        }else{
            if(Math.abs(speedY) < fish.getAgilityY()){
                speedY+=fish.getSpeedRateY();
            }            
        }
    }
    public static void goLeft(){
        moveX=true;
        if(isFreeCam()){
            if(Math.abs(speedX) < agilityX){
                speedX-=speedRateX;
            }
        }else{
            if(Math.abs(speedX) < fish.getAgilityX()){
                speedX-=fish.getSpeedRateX();
            }            
        }
    }
    public static void goRight(){
        moveX=true;
        if(isFreeCam()){
            if(Math.abs(speedX) < agilityX){
                speedX+=speedRateX;
            }
        }else{
            if(Math.abs(speedX) < fish.getAgilityX()){
                speedX+=fish.getSpeedRateX();
            }            
        }
    }
    
    public static void setFreeCam(boolean set){
        freeCam = set;
    }
    
    public static int getDiferenceX(){
        return (int) (-fish.getX() + Camera.x+Server.getWIDTH()/2);
    }
    public static float getX(){
        return x;
    }
    public static float getY(){
        return y;
    }
    public static float getZoom(){
        return zoom;
    }    
    
    public static boolean isFreeCam(){
        return freeCam;
    }
    
    public static void updateCoord(){
        if(!freeCam){return;}
            if(moveY){
            }else{
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
    }
    public static void follow(Fish fish) {
        if(freeCam){return;}
        if(Camera.fish != fish){
            Camera.fish = fish;
            Camera.speedX =0;
            Camera.speedY =0;
        }
        
        float difX = Camera.x+Server.getWIDTH()/2 -fish.getX();
        float difY = Camera.y+Server.getHEIGHT()/2 -fish.getY();
        
//        System.out.println(difX);
//        if( Math.abs(difX) > 100){
//            x -= (2*difX)/10;
//        }else{
//            x -= difX/10;
//        }
        x -= difX/10;
        y -= difY/10;

        if(x<0){x=0;}
        if(y<0){y=0;}
        if(y>(GraphicsPanel.getBackground()[0].getHeight()-Server.getHEIGHT())){y=(GraphicsPanel.getBackground()[0].getHeight()-Server.getHEIGHT());}
        if(x>12000 - Server.getWIDTH()){x= 12000 - Server.getWIDTH();}
    }
    
    
}
