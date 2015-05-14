package scmAlgorithm;


import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.TreeUtilsBasic;
import org.apache.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.Random;
import org.jgrapht.traverse.*;
import treeUtils.TreeEquals;
import treeUtils.TreeUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Anika
 * Date: 27.10.14
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */


public class SCM {

    private LinkedHashMap<String, List<String>> TreeCut1 = new LinkedHashMap<String, List<String>>();
    private LinkedHashMap<String, List<String>> TreeCut2 = new LinkedHashMap<String, List<String>>();

    public Map.Entry<String,List<String>>[] getTreeCut1EntrySet (){
        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        return mapValues.toArray(TreeCut1entries);
    }

    public Map.Entry<String,List<String>>[] getTreeCut2EntrySet (){
        Set<Map.Entry<String, List<String>>> mapValues = TreeCut2.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        return mapValues.toArray(TreeCut2entries);
    }



    /*public Tree getPaperConsensus (List<Tree> input) {
        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = getTreesForPaperSCM(output);
            one = save.get(0);
            two = save.get(1);
            three = save.get(2);
            System.out.println("Got one SCM: " +Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }*/

    /*public Tree getSuperfineConsensus (List<Tree> input){
        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = getTreesWithBiggestOverlap(output);
            one = save.get(0);
            two = save.get(1);
            three = SCM(one, two);
            System.out.println("Got one SCM: " +Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }*/

    /*public List<Tree> getTreesForPaperSCM (List<Tree> input){
        float curscore;
        float resolution;
        Tree scm;
        Tree maxscm = new Tree();
        float maxscore = 0;
        int mem1 = 0, mem2 = 0;
        int a, b, c = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    //TODO is it possible that two trees have no overlap?
                    if (getOverLappingNodes(iter, cur).size()==0);
                    else {
                        //System.out.println("Calculating scm of tree "+Newick.getStringFromTree(iter)+" and "+Newick.getStringFromTree(cur)+".");
                        scm = SCM(iter, cur);
                        //TODO look up the definition of resolution
                        //a = scmAlgorithm.vertexCount();
                        //b = scmAlgorithm.getLeaves().length-3;
                        //c = scmAlgorithm.getLeaves().length;
                        resolution = scm.vertexCount() / (scm.getLeaves().length-3);
                        //resolution = a / b;
                        //TODO is this possible or do I have to take |L(Ti)UL(Tj)|?
                        curscore = resolution / scm.getLeaves().length;
                        //curscore = resolution / c;
                        if (curscore > maxscore){
                            maxscm = scm;
                            maxscore = curscore;
                            mem1 = input.indexOf(iter);
                            mem2 = input.indexOf(cur);
                        }
                    }
                }
            }
        }
        if (maxscore == 0 && input.size()>=2){
            output.add(input.get(0));
            output.add(input.get(1));
            System.err.println("In der Liste gibt es keine Bäume mit Scoring Ã¼ber 0");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(maxscm);
        }
        return output;
    }*/

    /*public List<Tree> getTreesWithBiggestOverlap (List<Tree> input){
        int cursize;
        int maxsize = 0;
        int mem1 = 0, mem2 = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    cursize = getOverLappingNodes(iter, cur).size();
                    if (cursize > maxsize){
                        maxsize = cursize;
                        mem1 = input.indexOf(iter);
                        mem2 = input.indexOf(cur);
                    }
                }
            }
        }
        if (maxsize == 0 && input.size()>=2){
            output.add(input.get(0));
            output.add(input.get(1));
            System.err.println("In der Liste gibt es keine Bäume mit Overlap");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
        }
        return output;
    }*/


    public void takeCareOfCollisions(){
        Set<Map.Entry<String, List<String>>> mapValues1 = TreeCut1.entrySet();
        Set<Map.Entry<String, List<String>>> mapValues2 = TreeCut2.entrySet();
        int maplength1 = mapValues1.size();
        int maplength2 = mapValues2.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength1];
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength2];
        mapValues1.toArray(TreeCut1entries);
        mapValues2.toArray(TreeCut2entries);
        Map<List<String>, List<String>> order = new HashMap();
        List<String> currvalue;
        String currkey;
        List<String> currlist;

        for (int iter1=maplength1-1; iter1>=0; iter1--){
            currvalue = TreeCut1entries[iter1].getValue();
            currkey = TreeCut1entries[iter1].getKey();
            if (order.containsKey(currvalue)){
                currlist = order.get(currvalue);
            }
            else {
                currlist = new ArrayList<String>();
            }
            currlist.add(currkey);
            currlist.add("Tree1");
            order.put(currvalue, currlist);
        }


        for (int iter2=maplength2-1; iter2>=0; iter2--){
            currvalue = TreeCut2entries[iter2].getValue();
            currkey = TreeCut2entries[iter2].getKey();
            if (order.containsKey(currvalue)){
                currlist = order.get(currvalue);
            }
            else {
                currlist = new ArrayList<String>();
            }
            currlist.add(currkey);
            currlist.add("Tree2");
            order.put(currvalue, currlist);
        }


        for (List<String> ke : order.keySet()){
            List<String> list = order.get(ke);
            if (list.contains("Tree1")&&list.contains("Tree2")){
                while (list.contains("Tree1")){
                    list.remove("Tree1");
                }
                while (list.contains("Tree2")){
                    list.remove("Tree2");
                }
                for (String el : list){
                    if (TreeCut2.containsKey(el)){
                        TreeCut2.remove(el);
                    }
                    if (TreeCut1.containsKey(el)){
                        TreeCut1.remove(el);
                    }
                }
                List<String> newlist;
                List<String> work;
                for (int iter=list.size()-1; iter>0; iter--){
                    work = new ArrayList<String>(list);
                    newlist = work.subList(0,iter);
                    newlist.addAll(ke);
                    newlist.add("Polytomy");
                    TreeCut1.put(list.get(iter), newlist);
                }
                TreeCut1.put(list.get(0), ke);
            }
        }

    }

    public boolean checkDirectionOfBackbone(List<String> nodes, Tree one, Tree two){
        List<String> dfiterator1 = TreeUtils.generateDepthFirstListChildren(one);
        List<String> dfiterator2 = TreeUtils.generateDepthFirstListChildren(two);
        boolean foundfirst = false;
        boolean foundlast = false;
        int iter = 0;
        String cur;
        int posf1;
        int posl1;
        String elf1 = "";
        String ell1 = "";
        //search for the first occurence of an element both trees have in common in depth first iterator of tree 1
        do {
            cur = dfiterator1.get(iter);
            if (nodes.contains(cur)) {
                foundfirst = true;
                //remember the element and its position
                posf1 = iter;
                elf1 = cur;
            }
            iter ++;
        } while (!foundfirst);
        //search for the last occurence of an element both trees have in common in depth first iterator of tree 1
        iter = dfiterator1.size()-1;
        do {
            cur = dfiterator1.get(iter);
            if (nodes.contains(cur)) {
                foundlast = true;
                //remember the element and its position
                posl1 = iter;
                ell1 = cur;
            }
            iter--;
        } while (!foundlast);
        //search for the first and last non-cut element from tree 1 in depth first iterator of tree 2
        int posf2 = dfiterator2.indexOf(elf1);
        int posl2 = dfiterator2.indexOf(ell1);
        //if the first appears later than the last, the list has to be rotated
        if (posf2 > posl2) return true;
        else return false;
    }


    public Tree getSCM (Tree one, Tree two){
        ArrayList<String> nodesofboth = TreeUtils.getOverLappingNodes(one, two);
        Tree result;
        Tree between;
        NConsensus con = new NConsensus();
        con.getLog().setLevel(Level.OFF);
        if (nodesofboth.size()==0){
            System.err.println("Kein SCM möglich, da kein Overlap");
            return new Tree();
        }
        if (nodesofboth.size() == one.getLeaves().length && nodesofboth.size() == two.getLeaves().length){
            result = con.consesusTree(new Tree[]{one, two}, 1.0);
            //System.err.println("SCM der Bäume ergab Consensus, da gleiches Taxaset.");
        }
        else {
            boolean turn = checkDirectionOfBackbone(nodesofboth, one, two);
            one = CutTree(one, nodesofboth, turn);
            two = CutTree(two, nodesofboth, turn);
            takeCareOfCollisions();
            between = con.consesusTree(new Tree[]{one, two}, 1.0);
            result = hangInNodes(between);
        }
        return result;

    }

    /*public Tree hangInNodes (Tree input){
        Tree output = input.cloneTree();
        String currlabel;
        List<String> currlist;
        List<TreeNode> finallist = new ArrayList<TreeNode>();
        TreeNode lca = new TreeNode();
        TreeNode newvertex;
        TreeNode betw1;
        TreeNode betw2;


        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut1entries);
        for (int iter=maplength-1; iter>=0; iter--){
            //System.out.println(test[iter].getKey()+" "+test[iter].getValue());
            currlabel = TreeCut1entries[iter].getKey();
            currlist = TreeCut1entries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                finallist = TreeUtils.helpgetTreeNodesFromLabels(currlist, output);
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        TreeCut1.clear();


        mapValues = TreeCut2.entrySet();
        maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut2entries);
        for (int iter=maplength-1; iter>=0; iter--){
            currlabel = TreeCut2entries[iter].getKey();
            currlist = TreeCut2entries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        TreeCut2.clear();

        return output;
    }*/

    public Tree hangInNodes (Tree input){
        Tree output = input.cloneTree();

        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut1entries);
        output = helphangInNodes(output, maplength, TreeCut1entries);
        TreeCut1.clear();

        mapValues = TreeCut2.entrySet();
        maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut2entries);
        output = helphangInNodes(output, maplength, TreeCut2entries);
        TreeCut2.clear();

        return output;
    }


    private Tree helphangInNodes(Tree input, int maplength, Map.Entry<String,List<String>>[] TreeCutentries){
        Tree output = input.cloneTree();
        String currlabel;
        List<String> currlist;
        List<TreeNode> finallist = new ArrayList<TreeNode>();
        TreeNode lca = new TreeNode();
        TreeNode newvertex;
        TreeNode betw1;
        TreeNode betw2;

        for (int iter=maplength-1; iter>=0; iter--){
            currlabel = TreeCutentries[iter].getKey();
            currlist = TreeCutentries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        return output;
    }



    public Tree CutTree (Tree input, List<String> keep, boolean turn){
        Tree output = input.cloneTree();
        //makes a list that contains all leaves in the input tree
        List<String> originalleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(output.getLeaves())));
        if (keep.isEmpty()) return new Tree();

        List<String> depthFirst = TreeUtils.generateDepthFirstListChildren(output);
        List<TreeNode> tokeep = new ArrayList<TreeNode>(TreeUtils.helpgetTreeNodesFromLabels(keep, output));
        List<TreeNode> cut = new ArrayList<TreeNode>(TreeUtils.helpgetTreeNodesFromLabels(depthFirst, output));
        if (turn){
            Collections.reverse(cut);
        }

        for (TreeNode iter : tokeep){
            cut.remove(iter);
        }
        boolean firstpass = true;
        int savewhere;
        if (TreeCut1.isEmpty()) savewhere = 1;
        else savewhere = 2;

        if (cut.size()==0) return output;

        List<TreeNode> between;
        List<TreeNode> between2;
        TreeNode par;
        String label;
        List<String> finallist;

        while (cut.size()!=0){
            for (TreeNode iter : cut){
                if (firstpass){
                    label = "";
                    par = iter.getParent();
                    finallist = new ArrayList<String>();
                    between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                    between2 = new ArrayList<TreeNode>(between);
                    for (TreeNode el : between2){
                        if (!originalleaves.contains(el.getLabel())) between.remove(el);
                    }
                    while (between.size() <= 1 && between.get(0).getParent()!=null){
                        par = par.getParent();
                        between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                        between2 = new ArrayList<TreeNode>(between);
                        for (TreeNode el : between2){
                            if (!originalleaves.contains(el.getLabel())) between.remove(el);
                        }
                    }
                    between.remove(iter);
                    if (between.size()==0){
                        System.err.println("Fehler bei CutTree");
                    }
                    if (between.size()>1 && par.getChildren().size()>2){
                        finallist.add("Polytomy");
                    }
                    for (TreeNode x : between){
                        finallist.add(x.getLabel());
                    }
                    if (savewhere == 1){
                        TreeCut1.put(label.concat(iter.getLabel()), finallist);
                    }
                    else {
                        TreeCut2.put(label.concat(iter.getLabel()), finallist);
                    }
                }
                output.removeVertex(iter);
            }
            firstpass = false;
            cut = new ArrayList<TreeNode>(Arrays.asList(output.getLeaves()));
            for (TreeNode iter : tokeep){
                cut.remove(iter);
            }
        }
        for (TreeNode x : output.vertices()){
            if (x.degree() == 2 && x!=output.getRoot()){
                for (TreeNode y : x.children()){
                    output.addEdge(x.getParent(),y);
                }
                output.removeVertex(x);

            }
        }
        TreeNode newroot = output.findLeastCommonAncestor(tokeep);
        if (!output.getRoot().equalsNode(newroot)){
            Tree finaltree = output.getSubtree(newroot);
            return finaltree;

        }
        return output;
    }



    public static void main (String args[]){

        /*GreedySCM hey = new GreedySCM();
        Tree b = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,f:1.0)");
        List<String> tryout = hey.generateDepthFirstListChildren(b);
        for (String x : tryout){
            System.out.println(x);
        }*/

        /*LinkedHashMap<String, List<String>> list = new LinkedHashMap<String, List<String>>();
        ArrayList<String> abc = new ArrayList<String>();
        abc.add("a");
        abc.add("b");
        abc.add("c");
        ArrayList<String> ab = new ArrayList<String>();
        ArrayList<String> a = new ArrayList<String>();
        ab.add("a");
        ab.add("b");
        a.add("a");
        list.put("1", abc);
        list.put("2", a);
        list.put("3", abc);
        list.put("4", ab);
        list.put("5", ab);
        for (String str : list.keySet()){
            System.out.println(str + " "+ list.get(str));
        }

        for( Map.Entry<String,String> entry : list.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " " + value);
        }

        Set<Map.Entry<String, List<String>>> mapValues = list.entrySet();
        int maplength = mapValues.size();
        Collection<List<String>> values = list.values();
        System.out.println(values.size());
        values.toArray();
        for (List<String> x : values){
            System.out.println(x);
        }*/
        //Map.Entry<String,String>[] test = new Map.Entry[maplength];
        //mapValues.toArray(test);
        //for (int iter=maplength-1; iter>=0; iter--){
        //    System.out.println(test[iter].getKey()+" "+test[iter].getValue());
        //}*/


        SCM hey = new SCM();
        //hey.testSCM();

        Tree test1 = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(e:1.0,f:1.0):1.0)");
        Tree test2 = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0,d:1.0):1.0,(e:1.0,f:1.0):1.0)");

        double resolution = (test1.vertexCount()-1-test1.getLeaves().length) / (test1.getLeaves().length-2);
        System.out.println("Auflösung von test1 ist "+resolution);
        resolution = TreeUtilsBasic.calculateTreeResolution(test1.getNumTaxa(), test1.vertexCount());
        System.out.println("Auflösung von test1 ist "+resolution);
        int one = test2.vertexCount()-1-test2.getNumTaxa();
        int two = test2.getNumTaxa()-2;
        int three = 3/4;
        resolution = (test2.vertexCount()-1-test2.getNumTaxa()) / (test2.getNumTaxa()-2);
        System.out.println("Auflösung von test2 ist "+resolution);
        resolution = (test2.vertexCount()-1-test2.getLeaves().length) / (test2.getLeaves().length-2);
        System.out.println("Auflösung von test2 ist "+resolution);
        resolution = TreeUtilsBasic.calculateTreeResolution(test2.getNumTaxa(), test2.vertexCount());
        System.out.println("Auflösung von test2 ist "+resolution);

        //Tree a = Newick.getTreeFromString("((t169:0.02056467440428987,t108:0.02563947678634368)100:0.06741310372226965,(((t495:0.10917012927375173,((t195:0.10340996876048182,t165:0.11249025586572521)40:1.28060618246117E-6,(t131:0.07252526964244901,((t4:0.06020329392131871,t312:0.038910241371599416)87:0.008360685022321818,t321:0.06830399985073354)99:0.01817592679073262)100:0.033484662747521245)88:0.012555393545572216)100:0.022777591692494933,t450:0.15388230193294133)100:0.02961663027317288,((t371:0.1389421237592064,(t92:0.1104898929326545,(t262:0.06916278290604252,t189:0.07459192428798872)100:0.03799810581845313)74:0.010270676287150152)100:0.03459439554786425,((t53:0.14110769660303102,(t181:0.11864673721728795,(t444:0.0671338216135925,(t261:0.0064592410178400585,t188:0.0041711215026039835)100:0.045561424248944334)100:0.10528041235376885)22:1.28060618246117E-6)100:0.03138328605910244,((t184:0.07901238266767743,(t414:0.08025856413488873,t144:0.09485297213022711)82:0.022352501216084276)100:0.07144367623540092,(((t47:0.056946757852635504,(t347:0.051978139057718226,(t481:0.04487642130752454,t235:0.05146445129010096)98:0.014032739588420958)98:0.014494878675988742)100:0.08167640798578357,((t290:0.09380134252475787,t202:0.048200537341168005)100:0.06780361910503069,(t421:0.1864277665012198,(t38:0.07738252450442691,(t341:0.09682860678743666,(t17:0.05137261558852251,t413:0.03917676869216877)100:0.07572430436185493)96:0.015898650714822025)100:0.04607997971513339)72:0.001213390885099741)96:0.01440099617743572)98:0.02337555299190737,(((((t225:0.006079313520494399,t103:0.008628640410399334)100:0.154833547526221,((t340:0.1953872336483595,(((t356:0.1262352768726709,t146:0.12662551532687943)98:0.044577805358935595,t483:0.14897544322315748)94:0.010395074741298025,((t161:0.1093188866266506,t221:0.08462512319398661)100:0.055228146719204174,(t26:0.07836240081104719,t349:0.07879123175734319)100:0.05695631440031426)97:0.01767013450317087)98:0.012403251964888425)100:0.013753862469104433,(((t156:0.05693835336392994,(t392:0.017564930888416885,(t231:0.00699556786714038,t296:0.003804812892347839)100:0.01330816082971064)100:0.02565141211010015)100:0.07626977834655616,(t440:0.03035186316395571,(t425:0.01785688347618177,(t79:1.28060618246117E-6,t252:1.28060618246117E-6)100:0.009681268247012622)100:0.02158456240997149)100:0.09821598003253676)100:0.05676905338552688,(((((t186:0.11495119124464204,t407:0.1363001988749887)83:0.010639182448428305,t14:0.13329635082098512)99:0.023044511958417092,t384:0.15166839397357673)100:0.020633790051151128,(((t279:0.08731762674954793,t96:0.0792598555900757)100:0.04944195696395667,(t368:0.1251460829029792,((((t378:0.022165305791404008,t315:0.029860920562994334)79:0.0066808351074927885,(t381:0.04021282019037454,t376:0.034465952754769796)30:0.0015123688117842761)100:0.04765112417502503,((t398:0.04681819603171684,t138:0.046006940287527734)39:0.0034283827064993,t34:0.04665607553569118)100:0.0355865494168686)100:0.030375845471925386,((t224:0.024170594849546218,t247:0.02016989692954509)100:0.0731182445972318,t18:0.07005229651658874)98:0.01594451607962281)98:0.010981939864435374)100:0.0272707950361778)100:0.021578419380139362,(t460:0.15057955272098647,(t269:0.08662750293115233,t136:0.07907697770944722)100:0.07255907188506434)68:0.004267348273175963)94:0.01227417876092108)60:0.0010631874147168621,(((t193:0.11505204903157051,(t267:0.12034264613322426,t302:0.13471494438483753)56:0.005794552889002644)97:0.016097234495596583,(t428:0.07352944842925313,(t228:0.03999016331613156,t37:0.03456088479610629)100:0.030977018385112177)100:0.06132770131142908)95:0.011245806528194643,((t187:0.11607085005577038,t180:0.08705894592470814)100:0.023783168035472528,((t452:0.028186089194060945,t395:0.026722208476295285)100:0.07552406732622392,t248:0.13356369306812604)99:0.023335799574215034)100:0.030969981322034917)82:0.005541446723073026)87:0.006962607165375047)80:0.0026723596896034356)100:0.011530490816767526)30:0.003596166440894441,(((t170:0.18139284735075084,((t191:0.12645728264204895,t462:0.0988045073549861)99:0.04041844494472375,((((t328:0.06372607114752471,t245:0.07907983855139955)99:0.03465159146308438,t3:0.1375945063734816)29:0.004297750628000487,(t369:0.1325504178166916,(t408:0.0714687051026849,t90:0.07968994192553958)100:0.03417270648204351)100:0.037517704704581295)88:0.014066514148964324,t49:0.1330166055152802)43:0.0044448638031386765)92:0.014767617445623856)44:0.009182903818733861,(((((t227:0.08205837435802442,(t200:0.05121219110636751,t303:0.07546493751308447)99:0.022650245772673973)100:0.07235110626290213,(t117:0.09244019804230474,(t401:0.07097925346726283,t137:0.06225843077175492)100:0.05777769244971311)98:0.022054134965792605)71:0.005851372077016756,(((t276:0.043126005218409834,(t222:0.009067033328298413,t362:0.011514153398918979)100:0.04084202671388816)100:0.05057720972285346,(t270:0.03876101139619697,(t380:0.003980966850629527,t390:0.0017858735142845048)100:0.0277014109914246)100:0.08047783772528755)100:0.05050445344424672,t327:0.11666750321285278)46:0.005585447068656795)53:0.004552864949622901,(t406:0.15021547063390955,(t500:0.03150488929538157,t367:0.017283020603765655)100:0.15541308435636889)35:0.012595479115761846)92:0.01615101409934024,((t223:0.13774925763357904,(t467:0.07492314708577083,t326:0.0639541841389407)100:0.05020278846663257)96:0.028996429932755537,t461:0.150342270299734)89:0.007427905043916359)100:0.03456257083898455)10:1.28060618246117E-6,(((t419:0.12630518623726733,(t490:0.0425279752398842,t81:0.032707038752805215)100:0.08293209737575868)97:0.03268151176585493,(t466:0.1276641988332817,(t399:0.13906554866472629,t151:0.14774998424640592)41:1.28060618246117E-6)100:0.03335503299234976)94:0.014702170122309361,((((t268:0.07081231268621989,(t29:0.05314239800413978,t281:0.0378578942268506)100:0.010971664115323243)100:0.07373254172377845,((t83:0.0947179463694813,t113:0.06973493760494509)100:0.06571402833079533,((t101:0.11088184214596145,((t265:0.09281739031849372,(t336:0.061506203315363946,t354:0.07210017487481564)99:0.029238095321665117)98:0.025762800116516854,t118:0.1014108732141534)87:0.014733752875598086)53:0.002598101346617261,t12:0.11772707761399336)100:0.0323683046613994)62:0.002191362089417505)100:0.019563835970112152,(t218:0.12374986683485793,(t58:0.11188623244772537,(t106:0.07217544088033027,t66:0.058766603855361914)97:0.020793085712610522)93:0.020136491923846662)100:0.0464878795764104)34:1.28060618246117E-6,(((((t121:0.12924788308414653,t93:0.15741892500820695)48:0.014209198457489983,((t474:0.041567922183912025,(t443:0.02734134286427507,(t438:0.01194149372227841,t299:0.006588604579050394)100:0.03066520418672206)41:0.0014785114631333037)100:0.09044274864750067,((t62:0.02759939919459967,t119:0.03474186855473828)80:1.28060618246117E-6,t64:0.04133201473118848)100:0.0978040610590435)100:0.041438421249138366)13:1.28060618246117E-6,t204:0.12200047921642883)55:0.0045724471639626875,(t288:0.1084120167506601,(t496:0.018079855489872124,t45:0.016222585899287073)100:0.08583826362153472)100:0.04679808117271949)45:0.0059589899835981374,((t464:0.12094100431651676,t67:0.10789050286291058)100:0.07991960516598831,(t124:0.07927095559501597,(t488:0.012723461585328824,t190:0.020826342144274763)100:0.06987076099305793)100:0.06284623802428504)19:1.28060618246117E-6)47:0.006729742226452831)79:0.014540405825929236)20:1.28060618246117E-6)26:0.0010383543954154085)10:1.28060618246117E-6,(t387:0.20538009218891223,((t182:0.10750773749970059,(t348:0.07271193853990027,(t198:0.06173016953396456,t431:0.04313362132159189)91:0.012908774316778362)100:0.024326757924139977)100:0.06603975551178011,((t346:0.07901530468419846,t24:0.13008253203494713)86:0.013901492546563685,t427:0.11177236945076131)100:0.04469989998477104)67:0.005127223770719831)92:0.015676675244401247)20:9.819960638786517E-4,((((t491:0.004057606692687111,t291:0.005742348191867697)100:0.1806042929454614,t497:0.2037315064451476)26:1.28060618246117E-6,(((t201:0.06754884943478977,t23:0.07858095462910024)100:0.06481873580614268,((t454:0.05054594094182899,t373:0.03784461690682863)100:0.09931538450474162,(((t179:0.08974545354222763,(t50:0.05910134374913718,(t423:0.009744594974440367,t389:1.28060618246117E-6)100:0.05014941655881632)100:0.046809609580083886)88:0.010118310247607436,(t84:0.05966438136457367,t420:0.05177841475604175)100:0.05436377093702334)84:0.006534449373480624,(t343:0.09001051086099993,t55:0.11119858867753188)88:0.01753588443638785)100:0.0314713909919055)94:0.00751249734848586)99:0.01839491344455796,(((t42:0.15337280855607727,t148:0.12722870273522469)86:0.009016327931657386,(((t256:0.09285029043063348,t183:0.07465982516101954)99:0.01990165479300553,t404:0.1197365819711296)70:0.0032061546513792675,((t274:0.006002912980422437,t36:0.011908877280136572)100:0.09285931717084228,(t259:0.058916801034139134,t44:0.05154962566903489)100:0.05722433334460059)69:0.007584791552409706)100:0.02899418744823094)100:0.024746638897693442,((t432:0.14104820660010747,((t48:0.08538504026682084,(t128:0.09161574533532613,(t143:0.0669272240471088,t309:0.06768246825124911)90:0.007612315227007921)100:0.04094044435166495)100:0.02182864670849406,(t499:0.12330326240925596,t22:0.1343574145058776)71:0.0016018876381818608)100:0.02296722933545503)100:0.021228651301429313,((t375:0.08707345632559439,((t403:0.0746048794502126,t139:0.0900721509934046)76:1.28060618246117E-6,((t229:0.018900720380834794,t88:0.01898603458577885)100:0.03168180697299456,t147:0.054028080272321914)100:0.03579908920016309)98:0.017671839004118048)100:0.04696848733192598,(t25:0.10413680555977896,(t171:0.060605593795599565,t174:0.07100976449969716)100:0.0360396489468767)100:0.03150014553669344)100:0.02302293124654936)100:0.01317639641055689)100:0.008535576038117845)74:0.001614968699715294)87:0.01095415641889886,((((t8:0.11598922228238877,((t411:0.03285691675243413,t263:0.0721851818170049)100:0.08939924099637697,t40:0.09792413001391818)81:0.011542560106631428)98:0.02161665278970565,((t155:0.07214886766246043,(t159:0.10041757737781204,t391:0.07184333387561968)1 8:0.0018813766339229303)51:0.0065035594315923356,t243:0.12039069066540481)100:0.03482018392251097)83:0.0053643758329177775,(((t360:0.10137098292243726,((t434:0.03908774950820112,t441:0.037457497470598164)100:0.09444134063157548,(t104:0.05378227422548194,(t453:0.051480242869899634,t337:0.03757857164379937)100:0.011829227901388807)100:0.08383084336080066)49:1.28060618246117E-6)100:0.03255679045732144,(t220:0.1274025581866953,((t366:0.09296725273237437,t210:0.08771438404139376)100:0.03679518869045666,t355:0.13203925959832258)65:0.0061965187400838916)94:0.0083008015081342)96:0.011323366875271359,(t426:0.09213943454916147,(t197:0.08561585098374117,t319:0.05696590785020593)90:1.28060618246117E-6)100:0.0780582882030906)76:0.005249149022060012)100:0.030256378332758276,(((t482:0.12345564313438066,(t329:0.091628458013025,t339:0.15007705248853567)86:0.016910723487421068)78:0.009652274533722175,((t194:0.1287896085573261,(t95:0.10332302961004684,(t433:0.07847880709080887,t257:0.03937381788978546)100:0.05762504147869876)91:0.013317630031816095)41:1.28060618246117E-6,t393:0.14374224381631429)94:0.011795111115484059)100:0.0265886198780697,((t98:0.009164086209246698,t485:0.00446672248485915)100:0.14945007105963737,((t470:0.1285659328361837,(t365:0.09915869660120957,(t277:0.05191713799030193,t436:0.04661760908780692)100:0.08381852382419772)92:0.017398827571112028)99:0.01867680026522861,(((t287:0.033762859856260326,t451:0.029172486323449282)100:0.029011418512700025,(t294:0.0027206339031963433,t325:0.001084117903976444)100:0.11188303299813171)100:0.03378704967060549,t13:0.14871987688563515)99:0.02020468791066357)68:0.0023735160255263336)100:0.023938052497163652)99:0.01434631237193819)62:0.00751774574616681)19:1.28060618246117E-6)75:0.0037749039628434825)99:0.01097836922577562)94:0.008739769423865673)83:0.007499766326436678)35:1.28060618246117E-6)100:0.06741310372226965);");
        //Tree b = Newick.getTreeFromString("((((t170:1.0,t469:1.0):1.0,((t49:1.0,(((t328:1.0,(t145:1.0,(t245:1.0,t463:1.0):1.0):1.0):1.0,((t10:1.0,t3:1.0):1.0,t478:1.0,t353:1.0):1.0):1.0,(t369:1.0,(t408:1.0,t90:1.0):1.0):1.0):1.0):1.0,(((t462:1.0,t65:1.0):1.0,t383:1.0):1.0,t480:1.0):1.0):1.0):1.0,((((t335:1.0,((t119:1.0,t62:1.0):1.0,t64:1.0):1.0):1.0,(((t443:1.0,t135:1.0):1.0,((t474:1.0,t71:1.0):1.0,t157:1.0):1.0):1.0,(t226:1.0,(t438:1.0,t299:1.0):1.0):1.0):1.0):1.0,t308:1.0,(t212:1.0,(t93:1.0,(t59:1.0,t253:1.0):1.0):1.0,(t97:1.0,t121:1.0):1.0):1.0,t204:1.0,(((t344:1.0,(t496:1.0,t45:1.0):1.0):1.0,((t206:1.0,t288:1.0):1.0,t310:1.0):1.0):1.0,(t293:1.0,((t89:1.0,t289:1.0):1.0,t120:1.0):1.0):1.0):1.0):1.0,(((t268:1.0,t422:1.0,((t456:1.0,(t7:1.0,(t281:1.0,t72:1.0):1.0):1.0):1.0,t29:1.0):1.0):1.0,((t439:1.0,((t113:1.0,((t83:1.0,t127:1.0):1.0,t260:1.0):1.0):1.0,t19:1.0):1.0):1.0,((t292:1.0,t118:1.0,((t354:1.0,t336:1.0):1.0,(t242:1.0,t265:1.0):1.0):1.0):1.0,(((t46:1.0,t101:1.0):1.0,t492:1.0):1.0,(t12:1.0,t150:1.0):1.0):1.0):1.0):1.0):1.0,(((((t133:1.0,t58:1.0):1.0,(t192:1.0,t305:1.0):1.0):1.0,(((t106:1.0,t284:1.0):1.0,t285:1.0):1.0,t66:1.0):1.0):1.0,((t218:1.0,(t31:1.0,t475:1.0):1.0):1.0,t217:1.0):1.0):1.0,t435:1.0):1.0):1.0,(t190:1.0,(t488:1.0,t358:1.0):1.0):1.0):1.0):1.0,(((t396:1.0,t464:1.0):1.0,t2:1.0):1.0,(((t67:1.0,t168:1.0):1.0,t85:1.0):1.0,t278:1.0):1.0):1.0);");
        //Tree c = hey.SCM(a, b);
        //Tree a = Newick.getTreeFromString("((t169:0.02056467440428987,t108:0.02563947678634368)100:0.06741310372226965,(((t495:0.10917012927375173,((t195:0.10340996876048182,t165:0.11249025586572521)40:1.28060618246117E-6,(t131:0.07252526964244901,((t4:0.06020329392131871,t312:0.038910241371599416)87:0.008360685022321818,t321:0.06830399985073354)99:0.01817592679073262)100:0.033484662747521245)88:0.012555393545572216)100:0.022777591692494933,t450:0.15388230193294133)100:0.02961663027317288,((t371:0.1389421237592064,(t92:0.1104898929326545,(t262:0.06916278290604252,t189:0.07459192428798872)100:0.03799810581845313)74:0.010270676287150152)100:0.03459439554786425,((t53:0.14110769660303102,(t181:0.11864673721728795,(t444:0.0671338216135925,(t261:0.0064592410178400585,t188:0.0041711215026039835)100:0.045561424248944334)100:0.10528041235376885)22:1.28060618246117E-6)100:0.03138328605910244,((t184:0.07901238266767743,(t414:0.08025856413488873,t144:0.09485297213022711)82:0.022352501216084276)100:0.07144367623540092,(((t47:0.056946757852635504,(t347:0.051978139057718226,(t481:0.04487642130752454,t235:0.05146445129010096)98:0.014032739588420958)98:0.014494878675988742)100:0.08167640798578357,((t290:0.09380134252475787,t202:0.048200537341168005)100:0.06780361910503069,(t421:0.1864277665012198,(t38:0.07738252450442691,(t341:0.09682860678743666,(t17:0.05137261558852251,t413:0.03917676869216877)100:0.07572430436185493)96:0.015898650714822025)100:0.04607997971513339)72:0.001213390885099741)96:0.01440099617743572)98:0.02337555299190737,(((((t225:0.006079313520494399,t103:0.008628640410399334)100:0.154833547526221,((t340:0.1953872336483595,(((t356:0.1262352768726709,t146:0.12662551532687943)98:0.044577805358935595,t483:0.14897544322315748)94:0.010395074741298025,((t161:0.1093188866266506,t221:0.08462512319398661)100:0.055228146719204174,(t26:0.07836240081104719,t349:0.07879123175734319)100:0.05695631440031426)97:0.01767013450317087)98:0.012403251964888425)100:0.013753862469104433,(((t156:0.05693835336392994,(t392:0.017564930888416885,(t231:0.00699556786714038,t296:0.003804812892347839)100:0.01330816082971064)100:0.02565141211010015)100:0.07626977834655616,(t440:0.03035186316395571,(t425:0.01785688347618177,(t79:1.28060618246117E-6,t252:1.28060618246117E-6)100:0.009681268247012622)100:0.02158456240997149)100:0.09821598003253676)100:0.05676905338552688,(((((t186:0.11495119124464204,t407:0.1363001988749887)83:0.010639182448428305,t14:0.13329635082098512)99:0.023044511958417092,t384:0.15166839397357673)100:0.020633790051151128,(((t279:0.08731762674954793,t96:0.0792598555900757)100:0.04944195696395667,(t368:0.1251460829029792,((((t378:0.022165305791404008,t315:0.029860920562994334)79:0.0066808351074927885,(t381:0.04021282019037454,t376:0.034465952754769796)30:0.0015123688117842761)100:0.04765112417502503,((t398:0.04681819603171684,t138:0.046006940287527734)39:0.0034283827064993,t34:0.04665607553569118)100:0.0355865494168686)100:0.030375845471925386,((t224:0.024170594849546218,t247:0.02016989692954509)100:0.0731182445972318,t18:0.07005229651658874)98:0.01594451607962281)98:0.010981939864435374)100:0.0272707950361778)100:0.021578419380139362,(t460:0.15057955272098647,(t269:0.08662750293115233,t136:0.07907697770944722)100:0.07255907188506434)68:0.004267348273175963)94:0.01227417876092108)60:0.0010631874147168621,(((t193:0.11505204903157051,(t267:0.12034264613322426,t302:0.13471494438483753)56:0.005794552889002644)97:0.016097234495596583,(t428:0.07352944842925313,(t228:0.03999016331613156,t37:0.03456088479610629)100:0.030977018385112177)100:0.06132770131142908)95:0.011245806528194643,((t187:0.11607085005577038,t180:0.08705894592470814)100:0.023783168035472528,((t452:0.028186089194060945,t395:0.026722208476295285)100:0.07552406732622392,t248:0.13356369306812604)99:0.023335799574215034)100:0.030969981322034917)82:0.005541446723073026)87:0.006962607165375047)80:0.0026723596896034356)100:0.011530490816767526)30:0.003596166440894441,(((t170:0.18139284735075084,((t191:0.12645728264204895,t462:0.0988045073549861)99:0.04041844494472375,((((t328:0.06372607114752471,t245:0.07907983855139955)99:0.03465159146308438,t3:0.1375945063734816)29:0.004297750628000487,(t369:0.1325504178166916,(t408:0.0714687051026849,t90:0.07968994192553958)100:0.03417270648204351)100:0.037517704704581295)88:0.014066514148964324,t49:0.1330166055152802)43:0.0044448638031386765)92:0.014767617445623856)44:0.009182903818733861,(((((t227:0.08205837435802442,(t200:0.05121219110636751,t303:0.07546493751308447)99:0.022650245772673973)100:0.07235110626290213,(t117:0.09244019804230474,(t401:0.07097925346726283,t137:0.06225843077175492)100:0.05777769244971311)98:0.022054134965792605)71:0.005851372077016756,(((t276:0.043126005218409834,(t222:0.009067033328298413,t362:0.011514153398918979)100:0.04084202671388816)100:0.05057720972285346,(t270:0.03876101139619697,(t380:0.003980966850629527,t390:0.0017858735142845048)100:0.0277014109914246)100:0.08047783772528755)100:0.05050445344424672,t327:0.11666750321285278)46:0.005585447068656795)53:0.004552864949622901,(t406:0.15021547063390955,(t500:0.03150488929538157,t367:0.017283020603765655)100:0.15541308435636889)35:0.012595479115761846)92:0.01615101409934024,((t223:0.13774925763357904,(t467:0.07492314708577083,t326:0.0639541841389407)100:0.05020278846663257)96:0.028996429932755537,t461:0.150342270299734)89:0.007427905043916359)100:0.03456257083898455)10:1.28060618246117E-6,(((t419:0.12630518623726733,(t490:0.0425279752398842,t81:0.032707038752805215)100:0.08293209737575868)97:0.03268151176585493,(t466:0.1276641988332817,(t399:0.13906554866472629,t151:0.14774998424640592)41:1.28060618246117E-6)100:0.03335503299234976)94:0.014702170122309361,((((t268:0.07081231268621989,(t29:0.05314239800413978,t281:0.0378578942268506)100:0.010971664115323243)100:0.07373254172377845,((t83:0.0947179463694813,t113:0.06973493760494509)100:0.06571402833079533,((t101:0.11088184214596145,((t265:0.09281739031849372,(t336:0.061506203315363946,t354:0.07210017487481564)99:0.029238095321665117)98:0.025762800116516854,t118:0.1014108732141534)87:0.014733752875598086)53:0.002598101346617261,t12:0.11772707761399336)100:0.0323683046613994)62:0.002191362089417505)100:0.019563835970112152,(t218:0.12374986683485793,(t58:0.11188623244772537,(t106:0.07217544088033027,t66:0.058766603855361914)97:0.020793085712610522)93:0.020136491923846662)100:0.0464878795764104)34:1.28060618246117E-6,(((((t121:0.12924788308414653,t93:0.15741892500820695)48:0.014209198457489983,((t474:0.041567922183912025,(t443:0.02734134286427507,(t438:0.01194149372227841,t299:0.006588604579050394)100:0.03066520418672206)41:0.0014785114631333037)100:0.09044274864750067,((t62:0.02759939919459967,t119:0.03474186855473828)80:1.28060618246117E-6,t64:0.04133201473118848)100:0.0978040610590435)100:0.041438421249138366)13:1.28060618246117E-6,t204:0.12200047921642883)55:0.0045724471639626875,(t288:0.1084120167506601,(t496:0.018079855489872124,t45:0.016222585899287073)100:0.08583826362153472)100:0.04679808117271949)45:0.0059589899835981374,((t464:0.12094100431651676,t67:0.10789050286291058)100:0.07991960516598831,(t124:0.07927095559501597,(t488:0.012723461585328824,t190:0.020826342144274763)100:0.06987076099305793)100:0.06284623802428504)19:1.28060618246117E-6)47:0.006729742226452831)79:0.014540405825929236)20:1.28060618246117E-6)26:0.0010383543954154085)10:1.28060618246117E-6,(t387:0.20538009218891223,((t182:0.10750773749970059,(t348:0.07271193853990027,(t198:0.06173016953396456,t431:0.04313362132159189)91:0.012908774316778362)100:0.024326757924139977)100:0.06603975551178011,((t346:0.07901530468419846,t24:0.13008253203494713)86:0.013901492546563685,t427:0.11177236945076131)100:0.04469989998477104)67:0.005127223770719831)92:0.015676675244401247)20:9.819960638786517E-4,((((t491:0.004057606692687111,t291:0.005742348191867697)100:0.1806042929454614,t497:0.2037315064451476)26:1.28060618246117E-6,(((t201:0.06754884943478977,t23:0.07858095462910024)100:0.06481873580614268,((t454:0.05054594094182899,t373:0.03784461690682863)100:0.09931538450474162,(((t179:0.08974545354222763,(t50:0.05910134374913718,(t423:0.009744594974440367,t389:1.28060618246117E-6)100:0.05014941655881632)100:0.046809609580083886)88:0.010118310247607436,(t84:0.05966438136457367,t420:0.05177841475604175)100:0.05436377093702334)84:0.006534449373480624,(t343:0.09001051086099993,t55:0.11119858867753188)88:0.01753588443638785)100:0.0314713909919055)94:0.00751249734848586)99:0.01839491344455796,(((t42:0.15337280855607727,t148:0.12722870273522469)86:0.009016327931657386,(((t256:0.09285029043063348,t183:0.07465982516101954)99:0.01990165479300553,t404:0.1197365819711296)70:0.0032061546513792675,((t274:0.006002912980422437,t36:0.011908877280136572)100:0.09285931717084228,(t259:0.058916801034139134,t44:0.05154962566903489)100:0.05722433334460059)69:0.007584791552409706)100:0.02899418744823094)100:0.024746638897693442,((t432:0.14104820660010747,((t48:0.08538504026682084,(t128:0.09161574533532613,(t143:0.0669272240471088,t309:0.06768246825124911)90:0.007612315227007921)100:0.04094044435166495)100:0.02182864670849406,(t499:0.12330326240925596,t22:0.1343574145058776)71:0.0016018876381818608)100:0.02296722933545503)100:0.021228651301429313,((t375:0.08707345632559439,((t403:0.0746048794502126,t139:0.0900721509934046)76:1.28060618246117E-6,((t229:0.018900720380834794,t88:0.01898603458577885)100:0.03168180697299456,t147:0.054028080272321914)100:0.03579908920016309)98:0.017671839004118048)100:0.04696848733192598,(t25:0.10413680555977896,(t171:0.060605593795599565,t174:0.07100976449969716)100:0.0360396489468767)100:0.03150014553669344)100:0.02302293124654936)100:0.01317639641055689)100:0.008535576038117845)74:0.001614968699715294)87:0.01095415641889886,((((t8:0.11598922228238877,((t411:0.03285691675243413,t263:0.0721851818170049)100:0.08939924099637697,t40:0.09792413001391818)81:0.011542560106631428)98:0.02161665278970565,((t155:0.07214886766246043,(t159:0.10041757737781204,t391:0.07184333387561968)18:0.0018813766339229303)51:0.0065035594315923356,t243:0.12039069066540481)100:0.03482018392251097)83:0.0053643758329177775,(((t360:0.10137098292243726,((t434:0.03908774950820112,t441:0.037457497470598164)100:0.09444134063157548,(t104:0.05378227422548194,(t453:0.051480242869899634,t337:0.03757857164379937)100:0.011829227901388807)100:0.08383084336080066)49:1.28060618246117E-6)100:0.03255679045732144,(t220:0.1274025581866953,((t366:0.09296725273237437,t210:0.08771438404139376)100:0.03679518869045666,t355:0.13203925959832258)65:0.0061965187400838916)94:0.0083008015081342)96:0.011323366875271359,(t426:0.09213943454916147,(t197:0.08561585098374117,t319:0.05696590785020593)90:1.28060618246117E-6)100:0.0780582882030906)76:0.005249149022060012)100:0.030256378332758276,(((t482:0.12345564313438066,(t329:0.091628458013025,t339:0.15007705248853567)86:0.016910723487421068)78:0.009652274533722175,((t194:0.1287896085573261,(t95:0.10332302961004684,(t433:0.07847880709080887,t257:0.03937381788978546)100:0.05762504147869876)91:0.013317630031816095)41:1.28060618246117E-6,t393:0.14374224381631429)94:0.011795111115484059)100:0.0265886198780697,((t98:0.009164086209246698,t485:0.00446672248485915)100:0.14945007105963737,((t470:0.1285659328361837,(t365:0.09915869660120957,(t277:0.05191713799030193,t436:0.04661760908780692)100:0.08381852382419772)92:0.017398827571112028)99:0.01867680026522861,(((t287:0.033762859856260326,t451:0.029172486323449282)100:0.029011418512700025,(t294:0.0027206339031963433,t325:0.001084117903976444)100:0.11188303299813171)100:0.03378704967060549,t13:0.14871987688563515)99:0.02020468791066357)68:0.0023735160255263336)100:0.023938052497163652)99:0.01434631237193819)62:0.00751774574616681)19:1.28060618246117E-6)75:0.0037749039628434825)99:0.01097836922577562)94:0.008739769423865673)83:0.007499766326436678)35:1.28060618246117E-6)100:0.06741310372226965);");
        //Tree b = Newick.getTreeFromString("((((t170:1.0,t469:1.0):1.0,((t49:1.0,(((t408:1.0,t90:1.0):1.0,t369:1.0):1.0,((t328:1.0,(t145:1.0,(t463:1.0,t245:1.0):1.0):1.0):1.0,((t3:1.0,t10:1.0):1.0,t478:1.0,t353:1.0):1.0):1.0):1.0):1.0,(((t65:1.0,t462:1.0):1.0,t383:1.0):1.0,t480:1.0):1.0):1.0):1.0,(((((t344:1.0,(t45:1.0,t496:1.0):1.0):1.0,((t288:1.0,t206:1.0):1.0,t310:1.0):1.0):1.0,(t293:1.0,((t89:1.0,t289:1.0):1.0,t120:1.0):1.0):1.0):1.0,t308:1.0,t204:1.0,((((t62:1.0,t119:1.0):1.0,t64:1.0):1.0,t335:1.0):1.0,((t226:1.0,(t299:1.0,t438:1.0):1.0):1.0,((t135:1.0,t443:1.0):1.0,(t157:1.0,(t474:1.0,t71:1.0):1.0):1.0):1.0):1.0):1.0,((t93:1.0,(t253:1.0,t59:1.0):1.0):1.0,t212:1.0,(t97:1.0,t121:1.0):1.0):1.0):1.0,(t190:1.0,(t488:1.0,t358:1.0):1.0):1.0,((((((((t83:1.0,t127:1.0):1.0,t260:1.0):1.0,t113:1.0):1.0,t19:1.0):1.0,t439:1.0):1.0,((t292:1.0,((t354:1.0,t336:1.0):1.0,(t242:1.0,t265:1.0):1.0):1.0,t118:1.0):1.0,((t150:1.0,t12:1.0):1.0,((t101:1.0,t46:1.0):1.0,t492:1.0):1.0):1.0):1.0):1.0,(t422:1.0,t268:1.0,((t456:1.0,(t7:1.0,(t72:1.0,t281:1.0):1.0):1.0):1.0,t29:1.0):1.0):1.0):1.0,((((t66:1.0,(t285:1.0,(t284:1.0,t106:1.0):1.0):1.0):1.0,((t192:1.0,t305:1.0):1.0,(t133:1.0,t58:1.0):1.0):1.0):1.0,(t217:1.0,((t475:1.0,t31:1.0):1.0,t218:1.0):1.0):1.0):1.0,t435:1.0):1.0):1.0):1.0):1.0,(((t396:1.0,t464:1.0):1.0,t2:1.0):1.0,(((t67:1.0,t168:1.0):1.0,t85:1.0):1.0,t278:1.0):1.0):1.0);");
        //Tree c = hey.SCM(a, b);

        //hey.testCuttingAndInserting();
        //hey.testCompareTreeStructure();




        //hey.testStrictConsensus();

        /*Tree test = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        TreeNode a = test.findLeastCommonAncestor(test.getVertex("a"), test.getVertex("c"));
        System.out.println("Indegree vom LCA von a und c ist "+a.indegree());
        System.out.println("Degree vom LCA von a und c ist "+a.degree());
        */



        /*test.removeVertex(test.getVertex("a"));
        for (TreeNode x : test.vertices()){
            if (x.degree() == 2 && x!=test.getRoot()){
                for (TreeNode y : x.children()){
                    test.addEdge(x.getParent(),y);
                }
                test.removeVertex(x);
            }
        }*/

        /*TreeNode b = test.getVertex("b");
        TreeNode d = test.getVertex("d");
        TreeNode e = test.getVertex("e");
        List<TreeNode> tokeep = new ArrayList<TreeNode>();
        tokeep.add(b);
        tokeep.add(d);
        tokeep.add(e);
        test = hey.CutTree(test, tokeep);
        String print = Newick.getStringFromTree(test);
        System.out.println(print);*/

        //hey.testCutTree();




    }

}
