package name.jurgenei.ast.core.model;

import java.util.List;

public record GrammarModel(List<GrammarRule> rules) {
    public GrammarModel {
        rules = List.copyOf(rules);
    }
}

