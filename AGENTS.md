# Wetator — AI Coding Agent Guide

This document provides everything an AI coding agent (or new contributor) needs to understand and work effectively in the Wetator codebase.

---

## Table of Contents

1. [Project Purpose & Overview](#1-project-purpose--overview)
2. [Architecture & Structure](#2-architecture--structure)
3. [Technology Stack](#3-technology-stack)
4. [Key Source Code Patterns](#4-key-source-code-patterns)
5. [Build & Development](#5-build--development)
6. [Testing](#6-testing)
7. [Configuration](#7-configuration)
8. [Coding Standards & Conventions](#8-coding-standards--conventions)
9. [Documentation](#9-documentation)
10. [Contributing Guidelines](#10-contributing-guidelines)

---

## 1. Project Purpose & Overview

**Wetator** is an **automated GUI test framework for web applications**, written in Java and active since 2008 (Apache 2.0 license).

**The core problem it solves**: Web UI test automation is traditionally brittle because tests reference UI elements by DOM IDs, CSS selectors, or element positions — all of which break whenever the UI is restructured. Wetator tests instead locate controls by their **visible labels, alt texts, and positional context**, so a button is referenced as `Submit My Truth`, not `#btn-247` or `xpath://button[@id='j:763223']`.

Tests are written in a **human-readable command language** that maps user actions (`open-url`, `click-on`, `select`, `set`) and content assertions (`assert-content`, `assert-title`). The test format is XML (`.wet`) — also available as Excel (`.xls`/`.xlsx`) and wiki text (`.wett`).

**Example test case** (`samples/wetator_testform/wetator_testform.wet`):
```xml
<command><d:open-url><d:url>http://www.wetator.org/testform</d:url></d:open-url></command>
<command><d:assert-title><d:title>WETATOR / Test Form</d:title></d:assert-title></command>
<command><d:set><d:wpath>Project name</d:wpath><d:value>HtmlUnit</d:value></d:set></command>
<command><d:select><d:wpath>programming language > JAVA</d:wpath></d:select></command>
<command><d:click-on><d:wpath>Submit My Truth</d:wpath></d:click-on></command>
<command><d:assert-content><d:content>We hope all your visionary WETATOR tests</d:content></d:assert-content></command>
```

**Supported technologies under test**: HTML, CSS, JavaScript (including sophisticated AJAX), MS Word, MS Excel, PDF, and ZIP (for content assertion on downloads).

**Browser emulation**: Firefox, Firefox ESR, Chrome, Edge — powered by HtmlUnit.

---

## 2. Architecture & Structure

```
wetator/
├── src/
│   ├── main/
│   │   ├── java/org/wetator/
│   │   │   ├── Wetator.java                  ← CLI entry point (main class)
│   │   │   ├── Version.java                  ← Version metadata
│   │   │   ├── WetatorScriptConverter.java   ← Converts between script formats
│   │   │   ├── backend/                      ← Browser abstraction layer
│   │   │   │   ├── IBrowser.java             ← Browser interface
│   │   │   │   ├── IControlFinder.java       ← Control discovery interface
│   │   │   │   ├── WPath.java                ← Path-based control addressing
│   │   │   │   ├── WeightedControlList.java  ← Ranked candidate control list
│   │   │   │   ├── ControlFeature.java       ← Feature enum (CLICK, SET, ...)
│   │   │   │   ├── control/                  ← Control capability interfaces
│   │   │   │   │   ├── IControl.java
│   │   │   │   │   ├── IClickable.java
│   │   │   │   │   ├── ISelectable.java
│   │   │   │   │   ├── IDeselectable.java
│   │   │   │   │   ├── ISettable.java
│   │   │   │   │   ├── IDisableable.java
│   │   │   │   │   ├── IFocusable.java
│   │   │   │   │   └── KeySequence.java
│   │   │   │   └── htmlunit/                 ← HtmlUnit backend implementation
│   │   │   │       ├── HtmlUnitBrowser.java  ← IBrowser implementation
│   │   │   │       ├── control/              ← 21 concrete HTML control wrappers
│   │   │   │       ├── finder/               ← Control-finding strategies
│   │   │   │       └── matcher/              ← 19 attribute/label matchers
│   │   │   ├── commandset/                   ← Command implementations
│   │   │   │   ├── AbstractCommandSet.java
│   │   │   │   ├── DefaultCommandSet.java    ← Built-in commands
│   │   │   │   ├── IncubatorCommandSet.java  ← Experimental commands
│   │   │   │   └── SqlCommandSet.java        ← Database commands
│   │   │   ├── core/                         ← Engine and configuration
│   │   │   │   ├── WetatorEngine.java        ← Main orchestrator
│   │   │   │   ├── WetatorContext.java       ← Per-test execution context
│   │   │   │   ├── WetatorConfiguration.java ← Config loading & properties
│   │   │   │   ├── ICommandSet.java          ← Command set plugin interface
│   │   │   │   ├── ICommandImplementation.java
│   │   │   │   ├── IScripter.java            ← Test file parser plugin interface
│   │   │   │   ├── IProgressListener.java    ← Observer for test lifecycle
│   │   │   │   ├── Command.java
│   │   │   │   ├── CommandDescriptor.java
│   │   │   │   ├── Parameter.java
│   │   │   │   ├── ParameterDescriptor.java
│   │   │   │   ├── TestCase.java
│   │   │   │   ├── Variable.java
│   │   │   │   └── searchpattern/            ← Wildcard/regex/automaton patterns
│   │   │   ├── progresslistener/             ← Test result reporting
│   │   │   │   ├── XMLResultWriter.java      ← XML report generation
│   │   │   │   ├── XSLTransformer.java       ← HTML report via XSL
│   │   │   │   ├── StdOutProgressListener.java
│   │   │   │   └── Log4jProgressListener.java
│   │   │   ├── scripter/                     ← Test file parsers
│   │   │   │   ├── XMLScripter.java          ← Parses .wet files
│   │   │   │   ├── ExcelScripter.java        ← Parses .xls/.xlsx files
│   │   │   │   ├── WikiTextScripter.java     ← Parses .wett files
│   │   │   │   └── LegacyXMLScripter.java   ← Parses legacy XML format
│   │   │   ├── scriptcreator/               ← Test file generators (write direction)
│   │   │   ├── util/                        ← Utility classes
│   │   │   ├── i18n/                        ← Internationalization (Messages.java)
│   │   │   ├── gui/                         ← Swing file chooser dialog
│   │   │   └── exception/                   ← Exception hierarchy
│   │   └── resources/
│   │       ├── wetator.config               ← Sample/default configuration
│   │       ├── js_filter.cfg                ← JS job filter configuration
│   │       └── log4j2.xml
│   └── test/
│       └── java/org/wetator/               ← 161 test classes
├── xsd/                                     ← XML schemas for test file formats
├── xsl/                                     ← XSL templates for HTML reports
├── samples/                                 ← Sample test suites (angular, google, etc.)
├── test/                                    ← Static quality-tool configurations
│   ├── checkstyle/checkstyle.xml
│   ├── pmd/
│   └── spotbugs/
├── assembly/                                ← Maven assembly descriptors
├── development.txt                          ← IDE setup & release procedure
├── actions.txt                              ← Action semantics documentation
├── exceptions.txt                           ← Exception decision tree
└── pom.xml
```

### Core Execution Flow

1. `Wetator.main()` parses CLI args → creates `WetatorEngine`
2. Engine reads `wetator.config`, initializes `ICommandSet`s and `IScripter`s, sets up `IBrowser`
3. For each **test file** × each **configured browser**: creates a `WetatorContext`, calls `IScripter.script()` to parse commands, executes each command via `ICommandImplementation.execute(context, command)`
4. `IProgressListener`s (e.g. `XMLResultWriter`) record every step; XSL transform produces the HTML report

### Key Relationships

- `WetatorEngine` owns: `WetatorConfiguration`, `IBrowser`, `List<ICommandSet>`, `List<IScripter>`, `List<IProgressListener>`
- `WetatorContext` is scoped to one file × one browser run; it holds the variable list and parent context (for module calls)
- Control finding: `IBrowser.getControlFinder()` → `IControlFinder` → `WeightedControlList` — the control with the highest weight wins
- `WPath` parses a `>` separated path string into nodes and optional table coordinates `[row, col]`

---

## 3. Technology Stack

| Category | Technology | Version |
|---|---|---|
| Language | Java | 1.8 target (Java 8) |
| Build | Apache Maven | ≥ 3.6.3 |
| Browser engine | HtmlUnit | 4.21.0 |
| Excel parsing | Apache POI (poi-ooxml + poi-scratchpad) | 5.5.1 |
| PDF parsing | Apache PDFBox | 3.0.6 |
| Logging | Apache Log4j 2 | 2.25.3 |
| Utilities | Apache Commons IO / Lang3 / Text | 2.21.0 / 3.20.0 / 1.15.0 |
| Markdown (`describe` cmd) | txtmark | 0.13 |
| Finite automaton (search patterns) | dk.brics:automaton | 1.12-4 |
| XML schema parsing | XSOM | 20140925 |
| Testing framework | JUnit 4 | 4.13.2 |
| Mocking | Mockito | 4.11.0 (last JDK 8 version) |
| Test web server | Eclipse Jetty | 9.4.58 |
| Test database | HyperSQL (HSQLDB) | 2.7.4 |
| Code quality | Checkstyle 12.3.1, SpotBugs 4.9.8, PMD 7.20.0 |
| Security scanning | CodeQL (GitHub Actions), OWASP Dependency Check |
| SBOM | CycloneDX Maven plugin | 2.9.1 |
| Distribution | Maven Shade Plugin (fat jar) + Assembly Plugin (zip) |
| CI | GitHub Actions (CodeQL), Dependabot (daily Maven updates) |

---

## 4. Key Source Code Patterns

### Plugin Interfaces (Extension Points)

**`ICommandSet`** (`core/ICommandSet.java`)  
A collection of named commands. Implementations: `DefaultCommandSet`, `IncubatorCommandSet`, `SqlCommandSet`. Register custom sets via `wetator.commandSets` in config.

**`ICommandImplementation`** (`core/ICommandImplementation.java`)  
Each command is a separate (usually inner) class implementing `execute(WetatorContext, Command)`.

**`IScripter`** (`core/IScripter.java`)  
Parses a test file into `List<Command>`. Flow: `isSupported(file)` → `script(file)` → `getCommands()`. Register custom scripters via `wetator.scripters`.

**`IBrowser`** (`backend/IBrowser.java`)  
Browser abstraction. Only implementation: `HtmlUnitBrowser`. Supports `BrowserType` enum: `FIREFOX_ESR`, `FIREFOX`, `CHROME`, `EDGE`.

**`IProgressListener`** (`core/IProgressListener.java`)  
Observer for test lifecycle events. Implementations: `XMLResultWriter`, `StdOutProgressListener`, `Log4jProgressListener`.

**`IControl`** (`backend/control/IControl.java`)  
Represents a UI control. Composed with capability interfaces: `IClickable`, `ISelectable`, `IDeselectable`, `ISettable`, `IDisableable`, `IFocusable`.

### Control Identification (Matcher Chain)

The `backend/htmlunit/matcher/` package contains 19 matchers that locate a control by different attributes, tried in priority order:

`ByIdMatcher`, `ByNameAttributeMatcher`, `ByLabelAttributeMatcher`, `ByHtmlLabelMatcher`, `ByAriaLabelAttributeMatcher`, `ByDataTestidMatcher`, `ByTextMatcher`, `ByTitleAttributeMatcher`, `ByPlaceholderAttributeMatcher`, `ByImageAltAttributeMatcher`, `ByImageSrcAttributeMatcher`, `ByInnerImageMatcher`, `ByLabelingTextBeforeMatcher`, `ByLabelingTextBeforeAsTextMatcher`, `ByLabelingTextAfterMatcher`, `ByValueAttributeMatcher`, `ByTableCoordinatesMatcher`.

### WPath

`WPath` (`backend/WPath.java`) is the addressing mechanism. A `>` separated path like `"programming language > JAVA"` narrows control discovery by context. Table coordinates `[row, col]` are also supported. The default separator `>` is configurable via `wetator.wpath.separator`.

### SearchPattern

`SearchPattern` (`core/searchpattern/`) implements DOS-style wildcard matching (`*`, `?`) using `dk.brics.automaton`. Results are cached in a 500-entry `SearchPatternCache`. Hierarchy: `SearchPattern` → `AutomatonShortMatcher`, `RegExpSearchPattern`, `TextOnlySearchPattern`, `MatchAllSearchPattern`.

### SecretString

`SecretString` (`util/SecretString.java`) wraps a string and tracks which segments are "secret" (e.g. passwords). `toString()` masks secrets with `****`; `getValue()` returns the real value. Variables prefixed with `$$` in config create secret strings.

### WeightedControlList

`WeightedControlList` (`backend/WeightedControlList.java`) collects candidate controls with a weight/priority score; the control with the highest weight is selected for action.

### Exception Hierarchy

See `exceptions.txt` for the full decision tree. Key exceptions and their handling:

| Exception | Cause | Engine reaction |
|---|---|---|
| `ConfigurationException` | Bad config | Abort whole test run |
| `InvalidInputException` | Bad command parameters | Abort test + skip remaining browsers |
| `CommandException` / `ActionException` | Command/action fails | Abort test |
| `AssertionException` | Assertion fails | Continue with next command |
| `BackendException` | No current page, not HTML | Let command decide |
| `ResourceException` | File read/write error | Abort whole test run |
| `ImplementationException` | Internal bug | Abort whole test run |

### Design Patterns Used

- **Strategy**: command implementations, matchers, finders, scripters
- **Observer**: progress listeners (`IProgressListener`)
- **Template Method**: `AbstractCommandSet.registerCommands()`
- **Decorator**: `SecretString`
- **Chain of Responsibility**: matcher chain for control identification
- **Factory/Registry**: `WetatorConfiguration` loads and instantiates command sets and scripters by class name via reflection

---

## 5. Build & Development

### Build Commands

```bash
# Compile, run unit tests, produce all artifacts
mvn package

# Also run Checkstyle, SpotBugs, PMD
mvn verify

# Skip one known-failing XSL test when building with JDK 8
mvn verify -Dtest=!XSLTransformerTest

# Run only unit tests (fast)
mvn test

# Release build + deploy to Maven Central
mvn -up clean deploy -Dtest=!XSLTransformerTest
```

### Build Artifacts

| Artifact | Description |
|---|---|
| `wetator-{version}.jar` | Library jar |
| `wetator-{version}-all.jar` | Fat jar (main class: `org.wetator.Wetator`) |
| `wetator-{version}.zip` | Distribution archive |
| `wetator-{version}-sources.jar` | Sources jar |
| `wetator-{version}-javadoc.jar` | Javadoc jar |
| `bom.json` | CycloneDX SBOM |

### IDE Setup

```bash
# Generate Eclipse project files
mvn eclipse:eclipse
```

Preferred IDE is Eclipse (with Eclipse Checkstyle Plugin). IntelliJ IDEA is also supported.

### Requirements

- **Java 8** (hard requirement — enforced by maven-enforcer-plugin)
- **Maven ≥ 3.6.3** (enforced by maven-enforcer-plugin)

### Integration Tests

Maven unpacks the release zip, then runs the actual `wetator.sh` / `wetator.bat` script against sample test files (`.wet`, `.wett`, `.xls`, `.xlsx`) during the `integration-test` phase. Check `target/test-release/app/logs/run_report.xsl.html` for results.

### Dependency Management

- Dependabot runs daily Maven dependency updates (`.github/dependabot.yml`)
- OWASP Dependency Check configured in `pom.xml` (currently commented out)
- Use `mvn versions:display-dependency-updates` and `mvn versions:display-plugin-updates` before a release

---

## 6. Testing

### Framework

**JUnit 4** — there is no JUnit 5 usage. **Mockito 4** (last version supporting JDK 8).

### Test Infrastructure

**`AbstractWebServerTest`** (`src/test/java/org/wetator/test/AbstractWebServerTest.java`)  
Base class that spins up an embedded **Jetty** server on port `4711` (overridable via `wetator.test.port` system property), serving static HTML from `test/webpage/` and Servlet-based endpoints (redirect, snoop, content, HTTP headers).

**`AbstractBrowserTest`** (`src/test/java/org/wetator/test/AbstractBrowserTest.java`)  
Provides `WetatorEngine` execution support with a `JUnitProgressListener`.

**`BrowserRunner`** / **`BrowserFrameworkMethod`** (`src/test/java/org/wetator/test/junit/`)  
Custom JUnit runner that re-runs the same test across multiple configured browsers automatically.

### Test Locations

| Location | Contents |
|---|---|
| `src/test/java/org/wetator/` | 161 test classes |
| `src/test/resources/` | XML test inputs and expected result XML fixtures |
| `test/webpage/` | Static HTML pages served by the test Jetty server |

### Test Categories

- **Engine**: `WetatorEngineExecuteTestsTest`, `WetatorContextExecuteTest`
- **Core**: `CommandTest`, `VariableTest`, `ParameterTest`, `TestCaseTest`
- **SearchPattern**: `SearchPatternTest` and subclasses
- **Backend controls**: `HtmlUnitInputTextTest`, `HtmlUnitButtonTest`, `HtmlUnitInputCheckBox*`, etc.
- **Matchers**: `ByIdMatcherTest`, `ByHtmlLabelMatcherTest`, `ByTableCoordinatesMatcherTest`, etc.
- **Identifier**: `HtmlUnitAnchorIdentifierTest`, `HtmlUnitInputTextIdentifierTest`, etc.
- **Scripters**: `XMLScripterTest`, `ExcelScripterTest`, `WikiTextScripterTest`
- **Progress listeners**: `XMLResultWriterTest`
- **Script creators**: `ManualXMLScriptCreatorTest`

---

## 7. Configuration

The primary config file is `wetator.config` (Java properties format). Location is auto-discovered as `./wetator.config`, or set via system property `wetator.config` or CLI flag `-p <path>`.

### Key Properties

| Property | Description | Default |
|---|---|---|
| `wetator.baseUrl` | Base URL of the application under test | *(required)* |
| `wetator.browser` | Comma-separated list of browsers to run | `Firefox_ESR` |
| `wetator.outputDir` | Directory for XML/HTML reports | `../logs` |
| `wetator.distinctOutput` | Use a separate sub-directory per run | `false` |
| `wetator.typingspeed` | Simulated typing speed (keystrokes/minute) | `200` |
| `wetator.jsTimeout` | Seconds to wait for JS jobs after an action | `1` |
| `wetator.httpTimeout` | HTTP response timeout in seconds | `90` |
| `wetator.xslTemplates` | Comma-separated XSL template paths for reports | — |
| `wetator.commandSets` | Additional `ICommandSet` class names | — |
| `wetator.controls` | Additional `IControl` class names | — |
| `wetator.scripters` | Additional `IScripter` class names | — |
| `wetator.wpath.separator` | WPath node delimiter | `>` |
| `wetator.jsJobFilterFile` | Path to JS job filter config | `js_filter.cfg` |
| `wetator.scriptPreProcessor` | `ScriptPreProcessor` class for JS patching | — |
| `wetator.retrospect` | Log N steps before an error (-1 = disabled) | `-1` |
| `wetator.proxyHost` / `wetator.proxyPort` | Proxy settings | — |
| `wetator.basicAuthUser` / `wetator.basicAuthPassword` | Basic auth | — |
| `wetator.ntlmUser` / `wetator.ntlmPassword` / etc. | NTLM auth | — |
| `wetator.clientCertificateKeyStoreUrl` | Client certificate keystore | — |
| `wetator.db.connections` | DB connection names (SQL command set) | — |
| `wetator.db.<name>.driver/url/user/password` | JDBC connection details | — |
| `wetator.scripter.excel.locale` | Locale for reading Excel test files | — |

### Variables

Define reusable variables directly in `wetator.config` or a separate `wetator.variables` file:

```properties
# Normal variable (value shown in logs)
$app_user=dobby

# Secret variable (value masked as **** in logs)
$$app_password=secret
```

Variables are referenced in test commands as `${app_user}` and `${app_password}`.

Built-in read-only context variables available in every test:
- `${wetator.testcase}` — name of the current test case
- `${wetator.browser}` — label of the current browser
- `${wetator.testfile}` — name of the current test file
- `${wetator.baseurl}` — the configured base URL

### CLI Flags

```
wetator [-p <config-file>] [-var <variables-file>] [-log] [-append] [test-file ...]
```

| Flag | Description |
|---|---|
| `-p <file>` | Specify config file path |
| `-var <file>` | Specify variables file path |
| `-log` | Enable debug logging to `wetator.log` |
| `-append` | Append to existing results (incompatible with `distinctOutput`) |
| `test-file ...` | One or more `.wet`/`.wett`/`.xls`/`.xlsx` files; wildcards supported |

---

## 8. Coding Standards & Conventions

All rules are enforced automatically at `mvn verify` via Checkstyle, PMD, and SpotBugs.

### Java Version

**Java 8 only.** No Java 9+ APIs, no `var`, no records, no text blocks, no switch expressions.

### File Header

Every `.java` file **must** begin with the Apache 2.0 copyright header (enforced by Checkstyle):

```java
/*
 * Copyright (c) 2008-2026 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

### Naming Conventions

| Element | Pattern | Example |
|---|---|---|
| Interface | `I` prefix + PascalCase | `IBrowser`, `ICommandSet` |
| Class | PascalCase | `WetatorEngine`, `HtmlUnitBrowser` |
| Local variable | `tmp` prefix + PascalCase | `tmpConfig`, `tmpFileNames` |
| Final local variable | `tmp` prefix or single letter | `tmpResult`, `i`, `j`, `k` |
| Method parameter | `a` or `an` prefix + PascalCase | `aContext`, `anArgsArray` |
| Catch variable | `e` prefix or `e` alone | `eConfig`, `e` |
| Constant (`static final`) | `UPPER_SNAKE_CASE` | `CONFIG_FILE_DEFAULT_NAME` |
| Package | lowercase only | `org.wetator.backend.htmlunit` |
| Type parameter | One or more uppercase letters | `T`, `RR` |

### Variables and Parameters

- Declare all local variables, parameters, and fields `final` wherever possible (Checkstyle `FinalLocalVariable` rule)
- One variable declaration per line (`MultipleVariableDeclarations`)
- One statement per line (`OneStatementPerLine`)

### Imports

- **No star imports** (`import java.util.*` is forbidden)
- **No static imports** except `org.wetator.core.ParameterDescriptor.optional` and `ParameterDescriptor.required`
- **No unused imports**

### Annotations

- `@Override` is **required** whenever overriding a method (Checkstyle enforces at error severity)
- Annotations go on their own line (except for multiple annotations on a variable, which may be on one line)

### Javadoc

- Required for all `public` and package-visible methods and types
- Class/interface declarations require at least one `@author` tag
- Non-empty `@param`, `@return`, and `@throws` descriptions
- `@Override`-only methods are exempt

### Code Style

- Spaces only — **no tab characters** (enforced per-line)
- Utility classes must hide their constructor (`HideUtilityClassConstructor`)
- No `finalize()` methods (`NoFinalizer`)
- No inner assignments (`InnerAssignment`)
- Use `equals(...)` with the literal/constant on the left side to avoid NPE (`EqualsAvoidNull`)

---

## 9. Documentation

| File/Location | Contents |
|---|---|
| `README.md` | Project overview, benefits, target audience, download links, contributing guide, license |
| `AGENTS.md` (this file) | Comprehensive AI/contributor guide |
| `CLAUDE.md` | Pointer to this file |
| `development.txt` | IDE setup, detailed build commands, release procedure |
| `actions.txt` | Internal pseudocode documentation of mouse/keyboard action semantics |
| `exceptions.txt` | Decision tree: which exception to throw and how the engine reacts |
| `wetator.org` | Full end-user documentation and Getting Started guide |
| `xsd/` | Versioned XML schemas for `.wet` test file format (self-documenting) |
| `samples/` | Working test suites for Angular, Google Search, GWT, htmx, PrimeFaces, etc. |
| Javadoc | Generated from source via `mvn javadoc:jar`; `@author` required on all types |

---

## 10. Contributing Guidelines

From `README.md`:

> Pull Requests and all other Community Contributions are essential for open source software. Every contribution — from bug reports to feature requests, typos to full new features — are greatly appreciated.  
> Please try to keep your pull requests small (don't bundle unrelated changes) and try to include test cases.

### Checklist for Contributors

1. **Java 8 compatibility** is a hard requirement — verified by the Maven enforcer plugin
2. **Run the full build before submitting**: `mvn -U clean test` (all tests must pass)
3. **Static analysis must pass**: `mvn verify` runs Checkstyle, SpotBugs, and PMD automatically
4. **Include test cases** for new functionality (JUnit 4 style)
5. **Keep PRs focused** — one logical change per PR
6. **Follow naming conventions** exactly (see section 8 above)

### How to Add a New Command

1. Add a registration call in `registerCommands()` of the appropriate `*CommandSet` (e.g. `DefaultCommandSet`)
2. Implement `ICommandImplementation` as an inner class of the command set
3. If the command set has its own XSD, add the new element to the corresponding `.xsd` file in `xsd/`
4. Add unit tests

### How to Add a New Control Matcher

1. Extend `AbstractHtmlUnitElementMatcher` (or `AbstractByAttributeMatcher`) in `backend/htmlunit/matcher/`
2. Register it in the appropriate finder in `backend/htmlunit/finder/`
3. Add unit tests in `src/test/java/org/wetator/backend/htmlunit/matcher/`

### How to Add a New Scripter (Test File Format)

1. Implement `IScripter` in `src/main/java/org/wetator/scripter/`
2. Register via `wetator.scripters` property in `wetator.config`
3. Add unit tests

### Release Procedure (Maintainers)

See `development.txt` for the full step-by-step release procedure, including version bumping, integration test execution, and Maven Central publishing.
