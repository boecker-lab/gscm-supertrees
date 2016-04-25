package scm.algorithm.treeSelector;

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
