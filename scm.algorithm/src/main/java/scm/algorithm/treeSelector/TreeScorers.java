package scm.algorithm.treeSelector;

/**
 * Created by fleisch on 24.11.15.
 */

import static scm.algorithm.treeSelector.TreeScorer.*;

public class TreeScorers {
    public enum ScorerType {
        UNIQUE_TAXA,
        UNIQUE_TAXA_ORIG,
        OVERLAP,
        OVERLAP_ORIG,
        CLADE_NUMBER,
        RESOLUTION,
        COLLISION_SUBTREES,
        COLLISION_POINT,
        UNIQUE_CLADE_NUMBER,
        UNIQUE_CLADE_RATE,
        UNIQUE_CLADES_LOST,
        UNIQUE_CLADES_REMAINING
    }


    //#################### Scorer ##################

    public static TreeScorer getScorer(ScorerType scorer) {
        return getScorer(false, scorer);
    }

    public static TreeScorer getScorer(boolean synced, ScorerType scorer) {
        switch (scorer) {
            case UNIQUE_TAXA:
                return newUniqueTaxonScorer(synced);
            case UNIQUE_TAXA_ORIG:
                return new UniqueTaxaNumberScorerOrig(synced) ;
            case OVERLAP:
                return newOverlapScorer(synced);
            case OVERLAP_ORIG:
                return new OverlapScorerOrig(synced);
            case CLADE_NUMBER:
                return new ConsensusCladeNumberScorer(synced);
            case RESOLUTION:
                return newResolutionScorer(synced);
            case COLLISION_SUBTREES:
                return new CollisionNumberScorer(synced);
            case COLLISION_POINT:
                return new CollisionPointNumberScorer(synced);
            case UNIQUE_CLADE_NUMBER:
                new UniqueTaxaNumberScorer(synced);
            case UNIQUE_CLADE_RATE:
                return new UniqueCladesRateScorer(synced);
            case UNIQUE_CLADES_LOST:
                return new UniqueCladesLostNumberScorer(synced);
            case UNIQUE_CLADES_REMAINING:
                return new UniqueCladesRemainingNumberScorer(synced);
            default:
                return null;
        }
    }


    public static TreeScorer newOverlapScorer(boolean synced) {
        return new OverlapScorer(synced);
    }

    public static TreeScorer newUniqueTaxonScorer(boolean synced) {
        return new UniqueTaxaNumberScorer(synced);
    }

    public static TreeScorer newTaxonScorer(boolean synced) {
        return new ConsensusTaxonNumberScorer(synced);
    }

    public static TreeScorer newResolutionScorer(boolean synced) {
        return new ConsensusResolutionScorer(synced);
    }

    public static TreeScorer newCollisionScorer(boolean synced) {
        return new CollisionNumberScorer(synced);
    }


    //#################### Scorer Arrays ##################

    public static TreeScorer[] getScorerArray(boolean synced, ScorerType... scorerTypes) {
        TreeScorer[] scorerArray = new TreeScorer[scorerTypes.length];
        for (int i = 0; i < scorerArray.length; i++) {
            scorerArray[i] = getScorer(synced, scorerTypes[i]);
        }
        return scorerArray;
    }

    public static TreeScorer[] getFullScorerArray(boolean synced) {
        TreeScorer[] sc = new TreeScorer[ScorerType.values().length];
        for (int i = 0; i < sc.length; i++) {
            sc[i] = getScorer(synced,ScorerType.values()[i]);
        }
        return sc;
    }

    public static TreeScorer[] getNuScorerArray(boolean synced) {
        return new TreeScorer[]{
                new TreeScorer.OverlapScorer(synced),
                new TreeScorer.CollisionNumberScorer(synced),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
                new TreeScorer.ConsensusCladeNumberScorer(synced),
                new TreeScorer.ConsensusResolutionScorer(synced),
                new TreeScorer.UniqueCladesNumberScorer(synced),
                new TreeScorer.UniqueCladesRemainingNumberScorer(synced),
                new TreeScorer.UniqueCladesLostNumberScorer(synced),
                new TreeScorer.UniqueCladesRateScorer(synced)
        };
    }

    public static TreeScorer[] newFastScorerArray(boolean synced) {
        return new TreeScorer[]{
                newOverlapScorer(synced),
                newUniqueTaxonScorer(synced),
                newTaxonScorer(synced)
        };
    }

    public static TreeScorer[] getGreenArray(boolean synced) {
        return new TreeScorer[]{
                new TreeScorer.OverlapScorer(synced),//is automatic included
                new TreeScorer.CollisionPointNumberScorer(synced),
//            new CollisionLostCladesNumberScorer(),
                new TreeScorer.CollisionNumberScorer(synced),
//            new BackboneSizeScorer(),
//            new BackboneCladeNumberScorer(),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
                new TreeScorer.UniqueTaxaRateScorer(synced),
//            new ConsensusBackboneCladeNumberScorer(),
                new TreeScorer.ConsensusBackboneResolutionScorer(synced),
//            new ConsensusBackboneSizeScorer(),
                new TreeScorer.ConsensusCladeNumberScorer(synced),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }


    public static TreeScorer[] getBest4Array(boolean synced) {
        return new TreeScorer[]{
//            new OverlapScorer(),//is automatic included
                new TreeScorer.CollisionPointNumberScorer(synced),
//            new CollisionLostCladesNumberScorer(),
//            new CollisionNumberScorer(),
//            new BackboneSizeScorer(),
//            new BackboneCladeNumberScorer(),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
//            new UniqueTaxaRateScorer(),
//            new ConsensusBackboneCladeNumberScorer(),
//            new ConsensusBackboneResolutionScorer(),
//            new ConsensusBackboneSizeScorer(),
                new TreeScorer.ConsensusCladeNumberScorer(synced),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }

    public static TreeScorer[] getMinimalArray(boolean synced) {
        return new TreeScorer[]{
                new TreeScorer.OverlapScorer(synced),//is automatic included
                new TreeScorer.CollisionPointNumberScorer(synced),
//            new CollisionLostCladesNumberScorer(),
//            new CollisionNumberScorer(),
//            new BackboneSizeScorer(),
//            new BackboneCladeNumberScorer(),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
//            new UniqueTaxaRateScorer(),
//            new ConsensusBackboneCladeNumberScorer(),
                new TreeScorer.ConsensusBackboneResolutionScorer(synced),
//            new ConsensusBackboneSizeScorer(),
//            new ConsensusCladeNumberScorer(),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }
}
