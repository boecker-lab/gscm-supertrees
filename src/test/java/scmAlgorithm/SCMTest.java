package scmAlgorithm;

import epos.algo.consensus.Consensus;
import epos.algo.consensus.nconsensus.NConsensus;
import org.junit.Test;
import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;
import phyloTree.model.tree.TreeNode;
import phyloTree.treetools.FN_FP_RateComputer;
import treeUtils.TreeEquals;
import treeUtils.TreeUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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
        Tree result = s.getSCM(one, two, false);
        String re = Newick.getStringFromTree(result);
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0,c:1.0,t:1.0,u:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,((i:1.0,y:1.0,j:1.0,f:1.0,(g:1.0,h:1.0):1.0):1.0,(k:1.0,l:1.0,m:1.0,x:1.0,w:1.0):1.0):1.0):1.0,((ab:1.0,ag:1.0,((ac:1.0,ad:1.0):1.0,(ae:1.0,af:1.0):1.0):1.0):1.0,(((n:1.0,o:1.0):1.0,(p:1.0,q:1.0):1.0):1.0,((bc:1.0,(ba:1.0,bb:1.0):1.0):1.0,(v:1.0,r:1.0,s:1.0):1.0):1.0):1.0):1.0);");
        double[] x = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{one, two});
        assertTrue(x[3] == 0);
        assertTrue(t.getTreeEquals(result,expected));

        Tree a = Newick.getTreeFromString("((((a:1.0,c:1.0):1.0,d:1.0):1.0,b:1.0):1.0,f:1.0)");
        Tree b = Newick.getTreeFromString("((((a:1.0,x:1.0):1.0,y:1.0):1.0,b:1.0):1.0,(f:1.0,z:1.0):1.0)");
        result = s.getSCM(a, b, false);
        expected = Newick.getTreeFromString("(((a:1.0,c:1.0,d:1.0,x:1.0,y:1.0):1.0,b:1.0):1.0,(z:1.0,f:1.0):1.0)");
        assertTrue(t.getTreeEquals(result, expected));
        List<Tree> trees = new ArrayList<Tree>();
        trees.add(a);
        trees.add(b);
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, trees.toArray(new Tree[trees.size()]));
        assertTrue(print[3] == 0);
        Tree maybe = Newick.getTreeFromString("((((a:1.0,c:1.0,x:1.0):1.0,d:1.0,y:1.0):1.0,b:1.0):1.0,(f:1.0,z:1.0):1.0)");
        print = FN_FP_RateComputer.calculateSumOfRates(maybe, trees.toArray(new Tree[trees.size()]));
        assertTrue(print[3] == 0);
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
        result = s.CutTree(a, tokeep);
        Tree expected = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,c:1.0)");
        assertTrue(t.getTreeEquals(result, expected));


        //BEISPIEL A eingeschränkt auf a, b
        tokeep.clear();
        tokeep.add("a");
        tokeep.add("b");
        result = s.CutTree(a, tokeep);
        expected = Newick.getTreeFromString("(a:1.0,b:1.0)");
        assertTrue(t.getTreeEquals(result, expected));

        //BEISPIEL C eingeschränkt auf a, b
        tokeep.clear();
        List<TreeNode> testt = new ArrayList(Arrays.asList(a.getLeaves()));
        boolean ze = testt.contains(c.getVertex("a"));
        assertFalse(ze);


        tokeep.add("a");
        tokeep.add("c");
        result = s.CutTree(c, tokeep);
        expected = Newick.getTreeFromString("(a:1.0,c:1.0)");
        assertTrue(t.getTreeEquals(result, expected));

        //BEISPIEL C eingeschränkt auf c, d
        tokeep.clear();
        tokeep.add("c");
        tokeep.add("d");
        result = s.CutTree(c, tokeep);
        expected = Newick.getTreeFromString("(c:1.0,d:1.0)");
        assertTrue(t.getTreeEquals(result, expected));


        //BEISPIEL C eingeschränkt auf d
        tokeep.clear();
        tokeep.add("d");
        result = s.CutTree(c, tokeep);
        String res = Newick.getStringFromTree(result);
        expected = Newick.getTreeFromString("d:1.0");
        assertTrue(t.getTreeEquals(result, expected));

        //BEISPIEL G eingeschränkt auf d, e
        tokeep.clear();
        tokeep.add("d");
        tokeep.add("e");
        result = s.CutTree(g, tokeep);
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
    public void testCuttingAndInserting() {
        InputStream in = SCMTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        List<Tree> alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        Tree tree = alltrees.get(1);
        Tree compare = tree.cloneTree();
        Tree cutcompare;
        Tree cuttree;
        Tree hanginconsensus;
        Tree consensus = new Tree();
        //create 100 random nodes in tree
        compare = randomInsert(compare, 100);
        ArrayList<String> nodesofboth = TreeUtils.getOverLappingNodes(tree, compare);
        assertTrue(TreeUtils.StringListsEquals(nodesofboth, TreeUtils.helpgetLabelsFromNodes(Arrays.asList(tree.getLeaves()))));
        SCM s = new SCM();
        cuttree = s.CutTree(tree, nodesofboth);
        cutcompare = s.CutTree(compare, nodesofboth);

        consensus = Consensus.getStrictConsensus(Arrays.asList(cuttree, cutcompare));
        TreeEquals equ = new TreeEquals();
        //Konsensusbaum gleich Usprungsbaum
        assertTrue(equ.getTreeEquals(tree, consensus));

        hanginconsensus = s.hangInNodes(consensus);
        //Baum mit eingefügten Vertices ist gleich Vergleichbaum
        assertTrue(equ.getTreeEquals(compare, hanginconsensus));


        Tree one = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,e:1.0):1.0,((f:1.0,g:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0);");
        Tree two = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,e:1.0):1.0):1.0,((g:1.0,f:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0):1.0,(k:1.0,l:1.0):1.0);");
        nodesofboth = TreeUtils.getOverLappingNodes(one, two);

        Tree hungin = s.getSCM(one, two, false);
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



        Tree reseins = Consensus.getStrictConsensus(Arrays.asList(a,b));
        Tree reszwei = Consensus.getStrictConsensus(Arrays.asList(c,d));
        Tree resdrei = Consensus.getStrictConsensus(Arrays.asList(e,f));
        Tree resvier = Consensus.getStrictConsensus(Arrays.asList(e,g));
        Tree resfuenf = Consensus.getStrictConsensus(Arrays.asList(e,h));


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
        Tree expected = Newick.getTreeFromString("((((a:1.0,b:1.0,y:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0,z:1.0):1.0,f:1.0)");
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(result, expected));

        InputStream in = SCMTest.class.getResourceAsStream("/100_20_superTree_sm.0.sourceTrees.scmTree.tre_OptRoot.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        List<Tree> alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        Tree realSCM = alltrees.get(0);
        in = SCMTest.class.getResourceAsStream("/100_20_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));

        CalculateSupertree res = new CalculateSupertree(CalculateSupertree.Type.RESOLUTION, CalculateSupertree.Info.OFF);
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 100/20 resolution ist "+Newick.getStringFromTree(result));
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 resolution Supertree: "+ (result.vertexCount() - result.getNumTaxa()));
        System.out.println("100/20 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 swenson-Supertree: "+(realSCM.vertexCount() - realSCM.getNumTaxa()));
        System.out.println("100/20 resolution FNFP gegen Eingabebaeume");
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()]));
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
        res = new CalculateSupertree(CalculateSupertree.Type.OVERLAP, CalculateSupertree.Info.OFF);
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 100/20 overlap ist "+Newick.getStringFromTree(result));
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 overlap Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("100/20 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 100/20 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        System.out.println("100/20 overlap FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()]));
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
        res = new CalculateSupertree(CalculateSupertree.Type.RESOLUTION, CalculateSupertree.Info.OFF);
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 500/50 resolution ist "+Newick.getStringFromTree(result));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 resolution Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("500/50 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        System.out.println("500/50 resolution FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()]));
        for (double a : print){
            System.out.print(a + " ");
        }
        System.out.println("\n"+"500/50 SWENSON FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(realSCM, alltrees.toArray(new Tree[alltrees.size()]));
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
        res = new CalculateSupertree(CalculateSupertree.Type.OVERLAP, CalculateSupertree.Info.OFF);
        result = res.getSupertree(alltrees);
        System.out.println("Supertree 500/50 overlap ist "+Newick.getStringFromTree(result));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 overlap Supertree: " + (result.vertexCount() - result.getNumTaxa()));
        System.out.println("500/50 swenson scm ist "+Newick.getStringFromTree(realSCM));
        System.out.println("Differenz von #Vertices und #Blaetter in 500/50 swenson-Supertree: " + (realSCM.vertexCount() - realSCM.getNumTaxa()));
        System.out.println("500/50 overlap FNFP gegen Eingabebaeume");
        print = FN_FP_RateComputer.calculateSumOfRates(result, alltrees.toArray(new Tree[alltrees.size()]));
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
    public void testProblem1(){
        Tree eins = Newick.getTreeFromString("((((((beta:1.0,gamma:1.0):1.0,(a:1.0,b:1.0):1.0):1.0,alpha:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(al:1.0,(e:1.0,f:1.0):1.0):1.0):1.0,(x:1.0,y:1.0):1.0);");
        Tree zwei = Newick.getTreeFromString("((((pi:1.0,((a:1.0,b:1.0):1.0,bl:1.0):1.0):1.0,(e:1.0,f:1.0):1.0,(delta:1.0,epsilon:1.0):1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(x:1.0,y:1.0):1.0);");
        SCM s = new SCM();
        Tree result = s.getSCM(eins, zwei, false);
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{eins, zwei});
        assertTrue(print[3] == 0);
        Tree correct = Newick.getTreeFromString("(((delta:1.0,epsilon:1.0):1.0,((a:1.0,b:1.0):1.0,bl:1.0,pi:1.0,alpha:1.0,(beta:1.0,gamma:1.0):1.0):1.0,(c:1.0,d:1.0):1.0,(al:1.0,(e:1.0,f:1.0):1.0):1.0):1.0,(x:1.0,y:1.0):1.0);");
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(result, correct));
    }

    @Test
    public void testCladeHangsAtTwoEdges (){
        Tree eins = Newick.getTreeFromString("(((((raus:1.0,((t1:1.0,t2:1.0):1.0,(a:1.0,(b:1.0,c:1.0):1.0,(g:1.0,h:1.0):1.0):1.0):1.0):1.0,(bli:1.0,blu:1.0):1.0):1.0,bla:1.0):1.0,d:1.0):1.0,x:1.0,((z:1.0,q:1.0):1.0,alpha:1.0):1.0,y:1.0);");
        Tree zwei = Newick.getTreeFromString("((((a:1.0,raus:1.0):1.0,(b:1.0,c:1.0):1.0,(g:1.0,h:1.0):1.0):1.0,(bli:1.0,blu:1.0):1.0):1.0,bla:1.0);");
        SCM s = new SCM();
        Tree result = s.getSCM(eins, zwei, false);
        double[] print = FN_FP_RateComputer.calculateSumOfRates(result, new Tree[]{eins, zwei});
        assertTrue(print[3] == 0);
    }

}
