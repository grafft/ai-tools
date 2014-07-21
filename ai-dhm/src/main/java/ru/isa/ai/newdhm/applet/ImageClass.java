package ru.isa.ai.newdhm.applet;

import cern.colt.matrix.tbit.BitMatrix;
import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.net.URL;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class ImageClass extends JPanel {
    private BufferedImage bi = null;
    private Toolkit toolkit;
    private MediaTracker tracker;
    private int width;
    private int height;
    private int[] pixels;
    private Image image;
    private int imageType;

    /*
     Если задан относительный путь, то он отсчитывается относительно корневой папки с классами или папки с jar-архивом.
     Это работает и при загрузке из JAR-архива (только для относительного пути).
     @param path полный/относительный путь к картинке.
     */
    public void load(String path) {
        Image im = null;
        URL url = getClass().getResource(path);
        if (url != null) {
            // Если относительный путь в папке с классми.
            try {
                im = ImageIO.read(url);
            } catch (IOException ex) {
            }
            if (im!=null)
                loadBufferedIm(im);
        } else {
            // Если полный путь.
            loadImage(path);
            if (image == null) {
                // Если относительный путь от корневой папки проекта или папки где лежит JAR-архив.
                File f = new File(path);
                if (f.isFile() && f.exists())
                    loadImage(f.getAbsolutePath());
            }
        }
    }

    public void loadImage(String path) {
        Image im = null;
        toolkit = Toolkit.getDefaultToolkit();
        tracker = new MediaTracker(this);
        try {
            im = toolkit.getImage(path);
            tracker.addImage(im, 0);
            // load all the image for later use
            tracker.waitForAll();
        } catch (InterruptedException ex) {
        }
        if (im.getWidth(this) != -1) loadBufferedIm(im);
    }

    public void setBufferedImage(BufferedImage im){
        image = null;
        width = im.getWidth(this);
        height = im.getHeight(this);
        bi = im;
        Graphics2D big = bi.createGraphics();
        //  // Выводим изображение image в графический контекст
        big.drawImage(bi, 0, 0, this);
        imageType = bi.getType();
    }

    public BufferedImage getBufferedImage(){
        return bi;
    }

    public void loadBufferedIm(Image im) {
        image = im;
        width = image.getWidth(this);
        height = image.getHeight(this);

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

    public BufferedImage createBufferedImFromBitMatrix(BitMatrix m, int w, int h){
        BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int j = 0; j < h; j++)
            for (int i = 0; i < w; i++) {
                if (m.get(i, j) == true)
                    im.setRGB(i, j, Color.gray.getRGB());
                else
                    im.setRGB(i, j, Color.lightGray.getRGB());
            }

        return im;
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





