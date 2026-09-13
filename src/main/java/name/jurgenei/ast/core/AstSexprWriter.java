package name.jurgenei.ast.core;

import name.jurgenei.ast.core.model.AstClass;
import name.jurgenei.ast.core.model.AstInheritance;
import name.jurgenei.ast.core.model.AstModel;
import name.jurgenei.ast.core.model.AstRelation;
import name.jurgenei.ast.core.model.RelationKind;

import java.util.Comparator;

/**
 * Renders AST class model as canonical S-expression text.
 */
public final class AstSexprWriter {

    public String write(final AstModel astModel) {
        final StringBuilder out = new StringBuilder();

        astModel.classes().stream()
                .map(AstClass::name)
                .sorted()
                .forEach(name -> out.append("(class ").append(name).append(")\n"));

        astModel.relations().stream()
                .sorted(Comparator
                        .comparing(AstRelation::sourceClass)
                        .thenComparing(AstRelation::roleName)
                        .thenComparing(AstRelation::targetClass))
                .forEach(rel -> out
                        .append('(')
                        .append(rel.kind() == RelationKind.REF ? "ref" : "rel")
                        .append(' ')
                        .append(rel.sourceClass())
                        .append(' ')
                        .append(rel.roleName())
                        .append(' ')
                        .append(rel.targetClass())
                        .append(' ')
                        .append(rel.cardinality().symbol())
                        .append(")\n"));

        astModel.inheritances().stream()
                .sorted(Comparator
                        .comparing(AstInheritance::childClass)
                        .thenComparing(AstInheritance::parentClass))
                .forEach(isa -> out
                        .append("(isa ")
                        .append(isa.childClass())
                        .append(' ')
                        .append(isa.parentClass())
                        .append(")\n"));

        return out.toString();
    }
}

