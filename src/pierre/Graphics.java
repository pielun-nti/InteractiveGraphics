package pierre;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

/**
 * This is a class
 * Created 2020-03-26
 *
 * @author Magnus Silverdal, Pierre Lundström
 */
public class Graphics extends Canvas implements Runnable {
    private String title = "Graphics";
    private int width;
    private int height;

    private JFrame frame;
    private BufferedImage image;
    private int[] pixels;

    private Thread thread;
    private boolean running = false;
    private int fps = 60;
    private int ups = 10;

    private Sprite s;
    private Sprite stwo;

    public Graphics(int w, int h) {
        this.width = w;
        this.height = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        frame = new JFrame();
        frame.setTitle(title);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        s = new Sprite(32,32);
        stwo = new Sprite(80,80);
    }

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
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = 0;
        }

        int x = (int)(Math.random()*(width-32));
        int y = (int)(Math.random()*(height-32));

        for (int i = 0 ; i < s.getHeight() ; i++) {
            for (int j = 0 ; j < s.getWidth() ; j++) {
                pixels[(y+i)*width + x+j] = s.getPixels()[i*s.getWidth()+j];
            }
        }

        int xtwo = (int)(Math.random()*(width-80));
        int ytwo = (int)(Math.random()*(height-80));

        for (int i = 0 ; i < stwo.getHeight() ; i++) {
            for (int j = 0 ; j < stwo.getWidth() ; j++) {
                pixels[(ytwo+i)*width + xtwo+j] = stwo.getPixels()[i*stwo.getWidth()+j];
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

}
