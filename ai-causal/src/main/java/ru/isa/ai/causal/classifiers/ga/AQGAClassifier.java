package ru.isa.ai.causal.classifiers.ga;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Author: Aleksandr Panov
 * Date: 11.06.2014
 * Time: 9:44
 */
public class AQGAClassifier extends AbstractClassifier {

    private static final Logger logger = LogManager.getLogger(AQGAClassifier.class.getSimpleName());

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);

        // class
        result.enable(Capabilities.Capability.NOMINAL_CLASS);

        return result;
    }

    @Override
    public void buildClassifier(Instances testData) throws Exception {
        // can classifier handle the data?
        getCapabilities().testWithFail(testData);
        // remove instances with missing class
        testData = new Instances(testData);
        testData.deleteWithMissingClass();

        buildRules(testData);
    }

    private void buildRules(Instances testData) throws IOException {
        int classIndex = testData.classAttribute().indexOfValue("1");
        int numObjects = testData.numInstances();
        int numObjectsPos = testData.attributeStats(testData.classIndex()).nominalCounts[classIndex];
        int numAttr = testData.numAttributes() - 1;

        long start, finish;
        int cont;
        double truthvalue;
        int cn;
        int n;
        int numgen;
        int sizegen;
        int ngen;
        int nadapt;
        int socialcard;
        int socialfine;
        boolean typega[] = {true, true, true, true, true};//тип га 0-стандартный га, 1-вга
        int typesel[] = {0, 1, 2, 2, 2};//тип селекции 0-пропорциональная, 1-ранговая, 2-турнирная
        int sizetur[] = {5, 5, 5, 7, 12};//размер турнира
        int typerec[] = {1, 1, 1, 1, 1};//тип рекомбинации 0-одноточечная, 1-двуточечная, 2-равномерная
        boolean mutadapt[] = {false, false, false, false, false};
        double mutation[] = {4, 4, 4, 4, 4};

        do {
            //truthvalue = HUGE;
            truthvalue = 2;

            start = System.currentTimeMillis();
            logger.info("Searching...");


            cn = 5;
            n = 500;
            numgen = 28;//data 13, data2 31
            sizegen = 3;
            ngen = 50;
            nadapt = 8;
            socialcard = (int) (0.05 * (n));//социальная карта
            socialfine = (int) (0.04 * (n / cn));//штраф

            int[][] tobj = new int[numObjectsPos][numAttr];
            int[][] fobj = new int[numObjects - numObjectsPos][numAttr];
            ArrayList<ArrayList<Boolean>> essential = new ArrayList<>();
            ArrayList<Integer> num_objects = new ArrayList<>();

            Enumeration instEnu = testData.enumerateInstances();
            int objCounter = 0;
            int posObjCounter = 0;
            while (instEnu.hasMoreElements()) {
                Instance instance = (Instance) instEnu.nextElement();
                Enumeration<Attribute> attrEventEnu = testData.enumerateAttributes();
                int attrCounter = 0;
                while (attrEventEnu.hasMoreElements()) {
                    Attribute attr = attrEventEnu.nextElement();
                    int value = 0;
                    switch (attr.type()) {
                        case Attribute.NOMINAL:
                            value = (int) instance.value(attr.index());
                            break;
                        case Attribute.NUMERIC:
                            double numVal = instance.value(attr.index());
                            double min = testData.attributeStats(attr.index()).numericStats.min;
                            double max = testData.attributeStats(attr.index()).numericStats.max;
                            double inter = max - min;
                            value = numVal < (min + inter) / 3 ? 1 : (numVal < min + 2 * inter / 3 ? 2 : 4);
                            break;
                    }
                    if ((int) instance.classValue() == 1)
                        tobj[posObjCounter][attrCounter] = value;
                    else
                        fobj[objCounter][attrCounter] = value;
                    attrCounter++;
                }
                objCounter++;
                posObjCounter++;
            }

            Population[] BestPop = new Population[100];
            int num_ob;
            int sizeBestPop = 0;

            int[][] tobj0 = tobj;
            while (tobj.length != 0) {
                ++sizeBestPop;
                BestPop[sizeBestPop - 1] = new Population(1, numgen, sizegen, tobj0, tobj, fobj);
                for (int restart = 0; restart < 5; ++restart) {
                    Coevolution mainCpop = new Coevolution(cn, n, numgen, sizegen, ngen, nadapt, socialcard, socialfine,
                            typega, typesel, sizetur, typerec, mutation, mutadapt,
                            truthvalue, tobj0, tobj, fobj);
                    double BestFit = -Double.MAX_VALUE;
                    mainCpop.init();
                    if (BestFit < mainCpop.bestfit) {
                        mainCpop.whatgener = 1;
                        BestFit = mainCpop.bestfit;
                    }

                    for (int h = 0; h < ngen; ++h) {
                        //процесс поиска, адаптационный интервал
                        mainCpop.adaptation();
                        ///если точное решение неизвестно
                        if (BestFit < mainCpop.bestfit) {
                            mainCpop.whatgener = h + 2;
                            BestFit = mainCpop.bestfit;
                        }
                        mainCpop.changeResourses();
                    }


                    //генетический алгоритм
                    finish = System.currentTimeMillis();
                    logger.info("Search was finished for  " + (finish - start) + "ms");

                    double bg = mainCpop.pop[0].bestgenotype.fit;
                    mainCpop.bestpop = mainCpop.pop[0];
                    for (int i = 1; i < cn; ++i) {
                        if (bg < mainCpop.pop[i].bestgenotype.fit) {
                            mainCpop.bestpop = mainCpop.pop[i];
                            bg = mainCpop.pop[i].bestgenotype.fit;
                        }
                    }
                    logger.info("\nbestfit = " + mainCpop.bestpop.bestgenotype.fit);
                    if (BestPop[sizeBestPop - 1].bestgenotype.fit < mainCpop.bestpop.bestgenotype.fit) {
                        System.arraycopy(mainCpop.bestpop.bestgenotype.genes, 0, BestPop[sizeBestPop - 1].bestgenotype.genes, 0, mainCpop.bestpop.bestgenotype.numGenes);
                        BestPop[sizeBestPop - 1].bestgenotype.fit = mainCpop.bestpop.bestgenotype.fit;
                    }
                }
                logger.info("\nBestPop = " + BestPop[sizeBestPop - 1].bestgenotype.fit);

                boolean ess_bool;
                ArrayList<Boolean> ess = new ArrayList<>();

                for (int j = 0; j < BestPop[sizeBestPop - 1].bestgenotype.numGenes; ++j) {
                    ess_bool = false;
                    for (int[] aTobj : tobj) {
                        if ((BestPop[sizeBestPop - 1].bestgenotype.genes[j] & aTobj[j]) == 0 && BestPop[sizeBestPop - 1].bestgenotype.genes[j] != 0) {
                            ess_bool = true;
                            break;
                        }
                    }
                    if (!ess_bool) {
                        for (int[] aFobj : fobj) {
                            if ((BestPop[sizeBestPop - 1].bestgenotype.genes[j] & aFobj[j]) == 0 && BestPop[sizeBestPop - 1].bestgenotype.genes[j] != 0) {
                                ess_bool = true;
                                break;
                            }
                        }
                    }
                    ess.add(ess_bool);
                }

                essential.add(ess);

                boolean found;
                ArrayList<int[]> tobj2 = new ArrayList<>();
                for (int[] aTobj : tobj) {
                    found = true;
                    for (int j = 0; j < BestPop[sizeBestPop - 1].bestgenotype.numGenes; ++j) {
                        if ((BestPop[sizeBestPop - 1].bestgenotype.genes[j] & aTobj[j]) == 0 && BestPop[sizeBestPop - 1].bestgenotype.genes[j] != 0) {
                            found = false;
                            break;
                        }
                    }
                    if (!found)
                        tobj2.add(aTobj);
                }
                tobj = new int[tobj2.size()][numAttr];
                for (int i = 0; i < tobj2.size(); i++)
                    System.arraycopy(tobj2.get(i), 0, tobj[i], 0, numAttr);

                num_ob = 0;
                for (int[] aTobj0 : tobj0) {
                    found = true;
                    for (int j = 0; j < BestPop[sizeBestPop - 1].bestgenotype.numGenes; ++j) {
                        if ((BestPop[sizeBestPop - 1].bestgenotype.genes[j] & aTobj0[j]) == 0 && BestPop[sizeBestPop - 1].bestgenotype.genes[j] != 0) {
                            found = false;
                            break;
                        }
                    }
                    if (found)
                        ++num_ob;
                }
                num_objects.add(num_ob);
                logger.info("num_ob = " + num_ob);
            }

            Coevolution mainCpop = new Coevolution(cn, n, numgen, sizegen, ngen, nadapt, socialcard, socialfine,
                    typega, typesel, sizetur, typerec, mutation, mutadapt,
                    truthvalue, tobj0, tobj, fobj);
            mainCpop.bestpop = BestPop[0];
            Coevolution mainCpop2 = new Coevolution(cn, n, numgen, sizegen, ngen, nadapt, socialcard, socialfine,
                    typega, typesel, sizetur, typerec, mutation, mutadapt,
                    truthvalue, tobj0, tobj, fobj);
            mainCpop2.bestpop = BestPop[1];
            //локальный спуск после ГА, надо будет сделать вывод, найденного после спуска
            //НАЧАЛО
            Population pop_for_loc = new Population(1, numgen, sizegen, tobj0, tobj, fobj);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mainCpop.bestpop.bestgenotype.numGenes; ++i)
                sb.append(mainCpop.bestpop.bestgenotype.genes[i]).append("\t");
            logger.info(sb);
            System.arraycopy(mainCpop.bestpop.bestgenotype.genes, 0, pop_for_loc.genots[0].genes, 0, mainCpop.bestpop.bestgenotype.numGenes);
            pop_for_loc.genots[0].fit = mainCpop.bestpop.bestgenotype.fit;
            pop_for_loc.bestgenotype.fit = mainCpop.bestpop.bestgenotype.fit;
            pop_for_loc.localeOpt(0);
            StringBuilder sb1 = new StringBuilder();
            for (int i = 0; i < pop_for_loc.genots[0].numGenes; ++i)
                sb1.append(pop_for_loc.genots[0].genes[i]).append("\t");
            logger.info(sb1);
            logger.info("bestfit after local_opt = " + pop_for_loc.bestgenotype.fit + " " + pop_for_loc.howmanyfit + " " + pop_for_loc.howmanyfit2);
            //КОНЕЦ

            //локальный спуск после ГА, надо будет сделать вывод, найденного после спуска
            //НАЧАЛО
            Population pop_for_loc2 = new Population(1, numgen, sizegen, tobj0, tobj, fobj);
            System.arraycopy(mainCpop.bestpop.bestgenotype.genes, 0, pop_for_loc2.genots[0].genes, 0, mainCpop.bestpop.bestgenotype.numGenes);
            pop_for_loc2.genots[0].fit = mainCpop.bestpop.bestgenotype.fit;
            pop_for_loc2.bestgenotype.fit = mainCpop.bestpop.bestgenotype.fit;
            pop_for_loc2.localeOpt2(0);
            logger.info("bestfit after local_opt = " + pop_for_loc2.bestgenotype.fit + " " + pop_for_loc2.howmanyfit + " " + pop_for_loc2.howmanyfit2);
            //КОНЕЦ

            //локальный спуск после ГА, надо будет сделать вывод, найденного после спуска
            //НАЧАЛО
            Population pop_for_loc3 = new Population(1, numgen, sizegen, tobj0, tobj, fobj);
            System.arraycopy(mainCpop.bestpop.bestgenotype.genes, 0, pop_for_loc3.genots[0].genes, 0, mainCpop.bestpop.bestgenotype.numGenes);
            pop_for_loc3.genots[0].fit = mainCpop.bestpop.bestgenotype.fit;
            pop_for_loc3.bestgenotype.fit = mainCpop.bestpop.bestgenotype.fit;
            pop_for_loc3.localeOpt3(0);
            logger.info("bestfit after local_opt = ", pop_for_loc3.bestgenotype.fit, " ", pop_for_loc3.howmanyfit, " ", pop_for_loc3.howmanyfit2);
            //КОНЕЦ

            for (int i = 0; i < cn; ++i) {
                mainCpop.howmanyfit += mainCpop.pop[i].howmanyfit;
                mainCpop.howmanyfit2 += mainCpop.pop[i].howmanyfit2;
            }

            String[] map_atr = new String[8];
            map_atr[0] = "0";
            map_atr[1] = "1";
            map_atr[2] = "2";
            map_atr[3] = "1,2";
            map_atr[4] = "3";
            map_atr[5] = "1,3";
            map_atr[6] = "2,3";
            map_atr[7] = "1,2,3";

            StringBuilder result = new StringBuilder();
            for (int bp = 0; bp < sizeBestPop; ++bp) {
                result.append("NUM_NEW_OBJECTS: ").append((int) (BestPop[bp].bestgenotype.fit / 1000));
                result.append("NUM_OBJECTS: ").append(num_objects.get(bp));
                result.append("RULE_").append(bp + 1).append(":");
                for (int i = 0; i < BestPop[bp].bestgenotype.numGenes; ++i) {
                    if (essential.get(bp).get(i))
                        result.append("attr_").append(i + 1).append("=").append(map_atr[BestPop[bp].bestgenotype.genes[i]]).append("\n");
                }
                result.append("\n");
            }


            //эти выводы нужны
            result.append("\nbestfit = ").append(mainCpop.bestpop.bestgenotype.fit).append("\n");
            result.append("howmanyfit = ").append(mainCpop.howmanyfit).append("\n");
            result.append("howmanydifferent = ").append(mainCpop.howmanyfit2).append("\n");
            result.append("howmanyfit_beforebest = ").append(mainCpop.howmanyfit_beforebest).append("\n");
            result.append("howmanydifferent_beforebest = ").append(mainCpop.howmanyfit2_beforebest).append("\n");
            result.append("whatgener = ").append(mainCpop.whatgener).append("\n");

            result.append("\nbestfit = ").append(mainCpop2.bestpop.bestgenotype.fit).append("\n");
            result.append("howmanyfit = ").append(mainCpop2.howmanyfit).append("\n");
            result.append("howmanydifferent = ").append(mainCpop2.howmanyfit2).append("\n");
            result.append("howmanyfit_beforebest = ").append(mainCpop2.howmanyfit_beforebest).append("\n");
            result.append("howmanydifferent_beforebest = ").append(mainCpop2.howmanyfit2_beforebest).append("\n");
            result.append("whatgener = ").append(mainCpop2.whatgener).append("\n");

            result.append("\n\nДля выхода введите 1. Для продолжения любое другое число.\nПеред продолжением вы можете изменить sequence.txt и файлы n-грамм\nЗакончить работу:\t");
            logger.info(result);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            cont = Integer.parseInt(br.readLine());
            logger.info("\n");
        } while (cont != 1);
    }
}
