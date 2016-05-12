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
package phylo.tree.algorithm.gscm.treeSelector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 22.10.15.
 */

/**
 * Functional Factory interface that can build every class
 * that extends from {@link TreeSelector}
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
@FunctionalInterface
public interface TreeSelectorFactory<T extends TreeSelector> {
    static Set<TreeSelector> selectors =  Collections.synchronizedSet(new HashSet<>());

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
