package AI;

import Fish.Fish;
import SeaFood.SeaFood;
import java.awt.geom.Point2D;

public abstract class AI {

    public static void seaFoodMovement(SeaFood food){
        
            switch((int)(Math.random()*4)){
                case 0:
                    food.goDown();
                    break;
                case 1:
                    food.goUp();
                    break;
                case 2:
                    food.goLeft();
                    break;
                case 3:
                    food.goRight();
                    break;
                case 4:
                    
                    break;
            }
        
        
    }
    
    public static void huntFishFood(Fish fish, Fish target){
        if(target==null){return;}
        
        if(Math.abs(fish.getX()-target.getSwimToLeft()[0].getWidth()/2 - target.getX()) > target.getSwimToLeft()[0].getWidth()/2){
            if(fish.getX() < target.getX()){
                fish.goRight();
            }else{
                fish.goLeft();
            }
        }

        if(Math.abs(fish.getY()+target.getSwimToLeft()[0].getHeight()/2 - target.getY()) > target.getSwimToLeft()[0].getHeight()/2){
            if(fish.getY() < target.getY()){
                fish.goDown();
            }else{
                fish.goUp();
            }       
        }else if(Math.abs(fish.getX()-target.getSwimToLeft()[0].getWidth()/2 - target.getX()) < 500){
            fish.charge();
        }
    }
    
    public static void hundSeaFood(Fish fish, SeaFood target){
        if(target==null){return;}
        
        if(Math.abs(fish.getX()+fish.getSwimToLeft()[0].getWidth()/2 - target.getX()) > fish.getSwimToLeft()[0].getWidth()/2){
            if(fish.getX() < target.getX()){
                fish.goRight();
            }else{
                fish.goLeft();
            }
        }
        if(Math.abs(fish.getY()+fish.getSwimToLeft()[0].getHeight()/2 - target.getY()) > fish.getSwimToLeft()[0].getHeight()/2){
            if(fish.getY() < target.getY()){
                fish.goDown();
            }else{
                fish.goUp();
            }       
        }
    }
    
    public static double distanceBetween(float x1, float y1, float x2, float y2){
        return Point2D.distance(x1, y1, x2, y2);
    }
    
}
