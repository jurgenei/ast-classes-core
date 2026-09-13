package name.jurgenei.ast.core;

import name.jurgenei.ast.core.model.AstModel;
import name.jurgenei.ast.core.model.GrammarModel;

/**
 * Tiny runner for local manual verification.
 */
public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final GrammarModel grammarModel = GrammarNodes.model(
                GrammarNodes.rule("assignment", GrammarNodes.seq(
                        GrammarNodes.label("target", GrammarNodes.ref("identifier")),
                        GrammarNodes.lit("="),
                        GrammarNodes.label("value", GrammarNodes.ref("expression")))),
                GrammarNodes.rule("identifier", GrammarNodes.ref("namePart")),
                GrammarNodes.rule("expression", GrammarNodes.ref("literalExpression")),
                GrammarNodes.rule("namePart", GrammarNodes.lit("ID")),
                GrammarNodes.rule("literalExpression", GrammarNodes.lit("NUMBER"))
        );

        final AstModel astModel = new AstClassesPipeline().deriveFromGrammarModel(grammarModel);
        System.out.println(new AstSexprWriter().write(astModel));
    }
}

