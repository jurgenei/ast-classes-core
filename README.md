# ast-classes-core

[![Build](https://github.com/jurgenei/ast-classes-core/actions/workflows/ci.yml/badge.svg)](https://github.com/jurgenei/ast-classes-core/actions/workflows/ci.yml)
[![Release](https://github.com/jurgenei/ast-classes-core/actions/workflows/release.yml/badge.svg)](https://github.com/jurgenei/ast-classes-core/actions/workflows/release.yml)
[![Coverage CI](https://github.com/jurgenei/ast-classes-core/actions/workflows/coverage.yml/badge.svg)](https://github.com/jurgenei/ast-classes-core/actions/workflows/coverage.yml)
[![CodeQL](https://github.com/jurgenei/ast-classes-core/actions/workflows/codeql.yml/badge.svg)](https://github.com/jurgenei/ast-classes-core/actions/workflows/codeql.yml)
[![Coverage](https://codecov.io/gh/jurgenei/ast-classes-core/graph/badge.svg?branch=main)](https://codecov.io/gh/jurgenei/ast-classes-core?branch=main)
[![Maven Central](https://img.shields.io/maven-central/v/name.jurgenei.ast/ast-classes-core.svg)](https://search.maven.org/artifact/name.jurgenei.ast/ast-classes-core)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-21+-green.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-9.5+-blue.svg)](https://gradle.org/)

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

## Maven Central publishing

Local prereqs:

1. `mavenCentralUsername` + `mavenCentralPassword` in `~/.gradle/gradle.properties`
2. Signing key configured (`signingKey` + `signingPassword`, optional `signingKeyId`) or local `gpg` keyring
3. Release version (remove `-SNAPSHOT`)

Example `~/.gradle/gradle.properties`:

```properties
mavenCentralUsername=YOUR_MAVEN_CENTRAL_TOKEN_USERNAME
mavenCentralPassword=YOUR_MAVEN_CENTRAL_TOKEN_PASSWORD
signingKey=YOUR_ASCII_ARMORED_PRIVATE_KEY
signingPassword=YOUR_SIGNING_KEY_PASSPHRASE
signingKeyId=YOUR_GPG_KEY_ID
```

Build publishable central bundle:

```bash
./gradlew clean packageCentralBundle
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

