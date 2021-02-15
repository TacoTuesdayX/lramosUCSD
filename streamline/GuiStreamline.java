/**
 * This file is the GUI implementation of the Streamline game. It provides    
 * a graphical window for the user to interact and play the game. 
 *
 * Name: Logan R. Ramos
 * Email: lramos@ucsd.edu
 */
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.util.Duration;

/**
 * The GuiStreamLine class contains the basic layout for the GUI of the 
 * streamline game. It contains specifications of color and size of the
 * graphical objects, and communicates between itself and the backend
 * portion of the game.
 */
public class GuiStreamline extends Application {
    static final double MAX_SCENE_WIDTH = 600;
    static final double MAX_SCENE_HEIGHT = 600;
    static final double PREFERRED_SQUARE_SIZE = 100;

    static final String TITLE = "CSE 8b Streamline GUI";
    static final String USAGE = 
        "Usage: \n" + 
        "> java GuiStreamline               - to start a game with default" +
        " size 6*5 and random obstacles\n" + 
        "> java GuiStreamline <filename>    - to start a game by reading g" +
        "ame state from the specified file\n" +
        "> java GuiStreamline <directory>   - to start a game by reading a" +
        "ll game states from files in\n" +
        "                                     the specified directory and " +
        "playing them in order\n";

    static final Color TRAIL_COLOR = Color.PALEVIOLETRED;
    static final Color GOAL_COLOR = Color.GOLD;
    static final Color OBSTACLE_COLOR = Color.TURQUOISE;
    static final Color ZAPPER_COLOR = Color.BLUE; 

    // Trail radius will be set to this fraction of the size of a board square.
    static final double TRAIL_RADIUS_FRACTION = 0.1;

    // Squares will be resized to this fraction of the size of a board square.
    static final double SQUARE_FRACTION = 0.8;
    static final double TRIANGLE_FRACTION = 0.8;

    Stage mainStage;
    Scene mainScene;
    Group levelGroup;                   // For obstacles and trails
    Group rootGroup;                    // Parent group for everything else
    Player playerRect;                  // GUI representation of the player
    RoundedSquare goalRect;             // GUI representation of the goal

    Shape[][] grid;                     // Same dimensions as the game board
    Shape[][] trailsGrid;               // Same dimensions as the game board

    Streamline game;                    // The current level
    ArrayList<Streamline> nextGames;    // Future levels

    MyKeyHandler myKeyHandler;          // for keyboard input

    /**
     * Gives the width of the board.
     *
     * @return the horrizontal size of the board
     */
    public int getBoardWidth() {
        return this.game.currentState.board[0].length;
    }


    /**
     * Gives the height of the board.
     *
     * @return the vertical size of the board
     */
    public int getBoardHeight() {
        return this.game.currentState.board.length;
    }


    /**
     * Calculates the square sizes of elements in the GUI based off of the
     * size of the canvas, and the grid size.
     *
     * @return the size of each square
     */
    public double getSquareSize() {
        double width = MAX_SCENE_WIDTH/game.currentState.board[0].length;
        double height = MAX_SCENE_HEIGHT/game.currentState.board.length;
        //return width if it's smallest
        if(width < height)
            return width;

        //return height if width is not smaller
        return height;
    }

    /**
     * Resets the entire grid and board. Compares between the backend
     * streamline and the GUI, and fixes any differences.
     */
    public void resetGrid() {
        //reference of the currentState's board
        char[][] board = game.currentState.board;
        //clears grid and trailsGrid
        this.grid = new Shape[board.length][board[0].length];
        this.trailsGrid = new Shape[board.length][board[0].length];
        //clears the groups
        rootGroup.getChildren().remove(goalRect);
        rootGroup.getChildren().remove(playerRect);
        levelGroup.getChildren().clear();

        for(int i = 0; i < board.length; i++){

            for(int j = 0; j < board[0].length; j++){
                //char value at each index
                char compare = board[i][j];
                //the current element
                Shape currentField;
                //coordinates of each index set
                double[] xyCoord = boardIdxToScenePos(j, i);

                //switch case comparing the element
                switch(compare){
                    //for down zapper
                    case GameState.DOWN_ZAP_CHAR: 
                        currentField = (new ZapperTriangle(Direction.DOWN,
                                    xyCoord[0], xyCoord[1], 
                                    getSquareSize()*TRIANGLE_FRACTION));
                        currentField.setFill(ZAPPER_COLOR);
                        grid[i][j] = currentField;
                        levelGroup.getChildren().add(currentField);
                        break;
                        //for right zapper
                    case GameState.RIGHT_ZAP_CHAR:
                        currentField = (new ZapperTriangle(Direction.RIGHT,
                                    xyCoord[0], xyCoord[1],
                                    getSquareSize()*TRIANGLE_FRACTION));
                        currentField.setFill(ZAPPER_COLOR);
                        grid[i][j] = currentField;
                        levelGroup.getChildren().add(currentField);
                        break;
                        //for up zapper
                    case GameState.UP_ZAP_CHAR:
                        currentField = (new ZapperTriangle(Direction.UP,
                                    xyCoord[0], xyCoord[1],
                                    getSquareSize()*TRIANGLE_FRACTION));
                        currentField.setFill(ZAPPER_COLOR);
                        grid[i][j] = currentField;
                        levelGroup.getChildren().add(currentField);
                        break;
                        //for left zapper
                    case GameState.LEFT_ZAP_CHAR:
                        currentField = (new ZapperTriangle(Direction.LEFT,
                                    xyCoord[0], xyCoord[1],
                                    getSquareSize()*TRIANGLE_FRACTION));
                        currentField.setFill(ZAPPER_COLOR);
                        grid[i][j] = currentField;
                        levelGroup.getChildren().add(currentField);
                        break;
                        //for obstacles
                    case GameState.OBSTACLE_CHAR:
                        currentField = new RoundedSquare(xyCoord[0], 
                                xyCoord[1], getSquareSize()*SQUARE_FRACTION);
                        currentField.setFill(OBSTACLE_COLOR);
                        grid[i][j] = currentField;
                        levelGroup.getChildren().add(currentField);
                        break;
                        //for player character
                    case GameState.PLAYER_CHAR:
                        grid[i][j] = this.playerRect;
                        rootGroup.getChildren().add(playerRect);
                        break;
                        //for goal character
                    case GameState.GOAL_CHAR:
                        grid[i][j] = this.goalRect;
                        rootGroup.getChildren().add(goalRect);
                        break;

                }
                //fill trailsGrid with transparent trail characters
                currentField = new Circle(xyCoord[0], xyCoord[1],
                        PREFERRED_SQUARE_SIZE*TRAIL_RADIUS_FRACTION, 
                        Color.TRANSPARENT);
                levelGroup.getChildren().add(currentField);
                trailsGrid[i][j] = currentField;
            }
        }
    }


    /**
     * Updates trail colors by converting from transparent to non transparent.
     * Only converts if the element is a trail_character.
     */
    public void updateTrailColors() {
        char[][] board = game.currentState.board;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                //if it's a trail_char, fill
                if(board[i][j] == GameState.TRAIL_CHAR)
                    trailsGrid[i][j].setFill(TRAIL_COLOR);
                //if not, then leave transparent
                else if(board[i][j] == GameState.SPACE_CHAR)
                    trailsGrid[i][j].setFill(null);
            }
        }
    }

    /** 
     * Coverts the given board column and row into scene coordinates.
     * Gives the center of the corresponding tile.
     * 
     * @param boardCol a board column to be converted to a scene x
     * @param boardRow a board row to be converted to a scene y
     * @return scene coordinates as length 2 array where index 0 is x
     */
    static final double MIDDLE_OFFSET = 0.5;
    public double[] boardIdxToScenePos (int boardCol, int boardRow) {
        double sceneX = ((boardCol + MIDDLE_OFFSET) * 
                (mainScene.getWidth() - 1)) / getBoardWidth();
        double sceneY = ((boardRow + MIDDLE_OFFSET) * 
                (mainScene.getHeight() - 1)) / getBoardHeight();
        return new double[]{sceneX, sceneY};
    }

    /**
     * This method is called every time the player moves. First checks if game
     * is won, handles that case if so. If not, then it updates player pos.
     *
     * @param fromCol original column location of the player
     * @param fromRow original row location of the player
     * @param toCol new column location of the player
     * @param toRow new column location of the player
     */
    public void onPlayerMoved(int fromCol, int fromRow, int toCol, int toRow)
    {
        if(game.currentState.levelPassed){
            rootGroup.getChildren().remove(playerRect);
            onLevelFinished();
        }
        else{
            resetGrid();
            updateTrailColors();

            //update the UI positions based off of movement.
            double[] playerPos = boardIdxToScenePos(
                    toCol, toRow
                    );
            playerRect.setSize(getSquareSize() * SQUARE_FRACTION);
            playerRect.setCenterX(playerPos[0]);
            playerRect.setCenterY(playerPos[1]);
        }
    }

    /**
     * This is a helper method that compares the inputed keyCode to a set of
     * possible key inputs. Determines what to do based off of the case.
     *
     * @param keyCode the code of the key pressed
     */
    void handleKeyCode(KeyCode keyCode)
    {
        double[] playerPos = boardIdxToScenePos(game.currentState.playerCol,
                game.currentState.playerRow);
        int oldX = game.currentState.playerCol;
        int oldY = game.currentState.playerRow;
        switch (keyCode) {
            case W:
                game.recordAndMove(Direction.UP);
                break;
            case UP:
                game.recordAndMove(Direction.UP);
                break;
            case D:
                game.recordAndMove(Direction.RIGHT);
                break;
            case RIGHT:
                game.recordAndMove(Direction.RIGHT);
                break;
            case S:
                game.recordAndMove(Direction.DOWN);
                break;
            case DOWN:
                game.recordAndMove(Direction.DOWN);
                break;
            case A:
                game.recordAndMove(Direction.LEFT);
                break;
            case LEFT:
                game.recordAndMove(Direction.LEFT);
                break;
            case U:
                System.out.println("Previous move has been undone");
                game.undo();
                break;
            case O:
                System.out.println("Saving to file...");
                game.saveToFile();
                break;
            case Q:
                System.out.println("Exiting Game...");
                System.exit(0);
            default:
                System.out.println("Possible commands:\n w - up\n " + 
                        "a - left\n s - down\n d - right\n u - undo\n " + 
                        "q - quit level");
                break;
        }

        onPlayerMoved(oldX, oldY, game.currentState.playerCol, 
                game.currentState.playerRow);
    }

    /**
     * This nested class handles keyboard input and calls handleKeyCode()
     */
    class MyKeyHandler implements EventHandler<KeyEvent>
    {
        public void handle(KeyEvent e)
        {
            handleKeyCode(e.getCode());
        }
    }


    /**
     * Determines what happens when when a new level is loaded. First resets
     * the grid, then updates player and goal position.
     */
    public void onLevelLoaded()
    {
        resetGrid();

        double squareSize = getSquareSize() * SQUARE_FRACTION;

        // Update the player position
        double[] playerPos = boardIdxToScenePos(
                game.currentState.playerCol, game.currentState.playerRow
                );
        playerRect.setSize(squareSize);
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);

        // Update the goal position
        double[] goalPos = boardIdxToScenePos(
                game.currentState.goalCol, game.currentState.goalRow
                );
        goalRect.setSize(squareSize);
        goalRect.setCenterX(goalPos[0]);
        goalRect.setCenterY(goalPos[1]);
    }

    /** 
     * Called when the player reaches the goal. Shows the winning animation
     * and loads the next level if there is one.
     */
    static final double SCALE_TIME = 175;  // milliseconds for scale animation
    static final double FADE_TIME = 250;   // milliseconds for fade animation
    static final double DOUBLE_MULTIPLIER = 2;
    public void onLevelFinished() {
        // Clone the goal rectangle and scale it up until it covers the screen

        // Clone the goal rectangle
        Rectangle animatedGoal = new Rectangle(
                goalRect.getX(),
                goalRect.getY(),
                goalRect.getWidth(),
                goalRect.getHeight()
                );
        animatedGoal.setFill(goalRect.getFill());

        // Scope for children
        {
            // Add the clone to the scene
            List<Node> children = rootGroup.getChildren();
            children.add(children.indexOf(goalRect), animatedGoal);
        }

        // Create the scale animation
        ScaleTransition st = new ScaleTransition(
                Duration.millis(SCALE_TIME), animatedGoal
                );
        st.setInterpolator(Interpolator.EASE_IN);

        // Scale enough to eventually cover the entire scene
        st.setByX(DOUBLE_MULTIPLIER * 
                mainScene.getWidth() / animatedGoal.getWidth());
        st.setByY(DOUBLE_MULTIPLIER * 
                mainScene.getHeight() / animatedGoal.getHeight());

        /*
         * This will be called after the scale animation finishes.
         * If there is no next level, quit. Otherwise switch to it and
         * fade out the animated cloned goal to reveal the new level.
         */
        st.setOnFinished(e1 -> {
                if(nextGames.size() == 0){
                System.exit(0);
                }
                rootGroup.getChildren().clear();
                rootGroup.getChildren().add(levelGroup);
                this.game = nextGames.remove(0); 
                /* TODO
                   check if there is no next game and if so, quit 
                   update the instances variables game and nextGames 
                   to switch to the next level
                 */


                // DO NOT MODIFY ANYTHING BELOW THIS LINE IN THIS METHOD

                // Update UI to the next level, but it won't be visible yet
                // because it's covered by the animated cloned goal
                onLevelLoaded();

                Rectangle fadeRect = new Rectangle(0, 0, 
                        mainScene.getWidth(), mainScene.getHeight());
                fadeRect.setFill(goalRect.getFill());

                // Scope for children
                {
                    // Add the fading rectangle to the scene
                    List<Node> children = rootGroup.getChildren();
                    children.add(children.indexOf(goalRect), fadeRect);
                }

                FadeTransition ft = new FadeTransition(
                        Duration.millis(FADE_TIME), fadeRect
                        );
                ft.setFromValue(1);
                ft.setToValue(0);

                // Remove the cloned goal after it's finished fading out
                ft.setOnFinished(e2 -> {
                        rootGroup.getChildren().remove(fadeRect);
                        });

                // Start the fade-out now
                ft.play();
        });

        // Start the scale animation
        st.play();
    }


    /** 
     * Performs file IO to populate game and nextGames using filenames from
     * command line arguments.
     */
    public void loadLevels() {
        game = null;
        nextGames = new ArrayList<Streamline>();

        List<String> args = getParameters().getRaw();
        if (args.size() == 0) {
            System.out.println("Starting a default-sized random game...");
            game = new Streamline();
            return;
        }

        // at this point args.length == 1

        File file = new File(args.get(0));
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting...", 
                    args.get(0));
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s...\n", 
                    args.get(0));
            game = new Streamline(args.get(0));
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        Arrays.sort(subfiles);
        for (int i=0; i<subfiles.length; i++) {
            File subfile = subfiles[i];

            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create a new game for each file, and add it to nextGames
            System.out.printf("Loading game %d/%d from file %s...\n",
                    i+1, subfiles.length, subfile.toString());
            nextGames.add(new Streamline(subfile.toString()));
        }

        // Switch to the first level
        game = nextGames.get(0);
        nextGames.remove(0);
    }


    /** 
     * The main entry point for all JavaFX Applications
     * Initializes instance variables, creates the scene, and sets up the UI
     * 
     * @param  primaryStage The window for this application
     * @throws Exception    [description]
     */
    public void start(Stage primaryStage) throws Exception {
        // Populate game and nextGames
        loadLevels();

        // Initialize the scene and our groups
        rootGroup = new Group();
        mainScene = new Scene(rootGroup, MAX_SCENE_WIDTH, MAX_SCENE_HEIGHT, 
                Color.GRAY);
        levelGroup = new Group();
        rootGroup.getChildren().add(levelGroup);

        //initialize and setFill for goalRect
        goalRect = new RoundedSquare(PREFERRED_SQUARE_SIZE * SQUARE_FRACTION);
        goalRect.setFill(GOAL_COLOR);

        //initialize player and resize
        playerRect = new Player();
        playerRect.setSize(PREFERRED_SQUARE_SIZE * SQUARE_FRACTION);

        onLevelLoaded();

        myKeyHandler = new MyKeyHandler();
        mainScene.setOnKeyPressed(myKeyHandler);
        // Make the scene visible
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }



    /** 
     * Execution begins here, but at this point we don't have a UI yet
     * The only thing to do is call launch() which will eventually result in
     * start() above being called.
     */
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.print(USAGE);
            return;
        }

        launch(args);
    }
}
