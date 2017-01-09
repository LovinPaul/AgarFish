package Game;


import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;



public abstract class GraphicsPanel {
    
//    private static BufferedImage redFish;
//    private static BufferedImage scaryFish;
    private static BufferedImage background[];
//    private static BufferedImage fishBoneLeft;
    
//    private static BufferedImage dumbGreenFishRestLeft[];
//    private static BufferedImage dumbGreenFishRestRight[];
//    private static BufferedImage dumbGreenFishSwimLeft[];
//    private static BufferedImage dumbGreenFishSwimRight[];
    
    private static BufferedImage angelFishRestLeft[];
    private static BufferedImage angelFishRestRight[];
    private static BufferedImage angelFishSwimLeft[];
    private static BufferedImage angelFishSwimRight[];
    private static BufferedImage angelFishBones[];
    
    private static BufferedImage guppieFishRestLeft[];
    private static BufferedImage guppieFishRestRight[];
    private static BufferedImage guppieFishSwimLeft[];
    private static BufferedImage guppieFishSwimRight[];
    private static BufferedImage guppieFishBones[];
    
    private static BufferedImage jaiFishRestLeft[];
    private static BufferedImage jaiFishRestRight[];
    private static BufferedImage jaiFishSwimLeft[];
    private static BufferedImage jaiFishSwimRight[];
    private static BufferedImage jaiFishBones[];    
    
    
    
    //SeaFood
    private static BufferedImage smlSquid[];
    
    
    //Enviroment
    private static BufferedImage bubles[];    
    

    public static void init(){
        String err = null;
        
        background = new BufferedImage[3];
        
//        dumbGreenFishRestLeft = new BufferedImage[6];
//        dumbGreenFishRestRight = new BufferedImage[6];
//        dumbGreenFishSwimLeft = new BufferedImage[6];
//        dumbGreenFishSwimRight = new BufferedImage[6];
        
        angelFishRestLeft = new BufferedImage[6];
        angelFishRestRight = new BufferedImage[6];
        angelFishSwimLeft = new BufferedImage[6];
        angelFishSwimRight = new BufferedImage[6];
        angelFishBones = new BufferedImage[2];
        
        guppieFishRestLeft = new BufferedImage[6];
        guppieFishRestRight = new BufferedImage[6];
        guppieFishSwimLeft = new BufferedImage[9];
        guppieFishSwimRight = new BufferedImage[9];
        guppieFishBones = new BufferedImage[2];

        jaiFishRestLeft = new BufferedImage[6];
        jaiFishRestRight = new BufferedImage[6];
        jaiFishSwimLeft = new BufferedImage[9];
        jaiFishSwimRight = new BufferedImage[9];
        jaiFishBones = new BufferedImage[2];
        
        //
        smlSquid = new BufferedImage[9];
        
        bubles = new BufferedImage[1];
        
        try {
//            err = "RedFish";
//            redFish = ImageIO.read(new File("Data\\Fish\\redFish.png"));
//            err = "ScaryFish";
//            scaryFish = ImageIO.read(new File("Data\\Fish\\ScaryFish.png"));
            err = "Background\\Bubles\\bubble-64px.png";
            bubles[0] = ImageIO.read(new File("Data\\Background\\Bubles\\bubble-64px.png"));
//            err = "Fish\\bone\\fishBonel.png";
//            fishBoneLeft = ImageIO.read(new File("Data\\Fish\\bone\\fishBonel.png"));
            
            err = "angelFish\\bones\\0.png";
            angelFishBones[0] = ImageIO.read(new File("Data\\Fish\\angelFish\\bones\\0.png"));
            err = "angelFish\\bones\\1.png";
            angelFishBones[1] = ImageIO.read(new File("Data\\Fish\\angelFish\\bones\\1.png"));
            err = "guppieFish\\bones\\0.png";
            guppieFishBones[0] = ImageIO.read(new File("Data\\Fish\\guppieFish\\bones\\0.png"));
            err = "guppieFish\\bones\\1.png";
            guppieFishBones[1] = ImageIO.read(new File("Data\\Fish\\guppieFish\\bones\\1.png"));
            err = "jaiFish\\bones\\0.png";
            jaiFishBones[0] = ImageIO.read(new File("Data\\Fish\\jaiFish\\bones\\0.png"));
            err = "jaiFish\\bones\\1.png";
            jaiFishBones[1] = ImageIO.read(new File("Data\\Fish\\jaiFish\\bones\\1.png"));

            
            
            for(int i=0; i<=10; i++){
                
                if(i<3){
                    err = "Background\\background2.png";
                    background[i] = ImageIO.read(new File("Data\\Background\\background2" + i + ".png"));
                }
                
                if(i<6){
//                    err = "dumbGreenFishRestLeft";
//                    dumbGreenFishRestLeft[i] = ImageIO.read(new File("Data\\Fish\\dumbGreenFish\\Rest_facing_left\\" + i + ".png"));
//                    err = "dumbGreenFishRestRight";
//                    dumbGreenFishRestRight[i] = ImageIO.read(new File("Data\\Fish\\dumbGreenFish\\Rest_facing_right\\" + i + ".png"));
//                    err = "dumbGreenFishSwimLeft";
//                    dumbGreenFishSwimLeft[i] = ImageIO.read(new File("Data\\Fish\\dumbGreenFish\\Swim_to_left\\" + i + ".png"));
//                    err = "dumbGreenFishSwimRight";
//                    dumbGreenFishSwimRight[i] = ImageIO.read(new File("Data\\Fish\\dumbGreenFish\\Swim_to_right\\" + i + ".png"));

                    err = "angelFishRestLeft";
                    angelFishRestLeft[i] = ImageIO.read(new File("Data\\Fish\\angelFish\\Rest_facing_left\\" + i + ".png"));
                    err = "angelFishRestRight";
                    angelFishRestRight[i] = ImageIO.read(new File("Data\\Fish\\angelFish\\Rest_facing_right\\" + i + ".png"));
                    err = "angelFishSwimLeft";
                    angelFishSwimLeft[i] = ImageIO.read(new File("Data\\Fish\\angelFish\\Swim_to_left\\" + i + ".png"));
                    err = "angelFishSwimRight";
                    angelFishSwimRight[i] = ImageIO.read(new File("Data\\Fish\\angelFish\\Swim_to_right\\" + i + ".png"));
                
                
                    err = "guppieFishRestLeft";
                    guppieFishRestLeft[i] = ImageIO.read(new File("Data\\Fish\\guppieFish\\Rest_facing_left\\" + i + ".png"));
                    err = "guppieFishRestRight";
                    guppieFishRestRight[i] = ImageIO.read(new File("Data\\Fish\\guppieFish\\Rest_facing_right\\" + i + ".png"));
                
                    err = "jaiFishRestLeft";
                    jaiFishRestLeft[i] = ImageIO.read(new File("Data\\Fish\\jaiFish\\Rest_facing_left\\" + i + ".png"));
                    err = "jaiFishRestRight";
                    jaiFishRestRight[i] = ImageIO.read(new File("Data\\Fish\\jaiFish\\Rest_facing_right\\" + i + ".png"));
                                    
                }
                if(i<8){
                    err = "guppieFishSwimLeft";
                    guppieFishSwimLeft[i] = ImageIO.read(new File("Data\\Fish\\guppieFish\\Swim_to_left\\" + i + ".png"));
                    err = "guppieFishSwimRight";
                    guppieFishSwimRight[i] = ImageIO.read(new File("Data\\Fish\\guppieFish\\Swim_to_right\\" + i + ".png"));
                    
                    err = "jaiFishSwimLeft";
                    jaiFishSwimLeft[i] = ImageIO.read(new File("Data\\Fish\\jaiFish\\Swim_to_left\\" + i + ".png"));
                    err = "jaiFishSwimRight";
                    jaiFishSwimRight[i] = ImageIO.read(new File("Data\\Fish\\jaiFish\\Swim_to_right\\" + i + ".png"));
                    
                    smlSquid[i] = ImageIO.read(new File("Data\\Fish\\plancton\\smlSquid\\" + i + ".png"));
                
                }
                
            }
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage() + "\nImage loading Error : " + err);
        }
        
    }
    
//    public static BufferedImage getRedFish(){
//        return redFish;
//    }
//    public static BufferedImage getScaryFish(){
//        return scaryFish;
//    }
//    public static BufferedImage getFishBone(){
//        return fishBoneLeft;
//    }    
    
    
//    public static BufferedImage[] getDumbGreenFishRestLeft(){
//        return dumbGreenFishRestLeft;
//    }
//    public static BufferedImage[] getDumbGreenFishRestRight(){
//        return dumbGreenFishRestRight;
//    }
//    public static BufferedImage[] getDumbGreenFishSwimLeft(){
//        return dumbGreenFishSwimLeft;
//    }
//    public static BufferedImage[] getDumbGreenFishSwimRight(){
//        return dumbGreenFishSwimRight;
//    }
    
    public static BufferedImage[] getAngelFishRestLeft(){
        return angelFishRestLeft;
    }
    public static BufferedImage[] getAngelFishRestRight(){
        return angelFishRestRight;
    }
    public static BufferedImage[] getAngelFishSwimLeft(){
        return angelFishSwimLeft;
    }
    public static BufferedImage[] getAngelFishSwimRight(){
        return angelFishSwimRight;
    }
    public static BufferedImage[] getAngelFishBones(){
        return angelFishBones;
    }
    
    public static BufferedImage[] getGuppieFishRestLeft(){
        return guppieFishRestLeft;
    }
    public static BufferedImage[] getGuppieFishRestRight(){
        return guppieFishRestRight;
    }
    public static BufferedImage[] getGuppieFishSwimLeft(){
        return guppieFishSwimLeft;
    }
    public static BufferedImage[] getGuppieFishSwimRight(){
        return guppieFishSwimRight;
    }
    public static BufferedImage[] getGuppieFishBones(){
        return guppieFishBones;
    }
    
    public static BufferedImage[] getJaiFishRestLeft(){
        return jaiFishRestLeft;
    }
    public static BufferedImage[] getJaiFishRestRight(){
        return jaiFishRestRight;
    }
    public static BufferedImage[] getJaiFishSwimLeft(){
        return jaiFishSwimLeft;
    }
    public static BufferedImage[] getJaiFishSwimRight(){
        return jaiFishSwimRight;
    }
    public static BufferedImage[] getJaiFishBones(){
        return jaiFishBones;
    }
    
    
    public static BufferedImage[] getSmlSquid(){
        return smlSquid;
    }
    
    
    public static BufferedImage[] getBackground(){
        return background;
    }
    public static BufferedImage[] getBubles(){
        return bubles;
    }    
    
    
}
