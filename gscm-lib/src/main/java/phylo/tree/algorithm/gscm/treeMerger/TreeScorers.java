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

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 24.11.15.
 */

/**
 * Factory method to get access to all implemented scoring functions by name
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
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
                return new TreeScorer.ConsensusCladeNumberScorer(synced);
            case UNIQUE_TAXA_ORIG:
                return new TreeScorer.UniqueTaxaNumberScorerOrig(synced) ;
            case OVERLAP:
                return new TreeScorer.OverlapScorer(synced);
            case OVERLAP_ORIG:
                return new TreeScorer.OverlapScorerOrig(synced);
            case CLADE_NUMBER:
                return new TreeScorer.ConsensusCladeNumberScorer(synced);
            case RESOLUTION:
                return new TreeScorer.ConsensusResolutionScorer(synced);
            case COLLISION_SUBTREES:
                return new TreeScorer.CollisionNumberScorer(synced);
            case COLLISION_POINT:
                return new TreeScorer.CollisionPointNumberScorer(synced);
            case UNIQUE_CLADE_NUMBER:
                new TreeScorer.UniqueTaxaNumberScorer(synced);
            case UNIQUE_CLADE_RATE:
                return new TreeScorer.UniqueCladesRateScorer(synced);
            case UNIQUE_CLADES_LOST:
                return new TreeScorer.UniqueCladesLostNumberScorer(synced);
            case UNIQUE_CLADES_REMAINING:
                return new TreeScorer.UniqueCladesRemainingNumberScorer(synced);
            default:
                return null;
        }
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
                new TreeScorer.OverlapScorer(synced),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
                new TreeScorer.ConsensusTaxonNumberScorer(synced)
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
