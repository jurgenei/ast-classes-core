package name.jurgenei.ast.core;

import name.jurgenei.ast.core.model.AstModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AstSexprWriterTest {

    @Test
    void writesCanonicalSexprLines() {
        AstModel astModel = new AstClassDeriver().derive(GrammarNodes.model(
                GrammarNodes.rule("assignment", GrammarNodes.seq(
                        GrammarNodes.label("target", GrammarNodes.ref("identifier")),
                        GrammarNodes.label("value", GrammarNodes.ref("expression")))),
                GrammarNodes.rule("identifier", GrammarNodes.ref("nameToken")),
                GrammarNodes.rule("expression", GrammarNodes.choice(
                        GrammarNodes.ref("functionCall"),
                        GrammarNodes.ref("binaryExpression"))),
                GrammarNodes.rule("functionCall", GrammarNodes.ref("identifier")),
                GrammarNodes.rule("binaryExpression", GrammarNodes.ref("identifier")),
                GrammarNodes.rule("nameToken", GrammarNodes.lit("ID"))
        ));

        String rendered = new AstSexprWriter().write(astModel);

        assertTrue(rendered.contains("(class Assignment)"));
        assertTrue(rendered.contains("(rel Assignment target Identifier 1)"));
        assertTrue(rendered.contains("(isa BinaryExpression Expression)"));
    }
}

