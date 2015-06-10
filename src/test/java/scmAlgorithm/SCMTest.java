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

    /*
    @Test
    public void testtakeCareOfCollisions (){
        SCM s = new SCM();
        TreeEquals t = new TreeEquals();
        Tree one = Newick.getTreeFromString("(((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((f:1.0,((g:1.0,h:1.0):1.0,(i:1.0,j:1.0):1.0):1.0):1.0,(k:1.0,(l:1.0,m:1.0):1.0):1.0):1.0):1.0,((ab:1.0,((ac:1.0,ad:1.0):1.0,(ae:1.0,af:1.0):1.0):1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,(ba:1.0,(r:1.0,s:1.0):1.0):1.0):1.0):1.0)");
        Tree two = Newick.getTreeFromString("(((((a:1.0,t:1.0):1.0,u:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((i:1.0,y:1.0):1.0,(w:1.0,(x:1.0,m:1.0):1.0):1.0):1.0):1.0,((ab:1.0,ag:1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,(((ba:1.0,bb:1.0):1.0,bc:1.0):1.0,(v:1.0,s:1.0):1.0):1.0):1.0):1.0)");
        Tree result = s.getSCM(one, two, false);
        String re = Newick.getStringFromTree(result);
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0,c:1.0,t:1.0,u:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((i:1.0,y:1.0,j:1.0,f:1.0,(g:1.0,h:1.0):1.0):1.0,(k:1.0,l:1.0,m:1.0,x:1.0,w:1.0):1.0):1.0):1.0,((ab:1.0,ag:1.0,((ac:1.0,ad:1.0):1.0,(ae:1.0,af:1.0):1.0):1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,((bc:1.0,(ba:1.0,bb:1.0):1.0):1.0,(v:1.0,r:1.0,s:1.0):1.0):1.0):1.0):1.0);");
        double[] x = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{one, two});
        for (double y : x){
            System.out.print(y+" ");
        }
        assertTrue(t.getTreeEquals(result,expected));

        Tree a = Newick.getTreeFromString("((((a:1.0,c:1.0):1.0,d:1.0):1.0,b:1.0):1.0,f:1.0)");
        Tree b = Newick.getTreeFromString("((((a:1.0,x:1.0):1.0,y:1.0):1.0,b:1.0):1.0,(f:1.0,z:1.0):1.0)");
        result = s.getSCM(a, b, false);
        expected = Newick.getTreeFromString("(((a:1.0,c:1.0,d:1.0,x:1.0,y:1.0):1.0,b:1.0):1.0,(z:1.0,f:1.0):1.0)");
        assertTrue(t.getTreeEquals(result, expected));
        List<Tree> trees = new ArrayList<Tree>();
        trees.add(a);
        trees.add(b);
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, trees.toArray(new Tree[trees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (double ac : print){
            System.out.print(ac + " ");
        }
        Tree maybe = Newick.getTreeFromString("((((a:1.0,c:1.0,x:1.0):1.0,d:1.0,y:1.0):1.0,b:1.0):1.0,(f:1.0,z:1.0):1.0)");
        print = FN_FP_RateComputer.calculateSumOfRates(maybe, trees.toArray(new Tree[trees.size()])); //sum FP has to be 0 for any SCM result [1]
        System.out.println();
        for (double ac : print){
            System.out.print(ac + " ");
        }
    }


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
        //Baum mit eingefügten Vertices ist gleich Vergleichbaum
        assertTrue(equ.getTreeEquals(compare, hanginconsensus));


        Tree one = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,e:1.0):1.0,((f:1.0,g:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0);");
        Tree two = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,e:1.0):1.0):1.0,((g:1.0,f:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0):1.0,(k:1.0,l:1.0):1.0);");
        nodesofboth = TreeUtils.getOverLappingNodes(one, two);
        turn = s.checkDirectionOfBackbone(nodesofboth, one, two);
        Tree cutone = s.CutTree(one, nodesofboth, turn);
        Tree cuttwo = s.CutTree(two, nodesofboth, turn);
        NConsensus nee = new NConsensus();
        Tree res = nee.consesusTree(new Tree[]{cutone, cuttwo}, 1.0);
        s.takeCareOfCollisions(res, one, two);
        Tree hungin = s.hangInNodes(res);
        System.out.println("hungin ist "+Newick.getStringFromTree(hungin));
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
        Tree result = s.getSCM(e, f, false);
        System.out.println(Newick.getStringFromTree(result));
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0,y:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0,z:1.0):1.0,f:1.0)");
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(result, expected));

        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\100\\20\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        //File fi = new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Source_Trees/RaxML/sm.0.sourceTrees_OptSCM-Rooting.tre");
        //Tree realSCM = Newick.getTreeFromFile(new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Super_Trees/Superfine/RaxML_Source/sm.0.sourceTrees.scmTree.tre"))[0];


        InputStream in = SCMTest.class.getResourceAsStream("/100_20_superTree_sm.0.sourceTrees.scmTree.tre_OptRoot.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        List<Tree> alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        Tree realSCM = alltrees.get(0);

        in = SCMTest.class.getResourceAsStream("/100_20_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        System.out.println("Das ist das Beispiel ->");
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        //for (Tree tree : alltrees){
        //    System.out.println(tree + " "+ Newick.getStringFromTree(tree));
        //}
        //File fi = new File("100_20_sourceTree:sm.o.sourceTrees_OptSCM-Rooting.tre");
        //File fi2 = new File("500_50_sourceTree:sm.o.sourceTrees_OptSCM-Rooting.tre");

        CalculateSupertree res = new CalculateSupertree("resolution", "on");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 100/20 resolution ist "+Newick.getStringFromTree(result));
        //Tree realSCM = Newick.getTreeFromFile(new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Super_Trees/Superfine/RaxML_Source/sm.0.sourceTrees.scmTree.tre"))[0];
        //Tree realSCM = Newick.getTreeFromFile(new File ("100_20_superTree_sm.0.sourceTrees.scmTree.tre"))[0];
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

        in = SCMTest.class.getResourceAsStream("/100_20_superTree_sm.0.sourceTrees.scmTree.tre_OptRoot.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        realSCM = alltrees.get(0);
        in = SCMTest.class.getResourceAsStream("/100_20_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        res = new CalculateSupertree("overlap", "off");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 100/20 overlap ist "+Newick.getStringFromTree(result));
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



        in = SCMTest.class.getResourceAsStream("/500_50_superTree_sm.0.sourceTrees.scmTree.tre_OptRoot.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        realSCM = alltrees.get(0);
        in = SCMTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        res = new CalculateSupertree("resolution", "off");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 500/50 resolution ist "+Newick.getStringFromTree(result));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 resolution Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("500/50 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        //todo mach was damit :)
        System.out.println("500/50 resolution FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"500/50 SWENSON FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(realSCM, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
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
        System.out.println("\n"+"500/50 SWENSON FNFP gegen Modeltree");
        print = FN_FP_RateComputer.calculateRates(realSCM, modelSCM, false);
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"\n");





        in = SCMTest.class.getResourceAsStream("/500_50_superTree_sm.0.sourceTrees.scmTree.tre_OptRoot.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        realSCM = alltrees.get(0);
        in = SCMTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        res = new CalculateSupertree("overlap", "off");
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 500/50 overlap ist "+Newick.getStringFromTree(result));
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

    @Test
    public void testAnotherExample(){
        SCM s = new SCM();
        TreeEquals tr = new TreeEquals();
        System.out.println("Schrittweises testen");

        //73cdc5dd aus one und two
        Tree currentconsensus = Newick.getTreeFromString("((t9:1.0,(t65:1.0,t69:1.0):1.0):1.0,((t89:1.0,t58:1.0):1.0,(t95:1.0,(((t88:1.0,(t41:1.0,t100:1.0):1.0):1.0,((t12:1.0,(t78:1.0,t4:1.0):1.0):1.0,(t85:1.0,(t98:1.0,(t22:1.0,t75:1.0):1.0):1.0):1.0):1.0):1.0,(((((t49:1.0,(t91:1.0,((t46:1.0,t55:1.0)100.0:1.0,t52:1.0):1.0)100.0:1.0)100.0:1.0,(((t60:1.0,(t87:1.0,t97:1.0):1.0):1.0,(t94:1.0,(t54:1.0,t72:1.0):1.0):1.0):1.0,((t53:1.0,t84:1.0)100.0:1.0,t5:1.0):1.0):1.0)100.0:1.0,((t26:1.0,t20:1.0):1.0,(t1:1.0,(t40:1.0,(t70:1.0,(t37:1.0,t28:1.0):1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t80:1.0,(t93:1.0,(t83:1.0,(t64:1.0,t15:1.0):1.0):1.0):1.0):1.0)100.0:1.0,t51:1.0):1.0):1.0):1.0):1.0);");
        Tree onetwoscm = currentconsensus.cloneTree();
        //3d980b94
        Tree one = Newick.getTreeFromString("((t9:0.054987521132841805,(t65:0.07521557818621927,t69:0.09078632559118642)96:0.006980430354888036)100:0.014555858187957463,((t89:0.09073166323905532,t58:0.10087472916417704)99:0.0071069596780251685,(t95:0.10356492482091678,((t80:0.09092633106699695,(((t49:0.04292419828063336,(t91:0.04210805892237853,(t55:0.024791548317378603,t46:0.032528915512921745)100:0.012755523151436576)89:0.00959736130461549)100:0.018981674650068257,(t53:0.012610060093753062,t84:0.011183143023783594)100:0.05955481184794004)100:0.013977657476357954,t1:0.07320327433956765)78:0.002526046443931063)100:0.03681836409854615,((t88:0.0945070137548482,(t41:0.06122956192588597,t100:0.08451238837136035)100:0.02236381313657539)88:0.0036152135705385525,(((t78:0.041857980537849206,t4:0.05859185580392285)100:0.01975464752940772,t12:0.060844805196572004)94:0.009994714353079056,(((t22:0.04377250465456753,t75:0.04139191213027748)100:0.03881733450467057,t98:0.10254235153877182)43:0.00879559458937779,t85:0.081746144721931)30:3.79246544195539E-4)96:0.012128149219354317)83:0.0016346620294501817)99:0.011681322414998316)76:0.00337490672025486)100:0.014555858187957463);");
        //49204123
        Tree two = Newick.getTreeFromString("(t51:0.37303012547209424,((((t26:0.08256303349060884,t20:0.03796791931085985)100:0.3716261612921274,(t1:0.3733628190121403,(t40:0.3057661765304502,(t70:0.21654956322349805,(t37:0.031461192523511206,t28:1.28747731903457E-6)100:0.10083452471468739)100:0.13475336997599993)87:0.03711994928858252)100:0.0409437423256119)90:0.022884159565395714,((t49:0.24812807844268672,(t91:0.1711776210636979,(t52:0.12227608811189365,(t46:0.10752637980542543,t55:0.13082194589157448)100:0.08931060184558916)47:0.00920893972638092)80:0.038603970117298714)100:0.1676745711355348,(((t60:0.08253559993160572,(t87:0.09204353234286111,t97:0.08985467036398831)85:0.006796959425953227)100:0.10883996927418618,((t54:0.05966579551405614,t72:0.02581238557341237)100:0.05234158724747444,t94:0.07117384270450372)100:0.1291495309499208)100:0.06173554374884402,((t84:0.06109381549075506,t53:0.08189672346747322)100:0.1662439995921057,t5:0.1997084588506871)79:0.0019060358760333194)100:0.07631953895046267)96:0.0687229810324413)75:0.020403950393181085,(t80:0.3525241366500182,((t83:0.1679092630207176,(t64:0.08442183907425846,t15:0.09271038380544808)99:0.08143990891980352)100:0.18189883957334188,t93:0.4099290948104745)91:0.028249399878848298)93:0.06037679557084066)93:0.37303012547209424);");
        Tree expected = s.getSCM(one, two, false);
        System.out.println("one and two "+Newick.getStringFromTree(expected));
        assertTrue(tr.getTreeEquals(expected, onetwoscm));
        Tree[] sourcetrees = new Tree[]{one, two};
        double[] print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        //System.out.println("okay");
        System.out.println();

        //2676c422 aus three und five
        currentconsensus = Newick.getTreeFromString("((((t85:1.0,(t18:1.0,t45:1.0)100.0:1.0)100.0:1.0,(((((t39:1.0,(t25:1.0,t75:1.0)100.0:1.0)100.0:1.0,(t61:1.0,t66:1.0)100.0:1.0,t22:1.0)100.0:1.0,(t6:1.0,t17:1.0):1.0):1.0,(t98:1.0,t73:1.0):1.0)100.0:1.0,((t12:1.0,(t78:1.0,((t4:1.0,t79:1.0)100.0:1.0,t74:1.0):1.0)100.0:1.0)100.0:1.0,t11:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((t16:1.0,t2:1.0)100.0:1.0,((t77:1.0,((t71:1.0,t88:1.0)100.0:1.0,(t90:1.0,(t59:1.0,t23:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t44:1.0,t24:1.0):1.0)100.0:1.0,((t10:1.0,(t100:1.0,t43:1.0)100.0:1.0)100.0:1.0,((t76:1.0,t82:1.0)100.0:1.0,t41:1.0):1.0)100.0:1.0)100.0:1.0,((((t26:1.0,t20:1.0):1.0,(t1:1.0,(t40:1.0,(t70:1.0,(t28:1.0,t37:1.0):1.0):1.0):1.0):1.0):1.0,((t49:1.0,(t91:1.0,(t46:1.0,t55:1.0):1.0):1.0):1.0,(((t94:1.0,(t72:1.0,t54:1.0):1.0):1.0,(t60:1.0,(t87:1.0,t97:1.0):1.0):1.0):1.0,(t53:1.0,t5:1.0):1.0):1.0):1.0):1.0,(t80:1.0,(t93:1.0,(t83:1.0,(t64:1.0,t15:1.0):1.0):1.0):1.0):1.0):1.0):1.0)100.0:1.0,t51:1.0);");
        Tree threefivescm = currentconsensus.cloneTree();
        //64948656
        Tree three = Newick.getTreeFromString("((((t18:0.0759263250893133,t45:0.07021579121319767)100:0.10697347547291042,t85:0.22836476007440049)100:0.6341342253956597,((t11:0.7304404002203917,(t12:0.6154838902191695,(t78:0.39311240128355535,((t4:0.08107146406539031,t79:0.08024553580218259)98:0.061751096853353345,t74:0.10653709820020252)100:0.36165171400948115)100:0.21763948977609826)98:0.09407476707763518)63:0.03815879273104731,(t98:0.7127304608358878,((t6:0.47374677908700513,t17:0.5967635890410097)100:0.12428318459803155,((t61:0.21582398160470576,t66:0.25337295600411247)100:0.17313511981332108,(t22:0.3500890196416706,(t39:0.1027820176274421,(t25:0.05166578512454078,t75:0.09520096053003949)99:0.060630359482473827)100:0.32896606499181535)42:1.14122640082863E-6)100:0.33057247478556123)75:0.05976539765541415)98:0.09025700796050377)94:0.05322999825606577)99:0.06155992556140815,((((t10:0.5954560772622253,(t100:0.14997642775232572,t43:0.1495551070589991)100:0.4962618590179594)95:0.11670921065702611,(t41:0.25690168013169196,(t76:0.12310121945545617,t82:0.12758304565610767)100:0.13531221704829646)100:0.33745393901924214)97:0.11068839506906296,(t16:0.39641052271519023,t2:0.4594273218615477)100:0.32394517138072626)9:1.14122640082863E-6,((t44:0.6984384744317446,t24:0.6409391152509648)70:0.02417414279175514,(((t71:0.4269777344671854,t88:0.31227530541558696)100:0.17510602412414664,(t90:0.18776462022902032,(t23:0.1916349232982231,t59:0.1817966354476787)100:0.08102287083963133)100:0.2947294664949311)100:0.2172991893250598,t77:0.627961752267288)100:0.12372157972918663)79:0.048687053502271885)99:0.06155992556140815);");
        //f60da62
        Tree five = Newick.getTreeFromString("(t51:0.2567829420352974,((((t45:0.05584859851902249,t18:0.03827396693152063)100:0.052635272649831186,t85:0.15785820644498047)100:0.39285014664242524,(((t22:0.3069943155143126,((t66:0.15228525338955365,t61:0.16157235525137179)100:0.13925557708880454,((t25:0.035541472992600846,t75:0.05214587349138846)100:0.025020633344945347,t39:0.05627390674505449)100:0.20063298603288154)33:1.06780185157964E-6)100:0.2121612065335444,(t98:0.18241188159847507,t73:0.16029983491054137)100:0.3272627126989126)100:0.08798485625848279,(t11:0.4539455332427911,((t78:0.2836586370182852,(t4:0.05044137797169084,t79:0.03524769880516037)100:0.2025804413429857)100:0.11026028432186107,t12:0.3420929255275001)85:0.02928689245300845)85:0.026275337693216518)92:0.018143316592531624)99:0.035544741892286846,((((t2:0.30592648170605385,t16:0.23735111777121706)100:0.18358165818854127,(t44:0.4200380965834409,(t77:0.42314081506561135,((t88:0.2450398165855797,t71:0.2629665479524521)100:0.14849209923541185,(t90:0.11577861327930489,(t23:0.15029596023597305,t59:0.1080168084104879)100:0.07827583415802075)100:0.14003040736337533)100:0.07714790357865382)100:0.08377462719525683)73:0.03200171899311812)59:0.019389197302917348,((t76:0.09149104678786708,t82:0.06019951692292751)100:0.37801198095023064,((t43:0.10352331389672545,t100:0.06710253169340724)100:0.32256107552320773,t10:0.3780022084462989)82:0.04648577969350311)97:0.0940256106133712)30:0.0010786037660648585,((((t26:0.08309356747795776,t20:0.040516016511434404)100:0.3727565172235628,(t1:0.42225474932064644,(t40:0.3510935638780678,((t28:0.005648097227490827,t37:0.030384274145709864)100:0.10024859571638137,t70:0.263765149097874)100:0.10704449925000081)80:0.037694080643182956)77:0.023236096392701314)91:0.034862668045933284,((t49:0.305544045482263,(t91:0.21635301688074723,(t46:0.12155593219794739,t55:0.11838402114709738)100:0.0987214099386142)79:0.04234599210694596)100:0.14599247172687635,((((t72:0.036522640234183536,t54:0.06480857131339794)100:0.057908609819860034,t94:0.0721630303078848)100:0.1264871094591558,((t87:0.0884603333746614,t97:0.10030378175847526)68:0.007431337012270831,t60:0.08327246917897599)100:0.11733079762995176)100:0.05648795803154411,(t53:0.25602882650532915,t5:0.18970747330626522)69:0.007309362118318221)100:0.08036539537599326)95:0.05645832950556943)79:0.014390787045528756,(t80:0.3890562646352084,(t93:0.39721037709612356,(t83:0.18673623306644618,(t64:0.08481584603877838,t15:0.07934759490882465)100:0.11094997454936852)100:0.2400664130494961)85:0.00947335691262557)97:0.07543971124995662)100:0.10756161384660862)80:0.03078030424595549)80:0.2567829420352974);");
        expected = s.getSCM(three, five, false);
        assertTrue(tr.getTreeEquals(expected, threefivescm));
        sourcetrees = new Tree[]{three, five};
        print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        //System.out.println("okay");
        System.out.println();

        //3b6e4aee aus one, two, three, five
        currentconsensus = Newick.getTreeFromString("((t9:1.0,(t65:1.0,t69:1.0):1.0):1.0,((t89:1.0,t58:1.0):1.0,(((((((t64:1.0,t15:1.0)100.0:1.0,t83:1.0)100.0:1.0,t93:1.0)100.0:1.0,t80:1.0)100.0:1.0,(((t26:1.0,t20:1.0)100.0:1.0,(t1:1.0,(((t37:1.0,t28:1.0)100.0:1.0,t70:1.0)100.0:1.0,t40:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((t91:1.0,((t55:1.0,t46:1.0)100.0:1.0,t52:1.0):1.0)100.0:1.0,t49:1.0)100.0:1.0,((t5:1.0,(t53:1.0,t84:1.0):1.0)100.0:1.0,((t94:1.0,(t54:1.0,t72:1.0)100.0:1.0)100.0:1.0,((t97:1.0,t87:1.0)100.0:1.0,t60:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t51:1.0,(((t10:1.0,(t100:1.0,t43:1.0):1.0):1.0,(t41:1.0,(t76:1.0,t82:1.0):1.0):1.0)100.0:1.0,(t16:1.0,t2:1.0):1.0,((t77:1.0,((t88:1.0,t71:1.0):1.0,(t90:1.0,(t59:1.0,t23:1.0):1.0):1.0):1.0):1.0,(t44:1.0,t24:1.0):1.0):1.0)100.0:1.0,((((t22:1.0,(t39:1.0,(t75:1.0,t25:1.0):1.0):1.0,(t61:1.0,t66:1.0):1.0)100.0:1.0,(t6:1.0,t17:1.0):1.0):1.0,(t98:1.0,t73:1.0):1.0)100.0:1.0,(t85:1.0,(t18:1.0,t45:1.0):1.0):1.0,(((t78:1.0,(t74:1.0,(t4:1.0,t79:1.0):1.0):1.0)100.0:1.0,t12:1.0)100.0:1.0,t11:1.0):1.0)100.0:1.0)100.0:1.0,t95:1.0):1.0):1.0);");
        Tree onetwothreefivescm = currentconsensus.cloneTree();
        //73cdc5dd aus one und two
        //2676c422 aus three und five
        expected = s.getSCM(onetwoscm, threefivescm, false);
        System.out.println("one, two, three, five "+Newick.getStringFromTree(expected));
        assertTrue(tr.getTreeEquals(onetwothreefivescm, expected));
        sourcetrees = new Tree[]{one, two, three, five};
        print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        //System.out.println("okay");
        System.out.println();

        //a5ebd6b aus four, six
        currentconsensus = Newick.getTreeFromString("(((t36:1.0,((t86:1.0,t9:1.0)100.0:1.0,(t67:1.0,(t81:1.0,(t31:1.0,t29:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t33:1.0,t50:1.0,t69:1.0)100.0:1.0,(t30:1.0,(t27:1.0,t65:1.0):1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((((t8:1.0,t62:1.0)100.0:1.0,t21:1.0)100.0:1.0,((t35:1.0,(t3:1.0,t38:1.0):1.0)100.0:1.0,(t56:1.0,t7:1.0):1.0):1.0)100.0:1.0,((t89:1.0,(t57:1.0,t48:1.0)100.0:1.0)100.0:1.0,(t34:1.0,t32:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((t42:1.0,(((t19:1.0,(t63:1.0,t14:1.0)100.0:1.0)100.0:1.0,t58:1.0)100.0:1.0,t68:1.0):1.0)100.0:1.0,(t95:1.0,t13:1.0)100.0:1.0,(t96:1.0,(t92:1.0,(t47:1.0,t99:1.0):1.0):1.0):1.0)100.0:1.0,(t51:1.0,(((t80:1.0,(t93:1.0,(t83:1.0,(t64:1.0,t15:1.0):1.0):1.0):1.0):1.0,(((t49:1.0,((t55:1.0,t46:1.0):1.0,(t52:1.0,t91:1.0):1.0):1.0):1.0,(((t94:1.0,(t72:1.0,t54:1.0):1.0):1.0,(t60:1.0,(t97:1.0,t87:1.0):1.0):1.0):1.0,(t5:1.0,t53:1.0):1.0):1.0):1.0,(t70:1.0,(t37:1.0,t28:1.0):1.0):1.0):1.0):1.0,((t44:1.0,((t10:1.0,(t100:1.0,t43:1.0):1.0):1.0,((t2:1.0,t16:1.0):1.0,((t88:1.0,t71:1.0):1.0,(t90:1.0,(t59:1.0,t23:1.0):1.0):1.0):1.0):1.0):1.0):1.0,((t85:1.0,(t18:1.0,t45:1.0):1.0):1.0,((t11:1.0,(t12:1.0,(t78:1.0,(t74:1.0,(t4:1.0,t79:1.0):1.0):1.0):1.0):1.0):1.0,(((t22:1.0,((t61:1.0,t66:1.0):1.0,(t39:1.0,(t25:1.0,t75:1.0):1.0):1.0):1.0):1.0,(t6:1.0,t17:1.0):1.0):1.0,(t98:1.0,t73:1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0)100.0:1.0)100.0;");
        Tree foursixscm = currentconsensus.cloneTree();
        //52ffabb0
        Tree four = Newick.getTreeFromString("(((((t89:0.5315530236521863,(t57:0.32770072944493706,t48:0.22829308320850317)100:0.1507622530717005)100:0.1486584776706161,(t32:0.2761436827722613,t34:0.3668710578257509)100:0.3565277252441377)100:0.44769306969753514,(((t3:0.5052281506094563,t35:0.47267953856766404)100:0.19942145332033637,(t56:0.008780514241044328,t7:0.043278804122540135)100:0.6799290231959424)100:0.40427982718051086,(t21:1.024798405530355,(t8:0.6909234329768025,t62:0.7296474217051097)95:0.12731363029514323)95:0.1326684663900318)52:0.044570963059420016)100:0.4258858376590028,(((t95:0.6643707885873157,t13:0.4861513007872309)100:0.9827896798503074,(t42:0.7805406539368444,(t68:0.6995436071518034,(t58:0.5743510551420986,((t63:0.39681933720308815,t14:0.3536727729602248)98:0.1263143665853067,t19:0.3599108841622481)94:0.11117052231724893)54:0.07876973716531635)98:0.17732023670658212)100:0.3449844824497025)37:0.09873626374536719,(t96:1.319160573233144,((t47:0.32114724737102823,t99:0.24493564270844476)97:0.2301692320730914,t92:0.7536124607387046)100:0.707000062876679)62:0.13054920733582423)65:0.1163942429072977)93:0.06325508056365375,((((t67:0.40481561906937863,(t81:0.3757495533395502,(t31:0.27077841010263753,t29:0.2371776646424748)100:0.11927223776964692)61:0.006907379876005359)100:0.15168231723228345,(t9:0.5297277569298112,t86:0.545744933073195)48:0.01279395940409044)100:0.309467295198082,t36:0.8206874043060975)100:0.3219852542386169,((t50:0.19498278464071978,(t69:0.34479371348167087,t33:0.2857354246199924)48:0.04546767144871023)100:0.8313745958109063,(t30:0.7261407064273012,t27:0.7532356507354232)100:0.5227617685684822)68:0.04838463339820076)93:0.06325508056365375);");
        //1fa15fd1
        Tree six = Newick.getTreeFromString("(((t36:0.5325664818372534,((t67:0.31344020684064716,(t81:0.3024358917336626,(t31:0.18463394110258427,t29:0.20774041881436195)100:0.08209629472842005)85:0.014611072190231023)100:0.1274207053929739,(t86:0.4349580186500413,t9:0.41214508159006685)58:0.015136428452463064)100:0.2299800230235685)100:0.24196513603774664,((t30:0.5723978474426257,(t65:0.45552867326516727,t27:0.43020355145235284)100:0.1078384800031407)100:0.34405603118213623,(t33:0.1913229242758057,(t69:0.25129873887155413,t50:0.18712441669937271)69:0.05504171430857853)100:0.64964566265055)47:0.022780921563732067)92:0.06961300297621024,((((t34:0.27459028718544637,t32:0.21428231884560425)100:0.27444324095323586,(t89:0.39547255246702423,(t48:0.23650786896852355,t57:0.21672339736321805)100:0.14377040797726479)91:0.09221097998329024)100:0.44330860517843507,(((t3:0.22500444267953706,t38:0.19544397411384978)100:0.10168160054058215,t35:0.4094967847773399)100:0.4723800582456769,(t21:0.734814295477482,(t8:0.5902367561140619,t62:0.557567507663251)83:0.061339251516097926)73:0.05394369421631455)56:0.05189660745399966)100:0.30456628053724344,(((t13:0.3383530001081953,t95:0.5343272385599137)100:0.6823065262518979,((t42:0.6788043222165381,(t58:0.4016433076236795,(t19:0.3057060190098972,(t14:0.32198757431627656,t63:0.28189222048912266)100:0.08875892960299894)95:0.08443983367953607)99:0.15723831186973744)100:0.3211026099840878,t96:1.023892196331173)34:0.046687484261140604)58:0.059527642250980506,(t51:1.3585861416724287,(((t80:0.7983061535464947,(((t64:0.178763670820658,t15:0.17455472215274032)95:0.13147381768550248,t83:0.41569393274914485)100:0.35191008367765575,t93:0.9272190061187355)44:0.046400089336083764)95:0.13442804520372412,(((t49:0.35024292554073777,((t55:0.2609838627119284,t46:0.2559999601635894)100:0.15211792868070037,(t52:0.3128191241483137,t91:0.3487802563040483)66:0.06240399924065792)100:0.14842606030404398)100:0.3192978119518481,(((t94:0.1644287353228902,(t72:0.07515331063377649,t54:0.096475916125331)100:0.11516109752366491)100:0.2877708461650511,((t97:0.18118490935233922,t87:0.17502630746542608)95:0.051584883357238626,t60:0.220644663641623)100:0.21273097907733904)100:0.07990476458650426,(t5:0.4775035094481206,t53:0.4823326088376796)88:0.060820848222589506)100:0.18263098997352098)100:0.17330889561217028,((t37:0.022355355314818758,t28:0.033863289260195946)100:0.2402768871993177,t70:0.4797301708659663)100:0.543088992866113)72:0.030699315692615834)100:0.23282489994411132,(((((t100:0.19469357452515915,t43:0.15146665290398112)100:0.6568819853050705,t10:0.657141359492134)100:0.35883731123524076,((t2:0.5800359649818764,t16:0.4869201631133254)100:0.42013211124783495,((t88:0.39734243458664265,t71:0.5182634897168845)100:0.1991410457620966,(t90:0.31622592579352016,(t59:0.23416929332741976,t23:0.25161556428391346)98:0.04931801480215636)100:0.326692402500196)100:0.4282778790646982)45:0.03486741056949864)48:0.0389191185542566,t44:0.9949328401688072)41:0.03329286690789816,(((t18:0.08083929583552989,t45:0.09540880972275845)98:0.12414711737486762,t85:0.27409210197863776)100:0.7042494773394532,((t11:0.9266935534688124,(t12:0.867479528161025,(t78:0.4350796031563459,(t74:0.14991414915055407,(t4:0.09424288598410945,t79:0.09638283678270398)99:0.06133867704259165)100:0.5052146723863783)100:0.21629429125574648)99:0.13868271407110838)74:0.04198062962488459,(((t22:0.43812559924805394,((t61:0.3036164269523805,t66:0.3052690007401717)100:0.179718294795062,(t39:0.11073936487362317,(t25:0.06137284952117864,t75:0.1043776232804302)100:0.06574317932956199)100:0.4086404837803306)34:1.32534435729352E-6)100:0.34217207651832143,(t6:0.6265429446121541,t17:0.7262453952427844)99:0.11428166747255546)84:0.052218304955530835,(t98:0.38163002974054866,t73:0.40488330461391925)100:0.496574648264098)100:0.13505214851414096)56:0.020572408821852343)98:0.12100852411655735)57:0.05343274165295189)47:0.015857423891504165)86:0.08570728821169961)35:1.32534435729352E-6)92:0.06961300297621024);");
        expected = s.getSCM(four, six, false);
        //assertTrue(tr.getTreeEquals(four, Newick.getTreeFromString("((((((t45:1.0,t98:1.0):1.0,(t15:1.0,(t87:1.0,t100:1.0):1.0):1.0):1.0,(t18:1.0,(t22:1.0,t95:1.0):1.0):1.0):1.0,(((t69:1.0,t92:1.0):1.0,(t80:1.0,(t41:1.0,(t71:1.0,t79:1.0):1.0):1.0):1.0):1.0,(t19:1.0,t72:1.0):1.0):1.0)100.0:1.0,(t2:1.0,((t9:1.0,(t30:1.0,t76:1.0):1.0)100.0:1.0,((t14:1.0,t65:1.0):1.0,(t25:1.0,t28:1.0):1.0):1.0):1.0):1.0)100.0:1.0,((t27:1.0,(t5:1.0,t60:1.0):1.0):1.0,(((((t38:1.0,(t8:1.0,t12:1.0):1.0)100.0:1.0,(t94:1.0,(t10:1.0,t85:1.0):1.0)100.0:1.0)100.0:1.0,(t74:1.0,(t56:1.0,(t78:1.0,t16:1.0):1.0):1.0):1.0):1.0,(t90:1.0,(((t29:1.0,t13:1.0)100.0:1.0,t36:1.0):1.0,(t44:1.0,t64:1.0):1.0):1.0):1.0)100.0:1.0,(t49:1.0,(t11:1.0,t83:1.0):1.0):1.0):1.0):1.0)100.0;")));
        assertTrue(tr.getTreeEquals(expected, foursixscm));
        System.out.println("four and six "+Newick.getStringFromTree(expected));
        sourcetrees = new Tree[]{four, six};
        print = FN_FP_RateComputer.calculateSumOfRates(expected, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        System.out.println();

        //7d3e0d35
        currentconsensus = Newick.getTreeFromString("(((((t2:1.0,t16:1.0)100.0:1.0,((t10:1.0,(t100:1.0,t43:1.0)100.0:1.0)100.0:1.0,(t41:1.0,(t76:1.0,t82:1.0):1.0):1.0):1.0,(((t71:1.0,t88:1.0)100.0:1.0,(t90:1.0,(t59:1.0,t23:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t77:1.0):1.0,(t44:1.0,t24:1.0):1.0)100.0:1.0,(((t45:1.0,t18:1.0)100.0:1.0,t85:1.0)100.0:1.0,((t73:1.0,t98:1.0)100.0:1.0,((((t75:1.0,t25:1.0)100.0:1.0,t39:1.0)100.0:1.0,(t66:1.0,t61:1.0)100.0:1.0,t22:1.0)100.0:1.0,(t17:1.0,t6:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t11:1.0,((t78:1.0,(t74:1.0,(t79:1.0,t4:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t12:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((((t52:1.0,(t46:1.0,t55:1.0)100.0:1.0,t91:1.0)100.0:1.0,t49:1.0)100.0:1.0,((((t54:1.0,t72:1.0)100.0:1.0,t94:1.0)100.0:1.0,((t97:1.0,t87:1.0)100.0:1.0,t60:1.0)100.0:1.0)100.0:1.0,(t5:1.0,(t53:1.0,t84:1.0):1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t26:1.0,t20:1.0):1.0,(t1:1.0,((t70:1.0,(t28:1.0,t37:1.0)100.0:1.0)100.0:1.0,t40:1.0):1.0):1.0):1.0)100.0:1.0,(t80:1.0,(t93:1.0,((t64:1.0,t15:1.0)100.0:1.0,t83:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t51:1.0)100.0:1.0,(((t21:1.0,(t8:1.0,t62:1.0):1.0):1.0,((t35:1.0,(t3:1.0,t38:1.0):1.0):1.0,(t56:1.0,t7:1.0):1.0):1.0):1.0,((t89:1.0,(t57:1.0,t48:1.0):1.0):1.0,(t34:1.0,t32:1.0):1.0):1.0):1.0,((t42:1.0,(t68:1.0,(t58:1.0,(t19:1.0,(t63:1.0,t14:1.0):1.0):1.0):1.0):1.0):1.0,(t95:1.0,t13:1.0):1.0,(t96:1.0,(t92:1.0,(t47:1.0,t99:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(((t69:1.0,t33:1.0,t50:1.0):1.0,(t30:1.0,(t65:1.0,t27:1.0):1.0):1.0)100.0:1.0,(t36:1.0,((t9:1.0,t86:1.0):1.0,(t67:1.0,(t81:1.0,(t31:1.0,t29:1.0):1.0):1.0):1.0):1.0):1.0)100.0:1.0)100.0;");
        Tree onetwothreefourfivesixscm = currentconsensus.cloneTree();
        //3b6e4aee aus one, two, three, five
        //a5ebd6b aus four, six
        expected = s.getSCM(onetwothreefivescm, foursixscm, false);
        assertTrue(tr.getTreeEquals(expected, onetwothreefourfivesixscm));
        System.out.println("supertree " + Newick.getStringFromTree(expected));
        sourcetrees = new Tree[]{one, two, three, four, five, six};
        print = FN_FP_RateComputer.calculateSumOfRates(expected, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();

    }
    */

    @Test
    public void strangeTest(){
        Tree eins = Newick.getTreeFromString("((((((beta:1.0,gamma:1.0):1.0,(a:1.0,b:1.0):1.0):1.0,alpha:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(al:1.0,(e:1.0,f:1.0):1.0):1.0):1.0,(x:1.0,y:1.0):1.0);");
        Tree zwei = Newick.getTreeFromString("((((pi:1.0,((a:1.0,b:1.0):1.0,bl:1.0):1.0):1.0,(e:1.0,f:1.0):1.0,(delta:1.0,epsilon:1.0):1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(x:1.0,y:1.0):1.0);");
        SCM s = new SCM();
        Tree result = s.getSCM(eins, zwei, true);
        System.out.println(Newick.getStringFromTree(result));
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{eins, zwei});
        for (double a : print){
            System.out.print(a+" ");
        }
        Tree correct = Newick.getTreeFromString("(((delta:1.0,epsilon:1.0):1.0,((a:1.0,b:1.0):1.0,bl:1.0,pi:1.0,alpha:1.0,(beta:1.0,gamma:1.0):1.0):1.0,(c:1.0,d:1.0):1.0,(al:1.0,(e:1.0,f:1.0):1.0):1.0):1.0,(x:1.0,y:1.0):1.0);");
        print = FN_FP_RateComputer.calculateSumOfRates(correct, new Tree[]{eins, zwei});
        for (double a : print){
            System.out.print(a+" ");
        }

    }

    /**@Test
    public void strangeTest(){
        Tree eins = Newick.getTreeFromString("(((((raus:1.0,((t1:1.0,t2:1.0):1.0,(a:1.0,(b:1.0,c:1.0):1.0,(g:1.0,h:1.0):1.0):1.0):1.0):1.0,(bli:1.0,blu:1.0):1.0):1.0,bla:1.0):1.0,d:1.0):1.0,x:1.0,((z:1.0,q:1.0):1.0,alpha:1.0):1.0,y:1.0);");
        Tree zwei = Newick.getTreeFromString("((((a:1.0,raus:1.0):1.0,(b:1.0,c:1.0):1.0,(g:1.0,h:1.0):1.0):1.0,(bli:1.0,blu:1.0):1.0):1.0,bla:1.0);");
        SCM s = new SCM();
        Tree result = s.getSCM(eins, zwei, true);
        System.out.println(Newick.getStringFromTree(result));
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{eins, zwei});
        for (double a : print){
            System.out.print(a+" ");
        }

    }*/

    /*
    @Test
    public void testOneExample(){
        System.out.println("Schrittweises testen");

        //bc2dbc2 aus one und two
        Tree currentconsensus = Newick.getTreeFromString("((t62:1.0,(t68:1.0,t34:1.0)100.0:1.0)100.0:1.0,(((((((((t86:1.0,t63:1.0)100.0:1.0,t55:1.0,(t31:1.0,t39:1.0)100.0:1.0)100.0:1.0,(t70:1.0,t46:1.0)100.0:1.0)100.0:1.0,((t58:1.0,(t77:1.0,t93:1.0)100.0:1.0)100.0:1.0,(((t54:1.0,t81:1.0)100.0:1.0,t67:1.0)100.0:1.0,t96:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t1:1.0,(t23:1.0,((t20:1.0,(t21:1.0,(t40:1.0,t53:1.0):1.0):1.0):1.0,(t35:1.0,(t52:1.0,t97:1.0):1.0):1.0):1.0):1.0):1.0)100.0:1.0,(((t88:1.0,t50:1.0)100.0:1.0,(t4:1.0,(t51:1.0,t3:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t75:1.0)100.0:1.0)100.0:1.0,(t48:1.0,(t84:1.0,(t26:1.0,t89:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t42:1.0,t61:1.0)100.0:1.0,t33:1.0)100.0:1.0)100.0:1.0,t43:1.0,t99:1.0)100.0:1.0)100.0;");
        //3138f11c
        Tree one = Newick.getTreeFromString("((t62:0.05628250463875502,(t34:0.05111798108341597,t68:0.0716674430425947)91:0.03562318675195494)100:0.11946302714192304,(t99:0.32367789283235776,(t43:0.24120969228066408,((t33:0.13703080769644582,(t42:0.016500538466177307,t61:0.03198788517723229)100:0.09960080614860357)100:0.24050616342588713,((t48:0.45825113619486063,((t89:0.16946402676769487,t26:0.2606737446325993)100:0.13840929582632097,t84:0.3461038048063489)65:0.011821289288280176)97:0.06993496038868594,((t75:0.6498124772437798,((t88:0.09986157185415986,t50:0.24056783411026791)100:0.32167820580761847,((t51:0.11960099978075825,t3:0.1645798868453852)100:0.4033935454667507,t4:0.3341548597374618)69:0.014588072151431302)100:0.06533495419797568)68:0.0036192201495379183,(t1:0.6033763550381124,(((t55:0.2881965790473344,((t31:0.1842643593526385,t39:0.1600728165960466)99:0.08777042900377541,(t63:0.03285907260806823,t86:0.05035361271758831)100:0.3466760855848438)53:0.012222016783155298)34:0.004597326586528972,(t70:0.04084188420720513,t46:0.04961544416244005)100:0.3922778741243722)100:0.15867573477029537,((t96:0.22687572806944079,(t67:0.09566136462660722,(t81:0.10458037198897728,t54:0.07268456658530288)96:0.03002915949970032)100:0.16524801714341134)100:0.11956035503696104,(t58:0.2803219742112587,(t77:0.06062724459814099,t93:0.113925307644606)100:0.1880421623875021)100:0.1413567364762721)100:0.15380367869623038)49:0.010986782795851472)100:0.07611066084477712)98:0.06224520875263833)87:0.03568648282723465)100:0.19353900162851118)65:0.0034374236272585947)100:0.11946302714192304);");
        //26044b0b
        Tree two = Newick.getTreeFromString("((t62:0.07218643030232133,(t68:0.07782510097622128,t34:0.06673459384829025)87:0.05295113557288415)100:0.22640397563058093,(t43:0.41842910727649474,(t99:0.4911282402225815,((t33:0.18754687802419864,(t42:0.02626109489576459,t61:0.05487269847465561)100:0.15986342511823437)100:0.4410066386020258,(((t84:0.6930865232531073,(t89:0.3454062398535116,t26:0.3574830498705032)100:0.2488901867985317)88:0.06291824347047523,t48:0.8043221173371342)97:0.09214609807440587,((((t50:0.29595785655690837,t88:0.29873700204697523)100:0.4409229985734275,(t4:0.7230495305661646,(t51:0.2614451229064478,t3:0.29338406317099713)100:0.6054604019142781)39:0.028023950645954962)99:0.11154314989867516,t75:1.0687950993803066)61:0.009390707286276551,((((t46:0.08442616445101432,t70:0.05448961495419389)100:0.5952753223730758,((t63:0.05965148110398947,t86:0.09174967209641148)100:0.5780772474284204,(t55:0.6256029012117871,(t31:0.38974360534121183,t39:0.2919799967356352)100:0.23563634405800385)9:1.25664209606445E-6)53:0.05321391091205385)100:0.315724922446319,(((t77:0.15294301041938202,t93:0.15899419539696646)100:0.23593877190050547,t58:0.4511626849772174)100:0.15214127240100328,(t96:0.583485943225274,(t67:0.1543759302856235,(t54:0.1212718316528532,t81:0.12027600691303723)98:0.041981114433873874)100:0.25904965360589677)100:0.28278783277224523)100:0.18941315163368286)96:0.08482213133078291,(t1:0.9840907658623657,(t23:1.193083823768093,((t20:0.35075958723547807,((t40:0.05894793211225352,t53:0.054288114314897246)100:0.015186729165987675,t21:0.10645118637230394)100:0.2428401072639307)100:0.3204700626633429,((t52:0.2585690365494116,t97:0.22490446974929318)100:0.2252236031196223,t35:0.49255581695033734)100:0.3076401480134928)100:0.25708081604211147)100:0.21912267693528295)90:0.0937848796193324)88:0.045637077621154834)100:0.17526873528354614)98:0.07930819682004188)100:0.26064103180167175)49:0.006595972114576445)100:0.22640397563058093);");
        Tree[] sourcetrees = new Tree[]{one, two};
        double[] print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        //50f52ce5 aus
        currentconsensus = Newick.getTreeFromString("((t43:1.0,(((((t89:1.0,t26:1.0)100.0:1.0,t84:1.0)100.0:1.0,t48:1.0)100.0:1.0,((((((t31:1.0,t39:1.0)100.0:1.0,t55:1.0,(t63:1.0,t86:1.0)100.0:1.0)100.0:1.0,(t46:1.0,t70:1.0)100.0:1.0)100.0:1.0,((((t54:1.0,t81:1.0)100.0:1.0,t67:1.0)100.0:1.0,t96:1.0)100.0:1.0,(t58:1.0,(t93:1.0,t77:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t1:1.0,(t23:1.0,((t20:1.0,(t21:1.0,(t40:1.0,t53:1.0):1.0):1.0):1.0,(t35:1.0,(t52:1.0,t97:1.0):1.0):1.0):1.0):1.0):1.0)100.0:1.0,(((t50:1.0,t88:1.0)100.0:1.0,((t51:1.0,t3:1.0)100.0:1.0,t4:1.0)100.0:1.0)100.0:1.0,t75:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t33:1.0,(t61:1.0,t42:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t99:1.0)100.0:1.0,((t34:1.0,t68:1.0)100.0:1.0,t62:1.0)100.0:1.0)100.0;");
        //7b788771
        Tree three = Newick.getTreeFromString("((t62:0.07218643030232133,(t68:0.07782510097622128,t34:0.06673459384829025)87:0.05295113557288415)100:0.22640397563058093,(t43:0.41842910727649474,(t99:0.4911282402225815,((t33:0.18754687802419864,(t42:0.02626109489576459,t61:0.05487269847465561)100:0.15986342511823437)100:0.4410066386020258,(((t84:0.6930865232531073,(t89:0.3454062398535116,t26:0.3574830498705032)100:0.2488901867985317)88:0.06291824347047523,t48:0.8043221173371342)97:0.09214609807440587,((((t50:0.29595785655690837,t88:0.29873700204697523)100:0.4409229985734275,(t4:0.7230495305661646,(t51:0.2614451229064478,t3:0.29338406317099713)100:0.6054604019142781)39:0.028023950645954962)99:0.11154314989867516,t75:1.0687950993803066)61:0.009390707286276551,((((t46:0.08442616445101432,t70:0.05448961495419389)100:0.5952753223730758,((t63:0.05965148110398947,t86:0.09174967209641148)100:0.5780772474284204,(t55:0.6256029012117871,(t31:0.38974360534121183,t39:0.2919799967356352)100:0.23563634405800385)9:1.25664209606445E-6)53:0.05321391091205385)100:0.315724922446319,(((t77:0.15294301041938202,t93:0.15899419539696646)100:0.23593877190050547,t58:0.4511626849772174)100:0.15214127240100328,(t96:0.583485943225274,(t67:0.1543759302856235,(t54:0.1212718316528532,t81:0.12027600691303723)98:0.041981114433873874)100:0.25904965360589677)100:0.28278783277224523)100:0.18941315163368286)96:0.08482213133078291,(t1:0.9840907658623657,(t23:1.193083823768093,((t20:0.35075958723547807,((t40:0.05894793211225352,t53:0.054288114314897246)100:0.015186729165987675,t21:0.10645118637230394)100:0.2428401072639307)100:0.3204700626633429,((t52:0.2585690365494116,t97:0.22490446974929318)100:0.2252236031196223,t35:0.49255581695033734)100:0.3076401480134928)100:0.25708081604211147)100:0.21912267693528295)90:0.0937848796193324)88:0.045637077621154834)100:0.17526873528354614)98:0.07930819682004188)100:0.26064103180167175)49:0.006595972114576445)100:0.22640397563058093);");
        //bc2dbc2 aus one und two
        sourcetrees = new Tree[]{one, two, three};
        print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        //55bce763 aus
        currentconsensus = Newick.getTreeFromString("((t43:1.0,t99:1.0,(((((((t55:1.0,(t31:1.0,t39:1.0):1.0,(t63:1.0,t86:1.0):1.0)100.0:1.0,(t46:1.0,t70:1.0):1.0):1.0,((t96:1.0,(t67:1.0,(t54:1.0,t81:1.0):1.0):1.0):1.0,(t58:1.0,(t93:1.0,t77:1.0):1.0):1.0):1.0)100.0:1.0,((((t21:1.0,(t40:1.0,t53:1.0):1.0)100.0:1.0,t20:1.0):1.0,(t35:1.0,(t52:1.0,t97:1.0):1.0):1.0)100.0:1.0,t23:1.0,t1:1.0,((((t9:1.0,t30:1.0):1.0,(t15:1.0,t41:1.0):1.0):1.0,(((t94:1.0,t10:1.0):1.0,(t38:1.0,t8:1.0):1.0):1.0,(t13:1.0,t29:1.0):1.0):1.0):1.0,(t73:1.0,(t17:1.0,t47:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t75:1.0,((t50:1.0,t88:1.0):1.0,(t4:1.0,(t3:1.0,t51:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t48:1.0,(t84:1.0,(t89:1.0,t26:1.0):1.0):1.0):1.0)100.0:1.0,(t33:1.0,(t61:1.0,t42:1.0):1.0):1.0):1.0)100.0:1.0,(t62:1.0,(t34:1.0,t68:1.0):1.0):1.0)100.0;");
        Tree testtest1 = currentconsensus.cloneTree();
        //77ea34ed
        Tree four = Newick.getTreeFromString("(t34:0.018934145250925957,(t43:0.03996642497870032,(t89:0.07546188687573818,(t3:0.05927568472366845,((t58:0.08956160316526188,(t63:0.04982171022061381,t55:0.0704200319089565)100:0.02839564179285603)82:0.005045375823426673,(((t21:0.013803959205598692,t40:0.007892130973071112)100:0.05446414902031913,t52:0.07509849224681271)100:0.042420055653473177,((((t9:0.04915477611936559,t30:0.06477295780441689)100:0.026606409134252544,(t15:0.07584965627458341,t41:0.06039105995710634)96:0.005824913777451459)100:0.012612487944621963,(((t94:0.031089289465767948,t10:0.01936729122363375)100:0.025017580765895293,(t38:0.01586900562550333,t8:0.02391078605549252)100:0.0272841975502718)100:0.022568161382416612,(t13:0.025840087806843916,t29:0.015280309034214548)100:0.05432857056949165)100:0.035491986732985446)99:0.007687215749054372,((t17:0.07811106349158181,t47:0.07341900036938541)100:0.03267849003615637,t73:0.1215794290009556)98:0.018544825669163427)99:0.00910094850379571)98:0.00586177098066847)98:0.009766266477993365)100:0.021636413795245547)100:0.020018789033884956)100:0.018934145250925957);");
        //50f52ce5 aus one, two und three
        sourcetrees = new Tree[]{one, two, three, four};
        print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        //4489c86 aus
        currentconsensus = Newick.getTreeFromString("((t62:1.0,(t68:1.0,t34:1.0):1.0):1.0,(t43:1.0,(t99:1.0,((t33:1.0,(t61:1.0,t42:1.0):1.0):1.0,((t48:1.0,(t84:1.0,(t26:1.0,t89:1.0):1.0):1.0):1.0,((t75:1.0,((t88:1.0,t50:1.0):1.0,(t4:1.0,(t3:1.0,t51:1.0):1.0):1.0):1.0):1.0,((((t55:1.0,(t70:1.0,t46:1.0):1.0):1.0,(t31:1.0,(t63:1.0,t86:1.0):1.0):1.0):1.0,((t58:1.0,(t93:1.0,t77:1.0):1.0):1.0,(t96:1.0,(t67:1.0,(t81:1.0,t54:1.0):1.0):1.0):1.0):1.0):1.0,(t1:1.0,(((((t27:1.0,(t60:1.0,t5:1.0)100.0:1.0)100.0:1.0,(((t83:1.0,t11:1.0)100.0:1.0,t49:1.0)100.0:1.0,(((((t12:1.0,t8:1.0)100.0:1.0,t38:1.0)100.0:1.0,(t94:1.0,(t10:1.0,t85:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t56:1.0,(t78:1.0,t16:1.0)100.0:1.0)100.0:1.0,t74:1.0)100.0:1.0)100.0:1.0,(t90:1.0,(t44:1.0,t64:1.0)100.0:1.0,(t36:1.0,(t13:1.0,t29:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((((t22:1.0,t95:1.0)100.0:1.0,t18:1.0)100.0:1.0,((t45:1.0,t98:1.0)100.0:1.0,(t15:1.0,t87:1.0,t100:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((t71:1.0,t79:1.0,t41:1.0)100.0:1.0,(t92:1.0,t69:1.0)100.0:1.0,t80:1.0)100.0:1.0,(t19:1.0,t72:1.0):1.0)100.0:1.0)100.0:1.0,(((t65:1.0,t14:1.0)100.0:1.0,t25:1.0,t28:1.0)100.0:1.0,(t9:1.0,(t76:1.0,t30:1.0)100.0:1.0)100.0:1.0,t2:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t91:1.0,t59:1.0):1.0):1.0,(t23:1.0,((t20:1.0,(t21:1.0,(t40:1.0,t53:1.0):1.0):1.0):1.0,(t35:1.0,(t97:1.0,t52:1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0);");
        Tree testtest2 = currentconsensus.cloneTree();
        //115d7e83
        Tree five = Newick.getTreeFromString("((t62:0.09103697715407845,(t68:0.09959821681576153,t34:0.09925103801127162)86:0.05425724683413221)100:0.16739527154799325,(t43:0.5077885363684264,(t99:0.545282675508921,((t33:0.18716663756729066,(t61:0.05288976856160155,t42:0.03257686309664143)100:0.20197142290299339)100:0.5085517561544944,((t48:0.943190280685845,((t26:0.431031350257592,t89:0.33795126731016417)100:0.2969070473087375,t84:0.7987978198339839)50:0.028258085368993054)100:0.1591409941365176,((t75:1.1481837449077916,((t88:0.2513292820314653,t50:0.3509636472012964)100:0.5572391943958191,((t3:0.3369455529160748,t51:0.2469733195863473)100:0.64775449097888,t4:0.5850859623530605)59:0.04894326627027762)100:0.12191928031361114)75:0.014079027979741024,(((((t70:0.09818956961383497,t46:0.06736859605351467)100:0.7493121294860043,t55:0.5522687678957958)6:1.12061695646573E-6,((t63:0.06017746315713887,t86:0.10236290697180164)100:0.6003868175393867,t31:0.5348431470690785)33:0.012003726074204432)100:0.2958235091082058,((t58:0.5159196391271332,(t93:0.2233730337192326,t77:0.11169759248145997)100:0.2840819924762049)98:0.22322788657930648,(t96:0.5232424132289919,(t67:0.16941702266260703,(t81:0.17395091447654065,t54:0.12617365202919562)98:0.03625667882421481)100:0.3419661637997401)100:0.26130871118607046)100:0.2545649818726372)89:0.10579364697552986,(t1:1.0046800016011792,(((((t27:0.21114874483020352,(t5:0.16279304101692466,t60:0.14590648765519307)93:0.0668912969715336)100:1.0508319868739797,(((t11:0.46543097492857155,t83:0.3049054437256695)100:0.33308075701516177,t49:0.9203417773983292)100:0.3126659131515816,(((t90:0.6827860608118542,(t44:0.2576921659483704,t64:0.43061420623929764)100:0.3120905927898484)58:0.032391095582469595,(t36:0.5644398913480734,(t29:0.17004644022601614,t13:0.21553968176957722)100:0.259376733028069)100:0.1794863217776043)100:0.17036834228868064,((t74:0.6368094166389684,(t56:0.6054434923453186,(t16:0.16860485912780046,t78:0.18933907047204337)100:0.44083784194415254)80:0.0665933451923901)99:0.13156821715085149,((t38:0.21086294403664887,(t8:0.15204203379992778,t12:0.12933461378602096)100:0.1191012257598117)100:0.30189325975552717,(t94:0.361114732801464,(t85:0.06520597404652942,t10:0.05989442221774066)100:0.25665844747529326)100:0.24864113298649543)100:0.22633458941269813)59:0.02704536084585843)100:0.1663022164253665)96:0.08542455270333736)30:1.12061695646573E-6,(((t25:0.5956885231090127,((t65:0.13341880486729632,t14:0.059903035008404916)100:0.5841001327207158,t28:0.6104236838356127)76:0.025152785563483368)100:0.26248251891498936,((t9:0.7671366229333798,(t76:0.08011704632642777,t30:0.02123339541453624)100:0.5910702624995121)100:0.21559029575885164,t2:0.9465601824628757)66:0.03835680295440637)94:0.13625427235854456,(((((t15:0.03942585999995477,t87:0.02058269155318723)100:0.08244248117366151,t100:0.09442296752453658)99:0.08462698019210217,(t98:0.09784527588357311,t45:0.08332420963442372)100:0.060465550668553004)96:0.050222017718379405,(t18:0.04750574573016345,(t22:0.0354580042586219,t95:0.0272222294888026)89:0.02497251292058706)99:0.18966835583213207)100:0.5384798167830651,(t19:0.8714509230889017,((t71:0.31729901836761154,(t79:0.30510815250576523,t41:0.3047870067832332)100:0.09050525635510488)100:0.1414506650354395,(t80:0.5374000755794646,(t92:1.12061695646573E-6,t69:0.0016131470920215869)100:0.3437214719492534)100:0.1191893247572191)100:0.1405207985493839)94:0.09570940665634624)92:0.11598307366519617)98:0.17048777742776414)63:0.07635955338131138,(t91:0.15833864984668672,t59:0.32010683842554966)100:1.1529546157068962)55:0.04203880917514869,(t23:1.2883927259744008,((t20:0.40039557021632266,(t21:0.10426201656656456,(t40:0.07833446011398346,t53:0.056588634120720085)92:0.0076559291256465345)99:0.2043472499679753)100:0.2872919553095978,(t35:0.5440185483715233,(t97:0.25134198917754985,t52:0.290177717171534)100:0.15008775573471497)100:0.3542948134715942)100:0.35798839778776753)30:0.04995741570237979)86:0.06128821280752579)76:0.07341466199687799)97:0.06714030240233142)98:0.1199168359416151)96:0.05709961285273232)100:0.3144312953494405)68:0.012894796801638908)100:0.16739527154799325);");
        //111cd35d
        Tree six = Newick.getTreeFromString("(((t2:0.0995352517617152,(((t30:0.0038813156557082115,t76:0.0014024724469630094)100:0.06511204585405903,t9:0.04097035644687546)97:0.018637016266213245,((t14:0.0019448263533454959,t65:0.009507126583884157)100:0.06160151915364663,(t25:0.029154203570306816,t28:0.07287363402124374)55:0.007363544706708868)100:0.02542236752496739)73:0.00113351706375263)100:0.014363163309068025,((((t45:0.011657291772081994,t98:0.010341485360165896)100:0.008696960675233982,(t15:0.0020759465310610007,(t87:0.0017920011199377694,t100:0.014332831508075333)84:0.0013718902661350283)97:0.007115174082773052)100:0.012403207276170978,(t18:0.0026226233041781663,(t22:8.61837433740174E-4,t95:1.21329263756371E-6)97:0.0017183118540303952)100:0.01792521192440408)100:0.06553696991662582,(((t69:1.21329263756371E-6,t92:8.67333001950601E-4)100:0.041495513428462284,(t80:0.03723616298592725,(t41:0.023717055956667513,(t71:0.036503409827761846,t79:0.03231080520217064)79:0.0039414815727589515)100:0.01660758777567447)73:0.0055495797663848885)100:0.01769784313799771,(t19:0.04601302619851822,t72:0.04042946231824277)100:0.028283751786159973)100:0.015819986293087524)67:0.0024435476648801673)83:0.004515241492329409,((t27:0.00758176305316483,(t5:0.015235305712014363,t60:0.017266227469113514)99:0.010971639219377253)100:0.08730431500673771,(((t11:0.041867712211330226,t83:0.0310008438602387)100:0.0494784566860051,t49:0.06857608149744639)100:0.032236560390060905,(((((t12:0.007430736408768021,t8:0.01705824124144441)100:0.012063163797255519,t38:0.017961446682111394)100:0.027581588039901777,(t94:0.04012839444828953,(t10:0.002089458413880142,t85:0.00409383803888706)98:0.015631772815007116)100:0.02441009331050475)100:0.032835850563642625,((t56:0.051695070076818915,(t78:0.012373195264016363,t16:0.013798657970854424)100:0.023776805666412392)95:0.008950353314307413,t74:0.06780016794548739)100:0.014020097055333063)84:0.0030769335292675963,(((t36:0.05753531844866584,(t13:0.02079642002189545,t29:0.020147395722518828)100:0.028424602193548757)68:0.007780319666429366,(t44:0.023923584411058237,t64:0.05654042251288139)100:0.029409539091137568)80:0.010246093004145545,t90:0.052417910074606534)100:0.012073142678727179)100:0.03236310870196033)62:0.010087698480361139)83:0.004515241492329409);");
        sourcetrees = new Tree[]{five, six};
        print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        //3f72aff0 aus
        currentconsensus = Newick.getTreeFromString("((t99:1.0,t43:1.0,(((((t26:1.0,t89:1.0)100.0:1.0,t84:1.0)100.0:1.0,t48:1.0)100.0:1.0,((((t4:1.0,(t51:1.0,t3:1.0)100.0:1.0)100.0:1.0,(t88:1.0,t50:1.0)100.0:1.0)100.0:1.0,t75:1.0)100.0:1.0,((((t70:1.0,t46:1.0)100.0:1.0,(t86:1.0,t63:1.0)100.0:1.0,t55:1.0,(t31:1.0,t39:1.0):1.0)100.0:1.0,((((t54:1.0,t81:1.0)100.0:1.0,t67:1.0)100.0:1.0,t96:1.0)100.0:1.0,(t58:1.0,(t93:1.0,t77:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t23:1.0,((t35:1.0,(t97:1.0,t52:1.0)100.0:1.0)100.0:1.0,(((t40:1.0,t53:1.0)100.0:1.0,t21:1.0)100.0:1.0,t20:1.0)100.0:1.0)100.0:1.0,t1:1.0,((((((t18:1.0,(t22:1.0,t95:1.0):1.0):1.0,((t45:1.0,t98:1.0):1.0,(t15:1.0,t87:1.0,t100:1.0):1.0):1.0):1.0,((t80:1.0,(t41:1.0,t71:1.0,t79:1.0):1.0,(t92:1.0,t69:1.0):1.0):1.0,(t19:1.0,t72:1.0):1.0):1.0)100.0:1.0,(t9:1.0,t2:1.0,(t25:1.0,t28:1.0,(t65:1.0,t14:1.0):1.0):1.0,(t30:1.0,t76:1.0):1.0)100.0:1.0)100.0:1.0,((t27:1.0,(t60:1.0,t5:1.0):1.0):1.0,(((((t38:1.0,(t8:1.0,t12:1.0):1.0)100.0:1.0,(t94:1.0,(t10:1.0,t85:1.0):1.0)100.0:1.0)100.0:1.0,(t74:1.0,(t56:1.0,(t78:1.0,t16:1.0):1.0):1.0):1.0):1.0,((t29:1.0,t13:1.0,t90:1.0,(t44:1.0,t64:1.0):1.0)100.0:1.0,t36:1.0):1.0)100.0:1.0,(t49:1.0,(t83:1.0,t11:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t73:1.0,(t17:1.0,t47:1.0):1.0):1.0,(t91:1.0,t59:1.0):1.0):1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t33:1.0,(t42:1.0,t61:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t34:1.0,t68:1.0)100.0:1.0,t62:1.0)100.0:1.0)100.0;");
        //55bce763 aus one, two, three, four
        //4489c86 aus five, six
        sourcetrees = new Tree[]{one, two, three, four, five, six};
        print = FN_FP_RateComputer.calculateSumOfRates(currentconsensus, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        System.out.println("Das ist der Supertree");
        Tree supertree = Newick.getTreeFromString("((t62:1.0,(t34:1.0,t68:1.0)100.0:1.0)100.0:1.0,(t43:1.0,((((t84:1.0,(t26:1.0,t89:1.0)100.0:1.0)100.0:1.0,t48:1.0)100.0:1.0,((t75:1.0,((t88:1.0,t50:1.0)100.0:1.0,(t4:1.0,(t51:1.0,t3:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t1:1.0,(((t97:1.0,t52:1.0)100.0:1.0,t35:1.0)100.0:1.0,(((t53:1.0,t40:1.0)100.0:1.0,t21:1.0)100.0:1.0,t20:1.0)100.0:1.0)100.0:1.0,t23:1.0,((((((t80:1.0,(t92:1.0,t69:1.0):1.0,(t41:1.0,t71:1.0,t79:1.0):1.0):1.0,(t19:1.0,t72:1.0):1.0):1.0,((t18:1.0,(t22:1.0,t95:1.0):1.0):1.0,((t45:1.0,t98:1.0):1.0,(t15:1.0,t87:1.0,t100:1.0):1.0):1.0):1.0)100.0:1.0,(t9:1.0,t2:1.0,(t28:1.0,t25:1.0,(t65:1.0,t14:1.0):1.0):1.0,(t30:1.0,t76:1.0):1.0)100.0:1.0)100.0:1.0,((t27:1.0,(t60:1.0,t5:1.0):1.0):1.0,(((((t94:1.0,(t10:1.0,t85:1.0):1.0)100.0:1.0,(t38:1.0,(t8:1.0,t12:1.0):1.0)100.0:1.0)100.0:1.0,(t74:1.0,(t56:1.0,(t78:1.0,t16:1.0):1.0):1.0):1.0):1.0,((t13:1.0,t29:1.0,t90:1.0,(t64:1.0,t44:1.0):1.0)100.0:1.0,t36:1.0):1.0)100.0:1.0,(t49:1.0,(t11:1.0,t83:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t73:1.0,(t17:1.0,t47:1.0):1.0):1.0,(t91:1.0,t59:1.0):1.0):1.0)100.0:1.0,((t55:1.0,(t86:1.0,t63:1.0)100.0:1.0,(t46:1.0,t70:1.0)100.0:1.0,(t31:1.0,t39:1.0):1.0)100.0:1.0,((t58:1.0,(t93:1.0,t77:1.0)100.0:1.0)100.0:1.0,(t96:1.0,(t67:1.0,(t54:1.0,t81:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t42:1.0,t61:1.0)100.0:1.0,t33:1.0)100.0:1.0)100.0:1.0,t99:1.0)100.0:1.0)100.0;");
        TreeEquals te = new TreeEquals();
        //Tree maybesupertree = Newick.getTreeFromString("((t99:1.0,(((((t26:1.0,t89:1.0)100.0:1.0,t84:1.0)100.0:1.0,t48:1.0)100.0:1.0,(((((((t54:1.0,t81:1.0)100.0:1.0,t67:1.0)100.0:1.0,t96:1.0)100.0:1.0,((t77:1.0,t93:1.0)100.0:1.0,t58:1.0)100.0:1.0)100.0:1.0,((t63:1.0,t86:1.0)100.0:1.0,(t70:1.0,t46:1.0)100.0:1.0,t55:1.0,(t31:1.0,t39:1.0):1.0)100.0:1.0)100.0:1.0,(t1:1.0,((t35:1.0,(t97:1.0,t52:1.0)100.0:1.0)100.0:1.0,(t20:1.0,((t40:1.0,t53:1.0)100.0:1.0,t21:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t23:1.0,((((((t18:1.0,(t22:1.0,t95:1.0):1.0):1.0,((t15:1.0,t87:1.0,t100:1.0):1.0,(t45:1.0,t98:1.0):1.0):1.0):1.0,((t80:1.0,(t41:1.0,t71:1.0,t79:1.0):1.0,(t92:1.0,t69:1.0):1.0):1.0,(t19:1.0,t72:1.0):1.0):1.0)100.0:1.0,(t9:1.0,t2:1.0,(t30:1.0,t76:1.0):1.0,(t25:1.0,t28:1.0,(t65:1.0,t14:1.0):1.0):1.0)100.0:1.0)100.0:1.0,((((((t38:1.0,(t8:1.0,t12:1.0):1.0)100.0:1.0,(t94:1.0,(t10:1.0,t85:1.0):1.0)100.0:1.0)100.0:1.0,(t74:1.0,(t56:1.0,(t78:1.0,t16:1.0):1.0):1.0):1.0):1.0,((t13:1.0,t29:1.0,t90:1.0,(t44:1.0,t64:1.0):1.0)100.0:1.0,t36:1.0):1.0)100.0:1.0,(t49:1.0,(t11:1.0,t83:1.0):1.0):1.0):1.0,(t27:1.0,(t5:1.0,t60:1.0):1.0):1.0):1.0)100.0:1.0,(t73:1.0,(t17:1.0,t47:1.0):1.0):1.0,(t91:1.0,t59:1.0):1.0):1.0)100.0:1.0)100.0:1.0,(t75:1.0,((t50:1.0,t88:1.0)100.0:1.0,(t4:1.0,(t3:1.0,t51:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t33:1.0,(t61:1.0,t42:1.0)100.0:1.0)100.0:1.0)100.0:1.0,t43:1.0)100.0:1.0,((t34:1.0,t68:1.0)100.0:1.0,t62:1.0)100.0:1.0)100.0;");
        assertTrue(te.getTreeEquals(supertree, currentconsensus));
        //assertTrue(te.getTreeEquals(supertree, maybesupertree));

        Tree test1 = Newick.getTreeFromString("((t43:1.0,t99:1.0,(((((((t55:1.0,(t31:1.0,t39:1.0):1.0,(t63:1.0,t86:1.0):1.0)100.0:1.0,(t46:1.0,t70:1.0):1.0):1.0,((t96:1.0,(t67:1.0,(t54:1.0,t81:1.0):1.0):1.0):1.0,(t58:1.0,(t93:1.0,t77:1.0):1.0):1.0):1.0)100.0:1.0,((((t21:1.0,(t40:1.0,t53:1.0):1.0)100.0:1.0,t20:1.0):1.0,(t35:1.0,(t52:1.0,t97:1.0):1.0):1.0)100.0:1.0,t23:1.0,t1:1.0,((((t9:1.0,t30:1.0):1.0,(t15:1.0,t41:1.0):1.0):1.0,(((t94:1.0,t10:1.0):1.0,(t38:1.0,t8:1.0):1.0):1.0,(t13:1.0,t29:1.0):1.0):1.0):1.0,(t73:1.0,(t17:1.0,t47:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t75:1.0,((t50:1.0,t88:1.0):1.0,(t4:1.0,(t3:1.0,t51:1.0):1.0):1.0):1.0):1.0)100.0:1.0,(t48:1.0,(t84:1.0,(t89:1.0,t26:1.0):1.0):1.0):1.0)100.0:1.0,(t33:1.0,(t61:1.0,t42:1.0):1.0):1.0):1.0)100.0:1.0,(t62:1.0,(t34:1.0,t68:1.0):1.0):1.0)100.0;");
        Tree test2 = Newick.getTreeFromString("((t62:1.0,(t68:1.0,t34:1.0):1.0):1.0,(t43:1.0,(t99:1.0,((t33:1.0,(t61:1.0,t42:1.0):1.0):1.0,((t48:1.0,(t84:1.0,(t26:1.0,t89:1.0):1.0):1.0):1.0,((t75:1.0,((t88:1.0,t50:1.0):1.0,(t4:1.0,(t3:1.0,t51:1.0):1.0):1.0):1.0):1.0,((((t55:1.0,(t70:1.0,t46:1.0):1.0):1.0,(t31:1.0,(t63:1.0,t86:1.0):1.0):1.0):1.0,((t58:1.0,(t93:1.0,t77:1.0):1.0):1.0,(t96:1.0,(t67:1.0,(t81:1.0,t54:1.0):1.0):1.0):1.0):1.0):1.0,(t1:1.0,(((((t27:1.0,(t60:1.0,t5:1.0)100.0:1.0)100.0:1.0,(((t83:1.0,t11:1.0)100.0:1.0,t49:1.0)100.0:1.0,(((((t12:1.0,t8:1.0)100.0:1.0,t38:1.0)100.0:1.0,(t94:1.0,(t10:1.0,t85:1.0)100.0:1.0)100.0:1.0)100.0:1.0,((t56:1.0,(t78:1.0,t16:1.0)100.0:1.0)100.0:1.0,t74:1.0)100.0:1.0)100.0:1.0,(t90:1.0,(t44:1.0,t64:1.0)100.0:1.0,(t36:1.0,(t13:1.0,t29:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((((t22:1.0,t95:1.0)100.0:1.0,t18:1.0)100.0:1.0,((t45:1.0,t98:1.0)100.0:1.0,(t15:1.0,t87:1.0,t100:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(((t71:1.0,t79:1.0,t41:1.0)100.0:1.0,(t92:1.0,t69:1.0)100.0:1.0,t80:1.0)100.0:1.0,(t19:1.0,t72:1.0):1.0)100.0:1.0)100.0:1.0,(((t65:1.0,t14:1.0)100.0:1.0,t25:1.0,t28:1.0)100.0:1.0,(t9:1.0,(t76:1.0,t30:1.0)100.0:1.0)100.0:1.0,t2:1.0)100.0:1.0)100.0:1.0)100.0:1.0,(t91:1.0,t59:1.0):1.0):1.0,(t23:1.0,((t20:1.0,(t21:1.0,(t40:1.0,t53:1.0):1.0):1.0):1.0,(t35:1.0,(t97:1.0,t52:1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0):1.0);");
        Tree expected = supertree;
        SCM s = new SCM();
        Tree result = s.getSCM(test1, test2, false);
        System.out.println("Hiiier ist der Consensus");
        sourcetrees = new Tree[]{test1, test2};
        print = FN_FP_RateComputer.calculateSumOfRates(result, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        sourcetrees = new Tree[]{one, two, three, four, five, six};
        print = FN_FP_RateComputer.calculateSumOfRates(result, sourcetrees);
        for (double a : print){
            System.out.print(a+" ");
        }
        System.out.println();
        assertTrue(te.getTreeEquals(test1, testtest1));
        assertTrue(te.getTreeEquals(test2, testtest2));
        assertTrue(te.getTreeEquals(result, expected));
    }*/

}
