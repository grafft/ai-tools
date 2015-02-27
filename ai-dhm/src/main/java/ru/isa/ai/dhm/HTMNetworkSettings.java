package ru.isa.ai.dhm;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.isa.ai.olddhm.poolers.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by gmdidro on 11.02.2015.
 */
// jgrapht and jung looks like redundant in our case
public class HTMNetworkSettings {
    public boolean debug=false;

    public ArrayList<HTMRegionSettings> regions=new ArrayList<>();
    private ArrayList<Pair<Integer,Integer>> nodeConnection=new ArrayList<>();
    public ArrayList<String> inputSources=new ArrayList<>();

    public void setRegionsConnection(final int id1, final int id2) throws Exception
    {
        // we have to use array here, because final limitation of closure in Java
        final boolean[] regionExistenceFlag=new boolean[2]; // flags of existence of both ids regions
        regions.forEach(new Consumer<HTMRegionSettings>() {
            @Override
            public void accept(HTMRegionSettings htmRegionSettings) {
                regionExistenceFlag[0] = regionExistenceFlag[0]||htmRegionSettings.id==id1;
                regionExistenceFlag[1] = regionExistenceFlag[1]||htmRegionSettings.id==id2;
            }
        });

        if(regionExistenceFlag[0]&&regionExistenceFlag[1])
        {
            final boolean dublicatePairFlag[] = new boolean[1];
            nodeConnection.forEach(new Consumer<Pair<Integer, Integer>>() {
                                       @Override
                                       public void accept(Pair<Integer, Integer> pair) {
                                           if(pair==new Pair<Integer, Integer>(id1,id2))
                                               dublicatePairFlag[0]=true;
                                       }
                                   }
            );
            if(dublicatePairFlag[0])
                throw new Exception("You try to add already exist connection");
            else
                nodeConnection.add(new Pair<Integer, Integer>(id1,id2));
        }
        else throw new Exception("You try to add connection between inexistence region");
    }

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
