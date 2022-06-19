import bagel.*;
import bagel.util.Point;
import java.util.ArrayList;

public class Level {
    private final Image BACKGROUND_IMAGE0 = new Image("res/background0.png");
    private final Image BACKGROUND_IMAGE1 = new Image("res/background1.png");

    private int level;

    public Level(int level) {
        this.level = level;
    }

    public void update(Input input, Sailor sailor, Blackbeard blackbeard, ArrayList<Block> blocks,
                       ArrayList<NormalPirate> pirates, int level, Point topLeft, Point bottomRight,
                       Block treasure, ArrayList<Block> inventories, ArrayList<Image> itemIcons) {

        if (level == 0) {
            BACKGROUND_IMAGE0.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
        } else {
            BACKGROUND_IMAGE1.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

            // update blackbeard
            if (!blackbeard.isDead()) {
                blackbeard.update(blocks, topLeft, bottomRight, sailor, level);
            }

            // update treasure
            treasure.update();

            // update inventories
            for (Block item : inventories) {
                item.update();
            }

            // check for picking up inventories
            pickupInventories(inventories, itemIcons, sailor);

            // create location to put inventories icon
            int itemLocationX = 10;
            int itemLocationY = 40;
            for (Image itemIcon : itemIcons) {
                itemIcon.drawFromTopLeft(itemLocationX, itemLocationY);
                itemLocationY += itemIcon.getHeight();
            }
        }
        // update sailor
        sailor.update(input, blocks, topLeft, bottomRight, level);

        // update blocks
        for (Block block : blocks) {
            if (level == 1) {
                if (block.getCountExplosion() == block.getExplosionTime()) {
                    blocks.remove(block);
                    break;
                }
            }
            block.update();
        }

        // update normal pirates
        for (NormalPirate normalPirate : pirates) {
            if (normalPirate.isDead()) {
                pirates.remove(normalPirate);
                break;
            } else {
                normalPirate.update(blocks, topLeft, bottomRight, sailor, level);
            }
        }
    }

    /**
     * Method that checks for picking up inventories
     */
    public void pickupInventories(ArrayList<Block> inventories, ArrayList<Image> itemIcons, Sailor sailor) {
        for (Block item : inventories) {
            if (item instanceof Potion) {
                Potion potion = (Potion) item;
                if (potion.collideSailor(sailor)) {
                    itemIcons.add(potion.getIconImage());
                    potion.increaseCurrentHealth(sailor);
                    System.out.println("Sailor finds Potion. Sailor's current health: " +
                            (int) sailor.getHealthPoints() + "/"
                            + (int) sailor.getMaxHealthPoints());
                    inventories.remove(potion);
                    break;
                }
            } else if (item instanceof Elixir) {
                Elixir elixir = (Elixir) item;
                if (elixir.collideSailor(sailor)) {
                    itemIcons.add(elixir.getIconImage());
                    elixir.increaseHealth(sailor);
                    System.out.println("Sailor finds Elixir. Sailor's current health: " +
                            (int) sailor.getHealthPoints() + "/" + (int) sailor.getMaxHealthPoints());
                    inventories.remove(elixir);
                    break;
                }
            } else if (item instanceof Sword) {
                Sword sword = (Sword) item;
                if (sword.collideSailor(sailor)) {
                    itemIcons.add(sword.getIconImage());
                    sword.increaseDamagePoints(sailor);
                    System.out.println("Sailor finds Sword. Sailor's damage points increased to " +
                            sailor.getDamagePoints());
                    inventories.remove(sword);
                    break;
                }
            }
        }
    }
}
