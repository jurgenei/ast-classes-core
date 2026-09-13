package name.jurgenei.ast.core.model;

import java.util.List;

public record ChoiceNode(List<GrammarNode> alternatives) implements GrammarNode {
    public ChoiceNode {
        alternatives = List.copyOf(alternatives);
    }
}

