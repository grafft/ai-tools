package ru.isa.ai.dhm;

import cern.colt.matrix.tbit.BitMatrix;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.isa.ai.olddhm.poolers.Pair;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by gmdidro on 11.02.2015.
 */
public class HTMNetworkSettings {
    public boolean debug=false;

    public ArrayList<DHMSettings> regions;
    public ArrayList<Pair<Integer,Integer>> nodeConnection;
    public ArrayList<String> inputSources;

    private ArrayList<Pair<Integer,Integer>> getPairsWithId(final Integer id,final boolean first)
    {
        final ArrayList<Pair<Integer,Integer>> res=new ArrayList<>();

        nodeConnection.forEach(new Consumer<Pair<Integer, Integer>>() {
                                          @Override
                                          public void accept(Pair<Integer, Integer> pair) {
                                              if(id == (first ? pair.getLeft():pair.getRight()))
                                                res.add(pair);
                                          }
                                      }
        );
        return res;
    }

    public ArrayList<Pair<Integer,Integer>> getPairsWithFirstId(Integer id)  {
        return getPairsWithId(id, true);
    }

    public ArrayList<Pair<Integer,Integer>> getPairsWithSecondId(Integer id)  {
        return getPairsWithId(id, false);
    }

    public void save(String filepath){

        Serializer serializer = new Persister();

        File result = new File(filepath);

        try {
            serializer.write(this, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HTMNetworkSettings load(String filepath)  {

        Serializer serializer = new Persister();
        File source = new File(filepath);

        HTMNetworkSettings settings = null;
        try {
            settings = serializer.read(HTMNetworkSettings.class, source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings;
    }
}
