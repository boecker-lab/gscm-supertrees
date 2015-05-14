package scmAlgorithm;

import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.TreeUtilsBasic;
import junit.framework.Assert;
import org.junit.Test;
import treeUtils.TreeEquals;
import treeUtils.TreeEqualsTest;
import treeUtils.TreeUtils;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by fleisch on 08.05.15.
 */
public class SCMTest {



    @Test
    public void testCutTree() throws Exception {
        Tree a = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(d:1.0,c:1.0):1.0);");
        Tree b = Newick.getTreeFromString("((b:1.0,a:1.0):1.0,(c:1.0,d:1.0):1.0);");
        Tree c = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0):1.0,d:1.0);");
        Tree d = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,d:1.0);");
        Tree e = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        Tree f = Newick.getTreeFromString("(e:1.0,(d:1.0,(a:1.0,(b:1.0,c:1.0):1.0):1.0):1.0);");
        Tree g = Newick.getTreeFromString("((d:1.0,e:1.0):1.0,(a:1.0,(c:1.0,b:1.0):1.0):1.0)");
        Tree h = Newick.getTreeFromString("(d:1.0,(a:1.0,(b:1.0,(e:1.0,c:1.0):1.0):1.0):1.0)");
        Tree result = new Tree();
        List<String> tokeep = new ArrayList<String>();

        String print = "";

        //BEISPIEL A eingeschränkt auf a, b, c
        SCM s = new SCM();
        TreeEquals t = new TreeEquals();
        tokeep.add("a");
        tokeep.add("b");
        tokeep.add("c");
        result = s.CutTree(a, tokeep, false);
        Tree expected = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,c:1.0)");
        assertTrue(t.getTreeEquals(result, expected));


        //BEISPIEL A eingeschränkt auf a, b
        tokeep.clear();
        //a = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(d:1.0,c:1.0):1.0);");
        tokeep.add("a");
        tokeep.add("b");
        result = s.CutTree(a, tokeep, false);
        expected = Newick.getTreeFromString("(a:1.0,b:1.0)");
        assertTrue(t.getTreeEquals(result, expected));

        //BEISPIEL C eingeschränkt auf a, b
        tokeep.clear();
        List<TreeNode> testt = new ArrayList(Arrays.asList(a.getLeaves()));
        boolean ze = testt.contains(c.getVertex("a"));
        assertFalse(ze);


        tokeep.add("a");
        tokeep.add("c");
        result = s.CutTree(c, tokeep, false);
        expected = Newick.getTreeFromString("(a:1.0,c:1.0)");
        assertTrue(t.getTreeEquals(result, expected));

        //BEISPIEL C eingeschränkt auf c, d
        tokeep.clear();
        tokeep.add("c");
        tokeep.add("d");
        result = s.CutTree(c, tokeep, false);
        expected = Newick.getTreeFromString("(c:1.0,d:1.0)");
        assertTrue(t.getTreeEquals(result, expected));


        //BEISPIEL C eingeschränkt auf d
        tokeep.clear();
        tokeep.add("d");
        result = s.CutTree(c, tokeep, false);
        String res = Newick.getStringFromTree(result);
        expected = Newick.getTreeFromString("d:1.0");
        assertTrue(t.getTreeEquals(result, expected));

        //BEISPIEL G eingeschränkt auf d, e
        tokeep.clear();
        tokeep.add("d");
        tokeep.add("e");
        result = s.CutTree(g, tokeep, false);
        expected = Newick.getTreeFromString("(d:1.0, e:1.0)");
        assertTrue(t.getTreeEquals(result, expected));
        Map.Entry<String, List<String>>[] zwischen = s.getTreeCut1EntrySet();
        List<String> contained = zwischen[0].getValue();
        List<String> expectedlist = new ArrayList<String>();
        expectedlist.add ("c");
        assertTrue(TreeUtils.StringListsEquals(contained, expectedlist));
    }


    public Tree randomInsert(Tree tre, int nodenumber){
        //don't hang nodes at leaves
        Tree out = tre.cloneTree();
        String label = "";
        int vertexsize = 0;
        int randnumber = 0;
        boolean correct = false;
        Random rand = new Random();
        for (int iter=0; iter<nodenumber; iter++){
            label = "hangsatnode";
            vertexsize = tre.vertexCount();
            while (!correct){
                randnumber = rand.nextInt(vertexsize);
                if (!out.getVertex(randnumber).isLeaf()) correct = true;
            }
            label = label.concat(Integer.toString(randnumber));
            label = label.concat(",labeled_"+out.getVertex(randnumber).getLabel());
            label = label.concat(",try_"+Integer.toString(iter));
            TreeNode nod = new TreeNode(label);
            out.addVertex(nod);
            out.addEdge(out.getVertex(randnumber),nod);
        }
        return out;
    }

    @Test
    public void testCuttingAndInserting(){
        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        InputStream in = SCMTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        List<Tree> alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));


        Tree tree = alltrees.get(1);
        Tree compare = tree.cloneTree();
        Tree cutcompare;
        Tree cuttree;
        Tree hanginconsensus;
        Tree consensus = new Tree();
        //System.out.println(Newick.getStringFromTree(compare));
        NConsensus con = new NConsensus();
        //create 100 random nodes in tree
        compare = randomInsert(compare, 100);
        ArrayList<String> nodesofboth = TreeUtils.getOverLappingNodes(tree, compare);
        assertTrue(TreeUtils.StringListsEquals(nodesofboth, TreeUtils.helpgetLabelsFromNodes(Arrays.asList(tree.getLeaves()))));
        SCM s = new SCM();
        boolean turn = s.checkDirectionOfBackbone(nodesofboth, tree, compare);
        cuttree = s.CutTree(tree, nodesofboth, turn);
        cutcompare = s.CutTree(compare, nodesofboth, turn);
        consensus = con.consesusTree(new Tree[]{cuttree, cutcompare}, 1.0);

        TreeEquals equ = new TreeEquals();
        //Konsensusbaum gleich Usprungsbaum
        assertTrue(equ.getTreeEquals(tree, consensus));

        hanginconsensus = s.hangInNodes(consensus);
        //Baum mit eingefühten Vertices ist gleich Vergleichbaum
        assertTrue(equ.getTreeEquals(compare, hanginconsensus));


        Tree one = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,e:1.0):1.0,((f:1.0,g:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0);");
        Tree two = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,e:1.0):1.0):1.0,((g:1.0,f:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0):1.0,(k:1.0,l:1.0):1.0);");
        nodesofboth = TreeUtils.getOverLappingNodes(one, two);
        turn = s.checkDirectionOfBackbone(nodesofboth, one, two);
        Tree cutone = s.CutTree(one, nodesofboth, turn);
        Tree cuttwo = s.CutTree(two, nodesofboth, turn);
        NConsensus nee = new NConsensus();
        Tree res = nee.consesusTree(new Tree[]{cutone, cuttwo}, 1.0);
        Tree hungin = s.hangInNodes(res);
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0,e:1.0):1.0,((f:1.0,g:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0):1.0,(k:1.0,l:1.0):1.0)");
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(hungin, expected));
    }

    @Test
    public void testStrictConsensus(){
        Tree a = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(d:1.0,c:1.0):1.0);");
        Tree b = Newick.getTreeFromString("((b:1.0,a:1.0):1.0,(c:1.0,d:1.0):1.0);");
        Tree c = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0):1.0,d:1.0);");
        Tree d = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,d:1.0);");
        Tree e = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        Tree f = Newick.getTreeFromString("(e:1.0,(d:1.0,(a:1.0,(b:1.0,c:1.0):1.0):1.0):1.0);");
        Tree g = Newick.getTreeFromString("((d:1.0,e:1.0):1.0,(a:1.0,(c:1.0,b:1.0):1.0):1.0)");
        Tree h = Newick.getTreeFromString("(d:1.0,(a:1.0,(b:1.0,(e:1.0,c:1.0):1.0):1.0):1.0)");

        NConsensus eins = new NConsensus();
        NConsensus zwei = new NConsensus();
        NConsensus drei = new NConsensus();
        NConsensus vier = new NConsensus();
        NConsensus fuenf = new NConsensus();

        Tree reseins = eins.consesusTree(new Tree[]{a,b},1.0);
        Tree reszwei = zwei.consesusTree(new Tree[]{c,d},1.0);
        Tree resdrei = drei.consesusTree(new Tree[]{e,f},1.0);
        Tree resvier = vier.consesusTree(new Tree[]{e,g},1.0);
        Tree resfuenf = fuenf.consesusTree(new Tree[]{e,h},1.0);

        Tree expeins = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0)");
        Tree expzwei = Newick.getTreeFromString("((a:1.0,b:1.0,c:1.0):1.0,d:1.0)");
        Tree expdrei = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,d:1.0,e:1.0)");
        Tree expvier = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        Tree expfuenf = Newick.getTreeFromString("(a:1.0,b:1.0,c:1.0,d:1.0,e:1.0)");

        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(reseins, expeins));
        assertTrue(t.getTreeEquals(reszwei, expzwei));
        assertTrue(t.getTreeEquals(resdrei, expdrei));
        assertTrue(t.getTreeEquals(resvier, expvier));
        assertTrue(t.getTreeEquals(resfuenf, expfuenf));

    }

    @Test
    public void testSCM(){
        Tree e = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,f:1.0)");
        Tree f = Newick.getTreeFromString("((((a:1.0,y:1.0):1.0,c:1.0):1.0,z:1.0):1.0,f:1.0)");
        SCM s = new SCM();
        Tree result = s.getSCM(e, f);
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0,y:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0,z:1.0):1.0,f:1.0)");
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(result, expected));

        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\100\\20\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        //File fi = new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Source_Trees/RaxML/sm.0.sourceTrees_OptSCM-Rooting.tre");
        //Tree realSCM = Newick.getTreeFromFile(new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Super_Trees/Superfine/RaxML_Source/sm.0.sourceTrees.scmTree.tre"))[0];


        InputStream in = SCMTest.class.getResourceAsStream("/100_20_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        List<Tree> alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        //File fi = new File("100_20_sourceTree:sm.o.sourceTrees_OptSCM-Rooting.tre");
        //File fi2 = new File("500_50_sourceTree:sm.o.sourceTrees_OptSCM-Rooting.tre");

        CalculateSupertree res = new CalculateSupertree("resolution");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 100/20 resolution ist "+Newick.getStringFromTree(result));
        //Tree realSCM = Newick.getTreeFromFile(new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Super_Trees/Superfine/RaxML_Source/sm.0.sourceTrees.scmTree.tre"))[0];
        //Tree realSCM = Newick.getTreeFromFile(new File ("100_20_superTree_sm.0.sourceTrees.scmTree.tre"))[0];
        in = SCMTest.class.getResourceAsStream("/100_20_superTree_sm.0.sourceTrees.scmTree.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        Tree realSCM = alltrees.get(0);
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 resolution Supertree: "+ (result.vertexCount() - result.getNumTaxa()));
        System.out.println("100/20 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 swenson-Supertree: "+(realSCM.vertexCount() - realSCM.getNumTaxa()));
        //todo mach was damit :)
        System.out.println("100/20 resolution FNFP gegen Eingabebaeume");
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (double a : print){
            System.out.print(a + " ");
        }

        System.out.println("\n"+"100/20 resolution FNFP gegen swenson-Supertree");
        print = FN_FP_RateComputer.calculateRates(result, realSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        in = SCMTest.class.getResourceAsStream("/100_20_modelTree_sm_data.0.model_tree");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        Tree modelSCM = alltrees.get(0);
        System.out.println("\n"+"100/20 resolution FNFP gegen Modeltree");
        print = FN_FP_RateComputer.calculateRates(result, modelSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }

        System.out.println("\n"+"\n");

        in = SCMTest.class.getResourceAsStream("/100_20_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        res = new CalculateSupertree("overlap");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 100/20 overlap ist "+Newick.getStringFromTree(result));
        in = SCMTest.class.getResourceAsStream("/100_20_superTree_sm.0.sourceTrees.scmTree.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        realSCM = alltrees.get(0);
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 overlap Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("100/20 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        //todo mach was damit :)
        System.out.println("100/20 overlap FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"100/20 overlap FNFP gegen swenson-Supertree");
        print = FN_FP_RateComputer.calculateRates(result, realSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        in = SCMTest.class.getResourceAsStream("/100_20_modelTree_sm_data.0.model_tree");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        modelSCM = alltrees.get(0);
        System.out.println("\n"+"100/20 overlap FNFP gegen Modeltree");
        print = FN_FP_RateComputer.calculateRates(result, modelSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"\n");




        in = SCMTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        res = new CalculateSupertree("resolution");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 500/50 resolution ist "+Newick.getStringFromTree(result));
        in = SCMTest.class.getResourceAsStream("/500_50_superTree_sm.0.sourceTrees.scmTree.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        realSCM = alltrees.get(0);
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 resolution Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("500/50 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        //todo mach was damit :)
        System.out.println("500/50 resolution FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"500/50 resolution FNFP gegen swenson-Supertree");
        print = FN_FP_RateComputer.calculateRates(result, realSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        in = SCMTest.class.getResourceAsStream("/500_50_modelTree_sm_data.0.model_tree");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        modelSCM = alltrees.get(0);
        System.out.println("\n"+"500/50 resolution FNFP gegen Modeltree");
        print = FN_FP_RateComputer.calculateRates(result, modelSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"\n");






        in = SCMTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        res = new CalculateSupertree("overlap");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 500/50 overlap ist "+Newick.getStringFromTree(result));
        in = SCMTest.class.getResourceAsStream("/500_50_superTree_sm.0.sourceTrees.scmTree.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        realSCM = alltrees.get(0);
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 overlap Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("500/50 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        //todo mach was damit :)
        System.out.println("500/50 overlap FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"500/50 overlap FNFP gegen swenson-Supertree");
        print = FN_FP_RateComputer.calculateRates(result, realSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        in = SCMTest.class.getResourceAsStream("/500_50_modelTree_sm_data.0.model_tree");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        modelSCM = alltrees.get(0);
        System.out.println("\n"+"500/50 overlap FNFP gegen Modeltree");
        print = FN_FP_RateComputer.calculateRates(result, modelSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"\n");



    }


    @Test
    public void simpleTest(){
//        FN_FP_RateComputer.calculateRates()
//        FN_FP_RateComputer.calculateSumOfRates();
//        TreeUtilsBasic.calculateTreeResolution();
        assertEquals(4, 4);
        assertEquals(4,4);


    }

}
