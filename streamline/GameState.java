
/**
 * The GameState.java file lays out some of the visual and mechanical
 * components of the game. It creates an environment, as well as movement
 * functions that interact with the components of the environment. The File
 * at this stage is output ONLY, and can only take written arguments and calls.
 *
 * Name: Logan R. Ramos
 * Email: lramos@ucsd.edu
 *
 * Documentation on overriding the object equals() was provided by 
 * GeeksforGeeks
 * Source: https://www.geeksforgeeks.org/overriding-equals-method-in-java/
 */

import java.util.*;

/**
 * GameState class has methods to instantiate new GameStates, add randomized
 * obstacles and zappers, provide 4 different movements, and give an output
 * of the current GameState to the console
 */
public class GameState
{
    /* Provided constants */
    final static char PLAYER_CHAR = '@';
    final static char GOAL_CHAR = 'G';
    final static char SPACE_CHAR = ' ';
    final static char TRAIL_CHAR = '+';
    final static char OBSTACLE_CHAR = 'O';
    final static char DOWN_ZAP_CHAR = 'v'; 
    final static char UP_ZAP_CHAR = '^'; 
    final static char LEFT_ZAP_CHAR = '<'; 
    final static char RIGHT_ZAP_CHAR = '>'; 
    final static char NEWLINE_CHAR = '\n';
    final static char HORIZONTAL_BORDER_CHAR = '-';
    final static char SIDE_BORDER_CHAR = '|';

    /* Add your `final static` constants here
     *
     * Added variable:
     * Name: zappers
     * Type: char[]
     * Purpose: provide indexes of each type of zapper for short-hand
     */
    final static int totalDirections = 4;
    final static char[] zappers = {LEFT_ZAP_CHAR, DOWN_ZAP_CHAR, RIGHT_ZAP_CHAR
        , UP_ZAP_CHAR};
    final static int leftIndex = 0;
    final static int downIndex = 1;
    final static int rightIndex = 2;
    final static int upIndex = 3;
    //    final static char[][] rotated;
    /* Instance variables, do not add any */
    char[][] board; // 2-D array containing the tiles of the board, each tile has a value
    int playerRow; // the row of the playerin the board (0-indexing), access using board[playerRow][playerCol]
    int playerCol; // the column of the player in the board (0-indexing)
    int goalRow; // the row of the goal in the board (0-indexing)
    int goalCol; // the colum of the goal in the board (0-indexing)
    boolean levelPassed; // denotes whether the level is passed

    /**
     * Constructor for GameState objects. Instantiates each instance variable.
     *
     * @param height the height of the board
     * @param width the width of the board
     * @param playerRow the starting row location of player
     * @param playerCol the starting collumn location of player
     * @param goalRow the row of the goal
     * @param goalCol the collumn of the goal
     */
    public GameState(int height, int width, int playerRow, int playerCol, 
            int goalRow, int goalCol)
    {
        this.board = new char[height][width];
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        this.levelPassed = false;

        for(int i = 0; i < height; i++){
            for(int idx = 0; idx < width; idx++){
                this.board[i][idx] = SPACE_CHAR;
            }
        }

        this.board[playerRow][playerCol] = PLAYER_CHAR;
        this.board[goalRow][goalCol] = GOAL_CHAR;
    }

    /**
     * Constructor that makes a deep copy of another instance of GameState,
     * utilizes constructor chaining.
     *
     * @param other the instance to be copied
     */
    public GameState(GameState other)
    {
        //constructor chaining
        this(other.board.length, other.board[0].length,
                other.playerRow, other.playerCol, other.goalRow, other.goalCol);
        
        this.levelPassed = other.levelPassed;
        for(int i = 0; i < other.board.length; i++){
            this.board[i] = 
                Arrays.copyOf(other.board[i], other.board[i].length);
        }
    }

    /**
     * This method counts the total number of empty tiles within the GameState
     * board.
     *
     * @return the number of empty tiles in GameState board
     */
    int countEmptyTiles()
    {
        int total = 0;
        for(char[] arr : this.board){
            for(char val: arr){
                if(val == SPACE_CHAR)
                    total++;
            }
        }
        return total; 
    }

    /**
     * Sets random indexs of the 2D array (board) to a OBSTACLE_CHAR
     * which is an obstacle in the game.
     *
     * @param count the number of desired obstacles to be added
     */
    void addRandomObstacles(int count)
    {
        if(count > this.countEmptyTiles())
            return;

        Random randomizer = new Random();
        int row;
        int col;

        for(int i = 0; i < count; i++){
            row = randomizer.nextInt(board.length);
            col = randomizer.nextInt(board[0].length);

            if(board[row][col] == SPACE_CHAR){
                board[row][col] = OBSTACLE_CHAR;
            }
            else {
                i--;
            }
        }
    }
    /**
     * Sets random index of the 2D array (board) to a *_ZAP_CHAR
     * randomizes the type of zapper. 
     *
     * @param count number of random zappers to be added
     */
    void addRandomZappers(int count)
    {   
        //randomizer which will generate random int numbers for the 3 below
        Random randomizer = new Random();
        int row;
        int col;
        int zapperType;

        //return nothing if not enough slots to support 'count' many zappers
        if((count > this.countEmptyTiles()) || this.countEmptyTiles() < 0)
            return;

        for(int i = 0; i < count; i++){
            row = randomizer.nextInt(board.length);
            col = randomizer.nextInt(board[0].length);
            zapperType = randomizer.nextInt(totalDirections);
            if(board[row][col] == SPACE_CHAR){
                //switch case to determine shorthand which zapper to use
                switch (zapperType) {
                    case leftIndex:
                        board[row][col] = DOWN_ZAP_CHAR;
                        break;
                    case downIndex:
                        board[row][col] = UP_ZAP_CHAR;
                        break;
                    case rightIndex:
                        board[row][col] = LEFT_ZAP_CHAR;
                        break;
                    case upIndex:
                        board[row][col] = RIGHT_ZAP_CHAR;
                        break;
                }
            }
            else {
                i--;
            }
        } 
    }

    /**
     * Returns the index number for the specified zapChar
     * this is a helper method for rotateCounterClockwise()
     *
     * @param zapChar the zapper that is passed through
     */
    int indexOfZapper(char zapChar)
    {
        switch (zapChar){
            case LEFT_ZAP_CHAR:
                return leftIndex;
            case DOWN_ZAP_CHAR:
                return downIndex;
            case RIGHT_ZAP_CHAR:
                return rightIndex;
            case UP_ZAP_CHAR:
                return upIndex;
        }
        return -1; 
    }

    /**
     * Rotates the board and its contents 90 degrees counter clockwise.
     * It also updates the player and goal location, and adjusts the type
     * of zapper. 
     */
    void rotateCounterClockwise()
    {
        /**the temp variable temporarily holds the value of goalRow and 
         * playerRow to reassign playerRow and goalRow.
         */
        int temp;
        int indexOfFinalZapper = 3;
        char[][] rotated = new char[this.board[0].length][this.board.length];
        //for loop to rotate the elements of board
        for(int i = 0; i < board.length; i++){
            for(int idx = 0; idx < board[0].length; idx++){
                rotated[rotated.length-(idx+1)][i] = board[i][idx];
            }
        }
        //update player location
        temp = playerRow;
        playerRow = rotated.length - (playerCol+1);
        playerCol = temp;

        //update goal location
        temp = goalRow;
        goalRow = rotated.length - (goalCol+1);
        goalCol = temp;

        //for loop that rotates the zapper type properly
        for(int i = 0; i < rotated.length; i++){
            for(int idx = 0; idx < rotated[0].length; idx++){
                int zapperType = indexOfZapper(rotated[i][idx]);
                if(zapperType >= 0){
                    if(zapperType == indexOfFinalZapper){
                        zapperType = 0;
                    }
                    else{
                        zapperType++;
                    }
                    rotated[i][idx] = zappers[zapperType];    
                }
            }
        }
        this.board = rotated;
    }

    /**
     * Moves the character to the left of itself, leaving behind a trail of
     * PLAYER_CHAR characters to signify the already visited indexes. Prevents
     * movement into other objects of the board, and moving off the board. Also
     * determines if the game has been won.
     *
     */
    void moveLeft()
    {
        int zapper;

        if(playerCol != 0 && 
                (""+board[playerRow][playerCol-1]).equals(""+TRAIL_CHAR))
        {
            return;
        }

        while(playerCol > 0){
            if(board[playerRow][playerCol-1] == SPACE_CHAR || 
                    (board[playerRow][playerCol-1] == GOAL_CHAR)){
                board[playerRow][playerCol] = TRAIL_CHAR;
                board[playerRow][playerCol-1] = PLAYER_CHAR;
                playerCol--;
            }
            if(playerRow == goalRow && (playerCol == goalCol)){
                levelPassed = true;
                break;
            }
            if(playerCol == 0){
                 board[playerRow][playerCol] = PLAYER_CHAR;
                 break;
             }
            if(indexOfZapper(board[playerRow][playerCol-1]) >= 0){
                zapper = indexOfZapper(board[playerRow][playerCol-1]);
                board[playerRow][playerCol] = TRAIL_CHAR;
                board[playerRow][playerCol-1] = PLAYER_CHAR;
                playerCol--;
                switch(zapper){

                    case leftIndex:
                        this.move(Direction.LEFT);
                        break;
                    case downIndex:
                        this.move(Direction.DOWN);
                        break;
                    case upIndex:
                        this.move(Direction.UP);
                        break;
                    
                }
                return;
            }
            if(board[playerRow][playerCol-1] != SPACE_CHAR &&
                    (board[playerRow][playerCol-1] != GOAL_CHAR)){
                break;
            }
        }
    }
    /**
     * Moves the character in the designated direction. Takes a direction
     * and makes a call to rotate the board a set ammount of times until
     * the desired direction to move is to the left of the player. Then makes
     * a call to moveLeft(). and then rotates it back it its original state.
     *
     * @param direction enum reference of direction constants (up, down...)
     */
    void move(Direction direction)
    {
        int numRotate = direction.getRotationCount();

        for(int i = 0; i < numRotate; i++){

            this.rotateCounterClockwise();
        }
        this.moveLeft();
        for(int i = 0; i < (totalDirections-numRotate); i++){
            this.rotateCounterClockwise();
        }
    }
    /**
     * Converts the GameState to a single String representation of the board 
     * and its contents. 
     */

    @Override
        public String toString()
        {
            //multiple used to adjust the size of the top and bottom borders 
            int hBorderConstant = 2;
            //extends the horizontal border by 2 to account for the side border
            int extendByTwo = 2;
            String toString = "";
            
            for(int i = 0; i < board.length; i++){
                if(i == 0) {
                    for(int a = 0; a < (board[0].length*hBorderConstant)+
                            extendByTwo+1; a++)
                    {
                        toString += Character.toString(HORIZONTAL_BORDER_CHAR);
                    }
                    toString += Character.toString(NEWLINE_CHAR);
                }
                toString += Character.toString(SIDE_BORDER_CHAR);
                for(int idx = 0; idx < board[0].length; idx++){
                    toString += "" + SPACE_CHAR + board[i][idx];
                }
                toString += SPACE_CHAR + Character.toString(SIDE_BORDER_CHAR);
                toString += Character.toString(NEWLINE_CHAR);
                if(i == board.length-1){
                    for(int a = 0; a < (board[0].length*hBorderConstant)+
                            extendByTwo+1; a++){
                        toString += Character.toString(HORIZONTAL_BORDER_CHAR);
                    }
                    toString += Character.toString(NEWLINE_CHAR);
                }
            }
            return toString;
        }


    /**
     * Overrides the object equals method. Used to compare 2 gamestates
     * checks the values of both instance variables. 
     * 
     * @param other the object being compared to.
     */
    @Override
        public boolean equals(Object other)
        {
            //check if null
            if(other == null)
                return false;
            
            if(other == this)
                return true;

            if(other instanceof GameState)
            {
                //casting other into a GameState
                GameState compare = (GameState)other;

                //check if boardlengths are same
                if(this.board.length != compare.board.length)
                    return false;

                //check if board is the same
                for(int i = 0; i < compare.board.length; i++){
                    if(!(Arrays.equals(this.board[i], compare.board[i])))
                        return false;
                }

                //check if the location of the player is equal 
                if((this.playerRow != compare.playerRow) || (this.playerCol != 
                        compare.playerCol))
                {
                    return false;
                }

                //check if the locations of both goals are equal
                if((this.goalRow != compare.goalRow) || (this.goalCol != 
                        compare.goalCol))
                {
                    return false;
                }

                //check if both games are over or running
                if(this.levelPassed != compare.levelPassed)
                    return false;
                    
                return true;
            }
            return false; 
        }

}
