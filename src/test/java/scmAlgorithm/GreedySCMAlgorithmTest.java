package scmAlgorithm;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.TreeUtilsBasic;
import org.junit.Test;
import scmAlgorithm.treeScorer.OverlapScorer;
import scmAlgorithm.treeScorer.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreeSelector;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by fleisch on 16.02.15.
 */


public class GreedySCMAlgorithmTest {
    public final static String newickInput100 = "scmAlgorithm/sm.13.sourceTrees_OptSCM-Rooting.tre";
    public final static String newickSCM100 = "scmAlgorithm/sm.13.sourceTrees.scmTree.tre_OptRoot.tre";
    public final static String newickInput100_NORoot = "scmAlgorithm/sm.13.sourceTrees.tre";
    public final static String newickSCM100_NORoot = "scmAlgorithm/sm.13.sourceTrees.scmTree.tre";

    public final static String newickInput1000 = "scmAlgorithm/sm.5.sourceTrees_OptSCM-Rooting.tre";
    public final static String newickSCM1000 = "scmAlgorithm/sm.5.sourceTrees.scmTree.tre_OptRoot.tre";
    public final static String newickInput1000_NORoot = "scmAlgorithm/sm.5.sourceTrees.tre";
    public final static String newickSCM1000_NORoot = "scmAlgorithm/sm.5.sourceTrees.scmTree.tre";

    //tree constants
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML_SCM_OPT;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//    final static String sourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    //    final static String alternateSourceTreeLocation =  Global.SM_SOURCE_TREES_RAXML_MODEL_LEAST;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//    final static String alternateSourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    //    final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML_OPT_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SMOG_SCM_TREES_SUPER_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//

    @Test
    public void smidgenSample100() {
        URL r = getClass().getResource("/" + newickInput100);
        String s = r.getFile();
        File inputFile = new File(s);
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        Tree scm = Newick.getTreeFromFile(new File(getClass().getResource("/" + newickSCM100_NORoot).getFile()))[0];
        List<String> order = new ArrayList<>(TreeUtilsBasic.getLeafLabels(supertree.getRoot()));
        Collections.sort(order);
        TreeUtilsBasic.sortTree(supertree, order);
        TreeUtilsBasic.sortTree(scm, order);
        System.out.println("Clades_Real_SCM: " + (scm.vertexCount() - scm.getNumTaxa()));
        System.out.println(Newick.getStringFromTree(scm));
        System.out.println("Strict-100: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        assertEquals(scm.getNumTaxa(), supertree.getNumTaxa());
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

   /* @Test
    public void smidgenSample100_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput100_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Strict-100_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }*/

    @Test
    public void smidgenSample100Adam() {
        File inputFile = new File(getClass().getResource("/" + newickInput100).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.ADAMS), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-100: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    /*@Test
    public void smidgenSample100Adam_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput100_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.ADAMS), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-100_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }*/

    @Test
    public void smidgenSample100Loose() {
        File inputFile = new File(getClass().getResource("/" + newickInput100).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.SEMI_STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-100: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

   /* @Test
    public void smidgenSample100Loose_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput100_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.SEMI_STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-100_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }
*/

    @Test
    public void smidgenSample1000() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        Tree scm = Newick.getTreeFromFile(new File(getClass().getResource("/" + newickSCM1000_NORoot).getFile()))[0];
        System.out.println("Clades_Real_SCM: " + (scm.vertexCount() - scm.getNumTaxa()));
        System.out.println("Strict-1000: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        assertEquals(scm.getNumTaxa(), supertree.getNumTaxa());
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }

   /* @Test
    public void smidgenSample1000_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);
        Tree supertree = algo.getSupertree();
        System.out.println("Strict-1000_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }*/

    @Test
    public void smidgenSample1000Adam() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.ADAMS), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-1000: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    /*@Test
    public void smidgenSample1000Adam_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.ADAMS), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-1000_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }*/

    @Test
    public void smidgenSample1000Loose() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.SEMI_STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-1000: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    /*@Test
    public void smidgenSample1000Loose_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-1000_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }*/

  /*  @Test
    public void debugTest() {
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

        //0 <= instances <= 30
        final int instanceMin = 0;
        final int instanceMax = 30;
        final Map<String, String> TEMPLATES = new HashMap<String, String>(Global.NR_OF_TEMPLATE_TAGS);
        final DescriptiveStatistics[] scmTimes = new DescriptiveStatistics[4];

        int trees = 0;
        int fptrees = 0;
        int swfptrees = 0;
        int equalrees = 0;

        int swensonClades = 0;
        int scmClades = 0;
        int consensusClades = 0;
        for (int taxa : taxas) {
            TEMPLATES.put(Global.TAG_TAXA, Integer.toString(taxa));

            int currentInstanceMax = 0;
            //the 1000 taxa dataset has only 10 replicates
            if (taxa == 100 || taxa == 500) {
                currentInstanceMax = Math.min(30, instanceMax);
            } else if (taxa == 1000) {
                currentInstanceMax = Math.min(30, instanceMax);
//                currentInstanceMax = Math.min(10, OPTIONS.instanceMax);
            }
            for (int scaffoldFactorIndex = 0; scaffoldFactorIndex < scaffoldFactors.length; scaffoldFactorIndex++) {
                scmTimes[scaffoldFactorIndex] = new DescriptiveStatistics();
                TEMPLATES.put(Global.TAG_SCAFFOLD, Integer.toString(scaffoldFactors[scaffoldFactorIndex]));


                for (int instance = instanceMin; instance < currentInstanceMax; instance++) {
                    TEMPLATES.put(Global.TAG_INSTANCE, Integer.toString(instance));
                    File scmFile = new File(EvalUtils.expandTemplate(scmTreeLocation, TEMPLATES));

                    if (scmFile.exists()) {
                        trees++;
                        Tree swensonSCM = Newick.getTreeFromFile(scmFile)[0];
                        File inputFile = new File(EvalUtils.expandTemplate(sourceTreeLocation, TEMPLATES));
                        Tree[] input = Newick.getTreeFromFile(inputFile);


                        System.out.println();
                        System.out.println("### Calc SCM Trees ###");
                        int c = swensonSCM.vertexCount() - swensonSCM.getNumTaxa();
                        swensonClades += c;
                        System.out.println("SCM-SMID: " + (c));
                        System.out.println(Newick.getStringFromTree(swensonSCM));

                        long scmTime = System.currentTimeMillis();
//                        GreedySCMAlgorithm scmAlgorithm =  new GreedySCMAlgorithm(new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(input)));
                        RandomizedSCMAlgorithm scmAlgorithm = new RandomizedSCMAlgorithm(25, TreeUtilsBasic.cloneTrees(input),new OverlapScorer(TreeScorer.ConsensusMethods.STRICT));
//                        RandomizedSCMAlgorithm scmAlgorithm =  new RandomizedSCMAlgorithm(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),25,TreeUtilsBasic.cloneTrees(input));
//                        GreedySCMAlgorithm scmAlgorithm =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(input)));

                        List<Tree> scmTrees = scmAlgorithm.getSupertrees();
                        Tree scmTree = scmTrees.get(0);
                        scmTime = System.currentTimeMillis() - scmTime;
                        scmTimes[scaffoldFactorIndex].addValue(scmTime);
                        c = scmTree.vertexCount() - scmTree.getNumTaxa();
                        scmClades += c;
                        System.out.println("SCM: " + (c));
                        System.out.println(Newick.getStringFromTree(scmTree));
                        System.out.println("SCM Time " + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/" + instance + ": " + (float) scmTime / 1000f + "s");

                        File scmResultFile = new File("/home/fleisch/Work/scmResults/" + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/smog." + instance + ".scmResults.tre");
                        Newick.trees2File(scmResultFile, scmTrees);

                        NConsensus cons = new NConsensus();
//                        AdamsConsensus cons  = new AdamsConsensus();
                        cons.setThresholdUnchecked(0.5d);
                        Tree scmTreesConsensus = cons.getConsensusTree(scmTrees.toArray(new Tree[scmTrees.size()]));
                        c = scmTreesConsensus.vertexCount() - scmTreesConsensus.getNumTaxa();
                        consensusClades += c;
                        System.out.println("SCM-Consensus: " + (c));
                        System.out.println(Newick.getStringFromTree(scmTreesConsensus));
//                        System.out.println("SCM Time " + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/" + instance + ": " + (float)scmTime/1000f + "s");


                        double[] rates = FN_FP_RateComputer.calculateRates(scmTree, swensonSCM, false);
                        double[] sumOfRates = FN_FP_RateComputer.calculateSumOfRates(scmTree, input);
                        double[] swensonSumOfRates = FN_FP_RateComputer.calculateSumOfRates(swensonSCM, input);

                        List<String> order = new ArrayList<>(TreeUtilsBasic.getLeafLabels(scmTree.getRoot()));
                        Collections.sort(order);
                        TreeUtilsBasic.sortTree(swensonSCM, order);
                        TreeUtilsBasic.sortTree(scmTree, order);

                        System.out.println("FN/FP to Swenson: ");
                        System.out.println(Arrays.toString(rates));
                        if (Double.compare(rates[0], 0d) == 0 && Double.compare(rates[1], 0d) == 0)
                            equalrees++;

                        System.out.println("SFN/SFP to Source: ");
                        System.out.println(Arrays.toString(sumOfRates));
                        assertEquals(0, Double.compare(0d, sumOfRates[1]));
                        if (Double.compare(sumOfRates[1], 0d) != 0)
                            fptrees++;
                        System.out.println("SFN/SFP Swenson to Source: ");
                        System.out.println(Arrays.toString(swensonSumOfRates));
                        assertEquals(0, Double.compare(0d, swensonSumOfRates[1]));
                        if (Double.compare(swensonSumOfRates[1], 0d) != 0)
                            swfptrees++;

                        System.out.println("######################");
                        System.out.println();


                    }

                }
            }
        }
        System.out.println("trees calculated: " + trees);
        System.out.println("trees with false positives: " + fptrees + " relative: " + (double) fptrees / (double) trees);
        System.out.println("swenson trees with false positives: " + swfptrees + " relative: " + (double) swfptrees / (double) trees);
        System.out.println("trees equal to swenson: " + equalrees);
        System.out.println("swenson clades: " + swensonClades);
        System.out.println("scm clades: " + scmClades);
        System.out.println("scmCons clades: " + consensusClades);
        System.out.println("Fleisch vs Shel: " + ((double) scmClades / (double) swensonClades * 100) + "%");
        System.out.println("FleischCons vs Shel: " + ((double) consensusClades / (double) swensonClades * 100) + "%");
        System.out.print("Running Times: [");
        for (DescriptiveStatistics scmTime : scmTimes) {
            System.out.print(" " + scmTime.getMean() / 1000);
        }
        System.out.println("]");

    }

    @Test
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
//        AbstractSCMAlgorithm algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT),trees));
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusResolutionScorer(TreeScorer.ConsensusMethods.STRICT), trees));
//        AbstractSCMAlgorithm algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
        Tree st = algo.getSupertree();
        double time = (double) (System.currentTimeMillis() - t) / 1000d;

        System.out.printf("Running Time: " + time + "s");
        int taxa = st.getNumTaxa();
        System.out.println("Resolution: taxa: " + taxa + ", innerNodes: " + st.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, st.vertexCount()));
        double[] fnfp = FN_FP_RateComputer.calculateSumOfRates(st, trees);
        System.out.println("SFN/SFP to Source: " + Arrays.toString(fnfp));


    }


    @Test
    public void bioDataTest() {
        Global.BioDataSet[] bioDataSets = {Global.BioDataSet.BEE_TREE, Global.BioDataSet.LEGUMES_SM18, Global.BioDataSet.SAXIFRAGALES, Global.BioDataSet.MAMMALS, Global.BioDataSet.MARSUPIALS, Global.BioDataSet.SEABIRDS,Global.BioDataSet.PRIMATES};
//        Global.BioDataSet[] bioDataSets = {Global.BioDataSet.PRIMATES};


        for (Global.BioDataSet bioDataSet : bioDataSets) {
            System.out.println("########### Calculating SCM comparison for: " + bioDataSet.toString() + " ##########");
            File inputFile;
            File scmTreeFile;
            boolean rootErrorCorection = true;
            if (rootErrorCorection) {
                System.out.println("INFO: Root error correction: Rooting after SCM Tree... ");
                if (!bioDataSet.ROOTED) {
                    inputFile = new File(bioDataSet.SOURCE_TREES_OPT);
                    scmTreeFile = new File(bioDataSet.SCM_TREE);
                } else {
                    inputFile = new File(bioDataSet.SOURCE_TREES);
                    scmTreeFile = new File(bioDataSet.SCM_TREE);
                }
            } else {
                System.out.println("INFO: NO root errot correction for FlipCut... ");
                inputFile = new File(bioDataSet.SOURCE_TREES);
                scmTreeFile = new File(bioDataSet.SCM_TREE);
            }
            Tree[] trees = Newick.getTreeFromFile(inputFile);

            //root check
            int counter = 0;
            for (Tree tree : trees) {
                if (tree.getRoot().childCount() == 2)
                    counter++;
            }
            System.out.println(counter + " of " + trees.length + "are rooted: " + ((double) counter / (double) trees.length) * 100d + "%");

            long t = System.currentTimeMillis();
            AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new OverlapScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(trees)));
            Tree stOverlap = algo.getSupertree();
            double timeOverlap = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println();
            System.out.println("Overlap Running Time: " + timeOverlap + "s");
            int taxa = stOverlap.getNumTaxa();
            System.out.println("Overlap Resolution: taxa: " + taxa + ", innerNodes: " + stOverlap.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stOverlap.vertexCount()));
            double[] fnfp = FN_FP_RateComputer.calculateSumOfRates(stOverlap, trees);
            System.out.println("Overlap SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();

            t = System.currentTimeMillis();
            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusResolutionScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(trees)));
//         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
            Tree stRes = algo.getSupertree();
            double timeRes = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println("Resolution Running Time: " + timeRes + "s");
            taxa = stRes.getNumTaxa();
            System.out.println("Resolution Resolution: taxa: " + taxa + ", innerNodes: " + stRes.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stRes.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(stRes, trees);
            System.out.println("Resolution SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();

            t = System.currentTimeMillis();
            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new ConsensusCladeNumberScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(trees)));
//         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
            Tree stNum = algo.getSupertree();
            double timeNum = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println("CladeNumber Running Time: " + timeNum + "s");
            taxa = stNum.getNumTaxa();
            System.out.println("CladeNumber Resolution: taxa: " + taxa + ", innerNodes: " + stNum.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNum.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNum, trees);
            System.out.println("CladeNumber SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();


            t = System.currentTimeMillis();
            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new BackboneResolutionScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(trees)));
//         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
            Tree stResBack = algo.getSupertree();
            double timeResBack = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println("Backbone-Resolution Running Time: " + timeResBack + "s");
            taxa = stResBack.getNumTaxa();
            System.out.println("Backbone-Resolution Resolution: taxa: " + taxa + ", innerNodes: " + stResBack.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stResBack.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(stResBack, trees);
            System.out.println("Backbone-Resolution SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();

            t = System.currentTimeMillis();
            algo = new GreedySCMAlgorithm(new GreedyTreeSelector(new BackboneCladeNumberScorer(TreeScorer.ConsensusMethods.STRICT), TreeUtilsBasic.cloneTrees(trees)));
//         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
            Tree stNumBack = algo.getSupertree();
            double timeNumBack = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println("Backbone-CladeNumber Running Time: " + timeNumBack + "s");
            taxa = stNumBack.getNumTaxa();
            System.out.println("Backbone-CladeNumber Resolution: taxa: " + taxa + ", innerNodes: " + stNumBack.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumBack.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumBack, trees);
            System.out.println("Backbone-CladeNumber SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();



*//*            //randomized versions
            t = System.currentTimeMillis();
            algo = new RandomizedSCMAlgorithm(25,TreeUtilsBasic.cloneTrees(trees),new OverlapScorer(TreeScorer.ConsensusMethods.STRICT),new ConsensusCladeNumberScorer(TreeScorer.ConsensusMethods.STRICT),new ConsensusResolutionScorer(TreeScorer.ConsensusMethods.STRICT),new ConsensusCladeNumberScorer(TreeScorer.ConsensusMethods.STRICT),new BackboneCladeNumberScorer(TreeScorer.ConsensusMethods.STRICT),new BackboneResolutionScorer(TreeScorer.ConsensusMethods.STRICT));
//         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
            Tree stNumRand = algo.getSupertree();
            double timeNumRand = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println("RandOverlap Running Time: " + timeNumRand + "s");
            taxa = stNumRand.getNumTaxa();
            System.out.println("RandOverlap Resolution: taxa: " + taxa + ", innerNodes: " + stNumRand.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumRand.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumRand, trees);
            System.out.println("RandOverlap SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();*//*

            t = System.currentTimeMillis();
            algo = new RandomizedSCMAlgorithm(25,TreeUtilsBasic.cloneTrees(trees),new OverlapScorer(TreeScorer.ConsensusMethods.SEMI_STRICT));
//         algo =  new GreedySCMAlgorithm(new GreedyTreeSelector(new ResolutionScorer(TreeScorer.ConsensusMethods.STRICT),trees));
            List<Tree> temp =  algo.getSupertrees();
            NConsensus c =  new NConsensus();
            c.setMethod(NConsensus.METHOD_MAJORITY);
            Tree stNumRandRand = c.getConsensusTree(temp.toArray(new Tree[temp.size()]));
//            Tree stNumRandRand = algo.getSupertree();
            double timeNumRandRand = (double) (System.currentTimeMillis() - t) / 1000d;
            System.out.println(Newick.getStringFromTree(stNumRandRand));
            System.out.println("RandRandOverlap Running Time: " + timeNumRandRand + "s");
            taxa = stNumRandRand.getNumTaxa();
            System.out.println("RandRandOverlap Resolution: taxa: " + taxa + ", innerNodes: " + stNumRandRand.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, stNumRandRand.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(stNumRandRand, trees);
            System.out.println("RandRandOverlap SFN/SFP to Source: " + Arrays.toString(fnfp));
            System.out.println();




            //the warnow tree
            Tree warnowSCM = Newick.getTreeFromFile(scmTreeFile)[0];
            taxa = warnowSCM.getNumTaxa();
            System.out.println("Warnow Resolution: taxa: " + taxa + ", innerNodes: " + warnowSCM.vertexCount() + ", Resolution: " + TreeUtilsBasic.calculateTreeResolution(taxa, warnowSCM.vertexCount()));
            fnfp = FN_FP_RateComputer.calculateSumOfRates(warnowSCM, trees);
            System.out.println("Warnow SFN/SFP to Source: " + Arrays.toString(fnfp));


            System.out.println("########### DONE ##########");
            System.out.println();
            System.out.println();
        }


    }*/
}
