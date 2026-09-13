package name.jurgenei.ast.core.model;

import java.util.List;
import java.util.Set;

public record AstModel(Set<AstClass> classes, List<AstRelation> relations, List<AstInheritance> inheritances) {
    public AstModel {
        classes = Set.copyOf(classes);
        relations = List.copyOf(relations);
        inheritances = List.copyOf(inheritances);
    }
}

