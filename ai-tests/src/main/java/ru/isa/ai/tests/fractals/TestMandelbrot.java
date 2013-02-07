package ru.isa.ai.tests.fractals;

/**
 * Author: Aleksandr Panov
 * Date: 09.01.13
 * Time: 18:16
 */
public class TestMandelbrot {
    public static void main(String[] args) {
        double realCoord, imagCoord;
        double realTemp, imagTemp, realTemp2, arg;
        int iterations;
        for (imagCoord = 1.2; imagCoord >= -1.2; imagCoord -= 0.05) {
            for (realCoord = -0.6; realCoord <= 1.77; realCoord += 0.03) {
                iterations = 0;
                realTemp = realCoord;
                imagTemp = imagCoord;
                arg = (realCoord * realCoord) + (imagCoord * imagCoord);
                while ((arg < 4) && (iterations < 40)) {
                    realTemp2 = (realTemp * realTemp) - (imagTemp * imagTemp) - realCoord;
                    imagTemp = (2 * realTemp * imagTemp) - imagCoord;
                    realTemp = realTemp2;
                    arg = (realTemp * realTemp) + (imagTemp * imagTemp);
                    iterations += 1;
                }
                switch (iterations % 4) {
                    case 0:
                        System.out.print(".");
                        break;
                    case 1:
                        System.out.print("o");
                        break;
                    case 2:
                        System.out.print("0");
                        break;
                    case 3:
                        System.out.print("@");
                        break;
                }
            }
            System.out.println();
        }
    }
}
