"""Role-based permission classes for DRF."""

from rest_framework.permissions import BasePermission


class IsActiveAuthenticated(BasePermission):
    """Require authenticated user with ativo=True."""

    message = "Autenticacao necessaria ou usuario inativo."

    def has_permission(self, request, view):
        """Check user is authenticated and active."""
        return request.user and request.user.is_authenticated and request.user.ativo


class IsAdmin(BasePermission):
    """Only ADMIN role can access."""

    message = "Acesso restrito a administradores."

    def has_permission(self, request, view):
        """Check user has ADMIN role."""
        return request.user and request.user.is_authenticated and request.user.ativo and request.user.role == "ADMIN"


class IsOperadorOrAdmin(BasePermission):
    """OPERADOR or ADMIN roles can access."""

    message = "Acesso restrito a operadores e administradores."

    def has_permission(self, request, view):
        """Check user has OPERADOR or ADMIN role."""
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
        """Check user has any valid role."""
        return (
            request.user
            and request.user.is_authenticated
            and request.user.ativo
            and request.user.role in ("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
        )
