"""Custom social account adapter for SIGESI."""

import logging

from allauth.socialaccount.adapter import DefaultSocialAccountAdapter
from django.conf import settings

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
