# Feature: Authentication & Authorization (Phase 3)

## Summary

Implement Google OAuth2 authentication via django-allauth in headless mode for the React SPA frontend, plus role-based permission classes for DRF. This includes installing django-allauth with socialaccount and headless extras, configuring Google as an OAuth2 provider, creating a custom social account adapter to map Google profile data to the custom Usuario model fields (picture_url, provider, ativo, role), implementing admin user auto-creation from ADMIN_EMAIL, building DRF permission classes for the four roles (CIDADAO, OPERADOR, AGENTE, ADMIN), and adding a logout endpoint.

## User Story

As a backend developer migrating from Spring Boot to Django
I want Google OAuth2 login with role-based access control working
So that the React frontend can authenticate users and enforce permissions on API endpoints

## Problem Statement

The Django project has all 12 models and migrations from Phase 2, but no authentication or authorization. The Spring Boot app uses Spring Security OAuth2 with Google, custom OIDC user service, role-based endpoint restrictions, and auto-admin creation. All of this must be replicated in Django using django-allauth's headless mode for the React SPA at localhost:3000.

## Solution Statement

Use django-allauth 65.x with headless mode and Google social provider. Session-based authentication (not JWT) matches the Spring Boot approach. A custom `SocialAccountAdapter` handles mapping Google profile data to Usuario fields and auto-promoting ADMIN_EMAIL users. DRF permission classes check the Usuario.role field. The allauth headless API provides login/logout/session endpoints the React frontend can use.

## Metadata

| Field            | Value                                                     |
| ---------------- | --------------------------------------------------------- |
| Type             | NEW_CAPABILITY                                            |
| Complexity       | HIGH                                                      |
| Systems Affected | config/settings, config/urls, apps/usuarios, new apps/core |
| Dependencies     | django-allauth[socialaccount,headless] 65.x               |
| Estimated Tasks  | 10                                                        |

---

## UX Design

### Before State
```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                              BEFORE STATE                                   ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║   ┌──────────────┐         ┌──────────────┐         ┌──────────────┐        ║
║   │ React SPA    │ ──GET──►│ Django API   │ ──200──►│ Any response │        ║
║   │ localhost:3000│         │ localhost:8000│         │ (no auth!)   │        ║
║   └──────────────┘         └──────────────┘         └──────────────┘        ║
║                                                                             ║
║   USER_FLOW: Anyone can hit any endpoint without authentication             ║
║   PAIN_POINT: No login, no roles, no permissions, no session management     ║
║   DATA_FLOW: Request → Django → Response (no auth checks)                   ║
║                                                                             ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

### After State
```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                               AFTER STATE                                   ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║   ┌──────────────┐  form POST  ┌──────────────┐  302   ┌──────────────┐    ║
║   │ React SPA    │────────────►│ allauth      │───────►│ Google OAuth │    ║
║   │ localhost:3000│             │ /provider/   │        │ consent      │    ║
║   └──────────────┘             │ redirect     │        └──────┬───────┘    ║
║          ▲                     └──────────────┘               │            ║
║          │ redirect + session cookie                          │            ║
║          │                     ┌──────────────┐  callback     │            ║
║          └─────────────────────│ allauth      │◄──────────────┘            ║
║                                │ /callback/   │                            ║
║                                └──────┬───────┘                            ║
║                                       │ creates/updates Usuario            ║
║                                       │ sets role, picture_url, provider   ║
║                                       ▼                                    ║
║                                ┌──────────────┐                            ║
║   Authenticated requests:      │ Django       │                            ║
║   GET /api/solicitacoes/ ────► │ Session Auth │──► Permission check        ║
║                                │ + Role check │    (CIDADAO/OPERADOR/...)  ║
║                                └──────────────┘                            ║
║                                                                             ║
║   USER_FLOW: React → Google OAuth → Django session → role-gated API        ║
║   VALUE_ADD: Secure auth with same UX as Spring Boot version               ║
║   DATA_FLOW: Request → Session → Usuario.role → Permission → Response      ║
║                                                                             ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

### Interaction Changes
| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| `/_allauth/browser/v1/auth/session` | N/A | Returns auth status | React checks if logged in |
| `/_allauth/browser/v1/auth/provider/redirect` | N/A | Initiates Google login | React form POST starts OAuth flow |
| `/_allauth/browser/v1/auth/session` (DELETE) | N/A | Destroys session | Logout functionality |
| `/api/*` endpoints | No auth | Session + role required | Protected by permissions |
| `/accounts/google/login/callback/` | N/A | OAuth callback handler | Google redirects here |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/config/settings/base.py` | all | Current INSTALLED_APPS, MIDDLEWARE, REST_FRAMEWORK to UPDATE |
| P0 | `backend/config/urls.py` | all | Current URL config to UPDATE |
| P0 | `backend/apps/usuarios/models.py` | all | Usuario model with Role enum - adapter must populate these fields |
| P0 | `backend/apps/usuarios/apps.py` | all | Must add `ready()` method for signals |
| P1 | `src/main/java/com/sigesi/sigesi/config/SpringConfig.java` | all | Endpoint permission mapping to REPLICATE |
| P1 | `src/main/java/com/sigesi/sigesi/usuarios/UsuarioService.java:59-80` | processOAuthPostLogin | OAuth post-login logic to REPLICATE |
| P1 | `src/main/java/com/sigesi/sigesi/config/AdminUserInitializer.java` | all | Admin auto-creation to REPLICATE |
| P1 | `src/main/java/com/sigesi/sigesi/authentication/CustomOidcUserService.java` | all | Inactive user check to REPLICATE |
| P2 | `backend/pyproject.toml` | all | Dependencies to UPDATE |
| P2 | `backend/config/settings/local.py` | all | Dev overrides context |
| P2 | `backend/config/settings/production.py` | all | Prod security context |

**External Documentation:**
| Source | Section | Why Needed |
|--------|---------|------------|
| [django-allauth Headless Installation](https://docs.allauth.org/en/latest/headless/installation.html) | Setup | INSTALLED_APPS, MIDDLEWARE, URL patterns |
| [django-allauth Headless Configuration](https://docs.allauth.org/en/dev/headless/configuration.html) | HEADLESS_ONLY, HEADLESS_FRONTEND_URLS | Headless mode settings |
| [django-allauth Google Provider](https://docs.allauth.org/en/dev/socialaccount/providers/google.html) | SOCIALACCOUNT_PROVIDERS | Google client config |
| [django-allauth Social Account Adapter](https://docs.allauth.org/en/dev/socialaccount/adapter.html) | populate_user, save_user, pre_social_login | Custom field mapping |
| [django-allauth CORS for SPA](https://docs.allauth.org/en/dev/headless/cors.html) | CSRF_TRUSTED_ORIGINS, CORS headers | Cross-origin cookie handling |
| [django-allauth Account Configuration](https://docs.allauth.org/en/dev/account/configuration.html) | ACCOUNT_LOGIN_METHODS, ACCOUNT_SIGNUP_FIELDS | Modern settings (deprecation-free) |
| [DRF Permissions](https://www.django-rest-framework.org/api-guide/permissions/) | Custom permissions | Role-based permission classes |
| [django-allauth React SPA Example](https://github.com/pennersr/django-allauth/tree/main/examples/react-spa) | Full working example | Reference implementation |

---

## Patterns to Mirror

**SPRING BOOT ENDPOINT PERMISSIONS:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/config/SpringConfig.java:30-40
// REPLICATE this permission mapping in Django:
.requestMatchers("/api/enderecos/**").hasAnyRole("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
.requestMatchers("/api/solicitacoes/**").hasAnyRole("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
.requestMatchers("/api/cemiterios/**").hasAnyRole("OPERADOR", "ADMIN")
.requestMatchers("/api/jazigos/**").hasAnyRole("OPERADOR", "ADMIN")
.requestMatchers("/api/gavetas/**").hasAnyRole("OPERADOR", "ADMIN")
.requestMatchers("/api/documentos/**").hasAnyRole("OPERADOR", "ADMIN")
.requestMatchers("/api/usuarios/me").authenticated()
.requestMatchers("/api/usuarios/**").hasRole("ADMIN")
.anyRequest().authenticated()
```

**SPRING BOOT POST-LOGIN LOGIC:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/usuarios/UsuarioService.java:59-80
// REPLICATE in CustomSocialAccountAdapter.populate_user / pre_social_login:
String email = oAuth2User.getAttribute("email");
String name = oAuth2User.getAttribute("name");
String picture = oAuth2User.getAttribute("picture");

Usuario user = usuarioRepository.findByEmail(email)
    .map(u -> { u.setName(name); u.setPictureUrl(picture); return u; })
    .orElse(Usuario.builder()
        .email(email).name(name).pictureUrl(picture)
        .provider("google").ativo(true).role(Role.CIDADAO)
        .build());
return usuarioRepository.save(user);
```

**SPRING BOOT INACTIVE USER CHECK:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/authentication/CustomOidcUserService.java:27-29
// REPLICATE in adapter or permission class:
if (!user.getAtivo()) {
    throw new OAuth2AuthenticationException("Usuário inativo. Aguardando liberação.");
}
```

**SPRING BOOT ADMIN INITIALIZER:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/config/AdminUserInitializer.java:24-48
// REPLICATE via Django post_migrate signal:
// - If ADMIN_EMAIL not set, raise error
// - If user exists and is not ADMIN, promote to ADMIN
// - If user doesn't exist, create with role=ADMIN
```

**DJANGO APP CONFIG PATTERN:**
```python
# SOURCE: backend/apps/usuarios/apps.py
# EXTEND this pattern with ready() for signals:
class UsuariosConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.usuarios"
    verbose_name = "Usuarios"
```

**DJANGO SETTINGS PATTERN:**
```python
# SOURCE: backend/config/settings/base.py:12-16
# FOLLOW this env var pattern for new settings:
env = environ.Env(
    DEBUG=(bool, False),
    ALLOWED_HOSTS=(list, []),
    CORS_ALLOWED_ORIGINS=(list, ["http://localhost:3000", "http://ezioalves.space"]),
)
```

---

## Files to Change

| File | Action | Justification |
|------|--------|---------------|
| `backend/pyproject.toml` | UPDATE | Add django-allauth[socialaccount,headless] dependency |
| `backend/config/settings/base.py` | UPDATE | Add allauth apps, middleware, auth backends, allauth settings |
| `backend/config/settings/local.py` | UPDATE | Add dev-specific session/CSRF settings |
| `backend/config/urls.py` | UPDATE | Add allauth URL patterns |
| `backend/apps/usuarios/adapters.py` | CREATE | Custom SocialAccountAdapter for Google → Usuario field mapping |
| `backend/apps/usuarios/signals.py` | CREATE | post_migrate signal for admin user auto-creation |
| `backend/apps/usuarios/apps.py` | UPDATE | Add ready() to load signals |
| `backend/apps/core/__init__.py` | CREATE | Core app package init |
| `backend/apps/core/apps.py` | CREATE | Core app config |
| `backend/apps/core/permissions.py` | CREATE | Role-based DRF permission classes |

---

## NOT Building (Scope Limits)

- **JWT/Token authentication** - Using session-based auth (matches Spring Boot approach, simpler for SPA)
- **Email/password login** - Only Google OAuth2 (matches Spring Boot, which only supports Google)
- **Email verification flow** - Google already verifies emails
- **Password reset flow** - No passwords, only OAuth2
- **Mobile app authentication** - Only browser/SPA client (X-Session-Token not needed)
- **API endpoints for modules** - Those come in Phase 4+; only auth infrastructure here
- **Rate limiting** - Not in Spring Boot, not needed for MVP
- **Two-factor authentication** - Not in Spring Boot, not needed for MVP
- **Multiple OAuth providers** - Only Google (matches Spring Boot)

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: ADD django-allauth dependency

- **ACTION**: Add django-allauth with socialaccount and headless extras to pyproject.toml
- **IMPLEMENT**: Update the `dependencies` list in `backend/pyproject.toml`:
  ```toml
  "django-allauth[socialaccount,headless]>=65.4.0",
  ```
- **THEN**: Run `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv add "django-allauth[socialaccount,headless]>=65.4.0"`
- **GOTCHA**: The `[socialaccount,headless]` extras are required. Without `headless`, the headless API endpoints won't be available. Without `socialaccount`, Google provider won't work. Version >=65.4.0 ensures we get the new `ACCOUNT_LOGIN_METHODS` setting (deprecation-free).
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python -c "import allauth; print(allauth.__version__)"`

### Task 2: UPDATE `backend/config/settings/base.py` - Add allauth configuration

- **ACTION**: Add all django-allauth settings to base.py
- **IMPLEMENT**:
  1. Add `"django.contrib.sites"` to INSTALLED_APPS (before third-party apps)
  2. Add allauth apps to INSTALLED_APPS (after third-party, before local apps):
     ```python
     # Allauth
     "allauth",
     "allauth.account",
     "allauth.headless",
     "allauth.socialaccount",
     "allauth.socialaccount.providers.google",
     ```
  3. Add `"allauth.account.middleware.AccountMiddleware"` as the LAST entry in MIDDLEWARE
  4. Add `SITE_ID = 1` after INSTALLED_APPS
  5. Add `AUTHENTICATION_BACKENDS` after AUTH_USER_MODEL:
     ```python
     AUTHENTICATION_BACKENDS = [
         "django.contrib.auth.backends.ModelBackend",
         "allauth.account.auth_backends.AuthenticationBackend",
     ]
     ```
  6. Add allauth account settings:
     ```python
     # Allauth - Account settings
     ACCOUNT_USER_MODEL_USERNAME_FIELD = None
     ACCOUNT_USER_MODEL_EMAIL_FIELD = "email"
     ACCOUNT_LOGIN_METHODS = {"email"}
     ACCOUNT_SIGNUP_FIELDS = ["email*", "password1*", "password2*"]
     ACCOUNT_EMAIL_VERIFICATION = "none"
     ACCOUNT_UNIQUE_EMAIL = True
     ```
  7. Add allauth social account settings:
     ```python
     # Allauth - Social account settings
     SOCIALACCOUNT_ADAPTER = "apps.usuarios.adapters.CustomSocialAccountAdapter"
     SOCIALACCOUNT_AUTO_SIGNUP = True
     SOCIALACCOUNT_EMAIL_AUTHENTICATION = True
     SOCIALACCOUNT_EMAIL_AUTHENTICATION_AUTO_CONNECT = True
     SOCIALACCOUNT_STORE_TOKENS = False

     SOCIALACCOUNT_PROVIDERS = {
         "google": {
             "APPS": [
                 {
                     "client_id": GOOGLE_CLIENT_ID,
                     "secret": GOOGLE_CLIENT_SECRET,
                     "key": "",
                 }
             ],
             "SCOPE": ["profile", "email"],
             "AUTH_PARAMS": {"access_type": "online"},
             "OAUTH_PKCE_ENABLED": True,
             "FETCH_USERINFO": True,
             "VERIFIED_EMAIL": True,
         }
     }
     ```
  8. Add allauth headless settings:
     ```python
     # Allauth - Headless mode
     HEADLESS_ONLY = True

     HEADLESS_FRONTEND_URLS = {
         "socialaccount_login_error": OAUTH2_FAILURE_REDIRECT,
     }
     ```
  9. Add `CSRF_TRUSTED_ORIGINS`:
     ```python
     CSRF_TRUSTED_ORIGINS = env("CSRF_TRUSTED_ORIGINS", default="http://localhost:3000,http://ezioalves.space").split(",")
     ```
  10. Update `REST_FRAMEWORK` to add authentication and permission classes:
      ```python
      REST_FRAMEWORK = {
          "DEFAULT_SCHEMA_CLASS": "drf_spectacular.openapi.AutoSchema",
          "DEFAULT_FILTER_BACKENDS": [
              "django_filters.rest_framework.DjangoFilterBackend",
          ],
          "DEFAULT_PAGINATION_CLASS": "rest_framework.pagination.PageNumberPagination",
          "PAGE_SIZE": 20,
          "DEFAULT_AUTHENTICATION_CLASSES": [
              "rest_framework.authentication.SessionAuthentication",
          ],
          "DEFAULT_PERMISSION_CLASSES": [
              "apps.core.permissions.IsActiveAuthenticated",
          ],
      }
      ```
  11. Add `"apps.core"` to INSTALLED_APPS (in local apps section)
- **GOTCHA**: `ACCOUNT_USER_MODEL_USERNAME_FIELD = None` is critical because AbstractUser has a `username` field but our OAuth flow doesn't use it. This tells allauth to not require a username. However, AbstractUser still has the field - we need to handle this in the adapter by setting a unique username (e.g., email).
- **GOTCHA**: `SOCIALACCOUNT_PROVIDERS` uses `APPS` list (not `APP` dict) - this is the newer configuration pattern that avoids needing to configure providers via Django admin SocialApp model.
- **GOTCHA**: `HEADLESS_ONLY = True` disables allauth's template-based views, but the OAuth callback URL (`/accounts/google/login/callback/`) still works.
- **GOTCHA**: `django.contrib.sites` requires `SITE_ID = 1` and its migration creates the default site.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check`

### Task 3: UPDATE `backend/config/settings/local.py` - Dev session/CSRF settings

- **ACTION**: Add development-specific settings for cross-origin session handling
- **IMPLEMENT**: Add to local.py:
  ```python
  # CSRF settings for cross-origin development
  CSRF_TRUSTED_ORIGINS = ["http://localhost:3000"]

  # Session settings for development
  SESSION_COOKIE_SAMESITE = "Lax"
  SESSION_COOKIE_HTTPONLY = True
  SESSION_COOKIE_SECURE = False
  ```
- **GOTCHA**: `SESSION_COOKIE_SECURE = False` is already in local.py but ensure `SESSION_COOKIE_SAMESITE = "Lax"` is there for the OAuth redirect flow to work in dev (Google redirects back to Django, session cookie must be sent).
- **VALIDATE**: Python syntax valid

### Task 4: UPDATE `backend/config/urls.py` - Add allauth URL patterns

- **ACTION**: Add allauth URL routes for OAuth callback and headless API
- **IMPLEMENT**:
  ```python
  """Root URL configuration for SIGESI."""

  from django.contrib import admin
  from django.http import JsonResponse
  from django.urls import include, path
  from drf_spectacular.views import (
      SpectacularAPIView,
      SpectacularRedocView,
      SpectacularSwaggerView,
  )


  def health_check(request):
      """Health check endpoint."""
      return JsonResponse({"status": "ok"})


  urlpatterns = [
      path("admin/", admin.site.urls),
      path("api/health/", health_check, name="health-check"),
      path("api/schema/", SpectacularAPIView.as_view(), name="schema"),
      path("api/schema/swagger-ui/", SpectacularSwaggerView.as_view(url_name="schema"), name="swagger-ui"),
      path("api/schema/redoc/", SpectacularRedocView.as_view(url_name="schema"), name="redoc"),
      # Allauth - OAuth2 callback endpoints (needed even with HEADLESS_ONLY=True)
      path("accounts/", include("allauth.urls")),
      # Allauth - Headless API endpoints (/_allauth/browser/v1/...)
      path("_allauth/", include("allauth.headless.urls")),
  ]
  ```
- **GOTCHA**: Both URL patterns are needed. `accounts/` handles the Google OAuth callback redirect. `_allauth/` provides the headless API for the React frontend (session check, provider redirect, logout).
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check`

### Task 5: CREATE `backend/apps/core/__init__.py` and `backend/apps/core/apps.py`

- **ACTION**: Create the core app for shared utilities (permissions)
- **IMPLEMENT**:
  - `backend/apps/core/__init__.py` - empty file
  - `backend/apps/core/apps.py`:
    ```python
    """Core app configuration."""

    from django.apps import AppConfig


    class CoreConfig(AppConfig):
        """Configuration for the Core app."""

        default_auto_field = "django.db.models.BigAutoField"
        name = "apps.core"
        verbose_name = "Core"
    ```
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python -c "from apps.core.apps import CoreConfig; print(CoreConfig.name)"`

### Task 6: CREATE `backend/apps/core/permissions.py` - Role-based DRF permissions

- **ACTION**: Create permission classes that replicate Spring Boot's role-based endpoint access
- **IMPLEMENT**:
  ```python
  """Role-based permission classes for DRF."""

  from rest_framework.permissions import BasePermission


  class IsActiveAuthenticated(BasePermission):
      """Require authenticated user with ativo=True."""

      message = "Autenticacao necessaria ou usuario inativo."

      def has_permission(self, request, view):
          return (
              request.user
              and request.user.is_authenticated
              and request.user.ativo
          )


  class IsAdmin(BasePermission):
      """Only ADMIN role can access."""

      message = "Acesso restrito a administradores."

      def has_permission(self, request, view):
          return (
              request.user
              and request.user.is_authenticated
              and request.user.ativo
              and request.user.role == "ADMIN"
          )


  class IsOperadorOrAdmin(BasePermission):
      """OPERADOR or ADMIN roles can access."""

      message = "Acesso restrito a operadores e administradores."

      def has_permission(self, request, view):
          return (
              request.user
              and request.user.is_authenticated
              and request.user.ativo
              and request.user.role in ("OPERADOR", "ADMIN")
          )


  class IsAllRoles(BasePermission):
      """Any authenticated active user with any role can access."""

      message = "Autenticacao necessaria."

      def has_permission(self, request, view):
          return (
              request.user
              and request.user.is_authenticated
              and request.user.ativo
              and request.user.role in ("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
          )
  ```
- **MAPPING from Spring Boot**:
  - `hasAnyRole("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")` → `IsAllRoles` (used for `/api/enderecos/**`, `/api/solicitacoes/**`)
  - `hasAnyRole("OPERADOR", "ADMIN")` → `IsOperadorOrAdmin` (used for `/api/cemiterios/**`, `/api/jazigos/**`, `/api/gavetas/**`, `/api/documentos/**`)
  - `hasRole("ADMIN")` → `IsAdmin` (used for `/api/usuarios/**` except `/me`)
  - `.authenticated()` → `IsActiveAuthenticated` (default for all endpoints)
- **GOTCHA**: The `ativo` check in every permission class replicates the Spring Boot behavior where inactive users (ativo=False) are blocked. In Spring Boot this was done in `CustomOidcUserService.loadUser()` by throwing `OAuth2AuthenticationException`. Here we check it at the permission level for all API requests.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/core/permissions.py`

### Task 7: CREATE `backend/apps/usuarios/adapters.py` - Custom social account adapter

- **ACTION**: Create adapter to map Google OAuth2 data to Usuario custom fields
- **IMPLEMENT**:
  ```python
  """Custom social account adapter for SIGESI."""

  import logging

  from django.conf import settings

  from allauth.socialaccount.adapter import DefaultSocialAccountAdapter

  logger = logging.getLogger(__name__)


  class CustomSocialAccountAdapter(DefaultSocialAccountAdapter):
      """Map Google OAuth2 profile data to Usuario custom fields."""

      def populate_user(self, request, sociallogin, data):
          """Populate Usuario fields from Google profile on first signup."""
          user = super().populate_user(request, sociallogin, data)

          extra_data = sociallogin.account.extra_data

          # Set custom fields from Google profile
          user.picture_url = extra_data.get("picture", "")
          user.provider = sociallogin.account.provider
          user.ativo = True
          user.role = "CIDADAO"

          # Set username to email (AbstractUser requires username)
          user.username = data.get("email", "")

          return user

      def save_user(self, request, sociallogin, form=None):
          """Save user and check for admin email promotion."""
          user = super().save_user(request, sociallogin, form)

          admin_email = getattr(settings, "ADMIN_EMAIL", "")
          if admin_email and user.email == admin_email:
              user.role = "ADMIN"
              user.is_staff = True
              user.is_superuser = True
              user.save(update_fields=["role", "is_staff", "is_superuser"])
              logger.info("User promoted to admin on signup: %s", user.email)

          return user

      def pre_social_login(self, request, sociallogin):
          """Update existing user profile data on each login."""
          super().pre_social_login(request, sociallogin)

          if sociallogin.is_existing:
              user = sociallogin.user
              extra_data = sociallogin.account.extra_data

              # Update profile picture and name on every login
              # (matches Spring Boot processOAuthPostLogin behavior)
              user.picture_url = extra_data.get("picture", "")
              name = extra_data.get("name", "")
              if name:
                  parts = name.split(" ", 1)
                  user.first_name = parts[0]
                  user.last_name = parts[1] if len(parts) > 1 else ""

              user.save(update_fields=["picture_url", "first_name", "last_name"])
  ```
- **MAPPING from Spring Boot**:
  - `processOAuthPostLogin` creates/updates user → `populate_user` (create) + `pre_social_login` (update)
  - `email`, `name`, `picture` from OAuth2User → `extra_data` from Google
  - Default `role=CIDADAO`, `ativo=true`, `provider="google"` → same defaults
  - Admin email check in `save_user` → matches `AdminUserInitializer` behavior
- **GOTCHA**: `AbstractUser` requires a `username` field. We set it to the email in `populate_user`. The `ACCOUNT_USER_MODEL_USERNAME_FIELD = None` tells allauth not to use username for login, but the DB column still needs a value.
- **GOTCHA**: `pre_social_login` fires on EVERY login (not just first). This is where we update profile data for existing users, matching Spring Boot's `processOAuthPostLogin` which always updates name and picture.
- **GOTCHA**: The inactive user check from Spring Boot's `CustomOidcUserService` is handled by the `IsActiveAuthenticated` permission class instead. Allauth will still create the session, but API requests will be blocked by the `ativo` check in permissions.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/usuarios/adapters.py`

### Task 8: CREATE `backend/apps/usuarios/signals.py` - Admin user auto-creation

- **ACTION**: Create post_migrate signal to auto-create admin user from ADMIN_EMAIL
- **IMPLEMENT**:
  ```python
  """Signals for the Usuarios app."""

  import logging

  from django.conf import settings
  from django.contrib.auth import get_user_model
  from django.db import OperationalError, ProgrammingError
  from django.db.models.signals import post_migrate
  from django.dispatch import receiver

  logger = logging.getLogger(__name__)


  @receiver(post_migrate)
  def create_admin_user(sender, **kwargs):
      """Auto-create admin user from ADMIN_EMAIL after migrations."""
      admin_email = getattr(settings, "ADMIN_EMAIL", "")
      if not admin_email:
          return

      try:
          User = get_user_model()
          user, created = User.objects.get_or_create(
              email=admin_email,
              defaults={
                  "username": admin_email,
                  "role": "ADMIN",
                  "ativo": True,
                  "is_staff": True,
                  "is_superuser": True,
                  "provider": "system",
              },
          )

          if created:
              logger.info("Admin user created: %s", admin_email)
          elif user.role != "ADMIN":
              user.role = "ADMIN"
              user.is_staff = True
              user.is_superuser = True
              user.save(update_fields=["role", "is_staff", "is_superuser"])
              logger.info("Existing user promoted to admin: %s", admin_email)
      except (OperationalError, ProgrammingError):
          # Table doesn't exist yet during initial migration
          pass
  ```
- **MAPPING from Spring Boot**:
  - `AdminUserInitializer.run()` → `post_migrate` signal handler
  - `findByEmail(adminEmail).ifPresentOrElse(...)` → `get_or_create(email=...)` with defaults
  - Role promotion if existing user is not admin → `elif user.role != "ADMIN"` block
- **GOTCHA**: The `post_migrate` signal fires after EVERY `migrate` command for EVERY app. We guard with `try/except` for the case where the usuarios table doesn't exist yet (e.g., during initial Django migration). The `OperationalError`/`ProgrammingError` catch handles this.
- **GOTCHA**: Spring Boot's `AdminUserInitializer` raises `IllegalStateException` if ADMIN_EMAIL is blank. In Django, we silently return since the env var might not be set in all environments.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/usuarios/signals.py`

### Task 9: UPDATE `backend/apps/usuarios/apps.py` - Load signals in ready()

- **ACTION**: Add ready() method to UsuariosConfig to load the signals module
- **IMPLEMENT**:
  ```python
  """Usuarios app configuration."""

  from django.apps import AppConfig


  class UsuariosConfig(AppConfig):
      """Configuration for the Usuarios app."""

      default_auto_field = "django.db.models.BigAutoField"
      name = "apps.usuarios"
      verbose_name = "Usuarios"

      def ready(self):
          """Load signals when the app is ready."""
          import apps.usuarios.signals  # noqa: F401
  ```
- **GOTCHA**: The `noqa: F401` comment is needed because ruff would flag the import as unused. The import has the side effect of registering the signal handler.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/usuarios/apps.py`

### Task 10: Run migrations and verify full setup

- **ACTION**: Run allauth migrations, verify all components work together
- **COMMANDS**:
  ```bash
  cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend

  # 1. Generate/apply allauth migrations (sites, account, socialaccount)
  uv run python manage.py migrate

  # 2. Run Django system checks
  uv run python manage.py check

  # 3. Verify allauth URLs are registered
  uv run python manage.py show_urls 2>/dev/null || uv run python -c "
  from django.urls import reverse
  print('health:', reverse('health-check'))
  print('schema:', reverse('schema'))
  "

  # 4. Verify permission classes are importable
  uv run python -c "
  from apps.core.permissions import IsActiveAuthenticated, IsAdmin, IsOperadorOrAdmin, IsAllRoles
  print('Permissions OK')
  "

  # 5. Verify adapter is importable
  uv run python -c "
  from apps.usuarios.adapters import CustomSocialAccountAdapter
  print('Adapter OK')
  "

  # 6. Run ruff on all changed files
  uv run ruff check apps/core/ apps/usuarios/
  uv run ruff format --check apps/core/ apps/usuarios/
  ```
- **EXPECT**: All commands pass with exit 0
- **GOTCHA**: The `django.contrib.sites` migration creates a default Site with domain `example.com`. This is fine for development. In production, the Site domain should be updated via Django admin or a data migration.

---

## Testing Strategy

### Verification Tests (No unit tests per PRD)

| Check | What It Validates | Command |
|-------|-------------------|---------|
| Django check | All settings valid | `uv run python manage.py check` |
| Migrations | Allauth tables created | `uv run python manage.py migrate --check` |
| Ruff lint | Code style | `uv run ruff check apps/core/ apps/usuarios/` |
| Import permissions | Classes work | `uv run python -c "from apps.core.permissions import IsActiveAuthenticated"` |
| Import adapter | Adapter works | `uv run python -c "from apps.usuarios.adapters import CustomSocialAccountAdapter"` |
| Import signals | Signals registered | `uv run python -c "import apps.usuarios.signals"` |
| Admin user created | Signal works | `uv run python manage.py shell -c "from django.contrib.auth import get_user_model; User = get_user_model(); print(User.objects.filter(role='ADMIN').exists())"` |
| Allauth URLs | Routes registered | `uv run python -c "from django.urls import reverse; reverse('google_login')"` |

### Edge Cases Checklist

- [ ] ADMIN_EMAIL not set - signal should silently skip
- [ ] ADMIN_EMAIL user already exists with CIDADAO role - should promote to ADMIN
- [ ] ADMIN_EMAIL user already exists with ADMIN role - should be idempotent (no error)
- [ ] GOOGLE_CLIENT_ID/SECRET empty - allauth should still load (OAuth just won't work)
- [ ] Inactive user (ativo=False) - permission classes should block API access
- [ ] User with no role - should be blocked (role defaults to CIDADAO, so unlikely)
- [ ] `django.contrib.sites` migration not yet applied - post_migrate catches the error

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/core/ apps/usuarios/ config/ && uv run ruff format --check apps/core/ apps/usuarios/ config/
```

**EXPECT**: Exit 0, no errors

### Level 2: DJANGO_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check
```

**EXPECT**: System check identified no issues

### Level 3: MIGRATIONS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py migrate && uv run python manage.py showmigrations | grep -E "\[ \]"
```

**EXPECT**: All migrations applied (no `[ ]` unchecked migrations)

### Level 4: COMPONENT_VERIFICATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python -c "
from apps.core.permissions import IsActiveAuthenticated, IsAdmin, IsOperadorOrAdmin, IsAllRoles
from apps.usuarios.adapters import CustomSocialAccountAdapter
from django.contrib.auth import get_user_model
User = get_user_model()
admin_exists = User.objects.filter(role='ADMIN').exists()
print(f'Permissions: OK')
print(f'Adapter: OK')
print(f'Admin user exists: {admin_exists}')
print('ALL CHECKS PASSED')
"
```

**EXPECT**: All components importable, admin user exists

---

## Acceptance Criteria

- [ ] `uv run python manage.py check` passes with no issues
- [ ] `uv run python manage.py migrate` runs cleanly (allauth tables created)
- [ ] Admin user auto-created from ADMIN_EMAIL on migrate
- [ ] Custom `SocialAccountAdapter` populates Usuario fields (picture_url, provider, ativo, role)
- [ ] Permission classes `IsActiveAuthenticated`, `IsAdmin`, `IsOperadorOrAdmin`, `IsAllRoles` created and importable
- [ ] `REST_FRAMEWORK.DEFAULT_PERMISSION_CLASSES` set to `IsActiveAuthenticated`
- [ ] `REST_FRAMEWORK.DEFAULT_AUTHENTICATION_CLASSES` set to `SessionAuthentication`
- [ ] Allauth headless URLs registered (`/_allauth/browser/v1/...`)
- [ ] Allauth OAuth callback URL registered (`/accounts/google/login/callback/`)
- [ ] `HEADLESS_ONLY = True` set (no template-based auth views)
- [ ] `ruff check` passes on all changed files
- [ ] Inactive user (ativo=False) blocked by permission classes

---

## Completion Checklist

- [ ] Task 1: django-allauth dependency added via uv
- [ ] Task 2: base.py updated with all allauth settings
- [ ] Task 3: local.py updated with dev session/CSRF settings
- [ ] Task 4: urls.py updated with allauth routes
- [ ] Task 5: Core app created with apps.py
- [ ] Task 6: Permission classes created
- [ ] Task 7: Custom social account adapter created
- [ ] Task 8: Admin user auto-creation signal created
- [ ] Task 9: UsuariosConfig.ready() loads signals
- [ ] Task 10: Migrations applied and full verification passed
- [ ] Level 1: ruff passes
- [ ] Level 2: Django check passes
- [ ] Level 3: All migrations applied
- [ ] Level 4: Component verification passes
- [ ] All acceptance criteria met

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| allauth headless API flow differences from Spring Boot | MEDIUM | MEDIUM | OAuth flow is standard; React just submits form POST to provider redirect. Test early with real Google credentials. |
| AbstractUser username field conflict | LOW | HIGH | Set `ACCOUNT_USER_MODEL_USERNAME_FIELD = None` and populate username with email in adapter |
| CSRF token issues with cross-origin SPA | MEDIUM | MEDIUM | `CSRF_TRUSTED_ORIGINS` includes React origin; session cookies use SameSite=Lax |
| post_migrate signal fires before usuarios table exists | LOW | LOW | Wrapped in try/except OperationalError/ProgrammingError |
| Google OAuth redirect not working in dev | MEDIUM | LOW | Ensure Google Console has `http://localhost:8000/accounts/google/login/callback/` as authorized redirect URI |
| django.contrib.sites SITE_ID mismatch | LOW | MEDIUM | Use SITE_ID=1 (default); allauth creates the site during migration |
| Allauth breaking changes in future versions | LOW | LOW | Pinned to >=65.4.0; using non-deprecated settings (ACCOUNT_LOGIN_METHODS, ACCOUNT_SIGNUP_FIELDS) |

---

## Notes

**OAuth2 Flow (React SPA → Django → Google → Django → React):**
1. React creates hidden form, POSTs to `/_allauth/browser/v1/auth/provider/redirect` with `provider=google` and `callback_url=http://localhost:3000`
2. Django/allauth returns 302 → Google OAuth consent screen
3. User authorizes → Google redirects to `/accounts/google/login/callback/`
4. Allauth processes callback: exchanges code for tokens, fetches profile, calls `populate_user` (new) or `pre_social_login` (existing), creates session
5. Allauth redirects to `callback_url` (React). Session cookie is set.
6. React calls `/_allauth/browser/v1/auth/session` (GET with `credentials: include`) to verify authentication

**Google Console Configuration Required:**
- Authorized redirect URI: `http://localhost:8000/accounts/google/login/callback/`
- For production: `https://your-domain.com/accounts/google/login/callback/`

**Permission Class Usage (for Phase 4+):**
```python
# Endpoints accessible to all authenticated users (matches Spring Boot .authenticated())
class EnderecoViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAllRoles]

# Endpoints restricted to OPERADOR and ADMIN
class CemiterioViewSet(viewsets.ModelViewSet):
    permission_classes = [IsOperadorOrAdmin]

# Endpoints restricted to ADMIN only
class UsuarioViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAdmin]
```

**Allauth Headless API Endpoints Available After Setup:**
- `GET /_allauth/browser/v1/auth/session` - Check authentication status
- `DELETE /_allauth/browser/v1/auth/session` - Logout (destroy session)
- `POST /_allauth/browser/v1/auth/provider/redirect` - Start Google OAuth (form POST)
- `GET /_allauth/browser/v1/config` - Get allauth configuration

**Differences from Spring Boot Auth:**
- Spring Boot used `POST /api/auth/logout` → Django uses `DELETE /_allauth/browser/v1/auth/session` (different URL and method)
- Spring Boot custom `CustomOidcUserService` checked `ativo` during login → Django checks `ativo` at permission level on every request
- Spring Boot `JSESSIONID` cookie → Django `sessionid` cookie
- Spring Boot disabled CSRF entirely → Django keeps CSRF with `CSRF_TRUSTED_ORIGINS` for the SPA
