package ru.isa.ai.dhm;

import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.olddhm.poolers.Pair;

import java.util.ArrayList;

/**
 * Created by gmdidro on 12.02.2015.
 */
public class HTMConsole{

    public class ConsoleParams
    {
        public ConsoleParams(String htmConfigPath){this.htmConfigPath=htmConfigPath;}
        public String htmConfigPath;
    }

    public static void main(String[] args)
    {
        HTMNetworkSettings s=new HTMNetworkSettings();
        s.regions.add(new HTMRegionSettings(1));
        s.nodeConnection.add(new Pair<Integer, Integer>(1,2));
        s.save("asd.xml");
        //HTMNetworkSettings.load();
        /*

        HTMConsole con=new HTMConsole();
        ConsoleParams params=con.commandLineArgsProcessing(args);

        HTMNetworkSettings settings=HTMNetworkSettings.load(params.htmConfigPath);

        Neocortex neocortex=con.neocortexConstruction(settings);
        neocortex.initialization();
*/
    }

    private Neocortex neocortexConstruction(HTMNetworkSettings netset) {
        Neocortex neocortex=new Neocortex();
        for(int i=0;i<netset.regions.size();i++)
        {
            HTMRegionSettings regset=netset.regions.get(i);
            ArrayList<Pair<Integer,Integer>> arr=netset.getPairsWithFirstId(regset.id);

            //TODO P: add multiparent region ability to neocortex
            //neocortex.addRegion(regset,netset.regions.get(arr.get(0).getRight()));

        }
        return null;

    }

    private ConsoleParams commandLineArgsProcessing(String[] args) {
        return new ConsoleParams(args[0]);

    }
}
