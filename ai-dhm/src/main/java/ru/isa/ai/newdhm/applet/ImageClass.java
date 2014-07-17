package ru.isa.ai.newdhm.applet;

import cern.colt.matrix.tbit.BitMatrix;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.geom.*;

public class ImageClass extends JPanel {
    private BufferedImage bi = null;
    private Toolkit toolkit;
    private MediaTracker tracker;
    private int width;
    private int height;
    private int[] pixels;
    private Image image;
    private int imageType;

    public void load(String path) {
        toolkit = Toolkit.getDefaultToolkit();
        tracker = new MediaTracker(this);
        try {
            image = toolkit.getImage(path);
            tracker.addImage(image, 0);
            // load all the image for later use
            tracker.waitForAll();
        } catch (InterruptedException ex) {
        }

        width = image.getWidth(this);
        height = image.getHeight(this);

       /* byte[] bw = {(byte) 0xff, (byte) 0};
        IndexColorModel blackAndWhite = new IndexColorModel(
                1, 2, bw, bw, bw);
        bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, blackAndWhite);*/

        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = bi.createGraphics();
        //  // Выводим изображение image в графический контекст
        big.drawImage(image, 0, 0, this);

        imageType = bi.getType();
        pixels = new int[width * height];
        PixelGrabber px = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            px.grabPixels();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public boolean handleSinglePixel(int x, int y, int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        boolean value = false;
        if (red == 255 && green == 255 && blue == 255)
            value = false;
        else if (red == 0 && green == 0 && blue == 0)
            value = true;
        return value;
    }

    public BitMatrix getBitMatrix() {
        BitMatrix matrix = new BitMatrix(width, height);

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                matrix.put(i, j, handleSinglePixel(i, j, pixels[j * width + i]));
            }
        return matrix;
    }

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }

    public int getImageType() { return imageType; }

    public BufferedImage enlarge(int n) {
        int w = (n + 1) * bi.getWidth()  + 1;
        int h = (n + 1) * bi.getHeight() + 1;
        BufferedImage enlargedImage =
                new BufferedImage(w, h, imageType);
        Color c;
        int q = 0, p = 0;
        boolean fl = false;
        for (int y=0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                if (y % (w / bi.getWidth()) == 0) {
                    c = new Color(0, 0, 0);
                    if (!fl) {q++; fl = true;}
                    enlargedImage.setRGB(x, y, c.getRGB());
                } else if (x % (w / bi.getWidth()) == 0) {
                    p++;
                    c = new Color(0, 0, 0);
                    enlargedImage.setRGB(x, y, c.getRGB());
                } else {
                    enlargedImage.setRGB(x, y,bi.getRGB((x -p) / n, (y - q) / n));
                }
            }
            fl=false;
            p = 0;
        }
        return enlargedImage;
    }
}





