package ru.isa.ai.tests;

/**
 * Author: Aleksandr Panov
 * Date: 13.09.13
 * Time: 11:34
 */
public class SimpleTests {
    public static void main(String[] args){
        byte[] ascii = new byte[] {72, 97, 112, 112, 121, 32, 80, 114 ,111, 103, 114, 97, 109, 109, 101, 114, 115, 32, 68, 97, 121, 33};
        for (byte b : ascii) {
            System.out.print((char)b);
        }
    }
}
