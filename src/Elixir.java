import bagel.*;

public class Elixir extends Block {
    private final static Image ELIXIR_IMG = new Image("res/items/elixir.png");
    private final static Image ELIXIR_ICON = new Image("res/items/elixirIcon.png");
    private final static int INCREASE_MAX_HEALTH = 35;

    public Elixir(double x, double y) {
        super(x, y, ELIXIR_IMG);
    }

    public Image getIconImage() {
        return ELIXIR_ICON;
    }

    /**
     * Method that increases sailor's max health and refills health
     */
    public void increaseHealth(Sailor sailor) {

        // increase max health points by 35
        sailor.setMaxHealthPoint(sailor.getMaxHealthPoints() + INCREASE_MAX_HEALTH);

        // refill health
        sailor.setHealthPoints(sailor.getMaxHealthPoints());
    }
}
