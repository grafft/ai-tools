package ru.isa.ai.tests.mipt;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Aleksandr on 17.02.2015.
 */
public class InheritanceTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new MyPanel());

        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}

class MyPanel extends JPanel {
    public void paintComponent(Graphics g) {
        g.drawLine(0, 0, 100, 100);
    }
}
