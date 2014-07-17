package ru.isa.ai.newdhm.applet;

import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;

import java.awt.*;
import java.awt.geom.*;
import java.awt. image.*;
import java.awt.event.*;

public class test extends Frame{

    private BufferedImage bi;

    public test(String path){

        Toolkit tk =  Toolkit.getDefaultToolkit();
        Image img = null;
        MediaTracker tracker = new MediaTracker(this);
        try {
            img = tk.getImage(path);
            tracker.addImage(img, 0);
            tracker.waitForAll();
        } catch (InterruptedException ex) {
        }

        int width = img.getWidth(this);
        int height = img.getHeight(this);
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Создаем графический контекст big изображения bi
        Graphics2D big = bi.createGraphics();
      //  // Выводим изображение img в графический контекст
        big.drawImage(img, 0, 0, this);
     }

    public static BufferedImage enlarge(BufferedImage image, int n) {
        int w = (n+1) * image.getWidth() +1 ;
        int h = (n+1) * image.getHeight() + 1;
        BufferedImage enlargedImage =
                new BufferedImage(w, h, image.getType());
        Color c;
        int q = 0, p = 0;
        boolean fl = false;
        for (int y=0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                if (y % (w / image.getWidth()) == 0) {
                    c = new Color(0, 0, 0);
                    if (!fl) {q++; fl = true;}
                    enlargedImage.setRGB(x, y, c.getRGB());
                } else if (x % (w / image.getWidth()) == 0) {
                    p++;
                    c = new Color(0, 0, 0);
                    enlargedImage.setRGB(x, y, c.getRGB());
                } else {
                    enlargedImage.setRGB(x, y,image.getRGB((x -p) / n, (y - q) / n));
                }
            }
            fl=false;
            p = 0;
        }
        return enlargedImage;
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        int w = getSize().width;
        int h = getSize().height;

        int n = 10;
        BufferedImage biLarge = new BufferedImage(bi.getWidth() * n, bi.getHeight()* n, BufferedImage.TYPE_INT_RGB);
        biLarge = enlarge(bi, n);

        /*int bw = biLarge.getWidth(this);
        int bh = biLarge.getHeight(this);



        // Создаем аффинное преобразование
        AffineTransform at = new AffineTransform( );
        at.rotate(Math.PI/4);     // Задаем поворот на 45 градусов
        //по часовой стрелке вокруг левого верхнего угла.
        //Затем сдвигаем изображение вправо на величину bw
        at.preConcatenate(new AffineTransform(1, 0, 0, 1, bw, 0));
        // Определяем область хранения bimg преобразованного
        // изображения. Ее размер вдвое больше исходного
        BufferedImage bimg =
                new BufferedImage(2*bw, 2*bw, BufferedImage.TYPE_INT_RGB);
// Создаем объект biop,. содержащий преобразование at
        BufferedImageOp biop = new AffineTransformOp(at,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
// Преобразуем изображение, результат заносим в bimg
        biop.filter(biLarge, bimg);*/
// Выводим исходное изображение.
        g2.drawImage(biLarge, null, 100, 300);

// Выводим измененную преобразованием Ыор область bi
    ///  g2.drawImage(biLarge, biop, w/4+3, 30);
  }

    public static void main(String[] args){
        final String IMAGE_PATH = "D:\\work_folder\\image1.png";
        Frame f = new test(IMAGE_PATH);
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

        f.setSize(700, 700);
        f.setVisible(true) ;
    }

}
