package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.treetools.SingleTaxonReduction;
import epos.model.tree.treetools.TreeUtilsBasic;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TIntHashSet;
import scmAlgorithm.AbstractSCMAlgorithm;
import scmAlgorithm.GreedySCMAlgorithm;
import scmAlgorithm.treeScorer.AbstractOverlapScorer;
import scmAlgorithm.treeScorer.TreeScorer;

import java.util.*;

/**
 * Created by fleisch on 05.02.15.
 */
public abstract class GreedyTreeSelector<M extends Map<Tree, S>, S extends Collection<TreePair>> implements TreeSelector {
    protected final M treeToPairs;
    protected final TreeScorer scorer;

    public GreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        this.scorer = scorer;
        treeToPairs = getTreeToPairsInstance(trees.length - 1);
        init(trees);
    }

    public GreedyTreeSelector(TreeScorer scorer, Collection<Tree> treeCollection) {
        this(scorer, treeCollection.toArray(new Tree[treeCollection.size()]));
    }


    // only one time called
    private void init(Tree[] trees) {
        for (Tree tree : trees) {
            treeToPairs.put(tree, getTreePairCollectionInstance(trees.length - 1));
        }

        for (int i = 0; i < trees.length - 1; i++) {
            Tree first = trees[i];
            for (int j = i + 1; j < trees.length; j++) {
                Tree second = trees[j];
                TreePair pair = scorer.getScoredTreePair(first, second);
                if (pair != null) {
                    treeToPairs.get(first).add(pair);
                    treeToPairs.get(second).add(pair);
                }
            }
        }
    }

    //polls treepair from data structure
    public TreePair pollTreePair() {
        if (treeToPairs.size() > 1) {
            TreePair tp = findGlobalMax();
            removeTreePair(tp);
            return tp;
        }
        return null;
    }

    ;

    // adds tree and its paires to the data structure (O(2nlog(n)))
    public boolean addTree(Tree tree) {
        if (treeToPairs.isEmpty())
            return false;
        S pairsToAdd = getTreePairCollectionInstance(treeToPairs.size());

        //iterate over trees (O(n)) to add to new list and refresh old entries
        for (Map.Entry<Tree, S> entry : treeToPairs.entrySet()) {
            Tree old = entry.getKey();
            TreePair pair = scorer.getScoredTreePair(tree, old);

            if (pair != null) { //null pair have no overlap
                pairsToAdd.add(pair);//add to own list O(log(n))
                entry.getValue().add(pair); //resfresh O(log(n))
            }
        }
        //add new to map O(1)
        treeToPairs.put(tree, pairsToAdd);
        return true;
    }


    //(O(2nlog(n))
    private void removeTreePair(TreePair pair) {
        Tree t1 = pair.t1; //O(1)
        Tree t2 = pair.t2; //O(1)
        S s1 = treeToPairs.remove(t1); //O(1)
        S s2 = treeToPairs.remove(t2); //O(1)
        s1.remove(pair);
        s2.remove(pair);
        //O(n)
        for (TreePair treePair : s1) {
            treeToPairs.get(treePair.getPartner(t1))
                    .remove(treePair); //O(log(n))
        }
        //O(n)
        for (TreePair treePair : s2) {
            treeToPairs.get(treePair.getPartner(t2))
                    .remove(treePair); //O(log(n))
        }
    }

    protected abstract M getTreeToPairsInstance(int size);

    protected abstract S getTreePairCollectionInstance(int size);

    protected abstract TreePair getMax(S treePairs);

    protected abstract TreePair findGlobalMax();


//#############################################################################
//########################## IMPLEMENTATIONS ##################################
//#############################################################################


    public static class GTSMapPQ extends GreedyTreeSelector<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {

        public GTSMapPQ(TreeScorer scorer, Tree... trees) {
            super(scorer, trees);
        }

        @Override
        protected THashMap<Tree, PriorityQueue<TreePair>> getTreeToPairsInstance(int size) {
            return new THashMap<>(size);
        }


        @Override
        protected PriorityQueue<TreePair> getTreePairCollectionInstance(int size) {
            return new PriorityQueue<>(size);
        }

        @Override
        protected TreePair getMax(PriorityQueue<TreePair> treePairs) {
            return treePairs.peek();
        }

        //find best pair (O(nlog(n)))
        @Override
        protected TreePair findGlobalMax() {
            TreePair best = new TreePair(null, null, Double.NEGATIVE_INFINITY);
            //iteration in O(n)
            for (PriorityQueue<TreePair> treePairs : treeToPairs.values()) {
                TreePair max = getMax(treePairs); //get local best in O(log(n))
                if (max.compareTo(best) < 0)
                    best = max;
            }
            return best;
        }


    }


    public static void main(String[] args) {
        String prefix = "name1_name2_name3_name4_name5_name6_name7_name8_name9_";
        int treenum = 100;
        Tree[] tree1000 = new Tree[treenum];

        Random r = new Random();

        int[] sizes = r.ints(treenum, 40, 80).toArray();

        for (int i = 0; i < sizes.length; i++) {
            tree1000[i] = new Tree();
            Tree t = tree1000[i];
            TreeNode root = new TreeNode();
            t.addVertex(root);
            t.setRoot(root);
            System.out.println("tree: " + i);
            int[] taxa = new TIntHashSet(r.ints(sizes[i], 0, 100).toArray()).toArray();
            System.out.println("num taxa=" + taxa.length);
            for (int taxon : taxa) {
                TreeNode leaf = new TreeNode(prefix + taxon);
                t.addVertex(leaf);
                t.addEdge(root, leaf);
            }
        }
        Tree[] input =  cloneTrees(tree1000);


        long t = System.currentTimeMillis();
        GreedyTreeSelector g100 = new GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), input);
        double tInit = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("getMaxINIT: " + tInit + "s");
        TreePair p;
        while ((p = g100.pollTreePair()) != null) {
            g100.addTree(createTree(p));
        }
        double time = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("getMax: " + time + "s");
        System.out.println();
        System.out.println();

        input =  cloneTrees(tree1000);
        t = System.currentTimeMillis();
        g100 = new GTSMapPQ(new AbstractOverlapScorer.OverlapScorer(), input);
        tInit = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_JAVA_OWN_INIT: " + tInit + "s");
        while ((p = g100.pollTreePair()) != null) {
//            Tree tr = createTree(p);
//            g100.addTree(tr);
//            p.pruneToCommonLeafes();

            Tree tr = createTreeCommon(p);
            Tree trFull = createTree(p);
            p.pruneToCommonLeafes();
            System.out.println("t1 == t2: " + (p.t1.getNumTaxa() == p.t2.getNumTaxa()));
            System.out.println("t1/t2 == commonTree: " + (p.t1.getNumTaxa() == tr.getNumTaxa()));
            p.reinsertSingleTaxa(tr);
            System.out.println("fullTree == commonTree: " + (trFull.getNumTaxa() == tr.getNumTaxa()));
        }
        time = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_JAVA_OWN: " + time + "s");
        System.out.println();
        System.out.println();

        input =  cloneTrees(tree1000);
        t = System.currentTimeMillis();
        g100 = new GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), input);
        tInit = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_Trove_OWN_INIT: " + tInit + "s");
        while ((p = g100.pollTreePair()) != null) {
//            Tree tr = createTree(p);
//            g100.addTree(tr);
//            p.pruneToCommonLeafes();
            Tree tr = createTreeCommon(p);
            p.pruneToCommonLeafes();
            p.reinsertSingleTaxa(tr);
        }
        time = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_Trove__OWN: " + time + "s");
        System.out.println();
        System.out.println();

        input =  cloneTrees(tree1000);
        t = System.currentTimeMillis();
        g100 = new GTSMapPQ(new AbstractOverlapScorer.OverlapScorer(), input);
        tInit = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_JAVA_STR_INIT: " + tInit + "s");
        AbstractSCMAlgorithm algo =  new GreedySCMAlgorithm(g100);
        Tree st = algo.getSupertree();

        /*while ((p = g100.pollTreePair()) != null) {
//            Tree tr = createTree(p);
//            g100.addTree(tr);
//            p.pruneToCommonLeafes(new SingleTaxonReduction(null,false));
            Tree tr = createTreeCommon(p);
            SingleTaxonReduction str =  new SingleTaxonReduction(null,false);
            p.pruneToCommonLeafes(str);
            str.unmodify(Arrays.asList(tr));
        }*/
        time = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_JAVA_STR: " + time + "s");
        System.out.println();
        System.out.println();


        input =  cloneTrees(tree1000);
        t = System.currentTimeMillis();
        g100 = new GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), input);
        tInit = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_Trove_STR_INIT: " + tInit + "s");
        while ((p = g100.pollTreePair()) != null) {
//            Tree tr = createTree(p);
//            g100.addTree(tr);
//            p.pruneToCommonLeafes(new SingleTaxonReduction(null,false));
            Tree tr = createTreeCommon(p);
            SingleTaxonReduction str =  new SingleTaxonReduction(null,false);
            p.pruneToCommonLeafes(str);
            str.unmodify(Arrays.asList(tr));
        }
        time = (double) (System.currentTimeMillis() - t) / 1000d;
        System.out.println("gwtMaxPQ_Trove_STR: " + time + "s");
        System.out.println();
        System.out.println();
    }

    private static Tree createTree(TreePair p) {
        Tree tree = new Tree();
        TreeNode node = new TreeNode();
        tree.addVertex(node);
        tree.setRoot(node);
        Set<String> set = new HashSet<>();
        for (TreeNode treeNode : p.t1.getLeaves()) {
            set.add(treeNode.getLabel());
        }
        for (TreeNode treeNode : p.t2.getLeaves()) {
            set.add(treeNode.getLabel());
        }
        for (String s : set) {
            TreeNode leaf = new TreeNode(s);
            tree.addVertex(leaf);
            tree.addEdge(node, leaf);
        }
        return tree;
    }

    private static Tree createTreeCommon(TreePair p) {
        Tree tree = new Tree();
        TreeNode node = new TreeNode();
        tree.addVertex(node);
        tree.setRoot(node);

        for (String s : p.getCommonLeafes()) {
            TreeNode leaf = new TreeNode(s);
            tree.addVertex(leaf);
            tree.addEdge(node, leaf);
        }
        return tree;
    }

    private static Tree[] cloneTrees(Tree[] tree1000){
        Tree[] input =  new Tree[tree1000.length];
        for (int i = 0; i < tree1000.length; i++) {
            input[i] = tree1000[i].cloneTree();

        }
        return input;
    }
}
