
package Fish;

import Game.GraphicsPanel;

public class AngelFish extends Fish{    
    private static final long serialVersionUID = 1111L;
    
    public AngelFish(float x, float y, float speedX, float speedY){
        super.setX(x);
        super.setY(y);
        super.setSpeedX(speedX);
        super.setSpeedY(speedY);
        super.setAgilityX(7);
        super.setAgilityY(5);
        super.setSpeedRateX(1.25f);
        super.setSpeedRateY(0.75f);
        super.setMass(1000);
//        super.setHeight(122);
//        super.setWidth(98);
//        super.setRestFacingLeft(GraphicsPanel.getAngelFishRestLeft());
//        super.setRestFacingRight(GraphicsPanel.getAngelFishRestRight());
//        super.setSwimToLeft(GraphicsPanel.getAngelFishSwimLeft());
//        super.setSwimToRight(GraphicsPanel.getAngelFishSwimRight());
//        super.setFishBones(GraphicsPanel.getAngelFishBones());
    }
    
    public AngelFish() {
//        super.setX(x);
//        super.setY(y);
//        super.setSpeedX(speedX);
//        super.setSpeedY(speedY);
        super.setAgilityX(7);
        super.setAgilityY(5);
        super.setSpeedRateX(1);
        super.setSpeedRateY(0.5f);
        super.setMass(1000);
//        super.setHeight(122);
//        super.setWidth(98);
//        super.setRestFacingLeft(GraphicsPanel.getAngelFishRestLeft());
//        super.setRestFacingRight(GraphicsPanel.getAngelFishRestRight());
//        super.setSwimToLeft(GraphicsPanel.getAngelFishSwimLeft());
//        super.setSwimToRight(GraphicsPanel.getAngelFishSwimRight());
//        super.setFishBones(GraphicsPanel.getAngelFishBones());
    }

}
