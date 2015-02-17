package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;

/**
 * Created by fleisch on 06.02.15.
 */
public interface TreeSelector {

    public TreePair pollTreePair();
    public boolean addTree(Tree tree);

}
