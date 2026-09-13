package name.jurgenei.ast.core;

import name.jurgenei.ast.core.mapper.ParseTreeToGrammarModelMapper;
import name.jurgenei.ast.core.model.AstModel;
import name.jurgenei.ast.core.model.GrammarModel;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AstClassesPipelineTest {

    @Test
    void delegatesParseTreeToMapperThenDerivesAstClasses() {
        ParseTree parseTree = (ParseTree) Proxy.newProxyInstance(
                ParseTree.class.getClassLoader(),
                new Class<?>[]{ParseTree.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("toString")) {
                        return "fake-parse-tree";
                    }
                    return null;
                });

        GrammarModel model = GrammarNodes.model(
                GrammarNodes.rule("rootRule", GrammarNodes.ref("childRule")),
                GrammarNodes.rule("childRule", GrammarNodes.ref("tokenRule")),
                GrammarNodes.rule("tokenRule", GrammarNodes.lit("TOKEN"))
        );

        ParseTreeToGrammarModelMapper<ParseTree> mapper = tree -> model;

        AstModel astModel = new AstClassesPipeline().deriveFromParseTree(parseTree, mapper);

        assertTrue(astModel.classes().stream().anyMatch(c -> c.name().equals("RootRule")));
        assertTrue(astModel.classes().stream().anyMatch(c -> c.name().equals("ChildRule")));
        assertEquals(0, astModel.relations().size());
    }
}

