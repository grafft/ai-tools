package ru.isa.ai.ourhtm.spatialpooler.tests;

import cern.colt.matrix.tbit.BitVector;
import junit.framework.TestCase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import ru.isa.ai.ourhtm.algorithms.SpatialPooler;
import ru.isa.ai.ourhtm.algorithms.VerySimpleMapper;
import ru.isa.ai.ourhtm.structure.Column;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Region;
import ru.isa.ai.ourhtm.util.MathUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by APetrov on 22.07.2015.
 */
public class SpatialPoolerImageTest extends TestCase {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public void testRun() throws IOException {

    }

    public void testImage() throws IOException {
        Mat image = Highgui.imread("binary.jpg");
        int[] input = new int[image.width()*image.height()];


        for(int i=0;i<image.height();i++)
            for(int j=0;j<image.width();j++) {
                if (image.get(i, j)[0] > 125) {

                    input[i * image.width() + j] = 1;
                }
                else
                    input[i * image.width() + j] = 0;
            }

       /*
        Mat out2=new Mat(image.height(),image.width(), CvType.CV_8UC1);

        for(int i=0;i<image.height();i++)
            for(int j=0;j<image.width();j++) {
                if (input[i*image.width()+j]==1)
                    out2.put(i,j,255);
                else
                    out2.put(i,j,0);

            }
        Highgui.imwrite("out2.jpg", out2);
*/









        final int W=image.width(), H=image.height();
        System.out.println("W="+W);
        System.out.println("H="+H);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 3;
        settings.connectedPct=1;
        settings.connectedPerm=0.01;
        settings.xInput=W;
        settings.yInput=H;
        settings.potentialRadius=2;
        settings.xDimension=40;
        settings.yDimension=40;
        settings.initialInhibitionRadius=1;




        Region r=new Region(settings,new VerySimpleMapper());

        SpatialPooler sp=new SpatialPooler(settings);
        BitVector inputbit=new BitVector(input.length);
        MathUtils.assign(inputbit, input);
        for(Column c:r.getColumns())
            c.setIsActive(false);
        int[] ov=sp.updateOverlaps(r.getColumns(), inputbit);
        sp.inhibitionPhase(r.getColumns(), ov);
        sp.learningPhase(r.getColumns(), inputbit, ov);

//            System.out.println("COLS:");
        ArrayList<Column> cols=r.getColumns();

        Mat out=new Mat(settings.xDimension,settings.yDimension, CvType.CV_8UC1);

        for(int i=0;i<settings.yDimension;i++)
            for(int j=0;j<settings.xDimension;j++) {
                if (cols.get(i*settings.yDimension+j).isActive())
                    out.put(i,j,255);
                else
                    out.put(i,j,0);

            }
        Highgui.imwrite("out.jpg",out);


        return;
    }
}
