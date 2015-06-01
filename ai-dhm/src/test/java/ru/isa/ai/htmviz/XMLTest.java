package ru.isa.ai.htmviz;

import junit.framework.TestCase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by APetrov on 25.05.2015.
 */
public class XMLTest extends TestCase {



    public void testRun()
    {
        NPair fe = new NPair();
        fe.setNumber1(12);
        fe.setNumber2(13);
        fe.npair=new NPair();
        fe.npair.setNumber1(22);
        fe.npair.setNumber1(23);
        FileOutputStream fos1 = null;
        try {
            fos1 = new FileOutputStream("C:\\_Projecting\\_Aspir\\ProjectA\\My\\_htm\\ser.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        java.beans.XMLEncoder xe1 = new java.beans.XMLEncoder(fos1);
        xe1.writeObject(fe);
        xe1.flush();
        xe1.close();
    }
}
