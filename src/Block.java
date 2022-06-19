import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Block{
    private final static int DAMAGE_POINTS = 10;
    private final static int EXPLOSION_TIME = 30;
    private final double x;
    private final double y;
    private Image blockImage;

    private double countExplosion = 0;
    private boolean inExplosion = false;

    public Block(double startX, double startY, Image blockImage){
        this.x = startX;
        this.y = startY;
        this.blockImage = blockImage;
    }

    public int getDamagePoints() {
        return DAMAGE_POINTS;
    }

    public double getCountExplosion() {
        return countExplosion;
    }

    public int getExplosionTime() {
        return EXPLOSION_TIME;
    }

    public boolean isInExplosion() {
        return inExplosion;
    }

    public void setInExplosion(boolean inExplosion) {
        this.inExplosion = inExplosion;
    }

    public void setBlockImage(Image blockImage) {
        this.blockImage = blockImage;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        setCountExplosion();
        blockImage.drawFromTopLeft(x, y);
    }

    /**
     * Method that gets rectangle box around block
     */
    public Rectangle getBoundingBox(){
        double centreX = x + blockImage.getWidth()/2;
        double centreY = y + blockImage.getHeight()/2;
        return blockImage.getBoundingBoxAt(new Point(centreX, centreY));
    }

    /**
     * Method that checks if box collides with sailor
     */
    public boolean collideSailor(Sailor sailor) {
        Rectangle blockBox = getBoundingBox();
        Rectangle sailorBox = sailor.characterBoundingBox(sailor.getCurrentX(), sailor.getCurrentY());
        return (blockBox.intersects(sailorBox));
    }

    /**
     * Method that counts explosion time
     */
    public void setCountExplosion() {
        if(inExplosion) {
            countExplosion++;
            if (countExplosion == EXPLOSION_TIME) {
                inExplosion = false;
            }
        }
    }
}