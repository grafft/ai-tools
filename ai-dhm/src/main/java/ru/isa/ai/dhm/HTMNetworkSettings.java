package ru.isa.ai.dhm;

import cern.colt.matrix.tbit.BitMatrix;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.isa.ai.olddhm.poolers.Pair;

import java.io.File;
import java.util.*;

/**
 * Created by gmdidro on 11.02.2015.
 */
public class HTMNetworkSettings {
    public class RegionSettings {}
    //public class RegionSettings {}

    public ArrayList<RegionSettings> regions;
    public ArrayList<Pair<Integer,Integer>> regionConnection=new ArrayList<Pair<Integer, Integer>>();
    //public

    //String field1="123";
    public static void save(){

        Serializer serializer = new Persister();
        HTMNetworkSettings example = new HTMNetworkSettings();
        example.regionConnection.add(new Pair<Integer, Integer>(1,1));
        File result = new File("example.xml");

        try {
            serializer.write(example, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load()  {

        Serializer serializer = new Persister();
        File source = new File("example.xml");

        HTMNetworkSettings example = null;
        try {
            example = serializer.read(HTMNetworkSettings.class, source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Pair p : example.regionConnection)
            System.out.println(p.getLeft()+" "+p.getRight());
    }
}
