package scm.algorithm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;
import scm.algorithm.treeSelector.TreeScorer;
import scm.algorithm.treeSelector.TreeScorers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by fleisch on 06.11.15.
 */

@RunWith(Parameterized.class)
public class MultiGreedySCMAlgorithmTest extends BasicSCMTest {

    TreeScorer[] scorerArray;
    final static int ITERATIONS = 4;

    public MultiGreedySCMAlgorithmTest(Tree[] inputData, Tree scmResult, TreeScorer[] scorerArray) {
        super(inputData, scmResult);
        this.scorerArray = scorerArray;
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> paras = new LinkedList<>();

        paras.addAll(Arrays.asList(
                new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(), new TreeScorer[]{TreeScorers.newOverlapScorer(true)}},
                new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(), new TreeScorer[]{TreeScorers.newUniqueTaxonScorer(true)}},
                new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(), new TreeScorer[]{TreeScorers.newTaxonScorer(true)}},
                new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(), TreeScorers.getFullScorerArray(true)},
                new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), new TreeScorer[]{TreeScorers.newOverlapScorer(true)}},
                new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), new TreeScorer[]{TreeScorers.newUniqueTaxonScorer(true)}},
                new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), new TreeScorer[]{TreeScorers.newTaxonScorer(true)}},
                new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), TreeScorers.getFullScorerArray(true)}));

        return paras;

    }


    @Test
    public void multiScorerSmidGenTest() {
        StringBuffer buffer = new StringBuffer();
        System.out.println("Clades_Real_SCM: " + (scmResult.vertexCount() - scmResult.getNumTaxa()));
        System.out.println();


        long t = System.currentTimeMillis();
        MultiGreedySCMAlgorithm algo = new MultiGreedySCMAlgorithm(inputData, scorerArray);
        algo.run();
        Tree supertree = algo.getResult();
        buffer.append("Randomized-" + "strict" + "-250: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        buffer.append(SEP);
        assertNotNull(supertree);
        assertEquals(scmResult.getNumTaxa(), supertree.getNumTaxa());
        buffer.append(Newick.getStringFromTree(supertree));
        buffer.append(SEP);
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        buffer.append("Clades: " + inner);
        buffer.append(SEP);
        buffer.append(SEP);
        buffer.append(SEP);


        t = System.currentTimeMillis();
        algo = new MultiGreedySCMAlgorithm(inputData, scorerArray);
        algo.setThreads(4);
        algo.run();
        Tree supertreeP = algo.getResult();
        buffer.append("Randomized-Parallel-" + "method" + "-250: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        buffer.append(SEP);
        assertEquals(scmResult.getNumTaxa(), supertreeP.getNumTaxa());
        assertNotNull(supertreeP);
        assertEquals(scmResult.getNumTaxa(), supertreeP.getNumTaxa());
        buffer.append(Newick.getStringFromTree(supertreeP));
        buffer.append(SEP);
        inner = supertreeP.vertexCount() - supertreeP.getNumTaxa();
        buffer.append("Clades: " + inner);
        buffer.append(SEP);
        buffer.append(SEP);
        buffer.append(SEP);
        System.out.println(buffer.toString());
    }


    @Test
    public void randomizedSmidGenTest() {
        StringBuffer buffer = new StringBuffer();
        System.out.println("Clades_Real_SCM: " + (scmResult.vertexCount() - scmResult.getNumTaxa()));
        System.out.println();


        long t = System.currentTimeMillis();
        RandomizedSCMAlgorithm algo = new RandomizedSCMAlgorithm(ITERATIONS, inputData, scorerArray);
        algo.run();
        Tree supertree = algo.getResult();
        buffer.append("Randomized-" + "strict" + "-250: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        buffer.append(SEP);
        assertNotNull(supertree);
        assertEquals(scmResult.getNumTaxa(), supertree.getNumTaxa());
        buffer.append(Newick.getStringFromTree(supertree));
        buffer.append(SEP);
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        buffer.append("Clades: " + inner);
        buffer.append(SEP);
        buffer.append(SEP);
        buffer.append(SEP);


        t = System.currentTimeMillis();
        algo = new RandomizedSCMAlgorithm(ITERATIONS, inputData, scorerArray);
        algo.setThreads(2);
        algo.run();
        Tree supertreeP = algo.getResult();
        buffer.append("Randomized-Parallel-" + "method" + "-250: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        buffer.append(SEP);
        assertEquals(scmResult.getNumTaxa(), supertreeP.getNumTaxa());
        assertNotNull(supertreeP);
        assertEquals(scmResult.getNumTaxa(), supertreeP.getNumTaxa());
        buffer.append(Newick.getStringFromTree(supertreeP));
        buffer.append(SEP);
        inner = supertreeP.vertexCount() - supertreeP.getNumTaxa();
        buffer.append("Clades: " + inner);
        buffer.append(SEP);
        buffer.append(SEP);
        buffer.append(SEP);
        System.out.println(buffer.toString());
    }

}
