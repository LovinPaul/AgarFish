package Fish;

import Game.GraphicsPanel;

public class JaiFish extends Fish{
    
    public JaiFish(float x, float y, float speedX, float speedY){
        super.setX(x);
        super.setY(y);
        super.setSpeedX(speedX);
        super.setSpeedY(speedY);
        super.setAgilityX(10);
        super.setAgilityY(7);
        super.setSpeedRateX(1.25f);
        super.setSpeedRateY(0.75f);
        super.setMass(500);
        super.setRestFacingLeft(GraphicsPanel.getJaiFishRestLeft());
        super.setRestFacingRight(GraphicsPanel.getJaiFishRestRight());
        super.setSwimToLeft(GraphicsPanel.getJaiFishSwimLeft());
        super.setSwimToRight(GraphicsPanel.getJaiFishSwimRight());
        super.setFishBones(GraphicsPanel.getJaiFishBones());
    }
    
    public JaiFish() {
//        super.setX(x);
//        super.setY(y);
//        super.setSpeedX(speedX);
//        super.setSpeedY(speedY);
        super.setAgilityX(10);
        super.setAgilityY(7);
        super.setSpeedRateX(1);
        super.setSpeedRateY(0.5f);
        super.setMass(500);
        super.setRestFacingLeft(GraphicsPanel.getJaiFishRestLeft());
        super.setRestFacingRight(GraphicsPanel.getJaiFishRestRight());
        super.setSwimToLeft(GraphicsPanel.getJaiFishSwimLeft());
        super.setSwimToRight(GraphicsPanel.getJaiFishSwimRight());
        super.setFishBones(GraphicsPanel.getJaiFishBones());
        
    }
    
}
