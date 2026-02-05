# Feature: Implementar Avaliação de Cobertura de Testes com JaCoCo

## Feature Description

Implementar o JaCoCo (Java Code Coverage) como ferramenta de avaliação de cobertura de testes no projeto SIGESI. O JaCoCo instrumentará o bytecode durante a execução dos testes para medir quais linhas, branches e métodos são exercitados pelos testes existentes. A configuração incluirá geração de relatórios (HTML, XML, CSV), exclusão inteligente de código gerado (Lombok, MapStruct), e enforcement de thresholds mínimos de cobertura que falham o build quando não atingidos. A integração com o CI (GitHub Actions) garantirá monitoramento contínuo da cobertura.

## User Story

As a developer on the SIGESI project
I want to have automated test coverage analysis with enforceable thresholds
So that I can identify untested code, maintain quality standards, and prevent coverage regression

## Problem Statement

O projeto SIGESI possui 27 classes de teste cobrindo 16 módulos, mas não há nenhuma ferramenta configurada para medir a efetividade desses testes. Sem métricas de cobertura, não é possível identificar áreas críticas sem testes, monitorar regressões de cobertura, ou enforçar padrões mínimos de qualidade.

## Solution Statement

Configurar o plugin JaCoCo Maven para instrumentar testes, gerar relatórios multi-formato, e enforçar thresholds de cobertura no build. Adicionalmente, criar o `lombok.config` para excluir código gerado pelo Lombok da análise, e atualizar o CI pipeline para incluir a etapa de verificação de cobertura.

## Feature Metadata

**Feature Type**: New Capability
**Estimated Complexity**: Low-Medium
**Primary Systems Affected**: Maven build (pom.xml), CI pipeline (ci.yaml), Lombok config
**Dependencies**: JaCoCo Maven Plugin 0.8.14

---

## CONTEXT REFERENCES

### Relevant Codebase Files

- `pom.xml` (lines 145-220) - Why: Build plugins section where JaCoCo plugin will be added, after checkstyle plugin
- `pom.xml` (lines 29-34) - Why: Properties section where JaCoCo version property will be added
- `.github/workflows/ci.yaml` (lines 1-31) - Why: CI pipeline that needs coverage verification step
- `checkstyle.xml` (lines 1-100) - Why: Reference for code quality enforcement pattern already used in project
- `src/test/resources/application.properties` - Why: Test configuration, confirms H2 database is used for tests

### New Files to Create

- `lombok.config` - Lombok configuration to add `@Generated` annotation to generated code for JaCoCo exclusion

### Files to Modify

- `pom.xml` - Add JaCoCo plugin configuration and version property
- `.github/workflows/ci.yaml` - Add coverage verification step

### Relevant Documentation

- [JaCoCo Maven Plugin Official Docs](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
  - Plugin goals: prepare-agent, report, check
  - Why: Primary reference for all configuration options
- [JaCoCo Check Goal](https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html)
  - Coverage rules and threshold configuration
  - Why: Needed for configuring build-failure thresholds
- [Baeldung - Intro to JaCoCo](https://www.baeldung.com/jacoco)
  - Spring Boot + Maven integration guide
  - Why: Practical integration patterns
- [JaCoCo + Lombok Exclusion](https://dev.to/derlin/exclude-lombok-generated-code-from-test-coverage-jacocosonarqube-4nh1)
  - lombok.config approach for Generated annotation
  - Why: Critical for accurate coverage with Lombok projects

### Patterns to Follow

**Maven Plugin Pattern (from checkstyle plugin, pom.xml:199-219):**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.4.0</version>
    <configuration>
        <!-- global config -->
    </configuration>
    <executions>
        <execution>
            <id>check-style</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**CI Step Pattern (from ci.yaml:26-30):**
```yaml
- name: Lint with Checkstyle
  run: mvn checkstyle:check

- name: Run tests
  run: mvn test
```

**Property Version Pattern (from pom.xml:29-34):**
```xml
<properties>
    <java.version>21</java.version>
    <org.mapstruct.version>1.5.5.Final</org.mapstruct.version>
</properties>
```

---

## IMPLEMENTATION PLAN

### Phase 1: Foundation — Lombok Configuration

Create `lombok.config` at the project root to add `@lombok.Generated` annotation to all Lombok-generated code. JaCoCo (since 0.8.2) automatically excludes any code annotated with an annotation whose name contains "Generated".

### Phase 2: Core Implementation — JaCoCo Maven Plugin

Add the JaCoCo plugin to `pom.xml` with three executions:
1. `prepare-agent` — Instruments the JVM agent before tests run
2. `report` — Generates HTML/XML/CSV coverage reports after test phase
3. `check` — Enforces minimum coverage thresholds during verify phase

Configure exclusions for generated/boilerplate code (DTOs, MapperImpl, config classes, entities, enums, main application class).

### Phase 3: Integration — CI Pipeline

Update the GitHub Actions CI workflow to use `mvn verify` instead of `mvn test` so that coverage thresholds are enforced in the pipeline.

### Phase 4: Validation

Run full build to verify JaCoCo integration, inspect reports, and confirm thresholds are reasonable for the current test suite.

---

## STEP-BY-STEP TASKS

IMPORTANT: Execute every task in order, top to bottom. Each task is atomic and independently testable.

### Task 1: CREATE `lombok.config`

- **IMPLEMENT**: Create `lombok.config` at project root (`/home/joelfmjr/ifrn/curso/integrador/sigesi/lombok.config`) with content:
  ```properties
  config.stopBubbling = true
  lombok.addLombokGeneratedAnnotation = true
  ```
- **PATTERN**: Standard Lombok configuration file, recognized automatically by Lombok annotation processor
- **GOTCHA**: The file MUST be at the project root (same level as `pom.xml`). `config.stopBubbling = true` prevents Lombok from looking for config files in parent directories.
- **GOTCHA**: After creating this file, a `mvn clean compile` is needed for the `@Generated` annotations to be applied to newly compiled classes.
- **VALIDATE**: `ls -la /home/joelfmjr/ifrn/curso/integrador/sigesi/lombok.config`

### Task 2: UPDATE `pom.xml` — Add JaCoCo Version Property

- **IMPLEMENT**: Add JaCoCo version property in the `<properties>` section (after line 34):
  ```xml
  <jacoco.version>0.8.14</jacoco.version>
  ```
- **PATTERN**: Mirror existing version properties pattern (`pom.xml:32-34`)
- **VALIDATE**: `grep 'jacoco.version' pom.xml`

### Task 3: UPDATE `pom.xml` — Add JaCoCo Plugin

- **IMPLEMENT**: Add the JaCoCo Maven plugin AFTER the checkstyle plugin (after line 219, before `</plugins>`). The plugin must include:

  **Execution 1: prepare-agent** (default phase: initialize)
  - Goal: `prepare-agent`
  - Purpose: Sets up the JaCoCo Java agent to record coverage during test execution

  **Execution 2: report** (phase: test)
  - Goal: `report`
  - Generates HTML, XML, and CSV reports at `target/site/jacoco/`

  **Execution 3: check** (phase: verify)
  - Goal: `check`
  - Enforces coverage thresholds with `haltOnFailure=true`
  - Rules:
    - BUNDLE level: LINE >= 50%, BRANCH >= 40%
    - (Conservative starting thresholds — can be increased later)

  **Global Exclusions** (in top-level `<configuration>`):
  - `com/sigesi/sigesi/SigesiApplication.class` — Main application class
  - `com/sigesi/sigesi/config/**` — All configuration classes
  - `com/sigesi/sigesi/authentication/**` — Authentication classes
  - `com/sigesi/sigesi/**/dtos/**` — All DTO classes across all modules
  - `com/sigesi/sigesi/**/*MapperImpl.class` — MapStruct generated mapper implementations
  - `com/sigesi/sigesi/**/enums/**` — Enum packages
  - `com/sigesi/sigesi/pessoas/SexoEnum.class` — Standalone enum
  - `com/sigesi/sigesi/solicitacoes/SolicitacaoAssunto.class` — Standalone enum
  - `com/sigesi/sigesi/solicitacoes/SolicitacaoStatus.class` — Standalone enum
  - `com/sigesi/sigesi/demandas/DemandaStatus.class` — Standalone enum
  - `com/sigesi/sigesi/documentos/DocumentoTipo.class` — Standalone enum
  - `com/sigesi/sigesi/storage/StorageException.class` — Simple exception class
  - `com/sigesi/sigesi/arquivos/validation/InvalidFileException.class` — Simple exception class
  - `com/sigesi/sigesi/config/ConflictException.class` — Simple exception class
  - `com/sigesi/sigesi/config/NotFoundException.class` — Simple exception class
  - `com/sigesi/sigesi/notifications/**` — Event publishing (no tests)

- **PATTERN**: Follow same XML structure as checkstyle plugin (`pom.xml:199-219`)
- **IMPORTS**: No new dependencies needed — JaCoCo plugin includes its own runtime
- **GOTCHA**: The `<excludes>` block MUST be in the top-level `<configuration>`, NOT inside any `<execution>`. This ensures exclusions apply to all goals (prepare-agent, report, check).
- **GOTCHA**: Exclude patterns use forward slashes (`/`) and refer to compiled `.class` files, not `.java` source files.
- **GOTCHA**: Do NOT configure `maven-surefire-plugin` with `forkCount=0` — JaCoCo requires forked JVMs (Spring Boot parent already configures this correctly).
- **VALIDATE**: `mvn help:effective-pom | grep -A 5 jacoco`

### Task 4: UPDATE `.github/workflows/ci.yaml` — Add Coverage Step

- **IMPLEMENT**: Replace `mvn test` with `mvn verify` in the CI pipeline so that the JaCoCo `check` goal (bound to verify phase) is executed. Also add a step to upload the coverage report as an artifact for review.

  Updated steps:
  ```yaml
  - name: Run tests with coverage
    run: mvn verify

  - name: Upload coverage report
    if: always()
    uses: actions/upload-artifact@v4
    with:
      name: jacoco-report
      path: target/site/jacoco/
      retention-days: 14
  ```

- **PATTERN**: Follow existing CI step pattern (`ci.yaml:26-30`)
- **GOTCHA**: Use `if: always()` on the upload step so the report is uploaded even if the coverage check fails (useful for debugging).
- **GOTCHA**: The `mvn verify` command already includes `compile`, `test`, and `verify` phases, so the separate `mvn test` step is replaced, not added alongside.
- **VALIDATE**: Verify file syntax: `cat .github/workflows/ci.yaml`

### Task 5: VALIDATE — Run Full Build with Coverage

- **IMPLEMENT**: Execute the full build to verify JaCoCo integration:
  ```bash
  mvn clean verify
  ```
- **VALIDATE**: Check that the following files are generated:
  - `target/site/jacoco/index.html` (HTML report)
  - `target/site/jacoco/jacoco.xml` (XML report)
  - `target/site/jacoco/jacoco.csv` (CSV report)
- **VALIDATE**: `ls -la target/site/jacoco/`
- **GOTCHA**: If coverage is below the configured thresholds, the build will fail. In that case, temporarily lower the thresholds or use `mvn verify -Djacoco.skip=true` to skip coverage check while investigating.
- **GOTCHA**: Always run `mvn clean` before generating coverage reports to avoid stale class files.

---

## TESTING STRATEGY

### No New Test Classes Required

This feature adds tooling/configuration only — no new Java test classes are needed. The existing 27 test classes will be instrumented automatically by JaCoCo.

### Validation Points

1. JaCoCo agent properly instruments tests (coverage data file `target/jacoco.exec` is created)
2. Coverage reports are generated in all three formats (HTML, XML, CSV)
3. Lombok-generated code is excluded (verify by checking HTML report — Lombok methods should not appear)
4. MapStruct-generated code is excluded (verify `*MapperImpl` classes are not in report)
5. DTOs, config, and other excluded classes are not in the report
6. Coverage thresholds are enforced (build fails if below minimums)
7. CI pipeline runs `mvn verify` and uploads report artifact

---

## VALIDATION COMMANDS

Execute every command to ensure zero regressions and 100% feature correctness.

### Level 1: Syntax & Style

```bash
# Verify pom.xml is valid XML
mvn validate

# Run checkstyle to ensure no violations introduced
mvn checkstyle:check
```

### Level 2: Compilation

```bash
# Clean compile to apply lombok.config changes
mvn clean compile
```

### Level 3: Tests with Coverage

```bash
# Run tests and generate coverage report
mvn clean test

# Verify report files exist
ls target/site/jacoco/index.html
ls target/site/jacoco/jacoco.xml
ls target/site/jacoco/jacoco.csv
```

### Level 4: Full Verification (with threshold enforcement)

```bash
# Run full verify phase (includes coverage check)
mvn clean verify
```

### Level 5: Manual Validation

```bash
# Open HTML coverage report in browser
xdg-open target/site/jacoco/index.html

# Verify exclusions are working:
# 1. DTOs should NOT appear in the report
# 2. MapperImpl classes should NOT appear
# 3. Config classes should NOT appear
# 4. SigesiApplication should NOT appear
# 5. Lombok getters/setters should NOT inflate missed coverage
```

---

## ACCEPTANCE CRITERIA

- [x] `lombok.config` file exists at project root with `lombok.addLombokGeneratedAnnotation = true`
- [ ] JaCoCo Maven Plugin 0.8.14 is configured in `pom.xml` with prepare-agent, report, and check goals
- [ ] Coverage reports (HTML, XML, CSV) are generated at `target/site/jacoco/`
- [ ] Generated code is excluded: DTOs, MapperImpl, config, authentication, enums, exceptions, SigesiApplication
- [ ] Lombok-generated code is excluded via `@Generated` annotation
- [ ] Coverage thresholds are enforced (LINE >= 50%, BRANCH >= 40%) during `mvn verify`
- [ ] Build fails when coverage drops below thresholds
- [ ] CI pipeline updated to run `mvn verify` and upload coverage report artifact
- [ ] All existing tests continue to pass
- [ ] No checkstyle violations introduced
- [ ] `mvn clean verify` completes successfully

---

## COMPLETION CHECKLIST

- [ ] Task 1: `lombok.config` created
- [ ] Task 2: JaCoCo version property added to `pom.xml`
- [ ] Task 3: JaCoCo plugin configured in `pom.xml`
- [ ] Task 4: CI pipeline updated with coverage step
- [ ] Task 5: Full build validated successfully
- [ ] Coverage reports generated and inspected
- [ ] Exclusions verified in HTML report
- [ ] All validation commands passed

---

## NOTES

### Threshold Strategy

The initial thresholds (LINE 50%, BRANCH 40%) are intentionally conservative. The project has 27 test classes for ~126 source files, but many modules only have Controller tests (no Service or Entity tests). After the initial adoption:

- **Phase 2** (after writing more tests): Increase to LINE 70%, BRANCH 60%
- **Phase 3** (production-ready): Increase to LINE 80%, BRANCH 70%

### Skipping Coverage Temporarily

If needed during development, coverage checks can be skipped:
```bash
mvn verify -Djacoco.skip=true
```

### Future Enhancements

1. **SonarQube Integration**: The XML report at `target/site/jacoco/jacoco.xml` is ready for SonarQube via `sonar.coverage.jacoco.xmlReportPaths`
2. **Codecov Integration**: Add `codecov/codecov-action` to GitHub Actions for PR coverage comments
3. **Per-Class Thresholds**: Add CLASS-level rules to prevent a single well-tested class from masking untested ones
4. **Merge Report**: If integration tests are added later, use `jacoco:merge` to combine execution data

### JaCoCo + Lombok Compatibility

The `lombok.config` file is the standard and recommended approach. Since JaCoCo 0.8.2, any annotation whose simple name contains "Generated" (regardless of package) causes JaCoCo to skip that code element. Lombok's `@lombok.Generated` annotation satisfies this condition when `lombok.addLombokGeneratedAnnotation = true` is set.

<!-- EOF -->
