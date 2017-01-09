package Fish;

import Game.GraphicsPanel;

public class GuppieFish extends Fish{
    private static final long serialVersionUID = 1112L;
    public GuppieFish(float x, float y, float speedX, float speedY){
        super.setX(x);
        super.setY(y);
        super.setSpeedX(speedX);
        super.setSpeedY(speedY);
        super.setAgilityX(10);
        super.setAgilityY(7);
        super.setSpeedRateX(1.25f);
        super.setSpeedRateY(0.75f);
        super.setMass(750);
//        super.setRestFacingLeft(GraphicsPanel.getGuppieFishRestLeft());
//        super.setRestFacingRight(GraphicsPanel.getGuppieFishRestRight());
//        super.setSwimToLeft(GraphicsPanel.getGuppieFishSwimLeft());
//        super.setSwimToRight(GraphicsPanel.getGuppieFishSwimRight());
//        super.setFishBones(GraphicsPanel.getGuppieFishBones());
    }
    
    public GuppieFish() {
//        super.setX(x);
//        super.setY(y);
//        super.setSpeedX(speedX);
//        super.setSpeedY(speedY);
        super.setAgilityX(10);
        super.setAgilityY(7);
        super.setSpeedRateX(1);
        super.setSpeedRateY(0.5f);
        super.setMass(750);
//        super.setRestFacingLeft(GraphicsPanel.getGuppieFishRestLeft());
//        super.setRestFacingRight(GraphicsPanel.getGuppieFishRestRight());
//        super.setSwimToLeft(GraphicsPanel.getGuppieFishSwimLeft());
//        super.setSwimToRight(GraphicsPanel.getGuppieFishSwimRight());
//        super.setFishBones(GraphicsPanel.getGuppieFishBones());
        
    }
    
}
