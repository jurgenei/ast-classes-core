package name.jurgenei.ast.core.model;

public record AstRelation(String sourceClass, String roleName, String targetClass, Cardinality cardinality, RelationKind kind) {
}

