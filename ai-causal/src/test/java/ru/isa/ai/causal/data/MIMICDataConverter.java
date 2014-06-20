package ru.isa.ai.causal.data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 11.06.2014
 * Time: 17:03
 */
public class MIMICDataConverter {
    public static void main(String[] args) throws IOException {
        BufferedReader readerData = new BufferedReader(new FileReader(MIMICDataConverter.class.getResource("/all_data.txt").getPath()));
        BufferedReader readerFeature = new BufferedReader(new FileReader(MIMICDataConverter.class.getResource("/all_features.txt").getPath()));

        Map<Integer, String> features = new HashMap<>();
        Map<Integer, Integer> featuresStats = new HashMap<>();
        List<double[]> objects = new ArrayList<>();
        String line;
        int featureCounter = 0;
        while ((line = readerFeature.readLine()) != null) {
            String parts[] = line.split("\t");
            features.put(featureCounter, parts[1]);
            featureCounter++;
        }

        while ((line = readerData.readLine()) != null) {
            String parts[] = line.split("\t");
            double[] object = new double[features.size()];
            int counter = 0;
            for (int key : features.keySet()) {
                if (!parts[counter].equals("-")) {
                    object[counter] = Double.parseDouble(parts[counter]);
                    if (featuresStats.get(key) == null)
                        featuresStats.put(key, 1);
                    else
                        featuresStats.put(key, featuresStats.get(key) + 1);
                } else {
                    object[counter] = Double.MAX_VALUE;
                }
                counter++;
            }
            objects.add(object);
        }
        readerData.close();
        readerFeature.close();

        PrintWriter writer = new PrintWriter(new FileWriter("all_data_norm.csv"));
        StringBuilder featureBuilder = new StringBuilder();
        int counter = 0;
        List<Integer> acceptedFeatures = new ArrayList<>();
        for (int key : features.keySet()) {
            if (featuresStats.get(key) != null && featuresStats.get(key) > objects.size() * 0.9) {
                if (counter != 0)
                    featureBuilder.append("\t");
                featureBuilder.append(features.get(key));
                acceptedFeatures.add(key);
                counter++;
            }
        }
        writer.println(featureBuilder.toString());
        for (double[] object : objects) {
            StringBuilder objectBuilder = new StringBuilder();
            boolean toWrite = true;
            int counterAcc = 0;
            for (int key : acceptedFeatures) {
                if (counterAcc != 0)
                    objectBuilder.append("\t");
                if (object[key] == Double.MAX_VALUE) {
                    toWrite = false;
                    break;
                } else {
                    objectBuilder.append(object[key]);
                }
                counterAcc++;
            }
            if (toWrite)
                writer.println(objectBuilder.toString());
        }
        writer.flush();
        writer.close();
    }
}
