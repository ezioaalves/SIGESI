# Feature: Implementar Logout do Sistema

## Feature Description

Implementar a funcionalidade de logout no sistema SIGESI, permitindo que usuários autenticados via OAuth2 (Google) encerrem sua sessão de forma segura. O backend precisa expor um endpoint de logout que invalide a sessão HTTP do usuário e retorne uma resposta adequada para o frontend (SPA em localhost:3000) redirecionar o usuário à tela de login.

## User Story

As a authenticated user (any role: CIDADAO, AGENTE, OPERADOR, ADMIN)
I want to logout from the system
So that my session is securely terminated and I need to re-authenticate to access protected resources

## Problem Statement

Currently, the SIGESI Spring Security configuration (`SpringConfig.java`) does NOT configure any logout behavior. There is no `/api/auth/logout` endpoint or `.logout()` configuration in the security filter chain. Authenticated users have no way to programmatically end their session from the frontend application.

## Solution Statement

Add Spring Security `.logout()` configuration to the security filter chain in `SpringConfig.java` that:
1. Exposes a `POST /api/auth/logout` endpoint
2. Invalidates the HTTP session
3. Clears the security context
4. Clears authentication cookies
5. Returns an HTTP 200 JSON response (instead of redirect) since the frontend is a SPA

A configurable logout redirect URL property will be added to `application.properties` for flexibility.

## Feature Metadata

**Feature Type**: New Capability
**Estimated Complexity**: Low
**Primary Systems Affected**: `config/SpringConfig.java`, `application.properties`
**Dependencies**: None (uses Spring Security built-in logout support)

---

## CONTEXT REFERENCES

### Relevant Codebase Files

- `src/main/java/com/sigesi/sigesi/config/SpringConfig.java` (lines 24-47) - Why: Security filter chain where `.logout()` must be added alongside existing `.oauth2Login()` configuration
- `src/main/java/com/sigesi/sigesi/config/OAuth2LoginSuccessHandler.java` (lines 1-38) - Why: Shows the pattern for configurable redirect URLs via `@Value`
- `src/main/java/com/sigesi/sigesi/config/GlobalExceptionHandler.java` (lines 1-94) - Why: Shows the error response pattern used in the project
- `src/main/java/com/sigesi/sigesi/usuarios/UsuarioController.java` (lines 1-67) - Why: Shows the controller and endpoint patterns, `/api/usuarios/me` as auth context reference
- `src/main/resources/application.properties` (lines 31-33) - Why: Where OAuth2 redirect properties are defined; logout redirect will be added here
- `src/test/java/com/sigesi/sigesi/usuarios/UsuarioControllerTest.java` (lines 1-138) - Why: Test pattern reference using `@WebMvcTest`, `MockMvc`, `@AutoConfigureMockMvc(addFilters = false)`
- `checkstyle.xml` (lines 1-100) - Why: Checkstyle rules that must be followed (Javadoc on types, max line 140, max method 50 lines, etc.)

### New Files to Create

None. This feature only modifies existing files.

### Relevant Documentation

- [Spring Security Logout Documentation](https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html)
  - Specific section: Configuring Logout with HttpSecurity
  - Why: Official reference for `.logout()` DSL configuration
- [Spring Security LogoutSuccessHandler](https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html#jc-logout-success-handler)
  - Specific section: LogoutSuccessHandler
  - Why: Used to return JSON 200 response instead of redirect for SPA clients

### Patterns to Follow

**Property Configuration Pattern** (from `SpringConfig.java:21-22` and `OAuth2LoginSuccessHandler.java:22`):
```java
@Value("${app.oauth2.failure-redirect}")
private String failureRedirect;
```

**Security Filter Chain Pattern** (from `SpringConfig.java:25-47`):
```java
http
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(authorize -> authorize
        // ... existing rules
        .anyRequest().authenticated())
    .oauth2Login(oauth2 -> oauth2
        // ... existing config
    );
```

**Naming Conventions:**
- Properties use dot-notation: `app.oauth2.success-redirect`
- Environment variables use uppercase: `OAUTH2_SUCCESS_REDIRECT`
- Java fields use camelCase: `successRedirectUrl`

---

## IMPLEMENTATION PLAN

### Phase 1: Configuration

Add the logout redirect URL property to `application.properties` and `.env.example`.

### Phase 2: Core Implementation

Add `.logout()` configuration to the Spring Security filter chain in `SpringConfig.java`. This includes:
- Setting the logout URL to `/api/auth/logout`
- Invalidating the HTTP session
- Clearing authentication
- Deleting cookies (JSESSIONID)
- Configuring a custom `LogoutSuccessHandler` that returns HTTP 200 with JSON body (for SPA compatibility)

### Phase 3: Testing & Validation

Run Checkstyle, run tests, and validate the endpoint manually.

---

## STEP-BY-STEP TASKS

IMPORTANT: Execute every task in order, top to bottom. Each task is atomic and independently testable.

### Task 1: UPDATE `application.properties` - Add logout redirect property

- **IMPLEMENT**: Add a new property `app.oauth2.logout-redirect` with default value pointing to frontend login page
- **PATTERN**: Mirror existing redirect properties at lines 31-33 of `application.properties`
- **GOTCHA**: Use `${VARIABLE:default}` syntax for environment variable override support
- **VALIDATE**: `grep 'logout-redirect' src/main/resources/application.properties`

Add after line 33 of `application.properties`:
```properties
app.oauth2.logout-redirect=${OAUTH2_LOGOUT_REDIRECT:http://localhost:3000/login}
```

### Task 2: UPDATE `.env.example` - Document the new environment variable

- **IMPLEMENT**: Add `OAUTH2_LOGOUT_REDIRECT` to `.env.example` with a commented description
- **PATTERN**: Mirror existing environment variable documentation format
- **VALIDATE**: `grep 'LOGOUT' .env.example`

Add after existing OAuth variables:
```
OAUTH2_LOGOUT_REDIRECT=http://localhost:3000/login
```

### Task 3: UPDATE `SpringConfig.java` - Add logout configuration to security filter chain

- **IMPLEMENT**: Add `.logout()` block to the `HttpSecurity` configuration chain, between `.authorizeHttpRequests()` and `.oauth2Login()`. Configure:
  - `logoutUrl("/api/auth/logout")` - endpoint that triggers logout (POST)
  - `invalidateHttpSession(true)` - clear the HTTP session
  - `clearAuthentication(true)` - clear the security context authentication
  - `deleteCookies("JSESSIONID")` - remove session cookie
  - `logoutSuccessHandler(...)` - return HTTP 200 JSON response for SPA compatibility
- **IMPORTS**: Add `import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;` and `import org.springframework.http.HttpStatus;`
- **PATTERN**: Mirror the existing lambda-based configuration style used in `.csrf()` and `.oauth2Login()` blocks (SpringConfig.java:27-44)
- **GOTCHA**: The logout endpoint must NOT be behind authentication. Add `.requestMatchers("/api/auth/logout").authenticated()` before `.anyRequest().authenticated()` to ensure it requires an active session but allows the logout POST
- **GOTCHA**: Since CSRF is disabled, `POST /api/auth/logout` will work without a CSRF token
- **GOTCHA**: Use `HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)` to return 200 instead of redirect (SPA frontend pattern)
- **GOTCHA**: Checkstyle max method length is 50 lines - ensure the method stays within limit after adding logout config
- **GOTCHA**: Checkstyle max line length is 140 characters - break long lines appropriately
- **VALIDATE**: `mvn checkstyle:check`

The security filter chain should look like:
```java
http
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(authorize -> authorize
        // ... existing matchers ...
        .anyRequest().authenticated())
    .logout(logout -> logout
        .logoutUrl("/api/auth/logout")
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID")
        .logoutSuccessHandler(
            new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
    .oauth2Login(oauth2 -> oauth2
        // ... existing config ...
    );
```

### Task 4: VALIDATE - Run Checkstyle

- **IMPLEMENT**: Run Checkstyle to ensure no violations
- **VALIDATE**: `mvn checkstyle:check`

### Task 5: VALIDATE - Run tests

- **IMPLEMENT**: Run all existing tests to confirm no regressions
- **VALIDATE**: `mvn test`

### Task 6: VALIDATE - Build the project

- **IMPLEMENT**: Full build to confirm everything compiles and passes
- **VALIDATE**: `mvn clean install`

---

## TESTING STRATEGY

### Unit Tests

No new unit tests are strictly required for this feature because:
- The logout is configured entirely via Spring Security DSL (built-in framework behavior)
- Existing controller tests use `@AutoConfigureMockMvc(addFilters = false)` which bypasses security filters
- The `HttpStatusReturningLogoutSuccessHandler` is a Spring-provided class that is already tested by the framework

However, if integration testing is desired, a dedicated test could be added later.

### Integration Tests

Not required for this low-complexity feature. The logout behavior is a standard Spring Security feature being configured, not custom logic.

### Edge Cases

- Calling `POST /api/auth/logout` when not authenticated: Spring Security will return 401/403 by default (since `anyRequest().authenticated()` is configured)
- Calling `GET /api/auth/logout` instead of POST: Will return 405 Method Not Allowed (Spring Security logout defaults to POST)
- Session already expired: Spring Security handles this gracefully and still returns success

---

## VALIDATION COMMANDS

Execute every command to ensure zero regressions and 100% feature correctness.

### Level 1: Syntax & Style

```bash
mvn checkstyle:check
```

### Level 2: Unit Tests

```bash
mvn test
```

### Level 3: Full Build

```bash
mvn clean install
```

### Level 4: Manual Validation

After starting the application with `mvn spring-boot:run`:

1. Login via OAuth2 at `http://localhost:8080/oauth2/authorization/google`
2. Verify you are authenticated by calling `GET /api/usuarios/me`
3. Call `POST /api/auth/logout` (no body needed)
4. Expect HTTP 200 response
5. Call `GET /api/usuarios/me` again and expect 401/302 (redirect to login)

```bash
# After logging in and obtaining a session cookie:
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt -c cookies.txt -v
# Expected: HTTP 200

curl http://localhost:8080/api/usuarios/me -b cookies.txt -v
# Expected: HTTP 302 (redirect to OAuth2 login) or 401
```

---

## ACCEPTANCE CRITERIA

- [x] `POST /api/auth/logout` endpoint is available and triggers logout
- [x] HTTP session is invalidated after logout
- [x] JSESSIONID cookie is deleted after logout
- [x] Security context/authentication is cleared after logout
- [x] Endpoint returns HTTP 200 (not redirect) for SPA compatibility
- [x] All existing tests pass with zero regressions
- [x] Checkstyle validation passes with zero violations
- [x] Project builds successfully with `mvn clean install`
- [x] `application.properties` has configurable logout redirect URL
- [x] `.env.example` documents the new environment variable

---

## COMPLETION CHECKLIST

- [ ] All tasks completed in order
- [ ] Each task validation passed immediately
- [ ] All validation commands executed successfully
- [ ] Full test suite passes (unit + integration)
- [ ] No linting or type checking errors
- [ ] Manual testing confirms feature works
- [ ] Acceptance criteria all met
- [ ] Code reviewed for quality and maintainability

---

## NOTES

### Design Decisions

1. **`HttpStatusReturningLogoutSuccessHandler` over redirect**: Since SIGESI uses a separate SPA frontend (localhost:3000), the logout endpoint should return an HTTP 200 status with empty body instead of performing a server-side redirect. The frontend will handle navigation after receiving the success response.

2. **Endpoint path `/api/auth/logout`**: Placed under `/api/auth/` namespace to separate authentication-related endpoints from resource CRUD endpoints. This follows REST API conventions.

3. **No custom LogoutHandler needed**: Spring Security's built-in session invalidation and cookie deletion are sufficient. No need for custom token revocation since the app uses server-side sessions with OAuth2 (not JWT tokens).

4. **CSRF disabled**: Since CSRF is already disabled in the project, `POST /api/auth/logout` will work without a CSRF token. This is acceptable because the API is designed for use by a frontend SPA that authenticates via OAuth2 sessions.

### Future Considerations

- If the app migrates to JWT/stateless tokens, the logout mechanism will need to be updated to include token blacklisting
- Consider adding CORS configuration if frontend and backend are on different origins in production
- Consider adding an `OidcClientInitiatedLogoutSuccessHandler` if Google OpenID Connect RP-Initiated Logout is desired (to also sign out from Google)

<!-- EOF -->
