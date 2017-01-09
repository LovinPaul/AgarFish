package SeaFood;

import Game.GraphicsPanel;

public class SmlSquid extends SeaFood {
    
        public SmlSquid(float x, float y, float speedX, float speedY){
        super.setX(x);
        super.setY(y);
        super.setSpeedX(speedX);
        super.setSpeedY(speedY);
        super.setAgilityX(3);
        super.setAgilityY(3);
        super.setSpeedRateX(1.0f);
        super.setSpeedRateY(1.0f);
        super.setMass(50);
        super.setImage(GraphicsPanel.getSmlSquid());
    }
    
}
