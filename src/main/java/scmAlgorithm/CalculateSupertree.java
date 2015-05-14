package scmAlgorithm;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;

import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class CalculateSupertree {

    String type = "";

    //public enum type{}

    CalculateSupertree(String type){
        if (type.equalsIgnoreCase("overlap")) this.type = "overlap";
        else if (type.equalsIgnoreCase("resolution")) this.type = "resolution";
    }

    CalculateSupertree(int type){
        if (type == 0) this.type = "overlap";
        else if (type == 1) this.type = "resolution";
        //else System.err.println()
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public Tree getSupertree (List<Tree> input) {
        Scorer sc;
        if (getType() == "overlap"){
            sc = new OverlapScorer();
        }
        else {
            sc = new ResolutionScorer();
        }

        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = sc.getTreeswithbiggestScoreandSCM(output);

            one = save.get(0);
            two = save.get(1);
            three = save.get(2);
            System.out.println("Got one SCM: " + Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }


}
