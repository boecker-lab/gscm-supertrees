package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import parallel.DefaultIterationCallable;
import parallel.IterationCallableFactory;
import parallel.ParallelUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class DefaultGreedyTreeSelector<M extends Map<Tree, S>, S extends Collection<TreePair>> implements TreeSelector {
    private int threads = 1;
    private ExecutorService executor;
    private TreePairScoringCallableFactory factory;


    private TreeScorer scorer;
    private ConsensusMethod method;

    private Tree[] inputTrees;
    M treeToPairs;

    DefaultGreedyTreeSelector(ConsensusMethod method) {
        this.method = method;
    }


    DefaultGreedyTreeSelector(TreeScorer scorer, ConsensusMethod method) {
        this(method);
        this.scorer = scorer;

    }

    DefaultGreedyTreeSelector(TreeScorer scorer, ConsensusMethod method, Tree... trees) {
        this(scorer, method);
        this.inputTrees = trees;
    }


    DefaultGreedyTreeSelector() {
        this(ConsensusMethod.STRICT);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer) {
        this(scorer, ConsensusMethod.STRICT);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        this(scorer, ConsensusMethod.STRICT, trees);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer, Collection<Tree> treeCollection) {
        this(scorer, treeCollection.toArray(new Tree[treeCollection.size()]));
    }


    // only one time called
    @Override
    public void init() {
        Set<Tree> ts = new HashSet<>(inputTrees.length);
        Collections.addAll(ts, inputTrees);
        scorer.clearCache(ts);

        treeToPairs = getTreeToPairsInstance(inputTrees.length - 1);
        for (Tree tree : inputTrees) {
            treeToPairs.put(tree, getTreePairCollectionInstance(inputTrees.length - 1));
        }

        long time = System.currentTimeMillis();

        if (threads > 1) {
            try {
                createPairsParallel(inputTrees);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            createPairsSequencial(inputTrees);
        }


        double runtime = (double) (System.currentTimeMillis() - time) / 1000d;
        System.out.println("...Done in: " + runtime + "s");
    }

    @Override
    public void setInputTrees(Tree... inputTrees) {
        this.inputTrees = inputTrees;
//        init();//todo her or not
    }

    private void createPairsSequencial(Tree[] trees) {
        for (int i = 0; i < trees.length - 1; i++) {
            Tree t1 = trees[i];
            for (int j = i + 1; j < trees.length; j++) {
                Tree t2 = trees[j];
                final TreePair pair = new TreePair(t1, t2, scorer, newConsensusCalculatorInstance(method));
                if (pair != null && pair.score > Double.NEGATIVE_INFINITY) {
                    addPair(t1, pair);
                    addPair(t2, pair);
                }
            }
        }
    }

    private void createPairsParallel(Tree[] trees) throws ExecutionException, InterruptedException {
        final int size = (trees.length * (trees.length - 1)) / 2;
        final List<TreePair> pairsToAdd = new ArrayList<>(size);

        //sequential pair creation
        for (int i = 0; i < trees.length - 1; i++) {
            Tree t1 = trees[i];
            for (int j = i + 1; j < trees.length; j++) {
                Tree t2 = trees[j];
                pairsToAdd.add(
                        new TreePair(t1, t2, newConsensusCalculatorInstance(method)));
            }
        }
        // parallel scoring --> we have to score the pairs before adding them into the SORTED data structure
        scoreAndAddPairsParallel(pairsToAdd);
    }

    private void scoreAndAddPairsParallel(final List<TreePair> pairsToAdd) throws ExecutionException, InterruptedException {
        //parralel scoring
        if (executor == null)
            executor = Executors.newFixedThreadPool(threads);
        if (factory == null)
            factory = new TreePairScoringCallableFactory();
        List<Future<List<TreePair>>> submittedJobs = ParallelUtils.parallelBucketForEach(executor, factory, pairsToAdd, threads);

        //sequential adding
        for (Future<List<TreePair>> futures : submittedJobs) {
            for (TreePair pair : futures.get()) {
                if (pair != null && pair.score > Double.NEGATIVE_INFINITY) {
                    addPair(pair.t1, pair);
                    addPair(pair.t2, pair);
                }
            }
        }
    }

    //polls treepair from data structure
    public TreePair pollTreePair() {
        if (treeToPairs.size() > 1) {
            TreePair tp = getMax();
            removeTreePair(tp);
            return tp;
        }
        return null;
    }

    // adds tree and its paires to the data structure (O(2nlog(n)))
    public boolean addTree(final Tree tree) {
        if (!treeToPairs.isEmpty()) {
            try {
                if (threads > 1) {
                    addTreeParallel(tree);
                } else {
                    addTreeSequencial(tree);
                }

                if (treeToPairs.get(tree).isEmpty())
                    throw new IllegalArgumentException("Input tree have insufficient Overlap to calculate a supertree");

                return true;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void addTreeParallel(final Tree tree) throws ExecutionException, InterruptedException {
        //iterate over trees (O(n)) to add to new list and refresh old entries
        List<TreePair> pairsToAdd = new ArrayList<>(treeToPairs.size());
        for (Tree old : treeToPairs.keySet()) {
            pairsToAdd.add(
                    new TreePair(tree, old, newConsensusCalculatorInstance(method)));
        }

        //add new to map O(1)
        treeToPairs.put(tree, getTreePairCollectionInstance(treeToPairs.size()));

        //parralel scoring O(n))
        scoreAndAddPairsParallel(pairsToAdd);
    }

    private void addTreeSequencial(final Tree tree) {
        //iterate over trees (O(n)) to add to new list and refresh old entries
        S nuTreePairs = getTreePairCollectionInstance(treeToPairs.size());
        for (Tree old : treeToPairs.keySet()) {
            TreePair pair = new TreePair(tree, old, scorer, newConsensusCalculatorInstance(method));
            nuTreePairs.add(pair);
            addPair(old, pair);
        }
        //add new to map O(1)
        treeToPairs.put(tree, nuTreePairs);
    }


    //(O(2nlog(n))
    private void removeTreePair(TreePair pair) {
        Tree t1 = pair.t1; //O(1)
        Tree t2 = pair.t2; //O(1)
        removePair(t1, pair);
        removePair(t2, pair);
        S s1 = treeToPairs.remove(t1); //O(1)
        S s2 = treeToPairs.remove(t2); //O(1)
//            s1.remove(pair);
//            s2.remove(pair);
        //O(n)
        for (TreePair treePair : s1) {
            removePair(treePair.getPartner(t1), treePair);//O(log(n))
        }
        //O(n)
        for (TreePair treePair : s2) {
            removePair(treePair.getPartner(t2), treePair);//O(log(n))
        }
    }

    @Override
    public TreeScorer getScorer() {
        return scorer;
    }

    @Override
    public void setScorer(TreeScorer scorer) {
        this.scorer = scorer;
    }

    public ConsensusMethod getMethod() {
        return method;
    }

    @Override
    public void setThreads(int threads) {
        this.threads = threads;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }


    boolean addPair(Tree t, TreePair p) {
        return treeToPairs.get(t).add(p);
    }

    boolean removePair(Tree t, TreePair p) {
        return treeToPairs.get(t).remove(p);
    }

    @Override
    public int getNumberOfTrees() {
        return inputTrees.length;
    }

    //abstract classes
    abstract M getTreeToPairsInstance(int size);

    abstract S getTreePairCollectionInstance(int size);

    abstract TreePair getMax();


    private class TreePairScoringCallable extends DefaultIterationCallable<TreePair, TreePair> {
        protected TreePairScoringCallable(List<TreePair> jobs) {
            super(jobs);
        }
        @Override
        public TreePair doJob(TreePair pair) {
            return pair.calculateScore(scorer);
        }

    }

    private class TreePairScoringCallableFactory implements IterationCallableFactory<TreePairScoringCallable, TreePair> {
        @Override
        public TreePairScoringCallable newIterationCallable(List<TreePair> list) {
            return new TreePairScoringCallable(list);
        }
    }
}

