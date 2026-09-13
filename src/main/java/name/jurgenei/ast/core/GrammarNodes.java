package name.jurgenei.ast.core;

import name.jurgenei.ast.core.model.ChoiceNode;
import name.jurgenei.ast.core.model.GrammarModel;
import name.jurgenei.ast.core.model.GrammarNode;
import name.jurgenei.ast.core.model.GrammarRule;
import name.jurgenei.ast.core.model.LabelNode;
import name.jurgenei.ast.core.model.LiteralNode;
import name.jurgenei.ast.core.model.OptionalNode;
import name.jurgenei.ast.core.model.Repeat1Node;
import name.jurgenei.ast.core.model.RepeatNode;
import name.jurgenei.ast.core.model.RuleRefNode;
import name.jurgenei.ast.core.model.SequenceNode;

import java.util.Arrays;
import java.util.List;

/**
 * Small builder helpers for readable tests and demos.
 */
public final class GrammarNodes {
    private GrammarNodes() {
    }

    public static SequenceNode seq(final GrammarNode... nodes) {
        return new SequenceNode(Arrays.asList(nodes));
    }

    public static ChoiceNode choice(final GrammarNode... nodes) {
        return new ChoiceNode(Arrays.asList(nodes));
    }

    public static OptionalNode optional(final GrammarNode node) {
        return new OptionalNode(node);
    }

    public static RepeatNode star(final GrammarNode node) {
        return new RepeatNode(node);
    }

    public static Repeat1Node plus(final GrammarNode node) {
        return new Repeat1Node(node);
    }

    public static RuleRefNode ref(final String ruleName) {
        return new RuleRefNode(ruleName);
    }

    public static LiteralNode lit(final String text) {
        return new LiteralNode(text);
    }

    public static LabelNode label(final String name, final GrammarNode node) {
        return new LabelNode(name, node);
    }

    public static GrammarRule rule(final String name, final GrammarNode body) {
        return new GrammarRule(name, body);
    }

    public static GrammarModel model(final GrammarRule... rules) {
        return new GrammarModel(List.of(rules));
    }
}

