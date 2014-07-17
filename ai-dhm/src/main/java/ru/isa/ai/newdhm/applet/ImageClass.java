package ru.isa.ai.newdhm.applet;

import cern.colt.matrix.tbit.BitMatrix;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.geom.*;

public class ImageClass extends JPanel {
    BufferedImage bi = null;
    Toolkit toolkit;
    MediaTracker tracker;
    int width;
    int height;
    int[] pixels;
    Image image;

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
        pixels = new int[width * height];
        PixelGrabber px = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            px.grabPixels();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public boolean handlesinglepixel(int x, int y, int pixel) {
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
                matrix.put(i, j, handlesinglepixel(i, j, pixels[j * width + i]));
            }
        return matrix;
    }

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }

    public void paintAffTranf(Graphics g, double angleInRadians, int otstup) { //Math.PI/4
        Graphics2D g2 = (Graphics2D) g;
        // Создаем аффинное преобразование
        AffineTransform at = new AffineTransform();
        at.rotate(angleInRadians);     // Задаем поворот на x градусов
        //по часовой стрелке вокруг левого верхнего угла.
        //Затем сдвигаем изображение вправо на величину otstup
        at.preConcatenate(new AffineTransform(1, 0, 0, 1, otstup, 0));
        // Определяем область хранения bimg преобразованного
        // изображения. Ее размер вдвое больше исходного
        BufferedImage bimg =
                new BufferedImage(2 * otstup, 2 * otstup, BufferedImage.TYPE_INT_ARGB);
        // Создаем объект biop,. содержащий преобразование at
        BufferedImageOp biop = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        // Преобразуем изображение, результат заносим в bimg
        biop.filter(bi, bimg);
        // Выводим исходное изображение.
        g2.drawImage(bi, null, 10, 30);

        // Выводим измененную преобразованием область bi
        g2.drawImage(bi, biop, width / 4 + 3, 30);

        // Выводим преобразованное внутри области bimg изображение
        g2.drawImage(bimg, null, width / 2 + 3, 30);
    }
}





