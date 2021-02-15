/**
 * The Streamline file is intended to add input and output interaction for
 * Streamline. This file features the Streamline class which features 
 * methods used for user interaction.
 *
 * Name: Logan R. Ramos
 * ID: cs8bfa19dw
 * Email: lramos@ucsd.edu
 */


import java.util.*;
import java.io.*;

/**
 * The Streamline class features several methods used for interaction in
 * the streamline game. It has the ability to take in key arguments via
 * terminal, save to a file, load from a file, and determine which methods
 * in GameState are needed to complete the user's desired calls.
 *
 */
public class Streamline
{
    /* Provided constants */
    final static int DEFAULT_HEIGHT = 6;
    final static int DEFAULT_WIDTH = 5;
    final static String OUTFILE_NAME = "saved_streamline_game";

    /* Add your `final static` constants here */
    final static int DEFAULT_OBSTACLE_COUNT = 3;
    final static String upChar = "w";
    final static String downChar = "s";
    final static String leftChar = "a";
    final static String rightChar = "d";
    final static String saveChar = "o";
    final static String quitChar = "q";
    final static String undoChar = "u";
    /* Instance variables, do not add any */
    GameState currentState;
    List<GameState> previousStates;

    /**
     * Default no args constructor. Creates a new instance of the GameState
     * class and sets it to the default values specified by the constants
     * above. Also creates randomized obstacles and zappers (x3).
     */
    public Streamline()
    {
        this.currentState = new GameState(DEFAULT_HEIGHT, DEFAULT_WIDTH, 
                DEFAULT_HEIGHT-1, 0, 0, DEFAULT_WIDTH-1);
        currentState.addRandomObstacles(DEFAULT_OBSTACLE_COUNT);
        currentState.addRandomZappers(DEFAULT_OBSTACLE_COUNT);
        previousStates = new ArrayList<GameState>();
    }
    /**
     * This constructor takes in a file name, and makes a call to load to file
     * to instantiate the newly created Streamline object as the data that was
     * saved.
     *
     * @param filename the name of the save file
     */
    public Streamline(String filename)
    {
        try
        {
            loadFromFile(filename);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.previousStates = new ArrayList<GameState>();
    }
    
    /**
     * Method loadFromFile takes an existing file name, reads through the file
     * and creates a new GameState object based off of the details of the file
     * by making a call to the 6 parameter constructor. returns nothing if the
     * game is won.
     *
     * @param filename the name of the file to be read
     */
    protected void loadFromFile(String filename) throws IOException             
    {
        //required data for details of GameState 
        int boardVLength; int boardHLength; int playerVPosition;
        int playerHPosition; int goalVPosition; int goalHPosition;
        //file and scanner object to read through the file
        File f = new File(filename);
        Scanner reader = new Scanner(f);
        String token;
        
        //assigning each required data in order of the format of the file
        boardVLength = Integer.parseInt(reader.next());
        boardHLength = Integer.parseInt(reader.next());
        playerVPosition = Integer.parseInt(reader.next());
        playerHPosition = Integer.parseInt(reader.next());
        goalVPosition = Integer.parseInt(reader.next());
        goalHPosition = Integer.parseInt(reader.next());
        
        //return if game is won
        if((playerVPosition == goalVPosition) && (playerHPosition ==
                    goalHPosition))
        {
            return;
        }

        //create a new GameState object based off of the read data
        currentState = new GameState(boardVLength, boardHLength,
                playerVPosition, playerHPosition, goalVPosition,
                goalHPosition);
        
        //reads through the last several lines of the file and creates board
        int i = 0;
        token = reader.nextLine();
        while(reader.hasNextLine()){
            token = reader.nextLine();
            currentState.board[i] = token.toCharArray();
            i++;
        }
        reader.close();
       
    }
    
    /**
     * Takes in movement arguements and saves the current state of the game
     * prior to the movement. Assigns the old current state to a class variable
     * which can be referenced and used if the user calls an undo. Makes a
     * call to GameState.java move().
     *
     * @param direction the direction desired to be moved.
     */
    void recordAndMove(Direction direction)
    {   
        boolean addToPrevious = true;
        GameState checker = new GameState(currentState);
        
        //if null do not add to previous
        if(direction == null)
            addToPrevious = false;
        
        //move in desired direction
        checker.move(direction);
        
        //if the board does not move, do not add to previous
        if(checker.equals(currentState))
            addToPrevious = false;
        
        checker = new GameState(currentState);
        //make move and add to previous, if addToPrevious is true
        if(addToPrevious){
            previousStates.add(checker);
            currentState.move(direction);
        }
    } 
    
    /**
     * Undoes the last executed move by reinstatiating currentState
     * to the last element in previousStates ArrayList.
     */
    void undo()
    {
        if(previousStates.size() < 1)
            return;

        currentState = previousStates.remove(previousStates.size()-1);
    }
    /**
     * the Play() method is a major method within the Streamline class that
     * takes in the inputs passed through the console, and determines what to
     * do with that input. For example: input w would result in a movement up.
     */
    void play()
    {
        //scanner that reads inputs passed through console
        Scanner scanRead = new Scanner(System.in);
        String input;
        
        //loop that constantly runs until levelPassed is true
        while(!currentState.levelPassed){
            //prints the current state of the game and a request for argument >
            System.out.print(currentState.toString());
            System.out.print("> ");

            //reads the input
            input = scanRead.nextLine();
            
            //returns an error message if the input is more than 1 char
            if(input.length() > 1){
                System.out.println("Command must be one char long");
            }
            else{
                //switch case to deal with the input
                switch(input){

                    case upChar:
                        recordAndMove(Direction.UP);
                        break;
                    case leftChar:
                        recordAndMove(Direction.LEFT);
                        break;
                    case downChar:
                        recordAndMove(Direction.DOWN);
                        break;
                    case rightChar:
                        recordAndMove(Direction.RIGHT);
                        break;
                    case undoChar:
                        undo();
                        break;
                    case saveChar:
                        saveToFile();
                        break;
                    case quitChar:
                        return;
                    default:
                        //if the input was none of above, give possible args
                        System.out.println(
                        "Possible commands:\n w - up\n a - left\n s - " + 
                        "down\n d - right\n u - undo\n o - save to file\n"+
                        " q - quit level");
                }
            }
        }
        System.out.print(currentState.toString());
        System.out.println("Level passed!");
    }

    /**
     * Takes the current state and writes it to an already existing
     * file. Uses PrintWriter to write the details of the GameState
     * and the contents of the board.
     *
     */
    void saveToFile()
    {
        try {
            PrintWriter writer = new PrintWriter(OUTFILE_NAME);
            //writes the board size (Height Width)
            writer.write("" + currentState.board.length); 
            writer.write(" ");
            writer.write("" + currentState.board[0].length);
            writer.println();
            //writes the location of the player (Height Width)
            writer.write("" + currentState.playerRow);
            writer.write(" ");
            writer.write("" + currentState.playerCol);
            writer.println();
            //writes the location of the goal (Height Width
            writer.write("" + currentState.goalRow);
            writer.write(" ");
            writer.write("" + currentState.goalCol);
            writer.println();
            
            //For loop that writes the contents of current state's board
            for(int i = 0; i < currentState.board.length; i++){
                for(int j = 0; j < currentState.board[0].length; j++){
                    writer.write(currentState.board[i][j]);
                }

                writer.println();

            }
            writer.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    } 
}

