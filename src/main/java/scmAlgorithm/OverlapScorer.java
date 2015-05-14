package scmAlgorithm;

import epos.model.tree.Tree;
import treeUtils.TreeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class OverlapScorer extends Scorer {

    public double getScore(Tree one, Tree two){
        return TreeUtils.getOverLappingNodes(one, two).size();
    }

    public List<Tree> getTreeswithbiggestScoreandSCM(List<Tree> input){
        double cursize;
        double maxsize = 0;
        int mem1 = 0, mem2 = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    cursize = getScore(iter, cur);
                    if (cursize > maxsize){
                        maxsize = cursize;
                        mem1 = input.indexOf(iter);
                        mem2 = input.indexOf(cur);
                    }
                }
            }
        }
        SCM s = new SCM();
        if (maxsize == 0 && input.size()>=2){
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(s.getSCM(input.get(mem1), input.get(mem2)));
            System.err.println("In der Liste gibt es keine Bäume mit Overlap");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(s.getSCM(input.get(mem1), input.get(mem2)));
        }
        return output;
    }

}
