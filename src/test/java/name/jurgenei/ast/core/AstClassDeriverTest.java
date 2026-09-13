package name.jurgenei.ast.core;

import name.jurgenei.ast.core.model.AstInheritance;
import name.jurgenei.ast.core.model.AstModel;
import name.jurgenei.ast.core.model.AstRelation;
import name.jurgenei.ast.core.model.Cardinality;
import name.jurgenei.ast.core.model.GrammarModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AstClassDeriverTest {

    @Test
    void derivesClassesAndNamedRelations() {
        GrammarModel model = GrammarNodes.model(
                GrammarNodes.rule("assignment", GrammarNodes.seq(
                        GrammarNodes.label("target", GrammarNodes.ref("identifier")),
                        GrammarNodes.lit("="),
                        GrammarNodes.label("value", GrammarNodes.ref("expression")))),
                GrammarNodes.rule("identifier", GrammarNodes.ref("namePart")),
                GrammarNodes.rule("expression", GrammarNodes.ref("literalExpression")),
                GrammarNodes.rule("namePart", GrammarNodes.lit("ID")),
                GrammarNodes.rule("literalExpression", GrammarNodes.lit("NUMBER"))
        );

        AstModel ast = new AstClassDeriver().derive(model);

        assertTrue(ast.classes().stream().anyMatch(c -> c.name().equals("Assignment")));
        assertTrue(ast.classes().stream().anyMatch(c -> c.name().equals("Identifier")));
        assertTrue(ast.classes().stream().anyMatch(c -> c.name().equals("Expression")));

        assertTrue(ast.relations().contains(new AstRelation("Assignment", "target", "Identifier", Cardinality.ONE, name.jurgenei.ast.core.model.RelationKind.REL)));
        assertTrue(ast.relations().contains(new AstRelation("Assignment", "value", "Expression", Cardinality.ONE, name.jurgenei.ast.core.model.RelationKind.REL)));
    }

    @Test
    void preservesCardinalityFromRepeats() {
        GrammarModel model = GrammarNodes.model(
                GrammarNodes.rule("procedure", GrammarNodes.seq(
                        GrammarNodes.label("parameter", GrammarNodes.star(GrammarNodes.ref("parameterDef"))))),
                GrammarNodes.rule("parameterDef", GrammarNodes.ref("nameToken")),
                GrammarNodes.rule("nameToken", GrammarNodes.lit("ID"))
        );

        AstModel ast = new AstClassDeriver().derive(model);

        assertTrue(ast.relations().contains(new AstRelation("Procedure", "parameter", "ParameterDef", Cardinality.STAR, name.jurgenei.ast.core.model.RelationKind.REL)));
    }

    @Test
    void createsInheritanceForChoiceAlternatives() {
        GrammarModel model = GrammarNodes.model(
                GrammarNodes.rule("expression", GrammarNodes.choice(
                        GrammarNodes.ref("functionCall"),
                        GrammarNodes.ref("binaryExpression"),
                        GrammarNodes.ref("literalExpr"))),
                GrammarNodes.rule("functionCall", GrammarNodes.ref("identifier")),
                GrammarNodes.rule("binaryExpression", GrammarNodes.ref("identifier")),
                GrammarNodes.rule("literalExpr", GrammarNodes.ref("identifier")),
                GrammarNodes.rule("identifier", GrammarNodes.ref("nameToken")),
                GrammarNodes.rule("nameToken", GrammarNodes.lit("ID"))
        );

        AstModel ast = new AstClassDeriver().derive(model);

        assertTrue(ast.inheritances().contains(new AstInheritance("FunctionCall", "Expression")));
        assertTrue(ast.inheritances().contains(new AstInheritance("BinaryExpression", "Expression")));
        assertTrue(ast.inheritances().contains(new AstInheritance("LiteralExpr", "Expression")));
    }

    @Test
    void dropsLiteralOnlyRulesAndIgnoresLiteralNoise() {
        GrammarModel model = GrammarNodes.model(
                GrammarNodes.rule("statement", GrammarNodes.seq(
                        GrammarNodes.label("name", GrammarNodes.ref("identifier")),
                        GrammarNodes.lit(";"))),
                GrammarNodes.rule("identifier", GrammarNodes.ref("nameToken")),
                GrammarNodes.rule("terminator", GrammarNodes.lit(";")),
                GrammarNodes.rule("nameToken", GrammarNodes.lit("ID"))
        );

        AstModel ast = new AstClassDeriver().derive(model);

        assertFalse(ast.classes().stream().anyMatch(c -> c.name().equals("Terminator")));
        assertEquals(1, ast.relations().size());
        assertEquals("name", ast.relations().getFirst().roleName());
    }
}

