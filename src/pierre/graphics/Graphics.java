package pierre.graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Graphics GUI class in java
 *
 * @author Pierre Lundström
 */
public class Graphics extends Canvas implements Runnable {
    private BufferedImage logo = null;
    private BufferedImage resizedLogo = null;
    private int width;
    private int height;
    private JFrame frame;
    private BufferedImage image;
    private BufferedImage rawImage;
    private int[] pixels;
    private int scale;
    private Thread thread;
    private boolean running = false;
    private int fps = 60;
    private int ups = 30;
    private Sprite square1;
    private double t=0;
    private int xSquare1 = 0;
    private int ySquare1 = 0;
    private int vxSquare1 = 0;
    private int vySquare1 = 0;
    private int xSquare2 = 100;
    private int ySquare2 = 100;
    private JMenuBar mainMenuBar;
    private JMenu menuFile;
    private JMenu menuTools;
    private JMenu menuSettings;
    private JMenu menuPreferences;
    private JMenu menuAbout;
    private JPanel mainPanel;
    private JMenuItem itemExitProgram;
    private JMenuItem itemSaveImageAs;
    private JMenuItem itemImportImage;
    private JMenuItem itemChangePaintSize;
    private JMenuItem itemChangePaintColor;
    private JMenuItem itemSaveToConfig;
    private JMenuItem itemResetConfigOverride;
    private JMenuItem itemAbout;
    private Font myFont;
    private static Font itemFont;
    private static Font menuFont;
    private String ProgramTitle = "Graphics GUI";
    private static String programAuthor = "Pierre Lundström";
    private boolean firstTime = true;
    public static String configFileName = "paintconfig.txt";
    public static File config;
    public static String paintCoreDir = "paintcore";
    public static String paintLogsDir = "logs";
    public static int defaultColor = 0xFF00FF;
    private static boolean closeListener = false;

    /**
     * Constructor
     * @param w
     * @param h
     * @param scale
     */
    public Graphics(int w, int h, int scale) {
        this.width = w;
        this.height = h;
        this.scale = scale;
        initComponents();
        myFont = new Font("Verdana", java.awt.Font.PLAIN, 13);
        menuFont = new Font("Verdana", java.awt.Font.BOLD, 20);
        itemFont = new Font("Verdana", java.awt.Font.BOLD, 16);
        addComponents();
        changeAllFont(mainPanel, myFont);
        initListeners();
        initActionPerformed();
        initImages();
        runConfigCheck();
        initKeystrokes();
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        Dimension size = new Dimension(scale*width, scale*height);
        frame.setJMenuBar(mainMenuBar);
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setContentPane(mainPanel);
        mainPanel.add(this);
        frame.setResizable(false);
        frame.setTitle(ProgramTitle);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        this.addKeyListener(new MyKeyListener());
        this.addMouseMotionListener(new MyMouseMotionListener());
        this.addMouseListener(new MyMouseListener());
        this.requestFocus();
    }

    /**
     * Initializes all components in the JFrame
     */
    private void initComponents() {
        frame = new JFrame();
        mainPanel = new JPanel(new BorderLayout());
        mainMenuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuTools = new JMenu("Tools");
        menuSettings = new JMenu("Settings");
        menuPreferences = new JMenu("Preferences");
        menuAbout = new JMenu("About");
        itemExitProgram = new JMenuItem("Exit program");
        itemSaveImageAs = new JMenuItem("Save Image As");
        itemImportImage = new JMenuItem("Import Image");
        itemChangePaintColor = new JMenuItem("Change Paint Color");
        itemSaveToConfig = new JMenuItem("Save Current Session To Config");
        itemResetConfigOverride = new JMenuItem("Reset Config");
        itemAbout = new JMenuItem("About this program");
    }

    /**
     * Adds components to the mainpanel
     */
    private void addComponents() {
        menuFile.setFont(menuFont);
        menuFile.setIconTextGap(10);
        menuTools.setFont(menuFont);
        menuTools.setIconTextGap(10);
        menuSettings.setFont(menuFont);
        menuSettings.setIconTextGap(10);
        menuPreferences.setFont(menuFont);
        menuPreferences.setIconTextGap(10);
        menuAbout.setFont(menuFont);
        menuAbout.setIconTextGap(10);
        itemAbout.setFont(itemFont);
        itemExitProgram.setFont(itemFont);
        itemExitProgram.setToolTipText("Click here to exit the program");
        itemExitProgram.setIconTextGap(10);
        itemSaveImageAs.setFont(itemFont);
        itemImportImage.setFont(itemFont);
        itemImportImage.setIconTextGap(10);
        itemImportImage.setToolTipText("Import image");
        itemSaveImageAs.setIconTextGap(10);
        itemSaveImageAs.setToolTipText("Save image as");
        itemAbout.setToolTipText("Click here to view information about this program");
        itemAbout.setIconTextGap(10);
        itemExitProgram.setIconTextGap(10);
        itemChangePaintColor.setFont(itemFont);
        itemChangePaintColor.setToolTipText("Click here to change paint color");
        itemChangePaintColor.setIconTextGap(10);
        itemSaveToConfig.setToolTipText("Click here to save current variables to config");
        itemSaveToConfig.setIconTextGap(10);
        itemSaveToConfig.setFont(itemFont);
        itemResetConfigOverride.setFont(itemFont);
        itemResetConfigOverride.setIconTextGap(10);
        itemResetConfigOverride.setToolTipText("Click here to reset current config");
        menuFile.add(itemExitProgram);
        menuFile.add(itemImportImage);
        menuFile.add(itemSaveImageAs);
        menuFile.add(itemSaveToConfig);
        menuFile.add(itemResetConfigOverride);
        menuPreferences.add(itemChangePaintColor);
        menuAbout.add(itemAbout);
        mainMenuBar.add(menuFile);
        mainMenuBar.add(menuTools);
        mainMenuBar.add(menuSettings);
        mainMenuBar.add(menuPreferences);
        mainMenuBar.add(menuAbout);
    }

    /**
     * Action performed listeners
     */
    private void initActionPerformed() {
        itemExitProgram.addActionListener(actionEvent -> {
            confirmExitDialog();
        });
        itemSaveImageAs.addActionListener(actionEvent -> {
            final JFileChooser saveAsFileChooser = new JFileChooser();
            saveAsFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            saveAsFileChooser.setApproveButtonText("Save");
            //saveAsFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Jpeg File", "jpg"));
            saveAsFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Png file", "png"));
            saveAsFileChooser.setFileFilter(new FileNameExtensionFilter("Jpeg File", "jpg"));
            int actionDialog = saveAsFileChooser.showOpenDialog(this);
            if (actionDialog != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = saveAsFileChooser.getSelectedFile();
            if (!file.getName().endsWith(".jpg") & (!file.getName().endsWith(".png"))) {
                file = new File(file.getAbsolutePath() + ".jpg");
            }

            try {
                ImageIO.write(image, "jpg", file);
                JOptionPane.showMessageDialog(null, "Image saved successfully!", ProgramTitle, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error saving image to file: " + ex.toString(), ProgramTitle, JOptionPane.INFORMATION_MESSAGE);
                ex.printStackTrace();
            }

        });
        itemImportImage.addActionListener(actionEvent -> {
            //Need to fix so when i import it i can still paint on it
            //Do this by transfering image to pixels
            final JFileChooser openFileChooser = new JFileChooser();
            openFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            openFileChooser.setApproveButtonText("Import");
            //saveAsFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Jpeg File", "jpg"));
            openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Png file", "png"));
            openFileChooser.setFileFilter(new FileNameExtensionFilter("Jpeg File", "jpg"));
            int actionDialog = openFileChooser.showOpenDialog(this);
            if (actionDialog != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = openFileChooser.getSelectedFile();
            if (!file.getName().endsWith(".jpg") & (!file.getName().endsWith(".png"))) {
                file = new File(file.getAbsolutePath() + ".jpg");
            }

            try {
                rawImage = ImageIO.read(file);
                // Since the type of image is unknown it must be copied into an INT_RGB
                image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(rawImage, 0, 0, null);
                this.width = image.getWidth();
                this.height = image.getHeight();
                pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
                /*image = new BufferedImage(image.getWidth(), image.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(image, 0, 0, null);*/
                /*image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();*/
                JOptionPane.showMessageDialog(null, "Image imported successfully!", ProgramTitle, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error importing image: " + ex.toString(), ProgramTitle, JOptionPane.INFORMATION_MESSAGE);
                ex.printStackTrace();
            }
            /*
            Something like this:
                    BufferedImage image = null;
        try {
            BufferedImage rawImage = ImageIO.read(new File(path));
            // Since the type of image is unknown it must be copied into an INT_RGB
            image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            image.getGraphics().drawImage(rawImage, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
             */
        });
        itemAbout.addActionListener(actionEvent -> {
            JPanel jp = new JPanel();
            jp.setLayout(new BorderLayout());
            JLabel jl = new JLabel(
                    "<html>About <font color=blue><b>this program</font>:"
                            + "</b><br><br></html>");
            JLabel jl2 = new JLabel(
                    "<html>Made by <font color=blue><b>Pierre Lundström</font>."
                            + "</b><br><br></html>");
            Font font = new Font("Arial", java.awt.Font.PLAIN, 14);
            jl.setFont(font);
            jl2.setFont(font);
            jp.add(jl, BorderLayout.NORTH);
            jp.add(jl2);
            if (JOptionPane.showConfirmDialog(this, jp, ProgramTitle,
                    JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(resizedLogo))  == 0) {

            }
        });
        itemChangePaintColor.addActionListener(actionEvent -> {
            if (!firstTime) {
                JPanel jp = new JPanel();
                jp.setLayout(new BorderLayout());
                JLabel jl = new JLabel(
                        "<html>Enter <font color=blue><b>Paint Color</font>:  (current: " + square1.getColor() + ")"
                                + "</b><br><br></html>");
                Font font = new Font("Arial", java.awt.Font.PLAIN, 14);
                JTextField txt = new JTextField();
                txt.setFocusable(true);
                txt.setEditable(true);
                txt.setToolTipText("Enter paint color here (current: " + square1.getColor() + ")");
                txt.setSelectedTextColor(Color.RED);
                txt.setForeground(Color.BLUE);
                jl.setFont(font);
                txt.setFont(font);
                jp.add(jl, BorderLayout.NORTH);
                jp.add(txt);
                txt.requestFocus();
                if (JOptionPane.showConfirmDialog(this, jp, ProgramTitle,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(resizedLogo)) == 0) {
                    if (txt != null) {
                        if (!txt.getText().trim().equals("")) {
                            //int color = (int) Long.parseLong(txt.getText(), 16);
                            //int color = Color.decode();
                            //int color = Integer.parseInt(txt.getText().trim().replaceFirst("^#",""), 16);
                            int color = Integer.parseInt(txt.getText().trim());
                            square1.setColor(color);

                        } else {
                            JOptionPane.showMessageDialog(null, "Color cannot be null!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid color integer!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Input Cancelled");
                }
            } else {
                JOptionPane.showMessageDialog(null, "You haven't clicked with the mouse yet so the color is not set yet!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
            }
        });
        itemSaveToConfig.addActionListener(actionEvent -> {
            closeListener = false;
            saveToConfig();
        });
        itemResetConfigOverride.addActionListener(actionEvent -> {
            resetConfigOverride();
        });

        //Add item for paint color list guide (int -> color name/hexcolor code)
        //or else if statements
        //Add item for save to config, add save to close listener
        //Add item for reset config override
        //Actually for erasing I just need to pick black color then it erases it because the background is black by default, can add item for that later
    }

    /**
     * Reads data from config
     * and sometimes writes to config
     */
    private void runConfigCheck() {
        File core = new File(paintCoreDir);
        if (!core.exists()) {
            core.mkdirs();
        }
        File logs = new File(paintLogsDir);
        if (!logs.exists()) {
            logs.mkdirs();
        }
        config = new File(System.getProperty("user.dir") + "/" + paintCoreDir + "/" + configFileName);
        boolean empty = !config.exists() || config.length() == 0;
        resetConfigIfEmpty();

        //Also if not empty file read from config and add port and ip to variables from config
        if (!empty) {
            //Read config and add to variables
            try (Stream<String> all_lines = Files.lines(Paths.get(System.getProperty("user.dir") + "/" + paintCoreDir + "/" + configFileName))) {
                String program_title = all_lines.skip(2).findFirst().get().substring(16);
                String paint_color = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/" + paintCoreDir + "/" + configFileName)).get(3).substring(14);
                ProgramTitle = program_title;
                if (firstTime) {
                    //I had to create new sprite because otherwise square1 is null and I can't set color
                    //anyways now it keeps the color from the previous time application was opened
                    square1 = new Sprite(16,16, Integer.parseInt(paint_color));
                    firstTime = false;
                } else {
                    square1.setColor(Integer.parseInt(paint_color));
                }
                System.out.println("Running config check...");
                System.out.println("Program Title: " + ProgramTitle);
                System.out.println("Paint Color: " + paint_color);

            }       catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Couldnt read file " + configFileName + "!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
                resetConfigOverride();
                ex.printStackTrace();
            }
        }
    }


    /**
     * Resets config to default values if empty
     */
    public void resetConfigIfEmpty() {
        config = new File(System.getProperty("user.dir") + "/" + paintCoreDir + "/" + configFileName);
        boolean empty = !config.exists() || config.length() == 0;
        try {
            if (empty) {
                PrintWriter writer = new PrintWriter(config);
                writer.println("# Created config file at " + new Date(System.currentTimeMillis()));
                writer.println("# Copyright @ " + programAuthor);
                writer.println("program_title = " + ProgramTitle);
                /*if (square1 != null) {
                    writer.println("paint_color = " + square1.getColor());
                } else {*/
                    writer.println("paint_color = " + defaultColor);
               // }
                writer.close();
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Couldnt find file " + configFileName + "!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * A method for resetting config to default values even if it's not empty
     */
    public void resetConfigOverride() {
        config = new File(System.getProperty("user.dir") + "/" + paintCoreDir + "/" + configFileName);
//boolean empty = !config.exists() || config.length() == 0;
        try {
//            if (empty) {
            PrintWriter writer = new PrintWriter(config);
            writer.println("# Created config file at " + new Date(System.currentTimeMillis()));
            writer.println("# Copyright @ " + programAuthor);
            writer.println("program_title = " + ProgramTitle);
            writer.println("paint_color = " + defaultColor);
            writer.close();
            JOptionPane.showMessageDialog(this, "Successfully resetted config", ProgramTitle, JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Couldnt find file " + configFileName + "!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Save variables to config
     */
    public void saveToConfig() {
        config = new File(System.getProperty("user.dir") + "/" + paintCoreDir + "/" + configFileName);
//boolean empty = !config.exists() || config.length() == 0;
        try {
//            if (empty) {
            PrintWriter writer = new PrintWriter(config);
            writer.println("# Created config file at " + new Date(System.currentTimeMillis()));
            writer.println("# Copyright @ " + programAuthor);
            writer.println("program_title = " + ProgramTitle);
            writer.println("paint_color = " + square1.getColor());
            writer.close();
            if (!closeListener) {
                JOptionPane.showMessageDialog(this, "Successfully saved to config", ProgramTitle, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Couldnt find file " + configFileName + "!", ProgramTitle, JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    /**
     * Initializes Keystrokes
     */
    private void initKeystrokes() {
        itemExitProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_MASK));
    }
    /**
     * Initializes Images
     */
    private void initImages() {
        try {
            logo = ImageIO.read(
                    getClass().getResource("../img/logo.jpeg"));
            frame.setIconImage(logo);
            resizedLogo = resize(logo, 40, 40);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/file.png"));
            BufferedImage resizedImage = resize(image, 40, 40)  ;
            menuFile.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/tools.png"));
            BufferedImage resizedImage = resize(image, 40, 40)  ;
            menuTools.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/settings.png"));
            BufferedImage resizedImage = resize(image, 40, 40)  ;
            menuSettings.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/preferences.png"));
            BufferedImage resizedImage = resize(image, 40, 40)  ;
            menuPreferences.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/about.png"));
            BufferedImage resizedImage = resize(image, 40, 40)  ;
            menuAbout.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/exit.png"));
            BufferedImage resizedImage = resize(image, 40, 40);
            itemExitProgram.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/logo.jpeg"));
            BufferedImage resizedImage = resize(image, 40, 40);
            itemAbout.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(getClass().getResource("../img/changecolor.png"));
            BufferedImage resizedImage = resize(image, 40, 40);
            itemChangePaintColor.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

    /**
     * Initializes Listeners
     */
    private void initListeners() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                String ObjButtons[] = {"Yes","No"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit this program?",ProgramTitle + " WARNING",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==JOptionPane.YES_OPTION)
                {
                    //frame.dispose();
                    closeListener = true;
                    saveToConfig();
                    System.exit(0);
                }
            }
        });

    }
    /**
     * Confirm exit method
     */
    private void confirmExitDialog() {
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        JLabel jl = new JLabel(
                "<html>Are you <font color=red><b>sure that you want to exit the program?</font>"
                        + "</b><br><br></html>");
        Font font = new Font("Arial", java.awt.Font.PLAIN, 14);
        jl.setFont(font);
        jp.add(jl, BorderLayout.NORTH);
        if (JOptionPane.showConfirmDialog(this, jp, ProgramTitle,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(resizedLogo))  == 0) {
            //frame.dispose();
            System.exit(0);
        }
    }

    /**
     * Custom resize bufferedimage solution
     * @param image
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    /**
     * Changes all Fonts in container
     */
    public static void changeAllFont ( Component component, Font font )
    {
        component.setFont ( font );
        if ( component instanceof Container )
        {
            for ( Component child : ( ( Container ) component ).getComponents () )
            {
                changeAllFont ( child, font );
            }
        }
    }


    /**
     * Draw method
     */
    private void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        java.awt.Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    private void update() {
        if (!firstTime) {
            t += Math.PI / 180;

            for (int i = 0; i < square1.getHeight(); i++) {
                for (int j = 0; j < square1.getWidth(); j++) {
                    // if ((ySquare1+i)* * width <= (width * scale) & (xSquare1+)) {

                    //}
                    try {
                        //if ((ySquare1 + i) * height + j > height) {
                        //     System.out.println("return because y is of out bounds");
                        // }
                        //if (j <= width*scale & i < (height*mainMenuBar.getSize().getWidth())*scale || j <= width*scale & i <= (-height * scale)) {
                        pixels[(ySquare1 + i) * width + xSquare1 + j] = square1.getPixels()[i * square1.getWidth() + j];
                        //  System.out.println((ySquare1 + i) * width + xSquare1 + j + "= " + square1.getPixels()[i * square1.getWidth() + j]);
                        //}
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        //System.out.println("IndexOutOfBounds Exception: " + ex.toString());
                    }
                }
            }
        }

    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double frameUpdateinteval = 1000000000.0 / fps;
        double stateUpdateinteval = 1000000000.0 / ups;
        double deltaFrame = 0;
        double deltaUpdate = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            deltaFrame += (now - lastTime) / frameUpdateinteval;
            deltaUpdate += (now - lastTime) / stateUpdateinteval;
            lastTime = now;

            while (deltaUpdate >= 1) {
                update();
                deltaUpdate--;
            }

            while (deltaFrame >= 1) {
                draw();
                deltaFrame--;
            }
        }
        stop();
    }

    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (firstTime) {
                square1 = new Sprite(16,16,0xFF00FF);
                firstTime = false;
            }
            if (keyEvent.getKeyChar()=='a') {
                //vxSquare1 = -5;
                xSquare1 -= 5;
            } else if (keyEvent.getKeyChar()=='d') {
                //vxSquare1 = 5;
                xSquare1 += 5;
            } else if (keyEvent.getKeyChar()=='w') {
                //vySquare1 = -5;
                ySquare1 -= 5;
            } else if (keyEvent.getKeyChar()=='s') {
                //vySquare1 = 5;
                ySquare1 += 5;
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar()=='a' || keyEvent.getKeyChar()=='d') {
                //vxSquare1 = 0;
                //xSquare1 = 5;
            } else if (keyEvent.getKeyChar()=='w' || keyEvent.getKeyChar()=='s') {
                //vySquare1 = 0;
                //ySquare1 = 5;
            }
        }
    }

    private class MyMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getY() > height*scale || e.getX() > width*scale) {
                //System.out.println("return because x or y is out of bounds");
            } else {
                if (firstTime) {
                    square1 = new Sprite(16,16,0xFF00FF);
                    firstTime = false;
                }
                xSquare1 = e.getX()/scale;
                ySquare1 = e.getY()/scale;
            }
            //if (e.getX() <= width*scale && e.getY() <= height*scale) {
            //System.out.println("h: " + mainMenuBar.getSize().getHeight() + " w: " + mainMenuBar.getSize().getWidth());
            //if (e.getY() <= (height - mainMenuBar.getSize().getHeight())*scale & e.getX() <= ((width - 10) *scale)) {
                //xSquare1 = e.getX()/scale-(square1.getWidth()/2);
                //ySquare1 = e.getY()/scale-(square1.getHeight()/2);
            //}

        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    private class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.getY() > height*scale || mouseEvent.getX() > width*scale) {
                //System.out.println("return because x or y is out of bounds");
            } else {
                /*if (firstTime) {
                    square1 = new Sprite(16,16,defaultColor);
                    firstTime = false;
                }*/
                xSquare1 = mouseEvent.getX() / scale;
                ySquare1 = mouseEvent.getY() / scale;
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }


}