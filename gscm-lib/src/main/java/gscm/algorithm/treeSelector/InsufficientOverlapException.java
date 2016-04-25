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
package gscm.algorithm.treeSelector;

/**
 * Created by fleisch on 22.04.16.
 */
public class InsufficientOverlapException extends Exception {
    private static String message =  "Input trees have insufficient taxon overlap for supertree reconstruction!";

    public InsufficientOverlapException() {
        super(message);
    }

    public InsufficientOverlapException(String s) {
        super(s);
    }

    public InsufficientOverlapException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientOverlapException(Throwable cause) {
        super(message,cause);
    }
}
