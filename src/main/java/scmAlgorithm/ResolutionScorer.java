package scmAlgorithm;

import phyloTree.model.tree.Tree;
import treeUtils.TreeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class ResolutionScorer extends Scorer{

    public static final boolean debug = false;

    Tree scm = new Tree();

    public double getScore (Tree one, Tree two){

        SCM s = new SCM();
        scm = s.getSCM(one, two, false);
        if (debug){
            List<String> check = TreeUtils.StringListMultipleElements(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(scm.getLeaves())));
            if (!check.isEmpty()){
                System.out.println("tree contains the following leaves more than once: "+check);
            }
        }
        double resolution = phyloTree.model.tree.TreeUtils.calculateTreeResolution(scm.getNumTaxa(), scm.vertexCount());
        //return normalized resolution
        return resolution / scm.getLeaves().length;
    }

    public List<Tree> getTreeswithbiggestScoreandSCM(List<Tree> input){
        double curscore;
        double resolution;
        Tree maxscm = new Tree();
        double maxscore = 0;
        int mem1 = 0, mem2 = 0;
        int a, b, c = 0;
        List<Tree> output = new ArrayList<Tree>();
        SCM strict = new SCM();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    if (TreeUtils.getOverLappingNodes(iter, cur).size()==0);
                    else {
                        curscore = getScore(iter, cur);
                        if (curscore > maxscore) {
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
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(maxscm);
            System.err.println("List doesn't contain any trees that can be merged into an SCM tree");
        }
        else if (input.size()<2){
            System.err.println("Less than two trees in the input");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(maxscm);
        }
        return output;
    }

}
