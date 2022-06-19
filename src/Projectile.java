import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Projectile {
    private double x;
    private double y;
    private double directionX;
    private double directionY;
    private double speed;
    private double rotation;
    private Image image;
    private double damagePoints;

    public Projectile(double x, double y, double directionX, double directionY,
                      double speed, Image image, double damagePoints) {
        this.x = x;
        this.y = y;
        this.directionX = directionX;
        this.directionY = directionY;
        this.speed = speed;
        this.image = image;
        this.damagePoints = damagePoints;
    }

    public double getDamagePoints() {
        return damagePoints;
    }

    public void update() {
        x += speed * directionX;
        y += speed * directionY;

        DrawOptions option = new DrawOptions();

        // checks for 4 quadrants
        if (directionX > 0) {
            rotation = Math.atan(directionY / directionX);
        }
        else {
            rotation = Math.atan(directionY/directionX) + Math.PI;
        }
        image.draw(x, y, option.setRotation(rotation));
    }

    /**
     * Method that checks for collisions with level bounds
     */
    public boolean levelBoundsCollision (Point topLeft, Point bottomRight, Character normalPirate){
        double trueDepth = normalPirate.getCurrentImage().getHeight();
        if (x <= topLeft.x || y <= topLeft.y + trueDepth || y >= bottomRight.y + trueDepth|| x >= bottomRight.x) {
            return true;
        }
        return false;
    }

    /**
     * Method that checks for collisions with sailor
     */
    public boolean sailorCollision (Sailor sailor){
        Rectangle sailorBox = sailor.characterBoundingBox(sailor.getCurrentX(), sailor.getCurrentY());
        if (sailorBox.intersects(new Point(x, y))) {
            return true;
        }
        return false;
    }

}
