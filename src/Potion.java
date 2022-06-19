import bagel.*;

public class Potion extends Block {
    private final static Image POTION_IMG = new Image("res/items/potion.png");
    private final static Image POTION_ICON = new Image("res/items/potionIcon.png");
    private final int INCREASE_HEALTH_POINTS = 25;

    public Potion(double x, double y) {
        super(x, y, POTION_IMG);
    }

    public Image getIconImage() {
        return POTION_ICON;
    }

    /**
     * Method that increase sailor's health by 25
     */
    public void increaseCurrentHealth(Sailor sailor) {

        // sum normally if sailor's current health points have not reached max health
        if ((sailor.getHealthPoints() + INCREASE_HEALTH_POINTS) < sailor.getMaxHealthPoints()) {
            sailor.setHealthPoints(sailor.getHealthPoints() + INCREASE_HEALTH_POINTS);
        }

        // can not reach max health points
        else {
            sailor.setHealthPoints(sailor.getMaxHealthPoints());
        }
    }
}
