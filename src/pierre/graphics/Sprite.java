package pierre.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * This is a class
 * Created 2020-03-26
 *
 * @author Magnus Silverdal
 */
public class Sprite {
    private int width;
    private int height;
    private int[] pixels;
    private int color;

    public Sprite(int w, int h) {
        this.width = w;
        this.height = h;
        pixels = new int[w*h];
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = 0xFFFFFF;
        }
    }

    public Sprite(int w, int h, int col) {
        this.width = w;
        this.height = h;
        this.color = col;
        pixels = new int[w*h];
        for (int i = 0 ; i < pixels.length ; i++) {
                pixels[i] = col;
        }
    }


    public Sprite(String path) {
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
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setColor(int color) {
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = color;
        }
        this.color = color;
        //Graphics.colorBeforeErase = color;
        Graphics.currentPaintColor = color;
        System.out.println("Set Color to " + color);
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public  void setHeight(int h) {
        this.height = h;
    }

    public void setEraseColor(int color) {
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = color;
        }
        this.color = color;
        System.out.println("Set Erase Color to " + color);
    }

    public int getColor() {
        return color;
    }
}