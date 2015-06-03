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



    public void testRun() {
        HTMSerialization hs = new HTMSerialization();
        HTMSerialization.Region r = hs.new Region(0);
        r.height = 2;
        r.width = 2;
        for (int i = 0; i < r.height * r.width; i++) {
            HTMSerialization.Column c = hs.new Column(i);
            c.cells.add(hs.new Cell(0));
            r.cols.add(c);
        }

        r.cols.get(0).cells.get(0).synapces.add(hs.new Synapse(1,0));
        r.cols.get(0).potentialSynapses.put(0,hs.new Synapse(1,0));

        hs.regions.add(r);

        Gson gson = new Gson();
        String json = gson.toJson(hs);
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
