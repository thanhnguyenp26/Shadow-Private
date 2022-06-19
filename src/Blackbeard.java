import bagel.*;

import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

import java.util.ArrayList;

public class Blackbeard extends Character{
    private final static Image PIRATE_LEFT = new Image("res/blackbeard/blackbeardLeft.png");
    private final static Image PIRATE_RIGHT = new Image("res/blackbeard/blackbeardRight.png");
    private final static Image PIRATE_HIT_LEFT = new Image("res/blackbeard/blackbeardHitLeft.png");
    private final static Image PIRATE_HIT_RIGHT = new Image("res/blackbeard/blackbeardHitRight.png");

    private final static int MAX_HEALTH_POINTS = 90;
    private final static int FONT_SIZE = 15;
    protected final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private final static int INVINCIBLE_TIME = 1500;
    private final static int COOLDOWN = 1500;
    private final static int TARGET_BOX = 200;

    private double moveSize;
    private int random; // choose a random direction
    private double directionX;
    private double directionY;
    private double countInvincible = 0;
    private double countCoolDown = 0;
    private boolean attackedBySailor = false;
    private boolean fired = false;

    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private final static Image PROJECTILE = new Image("res/blackbeard/blackbeardProjectile.png");
    private final static double SPEED = 0.8;
    private final static double DAMAGE_POINTS = 20;

    public Blackbeard(double startX, double startY) {
        super(startX, startY, PIRATE_RIGHT, MAX_HEALTH_POINTS);
        random = (int)(Math.random() * 4 + 1);
        directionX = direction(random).x;
        directionY = direction(random).y;
        moveSize = Math.random() * (0.7 - 0.2) + 0.2;
        COLOUR.setBlendColour(GREEN);
    }

    public void update(ArrayList<Block> blocks, Point topLeft, Point bottomRight, Sailor sailor, int level) {

        // change to invincible state
        if (attackedBySailor) {
            countInvincible++;
            if (directionX > 0 || directionY != 0) {
                setCurrentImage(PIRATE_HIT_RIGHT);
            }
            else if (directionX < 0) {
                setCurrentImage(PIRATE_HIT_LEFT);
            }
        }

        // out of invincible
        if (!isInvincible(countInvincible)) {
            attackedBySailor = false;
            countInvincible = 0;
        }

        // draw correct image based on direction
        if ((directionX > 0 || directionY != 0) && !isInvincible(countInvincible)) {
            setCurrentImage(PIRATE_RIGHT);
        }
        else if (directionX < 0 && !isInvincible(countInvincible)) {
            setCurrentImage(PIRATE_LEFT);
        }

        move(directionX*moveSize, directionY*moveSize);

        Image currentImage = super.getCurrentImage();
        double x = super.getCurrentX();
        double y = super.getCurrentY();

        currentImage.drawFromTopLeft(x, y);
        checkCollisions(blocks, level);
        attackedBySailor(sailor);
        insideTargetBox(sailor);
        fireProjectile(sailor, topLeft, bottomRight);
        renderHealthPoints(getCurrentX(), getCurrentY() - 6, FONT);
        levelBoundsCollision (topLeft, bottomRight);
    }
    /**
     * Method that moves the pirate back to its previous position
     */
    @Override
    public void moveBack(){
        directionY *= -1;
        directionX *= -1;
    }

    /**
     * Method that choose a random direction
     */
    public Vector2 direction(int random) {
        if (random == 1) {
            // move up
            return new Vector2(0, 1);
        }
        else if (random == 2) {
            // move down
            return new Vector2(0, -1);
        }
        else if (random == 3) {
            // move left
            return new Vector2(-1, 0);
        }
        else {
            // move right
            return new Vector2(1, 0);
        }
    }

    /**
     * Method that checks for collisions between sailor and pirate
     */
    public void attackedBySailor(Sailor sailor){
        Rectangle pirateBox = characterBoundingBox(getCurrentX(), getCurrentY());
        if (sailor.attack() && attackedBySailor == false) {
            Rectangle sailorBox = characterBoundingBox(sailor.getCurrentX(), sailor.getCurrentY());
            if (pirateBox.intersects(sailorBox)) {
                attackedBySailor = true;
                setHealthPoints(getHealthPoints() - sailor.getDamagePoints());
                System.out.println("Sailor inflicts " + sailor.getDamagePoints() + " damage points on Blackbeard. " +
                        "Blackbeard's current health: " + (int)getHealthPoints() + "/" + (int)getMaxHealthPoints());
            }
        }
    }

    /**
     * Method that checks invincible state
     */
    public boolean isInvincible(double countInvincible) {
        if (countInvincible != 0 && countInvincible/FRAME_PER_MILLISEC <= INVINCIBLE_TIME) {
            return true;
        }
        return false;
    }

    /**
     * Method that fires projectile
     */
    public void fireProjectile(Sailor sailor, Point topLeft, Point bottomRight) {
        for (Projectile projectile : projectiles) {

            // disappear if touch level bounds
            if (projectile.levelBoundsCollision(topLeft, bottomRight, this)) {
                projectiles.remove(projectile);
                break;
            }

            // inflicts damage if collides sailor
            if (projectile.sailorCollision(sailor)) {
                sailor.setHealthPoints(sailor.getHealthPoints() - projectile.getDamagePoints());
                System.out.println("Projectile inflicts " + (int)projectile.getDamagePoints() + " damage points"
                        + " on Sailor. Sailor's current health: " + (int)sailor.getHealthPoints() + "/" +
                        (int)sailor.getMaxHealthPoints());
                projectiles.remove(projectile);
                break;
            }
            projectile.update();
        }

        if (fired) {

            // in cool down state after firing a projectile
            if (countCoolDown / FRAME_PER_MILLISEC <= COOLDOWN) {
                countCoolDown++;
            }

            //out of cool down state
            else {
                fired = false;
                countCoolDown = 0;
            }
        }
    }

    /**
     * Method that checks if sailor is inside target box
     */
    public void insideTargetBox(Sailor sailor) {
        double xTopLeft = getCentreX() - TARGET_BOX/2;
        double yTopLeft = getCentreY() - TARGET_BOX/2;
        Rectangle pirateBox = new Rectangle(new Point(xTopLeft, yTopLeft), TARGET_BOX, TARGET_BOX);
        Rectangle sailorBox = sailor.characterBoundingBox(sailor.getCurrentX(), sailor.getCurrentY());
        if (sailorBox.intersects(pirateBox)) {
            // projectiles ready
            if (countCoolDown == 0) {
                Point point = setProjectionDirection(sailor);
                projectiles.add(new Projectile(this.getCentreX(), this.getCentreY(),
                        point.x, point.y, SPEED, PROJECTILE, DAMAGE_POINTS));
                fired = true;
            }
        }
    }

    /**
     * Method that sets projectile's direction
     */
    public Point setProjectionDirection(Sailor sailor) {
        Point sailorPoint = new Point(sailor.getCentreX(), sailor.getCentreY());
        Point normalPiratePoint = new Point(this.getCentreX(), this.getCentreY());
        double xCoord;
        double yCoord;
        double length = sailorPoint.distanceTo(normalPiratePoint);
        xCoord = (sailorPoint.x - normalPiratePoint.x)/length;
        yCoord = (sailorPoint.y - normalPiratePoint.y)/length;
        return new Point(xCoord, yCoord);
    }
}
