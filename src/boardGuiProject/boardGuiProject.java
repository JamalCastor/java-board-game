package boardGuiProject;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Container;
import java.io.*;
import javax.swing.JLabel;
import java.awt.BorderLayout;  
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ButtonGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.prefs.Preferences;


//Main class for the board game application
public class boardGuiProject {

 // Entry point of the application
 public static void main(String[] args) {
     // Ensures that the GUI creation is done on the Event Dispatch Thread (EDT)
     SwingUtilities.invokeLater(boardGuiProject::initializeGUI);
 
 }

 // Method to initialize the GUI components
 private static void initializeGUI() {
     // Creating the main window frame
     JFrame window = new JFrame("2D-Board GUI");
     window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Window close operation
     window.setResizable(false); // Fixed window size 
     window.setLayout(new BorderLayout()); // Use BorderLayout as the layout manager

     // Creating the game panel where the game will be drawn
     GamePanel gamePanel = new GamePanel(32, 32);
    
     window.setJMenuBar(gamePanel.menuBar);
     window.add(gamePanel, BorderLayout.CENTER); // Add the game panel at the center

     // Finalizing the window setup
     window.pack(); // Pack the window to fit the preferred sizes
     window.setLocationRelativeTo(null); // Center the window on the screen
     window.setVisible(true); // Make the window visible
 }


 // Static inner class for the game panel
 private static class GamePanel extends JPanel implements ActionListener, Serializable  {	    // Menu Bar configuration
	    private JMenuBar menuBar;
	    private JMenu designMenu, drawMenu;
	    private JMenuItem designItem, drawItem;
	    private static final int entranceCell = 2;
	    private static final int exitCell = 3;
	    
	    // Attributes for the game
	    final int originalTileSize = 8; // Original tile size
	    final int scale = 2; // Scaling factor for the tile
	    final int tileSize = originalTileSize * scale; // Scaled tile size
	    final int maxScreenCol = 32; // Number of columns in the screen grid
	    final int maxScreenRow = 32; // Number of rows in the screen grid
	    final int screenWidth = tileSize * maxScreenCol; // Width of the screen
	    final int screenHeight = tileSize * maxScreenRow; // Height of the screen
	     
	    // Attributes for the game panel
	    private int[][] boardMatrix;
	    private int numRows, numColumns;
	    private List<Wall> walls;
	    private List<Skull> skulls;
	    private List<cactus> cacti;
	    private List<ruby> rubies;
	    private List<coin> coins;
	    private List<sapphire> sapphires;
	    
	    
	    public List<Skull> getSkulls() {
	        return skulls;
	    }

	    public List<cactus> getCacti() {
	        return cacti;
	    }

	    public List<ruby> getRubies() {
	        return rubies;
	    }

	    public List<coin> getCoins() {
	        return coins;
	    }

	    public List<sapphire> getSapphires() {
	        return sapphires;
	    }
	    
	    // Method to get the list of walls
	    public List<Wall> getWalls() {
	        return walls;
	    }
	    
	    public void addWall(Wall wall) {
	        this.walls.add(wall);
	    }

	    public void addSkull(Skull skull) {
	        this.skulls.add(skull);
	    }

	    public void addCactus(cactus cacti) {
	        this.cacti.add(cacti);
	    }

	    public void addRuby(ruby ruby) {
	        this.rubies.add(ruby);
	    }

	    public void addCoin(coin coin) {
	        this.coins.add(coin);
	    }

	    public void addSapphire(sapphire sapphire) {
	        this.sapphires.add(sapphire);
	    }

	    private Preferences prefs = Preferences.userNodeForPackage(GamePanel.class);
	    private static final String LAST_USED_FOLDER = "LAST_USED_FOLDER";
	    private Wall wall;
	    
	    //Empty cell instantiated 
	    private static final int emptyCell= 0;
	    //Wall cell instantiated 
	    private static final int wallCell = 1;
	    
	    // New constant for openings
	    private static final int openingCell = -2; 
	    private static final int SKULL = 2;
	    private static final int CACTUS = 3;
	    private static final int RUBY = 4;
	    private static final int COIN = 5;
	    private static final int SAPPHIRE = 6;
	    
	    private GameStateManager gameStateManager;

	    private boolean isBoardDesigned = false; // Flag to control rendering
	    
	    public int[][] getBoardMatrix() {
	        return boardMatrix;
	    }

	    public void setBoardMatrix(int[][] matrix) {
	        this.boardMatrix = matrix;
	    }
	    
	    public void placeWall(Wall wall, int row, int col) {
	        walls.add(wall);
	        boardMatrix[row][col] = Wall.WALL_CELL; 
	    }
	    
	    public class GameStateManager {
	    	
	    	 public void saveGame(GamePanel panel) {
	    		 String userHome = System.getProperty("user.home");
	    		    String filePath = userHome + File.separator + "game_state.dat";
	    		    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
	    	            // Serialize the board matrix
	    	            out.writeObject(panel.getBoardMatrix());
	    	            System.out.println("Board matrix saved.");

	    	            // Serialize Walls
	    	            List<Wall> walls = panel.getWalls();
	    	            out.writeInt(walls.size());
	    	            for (Wall wall : walls) {
	    	                out.writeObject(wall);	    	                    	              
	    	            }

	    	            // Serialize Skulls
	    	            List<Skull> skulls = panel.getSkulls(); 
	    	            out.writeInt(skulls.size());
	    	            for (Skull skull : skulls) {
	    	                out.writeObject(skull);	    	                  	              
	    	            }

	    	            // Serialize Cacti
	    	            List<cactus> cacti = panel.getCacti(); 
	    	            out.writeInt(cacti.size());   	            
	    	            for (cactus cactiItem : cacti) {
	    	                out.writeObject(cactiItem);	    	             	               
	    	            }

	    	            // Serialize Rubies
	    	            List<ruby> rubies = panel.getRubies(); 
	    	            out.writeInt(rubies.size());	    	            
	    	            for (ruby rubyItem : rubies) {
	    	                out.writeObject(rubyItem);	    	               	    	                
	    	            }

	    	            // Serialize Coins
	    	            List<coin> coins = panel.getCoins(); 
	    	            out.writeInt(coins.size());	    	           
	    	            for (coin coinItem : coins) {
	    	                out.writeObject(coinItem);	    	                 	             
	    	            }

	    	            // Serialize Sapphires
	    	            List<sapphire> sapphires = panel.getSapphires();
	    	            out.writeInt(sapphires.size());	    	      
	    	            for (sapphire sapphireItem : sapphires) {
	    	                out.writeObject(sapphireItem);
	    	            }

	    	        } catch (IOException e) {
	    	         
	    	            e.printStackTrace();
	    	        }
	    	    }

	    	  
	    	
	    	 public void loadGame(GamePanel panel) {
	    		 String userHome = System.getProperty("user.home");
	    		    String filePath = userHome + File.separator + "game_state.dat";
	    		    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
	    	            // Deserialize and set the board matrix
	    	            int[][] loadedMatrix = (int[][]) in.readObject();
	    	            panel.setBoardMatrix(loadedMatrix);

	    	            // Clear existing game state
	    	            panel.clearObjects();

	    	            // Deserialize Walls
	    	            int wallCount = in.readInt();
	    	            for (int i = 0; i < wallCount; i++) {
	    	                Wall wall = (Wall) in.readObject();
	    	                panel.addWall(wall); 
	    	            }

	    	            // Deserialize Skulls
	    	            int skullCount = in.readInt();
	    	            for (int i = 0; i < skullCount; i++) {
	    	                Skull skull = (Skull) in.readObject();
	    	                panel.addSkull(skull); 
	    	            }

	    	            // Deserialize Cacti
	    	            int cactusCount = in.readInt();
	    	            for (int i = 0; i < cactusCount; i++) {
	    	                cactus cacti = (cactus) in.readObject();
	    	                panel.addCactus(cacti); 
	    	            }

	    	            // Deserialize Rubies
	    	            int rubyCount = in.readInt();
	    	            for (int i = 0; i < rubyCount; i++) {
	    	                ruby ruby = (ruby) in.readObject();
	    	                panel.addRuby(ruby); 
	    	            }

	    	            // Deserialize Coins
	    	            int coinCount = in.readInt();
	    	            for (int i = 0; i < coinCount; i++) {
	    	                coin coin = (coin) in.readObject();
	    	                panel.addCoin(coin); 
	    	            }

	    	            // Deserialize Sapphires
	    	            int sapphireCount = in.readInt();
	    	            for (int i = 0; i < sapphireCount; i++) {
	    	                sapphire sapphire = (sapphire) in.readObject();
	    	                panel.addSapphire(sapphire); 
	    	            }

	    	            // Redraw the panel to reflect the loaded state
	    	            panel.repaint();
	    	        } catch (IOException | ClassNotFoundException e) {
	    	            e.printStackTrace();
	    	        }
	    	    }
	    	}
	    
	    
	    
	    
	    private BufferedImage getRandomWallOverlayImage() {
	        // Define an array of wall overlay images
	        BufferedImage[] overlayImages = { topImage, bottomImage, leftImage, rightImage };

	        // Randomly select an image from the array
	        int randomIndex = (int) (Math.random() * overlayImages.length);
	        return overlayImages[randomIndex];
	    }

	    
	    private void placeSkulls(int[][] boardMatrix, int count) {
	        placeItems(boardMatrix, new Random(), SKULL, count);
	    }

	    private void placeCacti(int[][] boardMatrix, int count) {
	        placeItems(boardMatrix, new Random(), CACTUS, count);
	    }

	    private void placeRubies(int[][] boardMatrix, int count) {
	        placeItems(boardMatrix, new Random(), RUBY, count);
	    }

	    private void placeCoins(int[][] boardMatrix, int count) {
	        placeItems(boardMatrix, new Random(), COIN, count);
	    }

	    private void placeSapphires(int[][] boardMatrix, int count) {
	        placeItems(boardMatrix, new Random(), SAPPHIRE, count);
	    }

	    private BufferedImage skullImage;
	    private BufferedImage cactusImage;
	    private BufferedImage rubyImage;
	    private BufferedImage coinImage;
	    private BufferedImage sapphireImage;
	  //Border walls
	    private BufferedImage borderTop; 
	    private BufferedImage topImage;
	    private BufferedImage bottomImage; 
	    private BufferedImage leftImage; 
	    private BufferedImage rightImage; 
	    
	    public GamePanel(int numRows, int numColumns) {
	        this.numRows = numRows;
	        this.numColumns = numColumns;
	        this.walls = new ArrayList<>(); // Initialize walls list
	        this.skulls = new ArrayList<>();
	        this.cacti = new ArrayList<>();
	        this.rubies = new ArrayList<>();
	        this.coins = new ArrayList<>();
	        this.sapphires = new ArrayList<>();
	        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set the preferred size
	        Color ochreColor = new Color(204, 119, 34);
            this.setBackground(ochreColor); // Set the background color
            this.setDoubleBuffered(true); // Set up double buffering
            createMenu(); // Initialize the menu bar
            boardMatrix = new int [numRows][numColumns]; //New code array object that holds both numRows and numColumns
            boardState();
            loadImages();
            setOpaque(false); // For the JPanel
            wall = new Wall();
            gameStateManager = new GameStateManager();
           
	    }
	    
	    private void onSaveButtonClick() {
	        gameStateManager.saveGame(this);
	    }
	    
	    private void onLoadButtonClick() {
	        JFileChooser fileChooser = new JFileChooser();

	        // Get the last used folder from preferences or default to the user's home directory
	        String lastUsedFolderPath = prefs.get(LAST_USED_FOLDER, System.getProperty("user.home"));
	        fileChooser.setCurrentDirectory(new File(lastUsedFolderPath));

	        int option = fileChooser.showOpenDialog(this); // 'this' should be your JFrame or JPanel
	        if (option == JFileChooser.APPROVE_OPTION) {
	            File selectedFile = fileChooser.getSelectedFile();
	            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
	            
	            // Save the current folder for next time
	            prefs.put(LAST_USED_FOLDER, selectedFile.getParent());

	            loadGameFromFile(selectedFile); // Call your method to load the game
	        }
	    }




	    
	    private void loadGameFromFile(File file) {
	        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
	            // Deserialize the boardMatrix
	            int[][] loadedMatrix = (int[][]) in.readObject();
	            setBoardMatrix(loadedMatrix);


	            // Loop through each cell in the loaded matrix
	            for (int i = 0; i < loadedMatrix.length; i++) {
	                for (int j = 0; j < loadedMatrix[i].length; j++) {
	                    int cellValue = loadedMatrix[i][j];
	                    switch (cellValue) {
	                        case Wall.WALL_CELL:
	                            addWall(new Wall(i, j));
	                            break;
	                        case Skull.SKULL_CODE:
	                            addSkull(new Skull(i, j));
	                            break;
	                        case cactus.CACTUS_CODE:
	                            addCactus(new cactus(i, j));
	                            break;
	                        case ruby.RUBY_CODE:
	                            addRuby(new ruby(i, j));
	                            break;
	                        case coin.COIN_CODE:
	                            addCoin(new coin(i, j));
	                            break;
	                        case sapphire.SAPPHIRE_CODE:
	                            addSapphire(new sapphire(i, j));
	                            break;
	                        
	                    }
	                }
	            }

	            // Repaint the panel to reflect the loaded state
	            repaint();

	        } catch (IOException | ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }

	    private void loadImages() {
	        try {
	            skullImage = ImageIO.read(new File("images/skull.png"));
	            cactusImage = ImageIO.read(new File("images/cactus.png"));
	            rubyImage = ImageIO.read(new File("images/ruby.png"));
	            coinImage = ImageIO.read(new File("images/coin.png"));
	            sapphireImage = ImageIO.read(new File("images/sapphire.png"));
	            topImage =  ImageIO.read(new File("images/top.png"));
	            bottomImage = ImageIO.read(new File("images/bottom.png")); 
	            leftImage = ImageIO.read(new File("images/left.png")); 
	            rightImage = ImageIO.read(new File("images/right.png")); 
	         
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        boardState();
	    }
	    
	    
	    
	    private void boardState() {
	        Random random = new Random();
	        boardMatrix = new int[numRows][numColumns]; // Initializing the board matrix
	     
	      
	        
	        // Place obstacles and rewards
	        placeSkulls(boardMatrix, 16);     
	        placeCacti(boardMatrix, 16);      
	        placeRubies(boardMatrix, 16);     
	        placeCoins(boardMatrix, 16);  
	        placeSapphires(boardMatrix,16);
	   
	    }

	    private boolean isBorderCell(int row, int col) {
	        return row == 0 || row == numRows - 1 || col == 0 || col == numColumns - 1;
	    }

	    private void placeItems(int[][] matrix, Random random, int itemCode, int itemCount) {
	        for (int i = 0; i < itemCount; i++) {
	            int row, col;
	            do {
	                row = random.nextInt(numRows);
	                col = random.nextInt(numColumns);
	            } while (matrix[row][col] != 0); // Repeat if the cell is not empty

	            matrix[row][col] = itemCode;
	        }
	    }
	    
	    private void generateRandomWallPositions() {
	        Random random = new Random();
	        int totalWalls = 258; // Number of wall objects. (Meets requirements) I put 258 because it's counting the opening cells as a wall count so by putting 2 more it ensures that the wall count is indeed 256
	        int wallCount = 0; // Setting wall count to 0

	        // Create the entire border as walls
	        for (int row = 0; row < numRows; row++) {
	            boardMatrix[row][0] = wallCell; // Left border
	            boardMatrix[row][numColumns - 1] = wallCell; // Right border
	        }
	        for (int col = 0; col < numColumns; col++) {
	            boardMatrix[0][col] = wallCell; // Top border
	            boardMatrix[numRows - 1][col] = wallCell; // Bottom border
	        }

	        // Generate two random openings along the top and bottom borders
	        int opening1 = random.nextInt(maxScreenCol - 2) + 1;
	        int opening2 = random.nextInt(maxScreenCol - 2) + 1;

	        boardMatrix[0][opening1] = openingCell; // Opening along the top border
	        boardMatrix[numRows - 1][opening2] = openingCell; // Opening along the bottom border

	        wallCount = 2; // Start with 2 openings

	        while (wallCount < totalWalls) {
	            int row = random.nextInt(numRows);
	            int col = random.nextInt(numColumns);

	            // Check if the cell is neither a wall nor an opening
	            if (boardMatrix[row][col] != wallCell && boardMatrix[row][col] != openingCell) {
	                boardMatrix[row][col] = wallCell;
	                wallCount++;
	            }
	        }
	    }

	        
	    private void createMenu() {
	        menuBar = new JMenuBar();
	        
	        // Design menu
	        designMenu = new JMenu("Design");designItem = new JMenuItem("Generates a new board and draws.");
	        designItem.setToolTipText("Generates a new board and draws.");
            designItem = new JMenuItem("New board");
	        designItem.addActionListener(this);
	        designMenu.add(designItem);
	        designItem.setActionCommand("Design");
	        
	        
	        // Draw menu
	        drawMenu = new JMenu("Load");
	        drawItem = new JMenuItem("Load game file");
	        drawItem.setActionCommand("Load");

	        drawItem.addActionListener(this);
	        drawMenu.add(drawItem);
            
	        menuBar.add(designMenu);
	        menuBar.add(drawMenu);
	        
	    }
	    
	    public void clearObjects() {
	        // Clear the boardMatrix
	        for (int row = 0; row < boardMatrix.length; row++) {
	            for (int col = 0; col < boardMatrix[row].length; col++) {
	                boardMatrix[row][col] = 0; // Set each cell to the empty state
	            }
	        }

	        // Clear the lists of game objects
	        if (walls != null) {
	            walls.clear();
	        }
	        if (skulls != null) {
	            skulls.clear();
	        }
	        if (cacti != null) {
	            cacti.clear();
	        }
	        if (rubies != null) {
	            rubies.clear();
	        }
	        if (coins != null) {
	            coins.clear();
	        }
	        if (sapphires != null) {
	            sapphires.clear();
	        }
	    }
	   
	    public void placeObject(int objectType, int row, int col) {

	        // Check if the specified row and column are within valid bounds.
	        if (row >= 0 && row < boardMatrix.length && col >= 0 && col < boardMatrix[0].length) {
	            // Place the object of the specified type at the specified position.
	            boardMatrix[row][col] = objectType;
	            if (objectType == 1) {      
	                walls.add(new Wall(row, col));
	            }
	        }
	    }
	    
	    
	    
	    public JMenuBar getMenuBar() {
            return menuBar;
        }

	    public void actionPerformed(ActionEvent e) {
	        String actionCommand = e.getActionCommand();
	        if ("Design".equals(actionCommand)) {
	            Design();
	        } else if ("Load".equals(actionCommand)) {
	            // Load the board state from the serialized file
	            onLoadButtonClick(); 
	            System.out.println("Loading completed.");
	            repaint(); // Refresh the board
	        }
	    }

	    
	    public void Design() {
	       
	        boardMatrix = new int[numRows][numColumns]; // Reset the board matrix

	        generateRandomWallPositions(); // Generate wall position 
	        placeSkulls(boardMatrix, 16);
	        placeCacti(boardMatrix, 16);
	        placeRubies(boardMatrix, 16);
	        placeCoins(boardMatrix, 16);
	        placeSapphires(boardMatrix, 16);
	        isBoardDesigned = true; // Set the flag to true after designing the board
	        onSaveButtonClick(); 
	        repaint(); // Repaint the board
	    }
	    
	 

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        

	        Graphics2D g2d = (Graphics2D) g;
//	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Responsible for grid lines if you uncomment you'll see that everything is in an individual cell

	        // Define colors for different cell types
	        Color ochreColor = new Color(204, 119, 34); // Ochre color for empty cells
	        Color hazelColor = new Color(158, 126, 56); // Hazel color for border walls
	        Color lightHazelColor = new Color(198, 166, 96); // Light hazel color for inner walls

	        // Draw the game grid
	        for (int row = 0; row < numRows; row++) {
	            for (int col = 0; col < numColumns; col++) {
	                int cellValue = boardMatrix[row][col];

	                // Check if it's a border cell
	                boolean isBorderCell = row == 0 || row == numRows - 1 || col == 0 || col == numColumns - 1;

	                if (cellValue == wallCell || cellValue == 1) {
	                    // Render wall obstacle
	                    g2d.setColor(isBorderCell ? hazelColor : lightHazelColor);
	                    g2d.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);

//	                    if (!isBorderCell) {
//	                        // Select the appropriate wall image based on orientation
//	                        BufferedImage wallImage;
//	                        if (boardMatrix[row - 1][col] == wallCell) {
//	                            wallImage = wall.getTopImage(); // Wall above
//	                        } else if (boardMatrix[row + 1][col] == wallCell) {
//	                            wallImage = wall.getBottomImage(); // Wall below
//	                        } else if (boardMatrix[row][col - 1] == wallCell) {
//	                            wallImage = wall.getLeftImage(); // Wall to the left
//	                        } else if (boardMatrix[row][col + 1] == wallCell) {
//	                            wallImage = wall.getRightImage(); // Wall to the right
//	                        } else {
//	                            wallImage = getRandomWallOverlayImage(); // Random overlay image
//	                        }
//
//	                        // Draw the selected wall image
//	                        g.drawImage(wallImage, col * tileSize, row * tileSize, tileSize, tileSize, this);
//	                    }
	                } else {
	                    // Render an empty cell
	                    g2d.setColor(ochreColor);
	                    g2d.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
	                }
	                if (isBoardDesigned) {
	                    switch (cellValue) {
	                        case SKULL:
	                            g2d.drawImage(skullImage, col * tileSize, row * tileSize, this);
	                            break;
	                        case CACTUS:
	                            g2d.drawImage(cactusImage, col * tileSize, row * tileSize, this);
	                            break;
	                        case RUBY:
	                            g2d.drawImage(rubyImage, col * tileSize, row * tileSize, this);
	                            break;
	                        case COIN:
	                            g2d.drawImage(coinImage, col * tileSize, row * tileSize, this);
	                            break;
	                        case SAPPHIRE:
	                            g2d.drawImage(sapphireImage, col * tileSize, row * tileSize, this);
	                            break;
	                    }}

	            }}}}
 


		

 public void loadGame(GamePanel panel) {
	    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("game_state.dat"))) {
	        int[][] boardMatrix = (int[][]) in.readObject();
	        panel.setBoardMatrix(boardMatrix);

	        // Clear existing game state
	        panel.clearObjects();

	        while (true) {
	            try {              
	                int objectType = in.readInt(); // Read object type
	                int row = in.readInt();        // Read row position
	                int col = in.readInt();        // Read column position

	                // Place the object on the board based on its type and position
	                panel.placeObject(objectType, row, col);

	            } catch (EOFException e) {
	                // End of file reached
	                break;
	            }
	        }
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    }
	}


//Interface for game statistics
interface GameStats {
	 void placeObjects(int[][] boardMatrix, Random random, int itemCount);
	  int emptyCell = 0;
	
 }

abstract class GameEntity implements Serializable {
	private static final long serialVersionUID = 1L;
    protected abstract void writeEntity(ObjectOutputStream out) throws IOException;
    protected abstract void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeEntity(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        readEntity(in);
    }
}


//Abstract class for obstacles in the game
abstract class Obstacles extends GameEntity implements GameStats {	
	// Declare the abstract method with the correct signature
	 public void placeObjects(int[][] boardMatrix, int row, int col) {
		 
	 }

   
}
//First obstacle class wall
class Wall extends Obstacles{
	private static final long serialVersionUID = 1L;
	 private transient BufferedImage topImage;
	    private transient BufferedImage bottomImage;
	    private transient BufferedImage leftImage;
	    private transient BufferedImage rightImage;
	    private String topImagePath;
	    private String bottomImagePath;
	    private String leftImagePath;
	    private String rightImagePath;
	    public static final int WALL_CELL = 1;
	    private int row;
	    private int col;

	    // Constructor for specifying the row and column
	    public Wall(int row, int col) {
	        this.row = row;
	        this.col = col;
	        loadImage();
	    }

	    // Default constructor
	    public Wall() {
	        loadImage();
	    }

	    // Constructor with image paths
	    public Wall(String topImagePath, String bottomImagePath, String leftImagePath, String rightImagePath) {
	        this.topImagePath = topImagePath;
	        this.bottomImagePath = bottomImagePath;
	        this.leftImagePath = leftImagePath;
	        this.rightImagePath = rightImagePath;
	        loadImage();
	    }

	    // Method to load images
	    private void loadImage() {
	        try {
	            topImage = ImageIO.read(new File("images/top.png"));
	            bottomImage = ImageIO.read(new File("images/bottom.png"));
	            leftImage = ImageIO.read(new File("images/left.png"));
	            rightImage = ImageIO.read(new File("images/right.png"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // Getters and setters for row and column
	    public int getRow() {
	        return this.row;
	    }

	    public void setRow(int row) {
	        this.row = row;
	    }

	    public int getCol() {
	        return this.col;
	    }

	    public void setCol(int col) {
	        this.col = col;
	    }

	    // Getters for image paths
	    public String getTopImagePath() {
	        return topImagePath;
	    }

	    public String getBottomImagePath() {
	        return bottomImagePath;
	    }

	    public String getLeftImagePath() {
	        return leftImagePath;
	    }

	    public String getRightImagePath() {
	        return rightImagePath;
	    }

	    // Getters for images
	    public BufferedImage getTopImage() {
	        return topImage;
	    }

	    public BufferedImage getBottomImage() {
	        return bottomImage;
	    }

	    public BufferedImage getLeftImage() {
	        return leftImage;
	    }

	    public BufferedImage getRightImage() {
	        return rightImage;
	    }

	    @Override
	    protected void writeEntity(ObjectOutputStream out) throws IOException {
	        out.writeInt(this.row); // Write the row position
	        out.writeInt(this.col); // Write the column position
	        // Write other specific attributes
	    }

	    @Override
	    protected void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException {
	        this.row = in.readInt(); // Read the row position
	        this.col = in.readInt(); // Read the column position
	        // Read other specific attributes
	    }

	    // PlaceObjects method implementation
	    @Override
	    public void placeObjects(int[][] boardMatrix, Random random, int itemCount) {
	        // Implementation for placing walls
	    }
	}

//Second obstacle class skull
class Skull extends Obstacles {
	private static final long serialVersionUID = 1L;
    private transient BufferedImage image; // Marked as transient because BufferedImage is not serializable
    public static final int SKULL_CODE = 2; // Unique identifier for Skull
    private int row; // The row position of the Skull on the board
    private int col; // The column position of the Skull on the board

    // Constructor
    public Skull() {
        loadImage();
    }
    
    public Skull(int row, int col) {
        this.row = row;
        this.col = col;
        loadImage();
    }

    // Method to load the image
    private void loadImage() {
        try {
            image = ImageIO.read(new File("images/skull.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeObjects(int[][] boardMatrix, Random random, int itemCount) {
        int placedCount = 0;
        while (placedCount < itemCount) {
            int newRow = 1 + random.nextInt(boardMatrix.length - 2);
            int newCol = 1 + random.nextInt(boardMatrix[0].length - 2);

            if (boardMatrix[newRow][newCol] == emptyCell) {
                boardMatrix[newRow][newCol] = SKULL_CODE;
                this.row = newRow;
                this.col = newCol;
                placedCount++;
            }
        }
    }

    // Implement abstract methods from Obstacles, which in turn extends GameEntity
    @Override
    protected void writeEntity(ObjectOutputStream out) throws IOException {
        out.writeInt(this.row); // Write the row position
        out.writeInt(this.col); // Write the column position
        // Write other specific attributes
    }

    @Override
    protected void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.row = in.readInt(); // Read the row position
        this.col = in.readInt(); // Read the column position
     
    }

    // Getters and setters
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // Getter for the image, if needed
    public BufferedImage getImage() {
        return image;
    }
}


//3rd obstacle class cactus
class cactus extends Obstacles {
	private static final long serialVersionUID = 1L;
	private transient BufferedImage image; // Marked as transient because BufferedImage is not serializable
    public static final int CACTUS_CODE = 3;
    private int row; // The row position of the cactus on the board
    private int col; // The column position of the cactus on the board

    // Constructor
    public cactus() {
        loadImage();
    }
    
    public cactus(int row, int col) {
        this.row = row;
        this.col = col;
        loadImage();
    }

    // Method to load the image
    private void loadImage() {
        try {
            image = ImageIO.read(new File("images/cactus.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeObjects(int[][] boardMatrix, Random random, int itemCount) {
        int placedCount = 0;
        while (placedCount < itemCount) {
            int newRow = 1 + random.nextInt(boardMatrix.length - 2); // Rows 1 to numRows-2
            int newCol = 1 + random.nextInt(boardMatrix[0].length - 2); // Cols 1 to numColumns-2

            if (boardMatrix[newRow][newCol] == emptyCell) {
                boardMatrix[newRow][newCol] = CACTUS_CODE;
                this.row = newRow;
                this.col = newCol;
                placedCount++;
            }
        }
    }

    // Implement abstract methods from Obstacles, which in turn extends GameEntity
    @Override
    protected void writeEntity(ObjectOutputStream out) throws IOException {
        out.writeInt(this.row); // Write the row position
        out.writeInt(this.col); // Write the column position
   
    }

    @Override
    protected void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.row = in.readInt(); // Read the row position
        this.col = in.readInt(); // Read the column position

    }

    // Getters and setters
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // Getter for the image, if needed
    public BufferedImage getImage() {
        return image;
    }
}


//Abstract class for rewards in the game
abstract class Rewards extends GameEntity implements GameStats {	
	public void placeObjects(int[][] boardMatrix, int row, int col) {
		 
	 }
}

//First reward class ruby
class ruby extends Rewards {
	private static final long serialVersionUID = 1L;
	private transient BufferedImage image; // Marked as transient because BufferedImage is not serializable
    public static final int RUBY_CODE = 4;
    private int row; // The row position of the ruby on the board
    private int col; // The column position of the ruby on the board

    // Constructor
    public ruby() {
        loadImage();
    }
    
    public ruby(int row, int col) {
        this.row = row;
        this.col = col;
        loadImage();
    }

    // Method to load the image
    private void loadImage() {
        try {
            image = ImageIO.read(new File("images/ruby.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeObjects(int[][] boardMatrix, Random random, int itemCount) {
        int placedCount = 0;
        while (placedCount < itemCount) {
            int newRow = 1 + random.nextInt(boardMatrix.length - 2); // Rows 1 to numRows-2
            int newCol = 1 + random.nextInt(boardMatrix[0].length - 2); // Cols 1 to numColumns-2

            if (boardMatrix[newRow][newCol] == emptyCell) {
                boardMatrix[newRow][newCol] = RUBY_CODE;
                this.row = newRow;
                this.col = newCol;
                placedCount++;
            }
        }
    }

    // Implement abstract methods from Rewards, which in turn extends GameEntity
    @Override
    protected void writeEntity(ObjectOutputStream out) throws IOException {
        out.writeInt(this.row); // Write the row position
        out.writeInt(this.col); // Write the column position
    }

    @Override
    protected void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.row = in.readInt(); // Read the row position
        this.col = in.readInt(); // Read the column position
    }

    // Getter for the image
    public BufferedImage getImage() {
        return image;
    }

    // Getters and setters for row and column
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}

//Second rewards class coin
class coin extends Rewards {
	private static final long serialVersionUID = 1L;
	private transient BufferedImage image; // Marked as transient because BufferedImage is not serializable
    public static final int COIN_CODE = 5;
    private int row; // The row position of the coin on the board
    private int col; // The column position of the coin on the board

    // Constructor
    public coin() {
        loadImage();
    }
    
    public coin(int row, int col) {
        this.row = row;
        this.col = col;
        loadImage();
    }

    // Method to load the image
    private void loadImage() {
        try {
            image = ImageIO.read(new File("images/coin.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeObjects(int[][] boardMatrix, Random random, int itemCount) {
        int placedCount = 0;
        while (placedCount < itemCount) {
            int newRow = 1 + random.nextInt(boardMatrix.length - 2); // Rows 1 to numRows-2
            int newCol = 1 + random.nextInt(boardMatrix[0].length - 2); // Cols 1 to numColumns-2

            if (boardMatrix[newRow][newCol] == emptyCell) {
                boardMatrix[newRow][newCol] = COIN_CODE;
                this.row = newRow;
                this.col = newCol;
                placedCount++;
            }
        }
    }

    // Implement abstract methods from Rewards, which in turn extends GameEntity
    @Override
    protected void writeEntity(ObjectOutputStream out) throws IOException {
        out.writeInt(this.row); // Write the row position
        out.writeInt(this.col); // Write the column position
        // Write other specific attributes
    }

    @Override
    protected void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.row = in.readInt(); // Read the row position
        this.col = in.readInt(); // Read the column position
        // Read other specific attributes
    }

    // Getter for the image
    public BufferedImage getImage() {
        return image;
    }

    // Getters and setters for row and column
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}

//3rd reward class sapphire
class sapphire extends Rewards {
	private static final long serialVersionUID = 1L;
	private transient BufferedImage image; // Marked as transient because BufferedImage is not serializable
    public static final int SAPPHIRE_CODE = 6;
    private int row; // The row position of the sapphire on the board
    private int col; // The column position of the sapphire on the board

    // Constructor
    public sapphire() {
        loadImage();
    }
    
    public sapphire(int row, int col) {
        this.row = row;
        this.col = col;
        loadImage();
    }

    // Method to load the image
    private void loadImage() {
        try {
            image = ImageIO.read(new File("images/sapphire.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeObjects(int[][] boardMatrix, Random random, int itemCount) {
        int placedCount = 0;
        while (placedCount < itemCount) {
            int newRow = 1 + random.nextInt(boardMatrix.length - 2); // Rows 1 to numRows-2
            int newCol = 1 + random.nextInt(boardMatrix[0].length - 2); // Cols 1 to numColumns-2

            if (boardMatrix[newRow][newCol] == emptyCell) {
                boardMatrix[newRow][newCol] = SAPPHIRE_CODE;
                this.row = newRow;
                this.col = newCol;
                placedCount++;
            }
        }
    }

    // Implement abstract methods from Rewards, which in turn extends GameEntity
    @Override
    protected void writeEntity(ObjectOutputStream out) throws IOException {
        out.writeInt(this.row); // Write the row position
        out.writeInt(this.col); // Write the column position
        // Write other specific attributes
    }

    @Override
    protected void readEntity(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.row = in.readInt(); // Read the row position
        this.col = in.readInt(); // Read the column position
    }

    // Getter for the image
    public BufferedImage getImage() {
        return image;
    }

    // Getters and setters for row and column
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}