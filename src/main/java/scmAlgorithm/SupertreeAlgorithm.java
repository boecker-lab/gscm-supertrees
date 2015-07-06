package scmAlgorithm;

import epos.model.tree.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fleisch on 16.06.15.
 */
public interface SupertreeAlgorithm {
    //todo another package
    public Tree getSupertree();

    default List<Tree> getSupertrees() {
        List<Tree> t = new ArrayList<>(1);
        t.add(getSupertree());
        return t;
    }
}
