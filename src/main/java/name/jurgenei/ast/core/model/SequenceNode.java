package name.jurgenei.ast.core.model;

import java.util.List;

public record SequenceNode(List<GrammarNode> elements) implements GrammarNode {
    public SequenceNode {
        elements = List.copyOf(elements);
    }
}

