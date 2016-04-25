package scm.algorithm.treeSelector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fleisch on 22.10.15.
 */
@FunctionalInterface
public interface TreeSelectorFactory<T extends TreeSelector> {
    Set<TreeSelector> selectors =  Collections.synchronizedSet(new HashSet<>());

    T getNewSelectorInstance();

    static void shutdown(TreeSelector... toRemove){
        for (TreeSelector selector : toRemove) {
            selector.shutdown();
            selectors.remove(selector);
        }
    }

    static void shutdownAll(){
        selectors.forEach(TreeSelector::shutdown);
        selectors.clear();
    }
}
