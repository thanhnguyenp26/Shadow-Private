import bagel.*;
import bagel.util.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 1, 2022
 *
 * Please fill your name below
 * @author Thanh Nguyen Pham
 */

/**
 * All the codes related to Project 1 are from the solution
 */

public class ShadowPirate extends AbstractGame{
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "ShadowPirate";
    private final static double FRAME_PER_MILLISEC = 60.0/1000;
    private final static int TRANSITION_TIME = 3000;

    private final static String WORLD_FILE0 = "res/level0.csv";
    private final static String WORLD_FILE1 = "res/level1.csv";
    private final static String START_MESSAGE = "PRESS SPACE TO START";
    private final static String ATTACK_MESSAGE = "PRESS S TO ATTACK";
    private final static String INSTRUCTION_MESSAGE = "USE ARROW KEYS TO FIND LADDER";
    private final static String END_MESSAGE = "GAME OVER";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";
    private final static String LEVEL_COMPLETE = "LEVEL COMPLETE!";
    private final static String TREASURE_MESSAGE = "FIND THE TREASURE";

    private final Image BLOCK = new Image("res/block.png");
    private final Image BOMB = new Image("res/bomb.png");
    private final Image TREASURE = new Image("res/treasure.png");

    private final static int INSTRUCTION_OFFSET = 70;
    private final static int FONT_SIZE = 55;
    private final static int FONT_Y_POS = 402;
    private final Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private Level level0 = new Level(0);
    private Level level1 = new Level(1);
    private Level level = level0;

    private static Point TOP_LEFT;
    private static Point BOTTOM_RIGHT;


    private ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<Block> inventories = new ArrayList<>();
    private ArrayList<Image> itemIcons = new ArrayList<>();
    private ArrayList<NormalPirate> pirates = new ArrayList<>();
    private Sailor sailor;
    private Blackbeard blackbeard;
    private Block treasure;

    private boolean gameOn;
    private boolean gameEnd;
    private boolean gameWin;
    private int levelNum;
    private double transitionTime = 0;

    public ShadowPirate(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        gameWin = false;
        gameEnd = false;
        gameOn = false;
    }

    /**
     * Entry point for program
     */
    public static void main(String[] args){
        ShadowPirate game = new ShadowPirate();
        game.run();
    }

    /**
     * Method used to read file and create objects
     */
    private void readCSV(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            String line;
            if ((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Sailor")){
                    sailor = new Sailor(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
            }

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Block") && levelNum == 0){
                    blocks.add(new Block(Double.parseDouble(sections[1]), Double.parseDouble(sections[2]),
                            BLOCK));
                }
                if (sections[0].equals("Block") && levelNum == 1){
                    blocks.add(new Block(Double.parseDouble(sections[1]), Double.parseDouble(sections[2]),
                            BOMB));
                }
                else if (sections[0].equals("TopLeft")){
                    TOP_LEFT = new Point(Double.parseDouble(sections[1]), Double.parseDouble(sections[2]));
                }
                else if (sections[0].equals("BottomRight")){
                    BOTTOM_RIGHT = new Point(Double.parseDouble(sections[1]), Double.parseDouble(sections[2]));
                }
                else if (sections[0].equals("Pirate")){
                    pirates.add(new NormalPirate(Double.parseDouble(sections[1]), Double.parseDouble(sections[2])));
                }
                else if (sections[0].equals("Blackbeard")) {
                    blackbeard = new Blackbeard(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
                else if (sections[0].equals("Treasure")) {
                    treasure = new Block(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]), TREASURE);
                }
                else if (sections[0].equals("Sword")) {
                    inventories.add(new Sword(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                else if (sections[0].equals("Elixir")) {
                    inventories.add(new Elixir(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                else if (sections[0].equals("Potion")) {
                    inventories.add(new Potion(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Performs a state update. Pressing escape key,
     * allows game to exit.
     */
    @Override
    public void update(Input input){

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        if (!gameOn){

            // wait for 3 seconds before drawing screen for level 1
            if (levelNum == 1 && transitionTime/FRAME_PER_MILLISEC <= TRANSITION_TIME) {
                FONT.drawString(LEVEL_COMPLETE, (Window.getWidth()/2.0 - (FONT.getWidth(LEVEL_COMPLETE)/2.0)),
                        FONT_Y_POS);
                transitionTime++;
            }
            else {
                drawStartScreen(input);
            }
        }

        if (gameEnd){
            gameEnd = true;
            drawEndScreen(END_MESSAGE);
        }

        if (gameWin){
            drawEndScreen(WIN_MESSAGE);
        }

        // when game is running
        if (gameOn && !gameEnd && !gameWin){

            level.update(input, sailor, blackbeard, blocks, pirates, levelNum, TOP_LEFT, BOTTOM_RIGHT, treasure,
                    inventories, itemIcons);

            if (sailor.isDead()){
                gameEnd = true;
            }

            // win level 0, go to level 1
            if (levelNum == 0 && sailor.hasWonLevel0()){
                level = level1;
                levelNum = 1;
                gameOn = false;
            }

            // found treasure, win game
            if (levelNum == 1 && treasure!= null && treasure.collideSailor(sailor)) {
                gameWin = true;
            }
        }
    }

    /**
     * Method used to draw the start screen instructions
     */
    private void drawStartScreen(Input input){
        FONT.drawString(START_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(START_MESSAGE)/2.0)),
                FONT_Y_POS);
        FONT.drawString(ATTACK_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(ATTACK_MESSAGE)/2.0)),
                (FONT_Y_POS + INSTRUCTION_OFFSET));
        if (levelNum == 0) {
            FONT.drawString(INSTRUCTION_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(INSTRUCTION_MESSAGE)/2.0)),
                    (FONT_Y_POS + 2*INSTRUCTION_OFFSET));
        }
        if (levelNum == 1) {
            FONT.drawString(TREASURE_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(TREASURE_MESSAGE)/2.0)),
                    (FONT_Y_POS + 2*INSTRUCTION_OFFSET));
        }
        if (input.wasPressed(Keys.SPACE)){
            if (levelNum == 0) {
                readCSV(WORLD_FILE0);
            }
            else if (levelNum == 1){
                resetLevel();
                readCSV(WORLD_FILE1);
            }
            gameOn = true;
        }
    }

    /**
     * Method used to draw end screen messages
     */
    private void drawEndScreen(String message){
        FONT.drawString(message, (Window.getWidth()/2.0 - (FONT.getWidth(message)/2.0)), FONT_Y_POS);
    }

    /**
     * Method used to reset a level
     */
    private void resetLevel(){
        pirates = new ArrayList<>();
        blocks = new ArrayList<>();
        sailor.setHealthPoints(sailor.getMaxHealthPoints());
    }
}
