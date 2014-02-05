package ru.isa.ai.tests;

import ij.ImagePlus;
import ij.process.ByteProcessor;

//import javax.media.jai.KernelJAI;

/**
 * Author: Aleksandr Panov
 * Date: 27.08.13
 * Time: 14:25
 */
public class EdgeDetectionTest {
    public static void main(String[] args){
        new ImagePlus("My new image", new ByteProcessor(400, 400)).show();
    }

    private void roberts(){
        float[] roberts_h_data = { 0.0F, 0.0F, -1.0F,
                0.0F, 1.0F, 0.0F,
                0.0F, 0.0F, 0.0F
        };
        float[] roberts_v_data = {-1.0F, 0.0F, 0.0F,
                0.0F, 1.0F, 0.0F,
                0.0F, 0.0F, 0.0F
        };
//        KernelJAI kern_h = new KernelJAI(3,3,roberts_h_data);
//        KernelJAI kern_v = new KernelJAI(3,3,roberts_v_data);
    }

    private void prewitt(){
        float[] prewitt_h_data = { 1.0F, 0.0F, -1.0F,
                1.0F, 0.0F, -1.0F,
                1.0F, 0.0F, -1.0F
        };
        float[] prewitt_v_data = {-1.0F, -1.0F, -1.0F,
                0.0F, 0.0F, 0.0F,
                1.0F, 1.0F, 1.0F
        };
//        KernelJAI kern_h = new KernelJAI(3,3,prewitt_h_data);
//        KernelJAI kern_v = new KernelJAI(3,3,prewitt_v_data);
    }

    private void freichen(){
        float[] freichen_h_data = { 1.0F, 0.0F, -1.0F,
                1.414F, 0.0F, -1.414F,
                1.0F, 0.0F, -1.0F
        };
        float[] freichen_v_data = {-1.0F, -1.414F, -1.0F,
                0.0F, 0.0F, 0.0F,
                1.0F, 1.414F, 1.0F
        };
//        KernelJAI kern_h = new KernelJAI(3,3,freichen_h_data);
//        KernelJAI kern_v = new KernelJAI(3,3,freichen_v_data);
    }
}
