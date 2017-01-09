package Enviroment;


import Game.GraphicsPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Bubles {
    
    private BufferedImage image[];
    public static ArrayList<Bubles> bublesList = new ArrayList<>();
    private float x;
    private float y;
    public float speedX;
    public float speedY;

    public Bubles(float x, float y) {
        this.x = x;
        this.y = y;
        image = GraphicsPanel.getBubles();
        bublesList.add(this);
    }
    
    public static ArrayList<Bubles> getBublesList(){
        return bublesList;
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
    }
    //Draw
    public void draw(Graphics g){
        g.drawImage(image[0], (int)(this.x - Camera.x), (int)(this.y - Camera.y), null);
    }
}
