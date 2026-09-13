package name.jurgenei.ast.core.model;

/**
 * Cardinality symbols preserved from grammar to AST class relationships.
 */
public enum Cardinality {
    ONE("1"),
    OPTIONAL("?"),
    STAR("*"),
    PLUS("+");

    private final String symbol;

    Cardinality(final String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    /**
     * Combines nested grammar cardinalities into one resulting cardinality.
     */
    public static Cardinality combine(final Cardinality outer, final Cardinality inner) {
        if (outer == STAR || inner == STAR) {
            return STAR;
        }
        if ((outer == OPTIONAL && inner == PLUS) || (outer == PLUS && inner == OPTIONAL)) {
            return STAR;
        }
        if (outer == OPTIONAL || inner == OPTIONAL) {
            return OPTIONAL;
        }
        if (outer == PLUS || inner == PLUS) {
            return PLUS;
        }
        return ONE;
    }
}

