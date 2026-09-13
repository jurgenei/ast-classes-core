package name.jurgenei.ast.core;

import name.jurgenei.ast.core.mapper.ParseTreeToGrammarModelMapper;
import name.jurgenei.ast.core.model.AstModel;
import name.jurgenei.ast.core.model.GrammarModel;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Objects;

/**
 * End-to-end pipeline: ANTLR parse tree -> grammar model -> AST classes.
 */
public final class AstClassesPipeline {
    private final AstClassDeriver deriver;

    public AstClassesPipeline() {
        this(new AstClassDeriver());
    }

    public AstClassesPipeline(final AstClassDeriver deriver) {
        this.deriver = Objects.requireNonNull(deriver, "deriver");
    }

    public AstModel deriveFromGrammarModel(final GrammarModel grammarModel) {
        return deriver.derive(grammarModel);
    }

    public <T extends ParseTree> AstModel deriveFromParseTree(
            final T parseTree,
            final ParseTreeToGrammarModelMapper<T> mapper) {
        Objects.requireNonNull(parseTree, "parseTree");
        Objects.requireNonNull(mapper, "mapper");
        return deriver.derive(mapper.map(parseTree));
    }
}

