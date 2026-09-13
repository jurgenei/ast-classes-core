package name.jurgenei.ast.core.model;

/**
 * Normalized grammar model node.
 */
public sealed interface GrammarNode permits SequenceNode, ChoiceNode, OptionalNode, RepeatNode, Repeat1Node, RuleRefNode, LiteralNode, LabelNode {
}

