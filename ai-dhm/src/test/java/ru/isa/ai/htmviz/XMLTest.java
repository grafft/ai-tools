package ru.isa.ai.htmviz;

import com.google.gson.Gson;
import junit.framework.TestCase;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.core.Region;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by APetrov on 25.05.2015.
 */
public class XMLTest extends TestCase {



    public void testRun()
    {
       HTMSerialization fe = new HTMSerialization();
        HTMSerialization.Region r=fe.new Region();
        r.height=1;
        r.width=1;
        HTMSerialization.Column c=fe.new Column();
        c.cells.add(fe.new Cell());
        r.cols.add(c)                                 ;


        HTMSerialization.Region r2=fe.new Region();
        r2.height=2;
        r2.width=2;

        fe.regions.add(r);





        Gson gson = new Gson();
        String json = gson.toJson(fe);
        System.out.print(json);

       /* FileOutputStream fos1 = null;
        try {
            fos1 = new FileOutputStream("C:\\_Projecting\\_Aspir\\ProjectA\\My\\_htm\\ser.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        java.beans.XMLEncoder xe1 = new java.beans.XMLEncoder(fos1);
        xe1.writeObject(fe);
        xe1.flush();
        xe1.close();  */
    }
}
