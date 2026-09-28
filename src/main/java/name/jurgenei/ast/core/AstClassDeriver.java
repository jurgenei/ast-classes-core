package name.jurgenei.ast.core;

import name.jurgenei.ast.core.model.AstClass;
import name.jurgenei.ast.core.model.AstInheritance;
import name.jurgenei.ast.core.model.AstModel;
import name.jurgenei.ast.core.model.AstRelation;
import name.jurgenei.ast.core.model.Cardinality;
import name.jurgenei.ast.core.model.ChoiceNode;
import name.jurgenei.ast.core.model.GrammarModel;
import name.jurgenei.ast.core.model.GrammarNode;
import name.jurgenei.ast.core.model.GrammarRule;
import name.jurgenei.ast.core.model.LabelNode;
import name.jurgenei.ast.core.model.LiteralNode;
import name.jurgenei.ast.core.model.OptionalNode;
import name.jurgenei.ast.core.model.RelationKind;
import name.jurgenei.ast.core.model.Repeat1Node;
import name.jurgenei.ast.core.model.RepeatNode;
import name.jurgenei.ast.core.model.RuleRefNode;
import name.jurgenei.ast.core.model.SequenceNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Derives AST classes from normalized grammar model.
 */
public final class AstClassDeriver {

    public AstModel derive(final GrammarModel grammarModel) {
        final List<GrammarRule> rules = grammarModel.rules();
        final Map<String, GrammarRule> ruleByName = rules.stream()
                .collect(Collectors.toMap(GrammarRule::name, Function.identity()));

        final Set<String> classNames = rules.stream()
                .filter(rule -> !isLiteralOnly(rule.body()))
                .map(rule -> toClassName(rule.name()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        final Set<AstClass> classes = classNames.stream()
                .map(AstClass::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        final List<AstRelation> relations = new ArrayList<>();
        final List<AstInheritance> inheritances = new ArrayList<>();

        for (GrammarRule rule : rules) {
            if (isLiteralOnly(rule.body())) {
                continue;
            }
            final String sourceClass = toClassName(rule.name());
            collectRelations(sourceClass, rule.body(), Cardinality.ONE, classNames, relations);
            collectTopLevelInheritances(sourceClass, rule.body(), ruleByName, classNames, inheritances);
        }

        relations.sort(Comparator
                .comparing(AstRelation::sourceClass)
                .thenComparing(AstRelation::roleName)
                .thenComparing(AstRelation::targetClass));
        inheritances.sort(Comparator
                .comparing(AstInheritance::childClass)
                .thenComparing(AstInheritance::parentClass));

        return new AstModel(classes, relations, inheritances);
    }

    private void collectTopLevelInheritances(
            final String parentClass,
            final GrammarNode body,
            final Map<String, GrammarRule> ruleByName,
            final Set<String> classNames,
            final List<AstInheritance> inheritances) {
        if (!(body instanceof ChoiceNode(List<GrammarNode> alternatives))) {
            return;
        }
        for (GrammarNode alternative : alternatives) {
            final String childRuleName = unwrapDirectRuleRef(alternative);
            if (childRuleName == null) {
                continue;
            }
            final GrammarRule childRule = ruleByName.get(childRuleName);
            if (childRule == null || isLiteralOnly(childRule.body())) {
                continue;
            }
            final String childClass = toClassName(childRuleName);
            if (classNames.contains(childClass) && !childClass.equals(parentClass)) {
                inheritances.add(new AstInheritance(childClass, parentClass));
            }
        }
    }

    private String unwrapDirectRuleRef(final GrammarNode node) {
        if (node instanceof RuleRefNode(String ruleName)) {
            return ruleName;
        }
        if (node instanceof LabelNode labelNode) {
            return unwrapDirectRuleRef(labelNode.node());
        }
        return null;
    }

    private void collectRelations(
            final String sourceClass,
            final GrammarNode node,
            final Cardinality inheritedCardinality,
            final Set<String> classNames,
            final List<AstRelation> relations) {
        if (node instanceof LabelNode labelNode) {
            collectLabelRelation(sourceClass, labelNode, inheritedCardinality, classNames, relations);
            return;
        }
        if (node instanceof SequenceNode(List<GrammarNode> elements)) {
            for (GrammarNode child : elements) {
                collectRelations(sourceClass, child, inheritedCardinality, classNames, relations);
            }
            return;
        }
        if (node instanceof ChoiceNode(List<GrammarNode> alternatives)) {
            for (GrammarNode child : alternatives) {
                collectRelations(sourceClass, child, inheritedCardinality, classNames, relations);
            }
            return;
        }
        if (node instanceof OptionalNode(GrammarNode node3)) {
            collectRelations(
                    sourceClass,
                    node3,
                    Cardinality.combine(inheritedCardinality, Cardinality.OPTIONAL),
                    classNames,
                    relations);
            return;
        }
        if (node instanceof RepeatNode(GrammarNode node2)) {
            collectRelations(
                    sourceClass,
                    node2,
                    Cardinality.combine(inheritedCardinality, Cardinality.STAR),
                    classNames,
                    relations);
            return;
        }
        if (node instanceof Repeat1Node(GrammarNode node1)) {
            collectRelations(
                    sourceClass,
                    node1,
                    Cardinality.combine(inheritedCardinality, Cardinality.PLUS),
                    classNames,
                    relations);
        }
    }

    private void collectLabelRelation(
            final String sourceClass,
            final LabelNode labelNode,
            final Cardinality inheritedCardinality,
            final Set<String> classNames,
            final List<AstRelation> relations) {
        final LabelTarget labelTarget = resolveLabelTarget(labelNode.node(), inheritedCardinality);
        if (labelTarget == null) {
            return;
        }
        final String targetClass = toClassName(labelTarget.ruleName());
        if (!classNames.contains(targetClass)) {
            return;
        }
        relations.add(new AstRelation(
                sourceClass,
                labelNode.label(),
                targetClass,
                labelTarget.cardinality(),
                RelationKind.REL));
    }

    private LabelTarget resolveLabelTarget(final GrammarNode node, final Cardinality inheritedCardinality) {
        if (node instanceof RuleRefNode(String ruleName)) {
            return new LabelTarget(ruleName, inheritedCardinality);
        }
        if (node instanceof OptionalNode(GrammarNode node3)) {
            return resolveLabelTarget(
                    node3,
                    Cardinality.combine(inheritedCardinality, Cardinality.OPTIONAL));
        }
        if (node instanceof RepeatNode(GrammarNode node2)) {
            return resolveLabelTarget(
                    node2,
                    Cardinality.combine(inheritedCardinality, Cardinality.STAR));
        }
        if (node instanceof Repeat1Node(GrammarNode node1)) {
            return resolveLabelTarget(
                    node1,
                    Cardinality.combine(inheritedCardinality, Cardinality.PLUS));
        }
        if (node instanceof LabelNode nestedLabel) {
            return resolveLabelTarget(nestedLabel.node(), inheritedCardinality);
        }
        return null;
    }

    private boolean isLiteralOnly(final GrammarNode node) {
        if (node instanceof LiteralNode) {
            return true;
        }
        if (node instanceof RuleRefNode) {
            return false;
        }
        if (node instanceof LabelNode labelNode) {
            return isLiteralOnly(labelNode.node());
        }
        if (node instanceof OptionalNode(GrammarNode node3)) {
            return isLiteralOnly(node3);
        }
        if (node instanceof RepeatNode(GrammarNode node2)) {
            return isLiteralOnly(node2);
        }
        if (node instanceof Repeat1Node(GrammarNode node1)) {
            return isLiteralOnly(node1);
        }
        if (node instanceof SequenceNode(List<GrammarNode> elements)) {
            return elements.stream().allMatch(this::isLiteralOnly);
        }
        if (node instanceof ChoiceNode(List<GrammarNode> alternatives)) {
            return alternatives.stream().allMatch(this::isLiteralOnly);
        }
        return false;
    }

    static String toClassName(final String ruleName) {
        final String[] parts = ruleName.split("[^A-Za-z0-9]+");
        final StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            out.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                out.append(part.substring(1));
            }
        }
        return out.isEmpty() ? ruleName : out.toString();
    }

    private record LabelTarget(String ruleName, Cardinality cardinality) {
    }
}

