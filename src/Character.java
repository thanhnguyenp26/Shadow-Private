import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;

public abstract class Character {
    protected final static double FRAME_PER_MILLISEC = 60.0/1000;

    protected final static int ORANGE_BOUNDARY = 65;
    protected final static int RED_BOUNDARY = 35;
    protected final DrawOptions COLOUR = new DrawOptions();
    protected final static Colour GREEN = new Colour(0, 0.8, 0.2);
    protected final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    protected final static Colour RED = new Colour(1, 0, 0);

    private final static Image EXPLOSION = new Image("res/explosion.png");

    private double x;
    private double y;
    private double healthPoints;
    private double maxHealthPoint;
    private Image currentImage;

    public Character(double x, double y, Image currentImage, int maxHealthPoint) {
        this.x = x;
        this.y = y;
        this.currentImage = currentImage;
        this.maxHealthPoint = maxHealthPoint;
        this.healthPoints = maxHealthPoint;
    }

    public double getCurrentX() {
        return x;
    }

    public double getCentreX() {
        return x + currentImage.getWidth()/2;
    }

    public double getCurrentY() {
        return y;
    }

    public double getCentreY() {
        return y + currentImage.getHeight()/2;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public double getHealthPoints() {
        return healthPoints;
    }

    public double getMaxHealthPoints() {
        return maxHealthPoint;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

    public void setHealthPoints(double healthPoints) {
        this.healthPoints = healthPoints;
    }

    public void setMaxHealthPoint(double maxHealth) {
        this.maxHealthPoint = maxHealth;
    }

    /**
     * Method that checks for collisions between character and blocks
     */
    public void checkCollisions(ArrayList<Block> blocks, int level){

        Rectangle characterBox = characterBoundingBox(x, y);

        for (Block current : blocks) {
            Rectangle blockBox = current.getBoundingBox();
            if (characterBox.intersects(blockBox)) {

                // bomb explosion for sailor in level 1
                if (this instanceof Sailor && level == 1) {
                    current.setBlockImage(EXPLOSION);

                    // only inflicts damage once
                    if(!current.isInExplosion()) {
                        this.setHealthPoints(this.getHealthPoints() - current.getDamagePoints());
                        System.out.println("Bomb inflicts " + current.getDamagePoints() + " damage points on Sailor. "
                                + "Sailor's current health: " + (int)getHealthPoints() + "/" +
                                (int)getMaxHealthPoints());
                    }
                    current.setInExplosion(true);
                }
                moveBack();
                break;
            }
        }
    }

    /**
     * Method that creates bounding box for character
     */
    public Rectangle characterBoundingBox(double x, double y) {
        double centreX = x + currentImage.getWidth() / 2;
        double centreY = y + currentImage.getHeight() / 2;
        Rectangle characterBox = currentImage.getBoundingBoxAt(new Point(centreX, centreY));
        return characterBox;
    }

    /**
     * Method that checks for collisions with level bounds
     */
    public void levelBoundsCollision (Point topLeft, Point bottomRight){
        if (x <= topLeft.x || y <= topLeft.y || y >= bottomRight.y || x >= bottomRight.x) {
            moveBack();
        }
    }

    /**
     * Method that moves character back to its previous position
     */
    public abstract void moveBack();

    /**
     * Method that moves the character given the direction
     */
    public void move(double xMove, double yMove){
        x += xMove;
        y += yMove;
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    public void renderHealthPoints(double healthX, double healthY, Font font){
        double percentageHP = ((double) healthPoints/maxHealthPoint) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        else {
            COLOUR.setBlendColour(GREEN);
        }
        font.drawString(Math.round(percentageHP) + "%", healthX, healthY, COLOUR);
    }

    /**
     * Method that checks if sailor's health is <= 0
     */
    public boolean isDead(){
        return healthPoints <= 0;
    }
}

