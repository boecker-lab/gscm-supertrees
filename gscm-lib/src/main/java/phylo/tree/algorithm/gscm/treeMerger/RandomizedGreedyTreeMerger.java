/*
 * GSCM-Project
 * Copyright (C)  2016. Chair of Bioinformatics, Friedrich-Schilller University Jena.
 *
 * This file is part of the GSCM-Project.
 *
 * The GSCM-Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The GSCM-Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GSCM-Project.  If not, see <http://www.gnu.org/licenses/>;.
 *
 */
package phylo.tree.algorithm.gscm.treeMerger;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 23.03.15.
 */

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import phylo.tree.model.Tree;

import java.util.Random;

/**
 * Randomized implementation version of a greedy tree merger
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
public class RandomizedGreedyTreeMerger extends MapBasedGreedyTreeMerger<THashMap<Tree, THashSet<TreePair>>, THashSet<TreePair>> {
    public static final TreeMergerFactory<RandomizedGreedyTreeMerger> FACTORY =  () -> {
        RandomizedGreedyTreeMerger s =  new RandomizedGreedyTreeMerger();
        TreeMergerFactory.selectors.add(s);
        return s;
    };

    private static final Random RAND = new Random();

    private double sumOfScores = 0d;
    private THashSet<TreePair> pairs;

    private double[] indices;
    private TreePair[] pairToIndex;


    private RandomizedGreedyTreeMerger() {}

    @Override
    protected TreePair getMax() {
        if (treeToPairs.isEmpty())
            return TreePair.MIN_VALUE;
        return peekRandomPair();
    }

    @Override
    THashMap<Tree, THashSet<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<>(size);
    }

    @Override
    THashSet<TreePair> getTreePairCollectionInstance(int size) {
        return new THashSet<>(size);
    }


    @Override
    protected void addTreePair(TreePair p) {
        super.addTreePair(p);

        if (pairs.add(p)) {
            sumOfScores += p.score;
            clearCache();
        }
    }


    @Override
    void removePair(Tree tree,TreePair pair) {
        super.removePair(tree, pair);
        removePairFromRandomStructure(pair);
    }

    private void removePairFromRandomStructure(TreePair pair) {
        if (pairs.remove(pair)) {
            sumOfScores -= pair.score;
            clearCache();
        }
    }


    private void clearCache() {
        indices = null;
        pairToIndex = null;
    }


    private void initRandomStructures(){
        indices = new double[pairs.size()];
        pairToIndex = new TreePair[pairs.size()]; //todo, use index instead of this ugly set thingy
        int index = 0;
        double pre = 0d;
        for (TreePair pair : pairs) {
            pairToIndex[index] = pair;
            //this is for compatibility with negative scores
            if (sumOfScores > 0d) {
                pre += pair.score / sumOfScores;
            }else {
                pre += 1d - (pair.score / sumOfScores);
            }
            indices[index] = pre;
            index++;
        }
    }

    private TreePair peekRandomPair() {
        if (indices == null || pairToIndex == null){
            initRandomStructures();
        }
        double r = RAND.nextDouble();
        int start = 0;
        int end = indices.length - 1;

        TreePair pair = pairToIndex[0];

        while (start != end) {

            int index = start + (((end - start)) / 2);

            if (r > indices[index]) {
                start = index + 1;
                pair = pairToIndex[start];
            } else {
                end = index;
                pair = pairToIndex[end];
            }
        }

        return pair;
    }

    @Override
    public void setInputTrees(Tree... inputTrees) {
        super.setInputTrees(inputTrees);
        pairs = new THashSet<>(inputTrees.length * (inputTrees.length-1)/2);
    }
}
