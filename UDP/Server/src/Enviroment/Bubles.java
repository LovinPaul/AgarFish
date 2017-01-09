package Enviroment;


import Game.GraphicsPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Bubles {
    
    private BufferedImage image[];
    public static ArrayList<Bubles> bublesList = new ArrayList<>();
    private static int nrOfBubles;
    private float x;
    private float y;
    public float speedX;
    public float speedY;

    public Bubles(float x, float y) {
        this.x = x;
        this.y = y;
        image = GraphicsPanel.getBubles();
        bublesList.add(this);
        nrOfBubles++;
    }
    
    public static ArrayList<Bubles> getBublesList(){
        return bublesList;
    }
    public static int getNrOfBubles(){
        return nrOfBubles;
    }
    public static void nrOfBublesDecrement(){
        nrOfBubles--;
    }
    
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    //Update
    public void updateCoord() {
        
        y-= 3;
        
//        if(y>10){bublesList.remove(this);}
        
    }
    //Draw
    public void draw(Graphics g){
        float x = this.x - Camera.x;
        float y = this.y - Camera.y;
        g.drawImage(image[0], (int)x, (int)y, null);
        
    }
}
