package scmAlgorithm;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.TreeUtilsBasic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import scmAlgorithm.treeSelector.BasicTreeSelector;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreeScorer;
import scmAlgorithm.treeSelector.TreeSelector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

//import flipCut.Global;

/**
 * Created by fleisch on 16.02.15.
 */


@RunWith(Parameterized.class)
public class GreedySCMAlgorithmTest extends BasicSCMTest {
    TreeSelector.ConsensusMethod method;
    TreeScorer scorer;

    public GreedySCMAlgorithmTest(Tree[] inputData, Tree scmResult, TreeSelector.ConsensusMethod method, TreeScorer scorer) {
        super(inputData, scmResult);
        this.method = method;
        this.scorer = scorer;
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> paras = new LinkedList<>();
        for (TreeScorer scorer : TreeScorer.CompleteScorerCombo(false)) {
            for (TreeSelector.ConsensusMethod method : TreeSelector.ConsensusMethod.values()) {
                paras.addAll(Arrays.asList(
                        new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(), method, scorer},
                        new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), method, scorer}));
            }
        }
        return paras;
    }

    @Test
    public void testSmidgenSamples() {
        StringBuffer buf = new StringBuffer();

        buf.append("########## Test with following Parameters [" + inputData + ", " + scmResult + ", " + method + "] ##########");
        buf.append(SEP);
        long t = System.currentTimeMillis();
        TreeSelector selector = new GreedyTreeSelector(method);
        selector.setScorer(new TreeScorer.OverlapScorer());
        selector.setInputTrees(inputData);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        algo.run();
        Tree supertree = algo.getResult();
        List<String> order = new ArrayList<>(TreeUtilsBasic.getLeafLabels(supertree.getRoot()));
        Collections.sort(order);
        TreeUtilsBasic.sortTree(supertree, order);
        TreeUtilsBasic.sortTree(scmResult, order);
        buf.append("Clades-Swenson_SCM: " + (scmResult.vertexCount() - scmResult.getNumTaxa()));
        buf.append(SEP);
        buf.append(Newick.getStringFromTree(scmResult));
        buf.append(SEP);
        buf.append("Fleisch-" + method + "-SCM: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        buf.append(SEP);
        assertNotNull(supertree);
        assertEquals(scmResult.getNumTaxa(), supertree.getNumTaxa());
        buf.append(Newick.getStringFromTree(supertree));
        buf.append(SEP);
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        buf.append("Clades: " + inner);
        buf.append(SEP);
        buf.append("########## DONE ##########");
        buf.append(SEP);
        buf.append(SEP);
        buf.append(SEP);
        System.out.println(buf.toString());
    }


    //    @Test
    public void largeSample() throws IOException {
        Path inputFile = Paths.get("/media/fleisch/wallace/home@wallace/data/simulated/SMIDGenOutgrouped/10000/0/Source_Trees/RaxML/smo.0.sourceTrees.tre");
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile.toFile());
         /*Files.copy(inputFile, inputFile.getParent().resolve("smo.0.sourceTrees_UNSUFFICIENT.tre"));

        LinkedHashMap<Object, Set<Tree>> treesSets = new LinkedHashMap<>();
        Map<Object, Set<Tree>> finished = new HashMap<>();
        for (int i = 0; i < inputTrees.length; i++) {
            Tree tree = inputTrees[i];
            Set<String> leafset = TreeUtilsBasic.getLeafLabels(tree.getRoot());
            treesSets.put(leafset, new HashSet<>(Arrays.asList(tree)));
        }

        while (treesSets.size() > 0) {
            boolean unchanged = false;
            Map.Entry<Object, Set<Tree>> e = treesSets.entrySet().iterator().next();
            Set<String> f = (Set<String>) e.getKey();
            Set<Tree> fset = e.getValue();
            treesSets.remove(f);

            while (!unchanged) {
                unchanged = true;
                Set<Set<String>> toRemove = new HashSet<>();
                for (Object o : treesSets.keySet()) {
                    Set<String> n = (Set<String>) o;
                    Set<String> check = new HashSet<>(n);
                    check.retainAll(f);
                    if (check.size() > 2) {
                        f.addAll(n);
                        Set<Tree> nset = treesSets.get(n);
                        fset.addAll(nset);
                        toRemove.add(n);
                        unchanged = false;
                    }
                }

                for (Set<String> remove : toRemove) {
                    treesSets.remove(remove);
                }
            }
            finished.put(f, fset);
        }

        Set<String> best = new HashSet<>();
        Set<Tree> trees = null;
        for (Object o : finished.keySet()) {
            Set<String> n = (Set<String>) o;
            if (n.size()>best.size()){
                best =  n;
                trees = finished.get(n);
            }
        }
        Path modelFile = Paths.get("/media/fleisch/wallace/home@wallace/data/simulated/SMIDGenOutgrouped/10000/0/Model_Trees/pruned/smo.0.modelTree.tre");
        Path modelOLD =  modelFile.getParent().resolve("smo.0.modelTree_UNSUFFICIENT.tre");
        Tree model  = Newick.getTreeFromFile(modelFile.toFile())[0];
        Files.copy(modelFile,modelOLD);
        TreeUtilsBasic.keepLeafesAndPruneTree(model, TreeUtilsBasic.getLeafesFromLabels(best, model));
        Newick.tree2File(modelFile.toFile(), model);

        Path modelSourceFile = Paths.get("/media/fleisch/wallace/home@wallace/data/simulated/SMIDGenOutgrouped/10000/0/Source_Trees/ModelSourceTrees/smo.0.modelSourceTrees.tre");
        Tree[] modelSourceTrees = Newick.getTreeFromFile(modelSourceFile.toFile());
        Files.copy(modelSourceFile,modelSourceFile.getParent().resolve("smo.0.modelSourceTrees_UNSUFFICIENT.tre"));
        Path modelSourceOGFile = modelSourceFile.getParent().resolve("smo.0.modelSourceTreesOGs.dat");
        List<String> lines = new ArrayList<>(Files.readAllLines(modelSourceOGFile));
        Files.copy(modelSourceOGFile,modelSourceOGFile.getParent().resolve("smo.0.modelSourceTreesOGs_UNSUFFICIENT.dat"));


        List<Tree> modelSourceTreesNu = new ArrayList<>(trees.size());
        List<Tree> sourceTreesNu = new ArrayList<>(trees.size());

        for (int i = 0; i < inputTrees.length; i++) {
            Tree inputTree = inputTrees[i];
            if (trees.contains(inputTree)){
                modelSourceTreesNu.add(modelSourceTrees[i]);
                sourceTreesNu.add(inputTree);
//                lines.remove(i);
            }
        }

        Newick.trees2File(modelSourceFile.toFile(), modelSourceTreesNu);
        Newick.trees2File(inputFile.toFile(), sourceTreesNu);

//        Files.write(modelSourceOGFile,lines);

*/


        BasicTreeSelector selector = new GreedyTreeSelector();
        selector.setScorer(new TreeScorer.ConsensusResolutionScorer());
        selector.setInputTrees(inputTrees);
//        selector.setThreads(Runtime.getRuntime().availableProcessors());
        selector.setThreads(1);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);//rooting stuff
        algo.run();
        Tree supertree = algo.getResult();
        System.out.println("large example: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }

    //############################ EVAL TESTS #################################################




    /*    @Test
    public void createCrossTable() throws IOException {
        int[] taxas = {
                100,
//                500,
//                1000
        };

        int[] scaffoldFactors = {
                20,
                50,
                75,
                100
        };
        final Map<String, String> TEMPLATES = new HashMap<String, String>(Global.NR_OF_TEMPLATE_TAGS);
        final int instanceMax = 30;
        final int instanceMin = 0;
        Path source =   Paths.get("/home/qo53kab/Work/scmResults/smog/");



        for (int taxa : taxas) {
            ;
            Path fpFile = source.resolve(String.valueOf(taxa)).resolve("fn.cross.csv");
            Files.deleteIfExists(fpFile);
            Path unFile = source.resolve(String.valueOf(taxa)).resolve("unique.cross.csv");
            Files.deleteIfExists(unFile);
            Path sdFile = source.resolve(String.valueOf(taxa)).resolve("summedDistances.csv");
            Files.deleteIfExists(sdFile);
            boolean notDone = true;

            TEMPLATES.put(Global.TAG_TAXA, Integer.toString(taxa));

            int currentInstanceMax = 0;
            //the 1000 taxa dataset has only 10 replicates
            if (taxa == 100 || taxa == 500) {
                currentInstanceMax = Math.min(30, instanceMax);
            } else if (taxa == 1000) {
                currentInstanceMax = Math.min(30, instanceMax);
                //                currentInstanceMax = Math.min(10, instanceMax);
            }


            for (int scaffoldFactorIndex = 0; scaffoldFactorIndex < scaffoldFactors.length; scaffoldFactorIndex++) {

                TEMPLATES.put(Global.TAG_SCAFFOLD, Integer.toString(scaffoldFactors[scaffoldFactorIndex]));
                DescriptiveStatistics[][] fps =  new DescriptiveStatistics[fullStrictCombi.length][fullStrictCombi.length];
                DescriptiveStatistics[][] uniqueClades =  new DescriptiveStatistics[fullStrictCombi.length][fullStrictCombi.length];
                DescriptiveStatistics[] summedDistance = new DescriptiveStatistics[fullStrictCombi.length];
                String header = "SF " + scaffoldFactors[scaffoldFactorIndex] + SEP;

                Files.write(fpFile,header.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
                Files.write(unFile,header.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);

                for (int instance = instanceMin; instance < currentInstanceMax; instance++) {
                    TEMPLATES.put(Global.TAG_INSTANCE, Integer.toString(instance));
                    Path scmFile =  source.resolve(String.valueOf(taxa)).resolve(String.valueOf(scaffoldFactors[scaffoldFactorIndex])).resolve("smo." + instance +".standard-scmTrees.tre");
                    Tree[] trees =  Newick.getTreeFromFile(scmFile.toFile());
                    if (trees != null) {
                        for (int i = 0; i < trees.length; i++) {
                            Tree t1 = trees[i];
                            double fpSum = 0;
                            for (int j = 0; j < trees.length; j++) {
                                Tree t2 = trees[j];
                                if (fps[i][j]==null) {
                                    fps[i][j] = new DescriptiveStatistics();
                                    fps[j][i] = new DescriptiveStatistics();
                                }
                                if (uniqueClades[i][j] == null){
                                    uniqueClades[i][j] = new DescriptiveStatistics();
                                    uniqueClades[j][i] = new DescriptiveStatistics();
                                }


                                double[] rates =  FN_FP_RateComputer.calculateRates(t1,t2,false);
                                fpSum += rates[1];
                                fps[i][j].addValue(rates[1]);
                                uniqueClades[i][j].addValue(rates[1] - rates[0]);
                            }
                            if (summedDistance[i]==null)
                                summedDistance[i] =  new DescriptiveStatistics();
                            summedDistance[i].addValue(fpSum);
                        }

                    }else{
                        System.out.println("Tree file " + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/" + instance +"not found!");
                    }
                }
                StringBuffer outputFP = new StringBuffer();
                StringBuffer outputUN = new StringBuffer();
                StringBuffer outputSD = new StringBuffer();

                outputFP.append("Scorer");
                for (String name : scorerNames) {
                    outputFP.append("\t");
                    outputFP.append(name);
                }

                outputUN.append(outputFP);
                if (notDone) {
                    outputSD.append(outputFP);
                    outputSD.append(SEP);
                    notDone = false;
                }
                outputSD.append(scaffoldFactors[scaffoldFactorIndex]);

                for (int i = 0; i < scorerNames.length; i++) {
                    outputFP.append(SEP);
                    outputUN.append(SEP);
                    outputFP.append(scorerNames[i]);
                    outputUN.append(scorerNames[i]);
                    outputSD.append("\t");
                    outputSD.append(summedDistance[i].getMean());
                    for (int j = 0; j < scorerNames.length; j++) {
                        outputFP.append("\t");
                        outputUN.append("\t");

                            outputFP.append(fps[i][j].getMean());
                            if (uniqueClades[i][j] != null){
                                outputUN.append(uniqueClades[i][j].getMean());
                            }else{
                                outputUN.append(uniqueClades[j][i].getMean());
                            }
                    }
                }
                outputFP.append(SEP);
                outputUN.append(SEP);
                outputSD.append(SEP);

                Files.write(fpFile,outputFP.toString().getBytes(),StandardOpenOption.APPEND);
                Files.write(unFile,outputUN.toString().getBytes(),StandardOpenOption.APPEND);
                Files.write(sdFile,outputSD.toString().getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            }
        }
    }*/

   /* @Test
    public void debugTest() throws IOException {
        int[] taxas = {
//                100,
                500,
//                1000
        };

        int[] scaffoldFactors = {
                20,
                50,
                75,
                100
        };


        Path fnFile = Paths.get("/home/qo53kab/Work/scmResults/fn.scm-rand-25-c4." + taxas[0] + ".csv");
        Path fpFile = Paths.get("/home/qo53kab/Work/scmResults/fp.scm-rand-25-c4." + taxas[0] + ".csv");
        ;
        Path sfnFile = Paths.get("/home/qo53kab/Work/scmResults/sfn.scm-rand-25-c4." + taxas[0] + ".csv");
        ;
        Path sfpFile = Paths.get("/home/qo53kab/Work/scmResults/sfp.scm-rand-25-c4." + taxas[0] + ".csv");
        ;
        Path resolutionFile = Paths.get("/home/qo53kab/Work/scmResults/resolution.scm-rand-25-c4." + taxas[0] + ".csv");

        List<List<Tree>> resultTrees = new ArrayList<>(4);
        for (int i = 0; i < scaffoldFactors.length; i++) {
            resultTrees.add(new ArrayList<>(fullStrictCombi.length));
        }


        Path[] files = {
                fnFile,
                fpFile,
                sfnFile,
                sfpFile,
                resolutionFile,
        };

        String header = "Scorer\t20\t50\t75\t100\n";
        for (Path file : files) {
            Files.deleteIfExists(file);
            Files.write(file, header.getBytes(), StandardOpenOption.CREATE);
        }

//        List<TreeScorer[]> combis =  Arrays.asList(
//                new TreeScorer[] {new CollisionPointNumberScorer()},
//                new TreeScorer[] {new UniqueTaxaNumberScorer()},
//                new TreeScorer[] {new ConsensusResolutionScorer()},
//                new TreeScorer[] {new ConsensusCladeNumberScorer()}
//
//                new TreeScorer[]{new OverlapScorer()},//is automatic included},
//                new TreeScorer[]{new CollisionPointNumberScorer()},
//                new TreeScorer[]{new CollisionLostCladesNumberScorer()},
//                new TreeScorer[]{new CollisionNumberScorer()},
//                new TreeScorer[]{new BackboneSizeScorer()},
//                new TreeScorer[]{new BackboneCladeNumberScorer()},
//                new TreeScorer[]{new UniqueTaxaNumberScorer()},
//                new TreeScorer[]{new UniqueTaxaRateScorer()},
//                new TreeScorer[]{new ConsensusBackboneCladeNumberScorer()},
//                new TreeScorer[]{new ConsensusBackboneResolutionScorer()},
//                new TreeScorer[]{new ConsensusBackboneSizeScorer()},
//                new TreeScorer[]{new ConsensusCladeNumberScorer()},
//                new TreeScorer[]{new ConsensusResolutionScorer()}
//                   b4
//                 minimalCombi,
//                greenCombi
//                fullStrictCombi
//        );

        List<TreeScorer[]> combis = new ArrayList<>();
//        combis.add(new TreeScorer[] {new CollisionLostCladesNumberScorer()});
//        combis.add(new TreeScorer[] {new CollisionNumberScorer()});
        combis.add(new TreeScorer[]{new CollisionPointNumberScorer()});
        combis.add(new TreeScorer[]{new CollisionMultiCollisionPointScorer()});
//        combis.add(new TreeScorer[] {new CollisionPointMultiCollissionTieBreakScorer()});
        combis.add(new TreeScorer[]{new SubsetUnitOverlapScorer()});
        combis.add(new TreeScorer[]{new SubsetUnitOverlapDifferenceScorer()});
        combis.add(new TreeScorer[]{new SubsetUnitOverlapRateScorer()});
        combis.add(new TreeScorer[]{new UniqueTaxaNumberScorer()});
//        combis.add(new TreeScorer[] {new UniqueTaxaNumberScorer()});
//        combis.add(b4);


        int s = -1;
        for (TreeScorer[] scorer : combis) {
            s++;

//        }
//        for (int s = 0; s < fullStrictCombi.length; s++) {
//            TreeScorer scorer = fullStrictCombi[s];
//            TreeScorer[] scorer = fullStrictCombi[s];

            //0 <= instances <= 30
            final int instanceMin = 0;
            final int instanceMax = 30;
            final Map<String, String> TEMPLATES = new HashMap<String, String>(Global.NR_OF_TEMPLATE_TAGS);
            final DescriptiveStatistics[] scmTimes = new DescriptiveStatistics[4];

            final DescriptiveStatistics[] fn = new DescriptiveStatistics[4];
            final DescriptiveStatistics[] fp = new DescriptiveStatistics[4];
            final DescriptiveStatistics[] sfn = new DescriptiveStatistics[4];
            final DescriptiveStatistics[] sfp = new DescriptiveStatistics[4];

            final DescriptiveStatistics[] resolution = new DescriptiveStatistics[4];

            DescriptiveStatistics[][] stats = {
                    fn, fp, sfn, sfp, resolution
            };
            for (int i = 0; i < stats.length; i++) {

                for (int j = 0; j < stats[i].length; j++) {
                    stats[i][j] = new DescriptiveStatistics();
                }
            }

            int[] trees = new int[4];
            int[] fptrees = new int[4];
            int[] swfptrees = new int[4];
            int[] equalrees = new int[4];

            int[] consSwensonCladesNotFound = new int[4];
            int[] swensonCladesNotFound = new int[4];

            int[] swensonClades = new int[4];
            int[] scmClades = new int[4];
            int[] consensusClades = new int[4];

            long scmTime = System.currentTimeMillis();

            StringBuffer fnLine = new StringBuffer();
            StringBuffer fpLine = new StringBuffer();
            StringBuffer sfnLine = new StringBuffer();
            StringBuffer sfpLine = new StringBuffer();
            StringBuffer resolutionLine = new StringBuffer();
            StringBuffer[] lines = {
                    fnLine,
                    fpLine,
                    sfnLine,
                    sfpLine,
                    resolutionLine
            };
            for (StringBuffer line : lines) {
//                line.append(scorer.toString());
                line.append(Arrays.toString(scorer));
            }

            for (int taxa : taxas) {
                TEMPLATES.put(Global.TAG_TAXA, Integer.toString(taxa));

                int currentInstanceMax = 0;
                //the 1000 taxa dataset has only 10 replicates
                if (taxa == 100 || taxa == 500) {
                    currentInstanceMax = Math.min(30, instanceMax);
                } else if (taxa == 1000) {
                    currentInstanceMax = Math.min(30, instanceMax);
                    //                currentInstanceMax = Math.min(10, instanceMax);
                }


                for (int scaffoldFactorIndex = 0; scaffoldFactorIndex < scaffoldFactors.length; scaffoldFactorIndex++) {
                    scmTimes[scaffoldFactorIndex] = new DescriptiveStatistics();
                    TEMPLATES.put(Global.TAG_SCAFFOLD, Integer.toString(scaffoldFactors[scaffoldFactorIndex]));


                    for (int instance = instanceMin; instance < currentInstanceMax; instance++) {
                        TEMPLATES.put(Global.TAG_INSTANCE, Integer.toString(instance));
                        File scmFile = new File(EvalUtils.expandTemplate(scmTreeLocation, TEMPLATES));
                        File modelFile = new File(EvalUtils.expandTemplate(modelTreeLocation, TEMPLATES));

                        if (scmFile.exists()) {

                            trees[scaffoldFactorIndex]++;
                            Tree swensonSCM = Newick.getTreeFromFile(scmFile)[0];
                            File inputFile = new File(EvalUtils.expandTemplate(sourceTreeLocation, TEMPLATES));
                            Tree[] input = Newick.getTreeFromFile(inputFile);


                            Tree modelTree = null;
                            if (modelFile.exists()) {
                                modelTree = Newick.getTreeFromFile(modelFile)[0];
                            }

                            System.out.println();
                            System.out.println("### Calc SCM Trees ###");
                            int c = swensonSCM.vertexCount() - swensonSCM.getNumTaxa();
                            swensonClades[scaffoldFactorIndex] += c;
                            System.out.println("SCM-SMID: " + (c));
                            System.out.println(Newick.getStringFromTree(swensonSCM));


                            GreedySCMAlgorithm scmAlgorithm = new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0], TreeUtilsBasic.cloneTrees(input)));
//                            MultiGreedySCMAlgorithm scmAlgorithm = new MultiGreedySCMAlgorithm(TreeUtilsBasic.cloneTrees(input), fullStrictCombi);
//                            RandomizedSCMAlgorithm scmAlgorithm =  new RandomizedSCMAlgorithm(true,25,TreeUtilsBasic.cloneTrees(input), scorer);

                            scmAlgorithm.run();
                            List<Tree> scmTrees = scmAlgorithm.getResults();
//                            Tree scmTree = scmAlgorithm.getMergedSupertree();

                            Tree scmTree = scmAlgorithm.getResult();
                            scmTime = System.currentTimeMillis() - scmTime;
                            scmTimes[scaffoldFactorIndex].addValue(scmTime);
                            c = scmTree.vertexCount() - scmTree.getNumTaxa();
                            scmClades[scaffoldFactorIndex] += c;

                            System.out.println("SCM: " + (c));
                            System.out.println(Newick.getStringFromTree(scmTree));
                            System.out.println("SCM Time " + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/" + instance + ": " + (float) scmTime / 1000f + "s");


                            LooseConsensus cons = new LooseConsensus();
                            cons.setInput(Arrays.asList(scmTree, swensonSCM), false);
                            cons.run();
                            Tree scmTreesConsensus = cons.getResult();
                            c = scmTreesConsensus.vertexCount() - scmTreesConsensus.getNumTaxa();
                            consensusClades[scaffoldFactorIndex] += c;
                            System.out.println("SCM-Consensus: " + (c));
                            System.out.println(Newick.getStringFromTree(scmTreesConsensus));
                            //                        System.out.println("SCM Time " + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/" + instance + ": " + (float)scmTime/1000f + "s");


//                            Path outFile =  Paths.get("/home/qo53kab/Work/scmResults/smog/").resolve(String.valueOf(taxa)).resolve(String.valueOf(scaffoldFactors[scaffoldFactorIndex])).resolve("smo." + instance +".standard-scmTrees.tre");
                            Path outFile = Paths.get("/home/qo53kab/Work/scmResults/smog/").resolve(String.valueOf(taxa)).resolve(String.valueOf(scaffoldFactors[scaffoldFactorIndex])).resolve("smo." + instance + ".randomized25-c4-scmTrees.tre");
                            Files.createDirectories(outFile.getParent());
                            if (s == 0) {
                                Files.deleteIfExists(outFile);
                                Files.write(outFile, (Newick.getStringFromTree(scmTree) + "\n").getBytes(), StandardOpenOption.CREATE);
                            } else {
                                Files.write(outFile, (Newick.getStringFromTree(scmTree) + "\n").getBytes(), StandardOpenOption.APPEND);
                            }
                            double[] swensonRates = FN_FP_RateComputer.calculateRates(scmTree, swensonSCM, false);
                            double[] consRates = FN_FP_RateComputer.calculateRates(scmTreesConsensus, swensonSCM, false);
                            double[] swensonSumOfRates = FN_FP_RateComputer.calculateSumOfRates(swensonSCM, input);

                            double[] modelRates = FN_FP_RateComputer.calculateRates(scmTree, modelTree, false);
                            double[] sumOfRates = FN_FP_RateComputer.calculateSumOfRates(scmTree, input);


                            fn[scaffoldFactorIndex].addValue(modelRates[0]);
                            fp[scaffoldFactorIndex].addValue(modelRates[1]);
                            sfn[scaffoldFactorIndex].addValue(sumOfRates[0]);
                            sfp[scaffoldFactorIndex].addValue(sumOfRates[1]);

                            resolution[scaffoldFactorIndex].addValue(TreeUtilsBasic.calculateTreeResolution(scmTree.getNumTaxa(), scmTree.vertexCount()));

                            List<String> order = new ArrayList<>(TreeUtilsBasic.getLeafLabels(scmTree.getRoot()));
                            Collections.sort(order);
                            TreeUtilsBasic.sortTree(swensonSCM, order);
                            TreeUtilsBasic.sortTree(scmTree, order);

                            System.out.println("FN/FP FleischSCM to Swenson: ");
                            System.out.println(Arrays.toString(swensonRates));
                            if (Double.compare(swensonRates[0], 0d) == 0 && Double.compare(swensonRates[1], 0d) == 0)
                                equalrees[scaffoldFactorIndex]++;
                            if (Double.compare(swensonRates[0], 0d) > 0)
                                swensonCladesNotFound[scaffoldFactorIndex]++;

                            System.out.println("FN/FP FleischCons to Swenson: ");
                            System.out.println(Arrays.toString(consRates));
                            if (Double.compare(consRates[0], 0d) > 0)
                                consSwensonCladesNotFound[scaffoldFactorIndex]++;

                            System.out.println("SFN/SFP to Source: ");
                            System.out.println(Arrays.toString(sumOfRates));
                            assertEquals(0, Double.compare(0d, sumOfRates[1]));
                            if (Double.compare(sumOfRates[1], 0d) != 0) {
                                File scmResultDir = new File("/home/fleisch/Work/scmBadResults/" + taxa + "/" + scaffoldFactors[scaffoldFactorIndex]);
                                scmResultDir.mkdirs();
                                File scmResultFile = new File(scmResultDir.getAbsolutePath() + "/sm." + instance + ".scmResults.tre");

                                Newick.trees2File(scmResultFile, scmTrees);

                                fptrees[scaffoldFactorIndex]++;
                            }
                            System.out.println("SFN/SFP Swenson to Source: ");
                            System.out.println(Arrays.toString(swensonSumOfRates));
                            assertEquals(0, Double.compare(0d, swensonSumOfRates[1]));
                            if (Double.compare(swensonSumOfRates[1], 0d) != 0)
                                swfptrees[scaffoldFactorIndex]++;

                            System.out.println("######################");
                            System.out.println();


                        }

                    }
                    fnLine.append("\t" + fn[scaffoldFactorIndex].getMean());
                    fpLine.append("\t" + fp[scaffoldFactorIndex].getMean());
                    sfnLine.append("\t" + sfn[scaffoldFactorIndex].getMean());
                    sfpLine.append("\t" + sfp[scaffoldFactorIndex].getMean());
                    resolutionLine.append("\t" + resolution[scaffoldFactorIndex].getMean());
                }
            }

            Files.write(fnFile, (fnLine.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(fpFile, (fpLine.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(sfnFile, (sfnLine.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(sfpFile, (sfpLine.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(resolutionFile, (resolutionLine.toString() + "\n").getBytes(), StandardOpenOption.APPEND);


            System.out.println("trees calculated: " + Arrays.toString(trees));
            System.out.print("trees with false positives: " + Arrays.toString(fptrees) + " relative: [");
            for (int i = 0; i < fptrees.length; i++) {
                System.out.print(" " + ((double) fptrees[i] / (double) trees[i]));
            }
            System.out.println(" ]");

            System.out.print("swenson trees with false positives: " + Arrays.toString(swfptrees) + " relative: [");
            for (int i = 0; i < fptrees.length; i++) {
                System.out.print(" " + ((double) swfptrees[i] / (double) trees[i]));
            }
            System.out.println(" ]");

            System.out.println("trees equal to swenson: " + Arrays.toString(equalrees));
            System.out.println("trees  with missing swenson clades: " + Arrays.toString(swensonCladesNotFound));
            System.out.println("consTrees with missing swenson clades: " + Arrays.toString(consSwensonCladesNotFound));
            System.out.println("swenson clades: " + Arrays.toString(swensonClades));
            System.out.println("scm clades: " + Arrays.toString(scmClades));
            System.out.println("scmCons clades: " + Arrays.toString(consensusClades));

            System.out.print("Fleisch vs Shel: [");
            for (int i = 0; i < fptrees.length; i++) {
                System.out.print(" " + ((double) scmClades[i] / (double) swensonClades[i] * 100) + "%");
            }
            System.out.println(" ]");

            System.out.print("FleischCons vs Shel: [");
            for (int i = 0; i < fptrees.length; i++) {
                System.out.print(" " + ((double) consensusClades[i] / (double) swensonClades[i] * 100) + "%");
            }
            System.out.println(" ]");

            System.out.print("Running Times: [");
            for (DescriptiveStatistics st : scmTimes) {
                if (st != null) {
                    System.out.print(" " + st.getMean() / 1000);
                }
            }
            System.out.println(" ]");
        }
    }*/


    /*@Test
    public void scmMultiEval() throws IOException {
        int[] taxas = {
//                100,
                500,
//                1000
        };

        int[] scaffoldFactors = {
                20,
                50,
                75,
                100
        };
        final Map<String, String> TEMPLATES = new HashMap<String, String>(Global.NR_OF_TEMPLATE_TAGS);
        final int instanceMax = 30;
        final int instanceMin = 0;
        List<int[]> scorerCombos =  Arrays.asList(
//                new int[]{0},
//                new int[]{1},
//                new int[]{2},
//                new int[]{3},
//                new int[]{4},
//                new int[]{5},
//                new int[]{6},
//                new int[]{7},
//                new int[]{8},
//                new int[]{9},
//                new int[]{10},
//                new int[]{11},
//                new int[]{12},
                new int[]{1,6,11,12}
//                new int[]{0,1,6,11,12},
//                new int[]{0,1,6,9,12},
//                new int[]{0,1,2,6,7,9,11,12},
//                new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12}
        );

        String[] statTypes = {
                "FN-Rate",
                "FP-Rate",
                "SFN-Rate",
                "SFP-Rate",
                "Resolution"
        };

        Path source =   Paths.get("/home/qo53kab/Work/scmResults/smog/");



        for (int taxa : taxas) {
            ;
            Path dataFile = source.resolve(String.valueOf(taxa)).resolve("smog." + taxa + ".scm-multi.stats.csv");
            Files.deleteIfExists(dataFile);

            TEMPLATES.put(Global.TAG_TAXA, Integer.toString(taxa));

            int currentInstanceMax = 0;
            //the 1000 taxa dataset has only 10 replicates
            if (taxa == 100 || taxa == 500) {
                currentInstanceMax = Math.min(30, instanceMax);
            } else if (taxa == 1000) {
                currentInstanceMax = Math.min(30, instanceMax);
                //                currentInstanceMax = Math.min(10, instanceMax);
            }

            DescriptiveStatistics[][][] stats = new DescriptiveStatistics[scaffoldFactors.length][scorerCombos.size()][statTypes.length];
            for (int scaffoldFactorIndex = 0; scaffoldFactorIndex < scaffoldFactors.length; scaffoldFactorIndex++) {
                TEMPLATES.put(Global.TAG_SCAFFOLD, Integer.toString(scaffoldFactors[scaffoldFactorIndex]));
                DescriptiveStatistics[][] currentScaffold = stats[scaffoldFactorIndex];

                for (int instance = instanceMin; instance < currentInstanceMax; instance++) {
                    TEMPLATES.put(Global.TAG_INSTANCE, Integer.toString(instance));
                    Path scmFile =  source.resolve(String.valueOf(taxa)).resolve(String.valueOf(scaffoldFactors[scaffoldFactorIndex])).resolve("smo." + instance +".standard-scmTrees.tre");
                    if (Files.exists(scmFile)) {
                        Tree[] trees =  Newick.getTreeFromFile(scmFile.toFile());
                        File modelFile = new File(EvalUtils.expandTemplate(modelTreeLocation, TEMPLATES));
                        Tree modelTree =  Newick.getTreeFromFile(modelFile)[0];
                        File inputFile = new File(EvalUtils.expandTemplate(sourceTreeLocation, TEMPLATES));
                        Tree[] inputTrees =  Newick.getTreeFromFile(inputFile);

                        for (int i = 0; i < scorerCombos.size(); i++) {
                            int[] scorerCombo = scorerCombos.get(i);
                            Tree scmTree = getSemiStrict(trees,scorerCombo);

                            DescriptiveStatistics[] currentComboStats =  currentScaffold[i];
                            double[] rates = FN_FP_RateComputer.calculateRates(scmTree,modelTree,false);
                            double[] sumOfrates = FN_FP_RateComputer.calculateSumOfRates(scmTree, inputTrees);
                            if (currentComboStats[0] == null)
                                currentComboStats[0] = new DescriptiveStatistics();
                            currentComboStats[0].addValue(rates[0]);
                            if (currentComboStats[1] == null)
                                currentComboStats[1] = new DescriptiveStatistics();
                            currentComboStats[1].addValue(rates[1]);
                            if (currentComboStats[2] == null)
                                currentComboStats[2] = new DescriptiveStatistics();
                            currentComboStats[2].addValue(sumOfrates[0]);
                            if (currentComboStats[3] == null)
                                currentComboStats[3] = new DescriptiveStatistics();
                            currentComboStats[3].addValue(sumOfrates[1]);
                            if (currentComboStats[4] == null)
                                currentComboStats[4] = new DescriptiveStatistics();
                            currentComboStats[4].addValue(TreeUtilsBasic.calculateTreeResolution(scmTree.getNumTaxa(), scmTree.vertexCount()));


                            Path outFile =  Paths.get("/home/qo53kab/Work/scmResults/smog/").resolve(String.valueOf(taxa)).resolve(String.valueOf(scaffoldFactors[scaffoldFactorIndex])).resolve("smo." + instance +".standard-c4-scmTrees.tre");
                            if (i==0) {
                                Files.createDirectories(outFile.getParent());
                                Files.deleteIfExists(outFile);
                                Files.write(outFile,(Newick.getStringFromTree(scmTree) + "\n").getBytes(),StandardOpenOption.CREATE);
                            } else {
                                Files.write(outFile,(Newick.getStringFromTree(scmTree) + "\n").getBytes(),StandardOpenOption.APPEND);
                            }
                        }

                    }
                }
            }
            String header = "Scorer\t20\t50\t75\t100";
            StringBuffer toFile = new StringBuffer();
            for (int statIndex = 0; statIndex < statTypes.length; statIndex++) {
                toFile.append(statTypes[statIndex])
                .append(SEP)
                .append(header)
                .append(SEP);
                for (int scorerIndex = 0; scorerIndex < scorerCombos.size(); scorerIndex++) {
                    toFile.append(getMultiScorerName(scorerCombos.get(scorerIndex)));
                    for (int scaffoldIndex = 0; scaffoldIndex < scaffoldFactors.length; scaffoldIndex++) {
                        DescriptiveStatistics toAdd = stats[scaffoldIndex][scorerIndex][statIndex];
                        toFile.append("\t").append(toAdd.getMean());
                    }
                    toFile.append(SEP);
                }
            }
            Files.write(dataFile,toFile.toString().getBytes(),StandardOpenOption.CREATE);
        }
    }*/

    /*@Test
    public void mammalsTest() {
        File inputFile = new File(getClass().getResource("/scmAlgorithm/Beck_Wtrees-s3.intree.phy").getFile());
        Tree[] trees = Newick.getTreeFromFile(inputFile);
        boolean removeOG = false;
        final String ogLabel = "Real_OG";

        if (removeOG) {
            for (Tree tree : trees) {
                TreeNode og = tree.getVertex(ogLabel);
                if (og != null) {
                    tree.removeVertex(og);
                    TreeUtilsBasic.pruneDegreeOneNodes(tree);
                }
            }
        }

        //root check
        int counter = 0;
        for (Tree tree : trees) {
            if (tree.getRoot().childCount() == 2)
                counter++;
        }
        System.out.println(counter + " of " + trees.length + "are rooted: " + ((double) counter / (double) trees.length) * 100d + "%");

        long t = System.currentTimeMillis();
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new OverlapScorer(), trees));
//        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusResolutionScorer(), trees));
//        AbstractSCMAlgorithm algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
        Tree st = algo.getSupertree();
        double time = (double) (System.currentTimeMillis() - t) / 1000d;

        System.out.printf("Running Time: " + time + "s");
        int taxa = st.getNumTaxa();
        System.out.println("Resolution: taxa: " + taxa + ", innerNodes: " + st.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, st.vertexCount()));
        double[] fnfp = FN_FP_RateComputer.calculateSumOfRates(st, trees);
        System.out.println("SFN/SFP to Source: " + Arrays.toString(fnfp));


    }
*/

//    @Test
//    public void bioDataTest() {
////        Global.BioDataSet[] bioDataSets = {Global.BioDataSet.BEE_TREE, Global.BioDataSet.LEGUMES_SM18, Global.BioDataSet.SAXIFRAGALES, Global.BioDataSet.MAMMALS, Global.BioDataSet.MARSUPIALS, Global.BioDataSet.SEABIRDS, Global.BioDataSet.PRIMATES};
//        Global.BioDataSet[] bioDataSets = {Global.BioDataSet.MAMMALS};
//
//
//        for (Global.BioDataSet bioDataSet : bioDataSets) {
//            System.out.println("########### Calculating SCM comparison for: " + bioDataSet.toString() + " ##########");
//            File inputFile;
//            File scmTreeFile;
//            boolean rootErrorCorection = true;
//            if (rootErrorCorection) {
//                System.out.println("INFO: Root error correction: Rooting after SCM Tree... ");
//                if (!bioDataSet.ROOTED) {
//                    inputFile = new File(bioDataSet.SOURCE_TREES_OPT);
//                    scmTreeFile = new File(bioDataSet.SCM_TREE);
//                } else {
//                    inputFile = new File(bioDataSet.SOURCE_TREES);
//                    scmTreeFile = new File(bioDataSet.SCM_TREE);
//                }
//            } else {
//                System.out.println("INFO: NO root errot correction for FlipCut... ");
//                inputFile = new File(bioDataSet.SOURCE_TREES);
//                scmTreeFile = new File(bioDataSet.SCM_TREE);
//            }
//            Tree[] trees = Newick.getTreeFromFile(inputFile);
//
//            //root check
//            int counter = 0;
//            for (Tree tree : trees) {
//                if (tree.getRoot().childCount() == 2)
//                    counter++;
//            }
//            System.out.println(counter + " of " + trees.length + "are rooted: " + ((double) counter / (double) trees.length) * 100d + "%");
//
//            long t = System.currentTimeMillis();
//            AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new OverlapScorer(,true,true), TreeUtilsBasic.cloneTrees(trees)));
//            Tree stOverlap = algo.getSupertree();
//            double timeOverlap = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println();
//            System.out.println("Overlap Running Time: " + timeOverlap + "s");
//            int taxa = stOverlap.getNumTaxa();
//            System.out.println("Overlap Resolution: taxa: " + taxa + ", innerNodes: " + stOverlap.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stOverlap.vertexCount()));
//            double[] fnfp = FN_FP_RateComputer.calculateSumOfRates(stOverlap, trees);
//            System.out.println("Overlap SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();
//
//            t = System.currentTimeMillis();
//            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new CollisionLostCladesNumberScorer(,true,true), TreeUtilsBasic.cloneTrees(trees)));
////         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
//            Tree stRes = algo.getSupertree();
//            double timeRes = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println("CollidingClades Running Time: " + timeRes + "s");
//            taxa = stRes.getNumTaxa();
//            System.out.println("CollidingClades Resolution: taxa: " + taxa + ", innerNodes: " + stRes.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stRes.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stRes, trees);
//            System.out.println("CollidingClades SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();
//
//            /*t = System.currentTimeMillis();
//            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusCladeNumberScorer(), TreeUtilsBasic.cloneTrees(trees)));
////         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
//            Tree stNum = algo.getSupertree();
//            double timeNum = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println("CladeNumber Running Time: " + timeNum + "s");
//            taxa = stNum.getNumTaxa();
//            System.out.println("CladeNumber Resolution: taxa: " + taxa + ", innerNodes: " + stNum.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNum.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNum, trees);
//            System.out.println("CladeNumber SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();
//*/
//
//           /* t = System.currentTimeMillis();
//            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusBackboneResolutionScorer(), TreeUtilsBasic.cloneTrees(trees)));
////         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
//            Tree stResBack = algo.getSupertree();
//            double timeResBack = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println("Backbone-Resolution Running Time: " + timeResBack + "s");
//            taxa = stResBack.getNumTaxa();
//            System.out.println("Backbone-Resolution Resolution: taxa: " + taxa + ", innerNodes: " + stResBack.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stResBack.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stResBack, trees);
//            System.out.println("Backbone-Resolution SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();*/
//
//            /*t = System.currentTimeMillis();
//            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusBackboneCladeNumberScorer(), TreeUtilsBasic.cloneTrees(trees)));
////         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
//            Tree stNumBack = algo.getSupertree();
//            double timeNumBack = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println("Backbone-CladeNumber Running Time: " + timeNumBack + "s");
//            taxa = stNumBack.getNumTaxa();
//            System.out.println("Backbone-CladeNumber Resolution: taxa: " + taxa + ", innerNodes: " + stNumBack.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumBack.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumBack, trees);
//            System.out.println("Backbone-CladeNumber SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();
//*/
//
//
//            /*//randomized versions
//            t = System.currentTimeMillis();
//            algo = new RandomizedSCMAlgorithm(25,TreeUtilsBasic.cloneTrees(trees),new OverlapScorer(),new ConsensusCladeNumberScorer(),new ConsensusResolutionScorer(),new ConsensusCladeNumberScorer(),new ConsensusBackboneCladeNumberScorer(),new ConsensusBackboneResolutionScorer());
////         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
//            Tree stNumRand = algo.getSupertree();
//            double timeNumRand = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println("RandOverlap Running Time: " + timeNumRand + "s");
//            taxa = stNumRand.getNumTaxa();
//            System.out.println("RandOverlap Resolution: taxa: " + taxa + ", innerNodes: " + stNumRand.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumRand.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumRand, trees);
//            System.out.println("RandOverlap SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();
//
//            t = System.currentTimeMillis();
//            algo = new RandomizedSCMAlgorithm(25,TreeUtilsBasic.cloneTrees(trees),new OverlapScorer(TreeScorer.ConsensusMethods.SEMI_STRICT));
////         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(),trees));
//            List<Tree> temp =  algo.getSupertrees();
//            NConsensus c =  new NConsensus();
//            c.setMethod(NConsensus.METHOD_MAJORITY);
//            Tree stNumRandRand = c.getConsensusTree(temp.toArray(new Tree[temp.size()]));
////            Tree stNumRandRand = algo.getSupertree();
//            double timeNumRandRand = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println(Newick.getStringFromTree(stNumRandRand));
//            System.out.println("RandRandOverlap Running Time: " + timeNumRandRand + "s");
//            taxa = stNumRandRand.getNumTaxa();
//            System.out.println("RandRandOverlap Resolution: taxa: " + taxa + ", innerNodes: " + stNumRandRand.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumRandRand.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumRandRand, trees);
//            System.out.println("RandRandOverlap SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();*/
//
//            //multiversion
//          /*  t = System.currentTimeMillis();
//            algo = new MultiGreedySCMAlgorithm(TreeUtilsBasic.cloneTrees(trees), new CollisionPointNumberScorer(),*//*new BackboneResolutionScorer(),*//*new ConsensusCladeNumberScorer(), new ConsensusResolutionScorer());
//            Tree stNumMulti = ((MultiGreedySCMAlgorithm) algo).getMergedSupertree();
//            double timeNumMulti = (double) (System.currentTimeMillis() - t) / 1000d;
//            System.out.println("Backbone-CladeNumber Running Time: " + timeNumMulti + "s");
//            taxa = stNumMulti.getNumTaxa();
//            System.out.println("Backbone-CladeNumber Resolution: taxa: " + taxa + ", innerNodes: " + stNumMulti.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumMulti.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumMulti, trees);
//            System.out.println("Backbone-CladeNumber SFN/SFP to Source: " + Arrays.toString(fnfp));
//            System.out.println();*/
//
//
//            //the warnow tree
//            Tree warnowSCM = Newick.getTreeFromFile(scmTreeFile)[0];
//            taxa = warnowSCM.getNumTaxa();
//            System.out.println("Warnow Resolution: taxa: " + taxa + ", innerNodes: " + warnowSCM.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, warnowSCM.vertexCount()));
//            fnfp = FN_FP_RateComputer.calculateSumOfRates(warnowSCM, trees);
//            System.out.println("Warnow SFN/SFP to Source: " + Arrays.toString(fnfp));
//
//
//            System.out.println("########### DONE ##########");
//            System.out.println();
//            System.out.println();
//        }
//
//
//    }
}
