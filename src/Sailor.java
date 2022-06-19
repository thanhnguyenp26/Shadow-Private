import bagel.*;
import bagel.util.Point;

import java.util.ArrayList;

public class Sailor extends Character {
    private final static Image SAILOR_LEFT = new Image("res/sailor/sailorLeft.png");
    private final static Image SAILOR_RIGHT = new Image("res/sailor/sailorRight.png");
    private final static Image SAILOR_HIT_LEFT = new Image("res/sailor/sailorHitLeft.png");
    private final static Image SAILOR_HIT_RIGHT = new Image("res/sailor/sailorHitRight.png");

    private final static int MOVE_SIZE = 4;

    private final static int MAX_HEALTH_POINTS = 100;
    private final static int FONT_SIZE = 30;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private final static int LADDER_X = 990;
    private final static int LADDER_Y = 630;

    private final static double HEALTH_X = 10;
    private final static double HEALTH_Y = 25;

    private final static int ATTACK_TIME = 1000;
    private final static int COOL_DOWN = 2000;

    private int damagePoints = 15;

    private double oldX;
    private double oldY;
    private double count = 0;

    public Sailor(double startX, double startY){
        super(startX, startY, SAILOR_RIGHT, MAX_HEALTH_POINTS);
        COLOUR.setBlendColour(GREEN);
    }

    public int getDamagePoints() {
        return damagePoints;
    }

    public void setDamagePoints(int damagePoints) {
        this.damagePoints = damagePoints;
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, ArrayList<Block> blocks, Point topLeft, Point bottomRight, int level){

        // has attacked for start counting cool down tim
        if (attack() || inCoolDown(count)) {
            count++;
        }

        // gets appropriate images
        if (inCoolDown(count)){
            if (getCurrentImage() == SAILOR_HIT_LEFT) setCurrentImage(SAILOR_LEFT);
            else if (getCurrentImage() == SAILOR_HIT_RIGHT) setCurrentImage(SAILOR_RIGHT);
        }

        // out of cool down, start counting again
        if (outOfCoolDown(count)) {
            count = 0;
        }

        // store old coordinates every time the sailor moves
        if (input.isDown(Keys.UP)) {
            setOldPoints();
            move(0, -MOVE_SIZE);
        } else if (input.isDown(Keys.DOWN)) {
            setOldPoints();
            move(0, MOVE_SIZE);
        } else if (input.isDown(Keys.LEFT)) {
            setOldPoints();
            move(-MOVE_SIZE, 0);
            if (inNormalStage(count) || inCoolDown(count)) {
                setCurrentImage(SAILOR_LEFT);
            }
            else if (inAttackStage(count)) {
                setCurrentImage(SAILOR_HIT_LEFT);
            }
        } else if (input.isDown(Keys.RIGHT)) {
            setOldPoints();
            move(MOVE_SIZE, 0);
            if (inNormalStage(count) || inCoolDown(count)) {
                setCurrentImage(SAILOR_RIGHT);
            }
            else if (inAttackStage(count)) {
                setCurrentImage(SAILOR_HIT_RIGHT);
            }

        } else if (input.wasPressed(Keys.S) && inNormalStage(count)) {
            if (getCurrentImage() == SAILOR_LEFT) {
                setCurrentImage(SAILOR_HIT_LEFT);
            } else if (getCurrentImage() == SAILOR_RIGHT){
                setCurrentImage(SAILOR_HIT_RIGHT);
            }
        }

        Image currentImage = super.getCurrentImage();
        double x = super.getCurrentX();
        double y = super.getCurrentY();

        currentImage.drawFromTopLeft(x, y);
        checkCollisions(blocks, level);
        renderHealthPoints(HEALTH_X, HEALTH_Y, FONT);
        levelBoundsCollision (topLeft, bottomRight);
    }

    /**
     * Method that stores the old coordinates of the sailor
     */
    public void setOldPoints(){
        double x = super.getCurrentX();
        double y = super.getCurrentY();
        oldX = x;
        oldY = y;
    }

    /**
     * Method that check if it is attacking
     */
    public boolean attack(){
        if (getCurrentImage() == SAILOR_HIT_LEFT || getCurrentImage() == SAILOR_HIT_RIGHT) return true;
        return false;
    }

    /**
     * Method that check if in attack stage
     */
    public boolean inAttackStage(double count){
        if (count/FRAME_PER_MILLISEC <= ATTACK_TIME && count != 0) return true;
        return false;
    }

    /**
     * Method that check if in cool down
     */
    public boolean inCoolDown(double count){
        if ((count/FRAME_PER_MILLISEC > ATTACK_TIME) && (count/FRAME_PER_MILLISEC <= (ATTACK_TIME + COOL_DOWN))) {
            return true;
        }
        return false;
    }

    /**
     * Method that check if out of cool down
     */
    public boolean outOfCoolDown(double count){
        if ((count/FRAME_PER_MILLISEC > ATTACK_TIME) && (count/FRAME_PER_MILLISEC > (ATTACK_TIME + COOL_DOWN))) {
            return true;
        }
        return false;
    }

    /**
     * Method that check if in normal stage
     */
    public boolean inNormalStage(double count){
        if (count == 0){
            return true;
        }
        return false;
    }

    /**
     * Method that moves the sailor back to its previous position
     */
    @Override
    public void moveBack(){
        super.setX(oldX);
        super.setY(oldY);
    }

    /**
     * Method that checks if sailor has reached the ladder
     */
    public boolean hasWonLevel0(){
        double x = super.getCurrentX();
        double y = super.getCurrentY();
        return (x >= LADDER_X) && (y > LADDER_Y);
    }
}
