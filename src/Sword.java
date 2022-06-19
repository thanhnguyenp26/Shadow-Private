import bagel.*;

public class Sword extends Block {
    private final static Image SWORD_IMG = new Image("res/items/sword.png");
    private final static Image SWORD_ICON = new Image("res/items/swordIcon.png");
    private final static int INCREASE_DAMAGE_POINTS = 15;

    public Sword(double x, double y) {
        super(x, y, SWORD_IMG);
    }

    public Image getIconImage() {
        return SWORD_ICON;
    }

    /**
     * Method that increases sailor's damage points by 15
     */
    public void increaseDamagePoints(Sailor sailor) {
        sailor.setDamagePoints(sailor.getDamagePoints() + INCREASE_DAMAGE_POINTS);
    }
}
