package gscm.algorithm.treeSelector;
/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com)
 * as part of the gscm
 * 27.04.16.
 */

import gnu.trove.map.hash.THashMap;
import phyloTree.model.tree.Tree;
import utils.parallel.DefaultIterationCallable;
import utils.parallel.ParallelUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 */
public class UpperBoundGreedyTreeSelector<T extends TreeScorerUpperBound> extends MapBasedGreedyTreeSelector<T,THashMap<Tree, List<TreePair>>, List<TreePair>> {
    public static final TreeSelectorFactory<UpperBoundGreedyTreeSelector> FACTORY = () -> {
        UpperBoundGreedyTreeSelector s = new UpperBoundGreedyTreeSelector();
        TreeSelectorFactory.selectors.add(s);
        return s;
    };

    private UpperBoundGreedyTreeSelector() {
    }

    @Override
    THashMap<Tree, List<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<>(size);
    }

    @Override
    List<TreePair> getTreePairCollectionInstance(int size) {
        return new ArrayList<>(size);
    }

    @Override
    protected TreePair initTreePair(TreePair pair) {
        return pair.calculateUpperBound(scorer);
    }

    //find best pair (O(nlog(n)))
    @Override
    protected TreePair getMax() {
        TreePair best = TreePair.MIN_VALUE;
//        TreePair startGuy = TreePair.MIN_VALUE;

        //iteration in O(n)
        for (List<TreePair > treePairs : treeToPairs.values()) {
            if (!treePairs.isEmpty()) {
                treePairs.sort((o1, o2) -> Double.compare(o2.getUpperBound(), o1.getUpperBound()));
                TreePair current = treePairs.get(0);
                if (current.getUpperBound() > best.getUpperBound())
                    best = current;
            }
        }

        if (!best.isScored())
            best.calculateScore(scorer);

        for (List<TreePair> treePairs : treeToPairs.values()) {
            final double toBeat = best.getScore();

            if (!treePairs.isEmpty() && best.getScore() < treePairs.get(0).getUpperBound()) {

                treePairs.sort((o1, o2) -> {
                    double s1;
                    if (o1.isScored())
                        s1 = o1.getScore();
                    else
                    if (o1.getUpperBound() > toBeat)
                        s1 = o1.calculateScore(scorer).getScore();
                    else
                        s1 = Double.NEGATIVE_INFINITY;
                    double s2;
                    if (o2.isScored())
                        s2 = o2.getScore();
                    else {
                        if (o2.getUpperBound() > toBeat)
                            s2 = o2.calculateScore(scorer).getScore();
                        else
                            s2 = Double.NEGATIVE_INFINITY;
                    }
                    return Double.compare(s2, s1);
                });

                TreePair max = treePairs.get(0);
                if (max.isScored() && max.compareTo(best) < 0) //Attention the comparator is reverse!
                    best = max;
            }
        }
        return best;
    }
}