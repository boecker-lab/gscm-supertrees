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
    public void testtakeCareOfCollisions (){
        SCM s = new SCM();
        TreeEquals t = new TreeEquals();
        Tree one = Newick.getTreeFromString("(((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((f:1.0,((g:1.0,h:1.0):1.0,(i:1.0,j:1.0):1.0):1.0):1.0,(k:1.0,(l:1.0,m:1.0):1.0):1.0):1.0):1.0,((ab:1.0,((ac:1.0,ad:1.0):1.0,(ae:1.0,af:1.0):1.0):1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,(ba:1.0,(r:1.0,s:1.0):1.0):1.0):1.0):1.0)");
        Tree two = Newick.getTreeFromString("(((((a:1.0,t:1.0):1.0,u:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((i:1.0,y:1.0):1.0,(w:1.0,(x:1.0,m:1.0):1.0):1.0):1.0):1.0,((ab:1.0,ag:1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,(((ba:1.0,bb:1.0):1.0,bc:1.0):1.0,(v:1.0,s:1.0):1.0):1.0):1.0):1.0)");
        System.out.println("Hier ist das Beispiel!");
        Tree result = s.getSCM(one, two);
        String re = Newick.getStringFromTree(result);
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0,c:1.0,t:1.0,u:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((i:1.0,y:1.0,j:1.0,f:1.0,(g:1.0,h:1.0):1.0):1.0,(k:1.0,l:1.0,m:1.0,x:1.0,w:1.0):1.0):1.0):1.0,((ab:1.0,ag:1.0,((ac:1.0,ad:1.0):1.0,(ae:1.0,af:1.0):1.0):1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,((bc:1.0,(ba:1.0,bb:1.0):1.0):1.0,(v:1.0,r:1.0,s:1.0):1.0):1.0):1.0):1.0);");
        double[] x = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{one, two});
        for (double y : x){
            System.out.print(y+" ");
        }
        assertTrue(t.getTreeEquals(result,expected));

        Tree a = Newick.getTreeFromString("((((a:1.0,c:1.0):1.0,d:1.0):1.0,b:1.0):1.0,f:1.0)");
        Tree b = Newick.getTreeFromString("((((a:1.0,x:1.0):1.0,y:1.0):1.0,b:1.0):1.0,(f:1.0,z:1.0):1.0)");
        result = s.getSCM(a, b);
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
        Tree result = s.getSCM(test1, test2);
        System.out.println("Hiiier ist der Consensus");
        assertTrue(te.getTreeEquals(result, expected));
    }

}
