# ast-classes-core

ANTLR Grammar -> Model -> AST Classes core library.

Focus v0.1:
- Java 21
- TDD-first slices
- deterministic model output
- direct parse-tree pipeline entry via mapper interface

## Architecture

```text
ANTLR ParseTree --(ParseTreeToGrammarModelMapper)--> GrammarModel --(AstClassDeriver)--> AstModel --(AstSexprWriter)--> S-Expr text
```

## Implemented derivation rules

From `jurgenei/papers/ANTLR_G4_to_AST_Classes_Spec.md`:
- Rule 1: parser rule -> class
- Rule 2: labels -> named `rel` relationships
- Rule 3: cardinalities (`1`, `?`, `*`, `+`)
- Rule 4: top-level alternatives -> `isa` inheritance
- Rule 5: literals ignored, literal-only rules dropped

## Run tests

In this workspace, command used:

```zsh
/Users/cs79en/Developer/GitHub/gradle/gradle-antlr-plugin/gradlew -p /Users/cs79en/Developer/GitHub/gradle/ast-classes-core test --no-daemon
```

## Run tiny demo

```zsh
/Users/cs79en/Developer/GitHub/gradle/gradle-antlr-plugin/gradlew -p /Users/cs79en/Developer/GitHub/gradle/ast-classes-core run --no-daemon
```

Expected output shape:

```lisp
(class Assignment)
(rel Assignment target Identifier 1)
(rel Assignment value Expression 1)
```

## Next iterations

1. Add concrete `ANTLRv4Parser` parse-tree mapper implementation.
2. Add duplicate-relation dedup and conflict diagnostics.
3. Add `ref` derivation strategy hooks.

