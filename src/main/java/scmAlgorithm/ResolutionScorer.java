package scmAlgorithm;

import epos.model.tree.Tree;
import epos.model.tree.treetools.TreeUtilsBasic;
import treeUtils.TreeUtils;
import scmAlgorithm.SCM;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class ResolutionScorer extends Scorer{

    Tree scm = new Tree();

    public double getScore (Tree one, Tree two){
        SCM s = new SCM();
        scm = s.getSCM(one, two);
        double resolution = TreeUtilsBasic.calculateTreeResolution(scm.getNumTaxa(), scm.vertexCount());
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
                        //TODO look up the definition of resolution
                        //resolution = scm.vertexCount() / (scm.getLeaves().length-3);
                        //TODO test resolution
                        //resolution = (scm.vertexCount() - 1 - scm.getLeaves().length) / (scm.getLeaves().length - 3);
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
            System.err.println("List contains no trees with a score bigger than 0");
        }
        else if (input.size()<2){
            System.err.println("No supertree. Less than two trees in the input");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(maxscm);
        }
        return output;
    }

}
