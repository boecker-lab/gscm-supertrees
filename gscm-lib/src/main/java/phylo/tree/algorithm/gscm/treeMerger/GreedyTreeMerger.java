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

import gnu.trove.map.hash.THashMap;
import phylo.tree.model.tree.Tree;

import java.util.PriorityQueue;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 14.10.15.
 */

/**
 * Simple Greedy Strict consensus merger implementation
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
public class GreedyTreeMerger extends MapBasedGreedyTreeMerger<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {
    public static final TreeMergerFactory<GreedyTreeMerger> FACTORY = () -> {
        GreedyTreeMerger s =  new GreedyTreeMerger();
        TreeMergerFactory.selectors.add(s);
        return s;
    };
    private GreedyTreeMerger() {}

    @Override
    THashMap<Tree, PriorityQueue<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<>(size);
    }

    @Override
    PriorityQueue<TreePair> getTreePairCollectionInstance(int size) {
        return new PriorityQueue<>(size);
    }

    //find best pair (O(nlog(n)))
    @Override
    protected TreePair getMax() {
        TreePair best = TreePair.MIN_VALUE;
        //iteration in O(n)
        for (PriorityQueue<TreePair> treePairs : treeToPairs.values()) {
            TreePair max = treePairs.peek(); //get local best in O(log(n))
            if (max != null && max.compareTo(best) < 0) //Attention the comparator is reverse!
                best = max;
        }
        return best;
    }
}