"""Email service for sending notifications via SMTP."""
import logging
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from pathlib import Path

import aiosmtplib
from jinja2 import Environment, FileSystemLoader, select_autoescape

from app.config import settings

logger = logging.getLogger(__name__)


class EmailService:
    """Service for sending emails via SMTP."""

    def __init__(self):
        """Initialize email service with Jinja2 template engine."""
        template_dir = Path(__file__).parent.parent / "templates" / "email"
        self.jinja_env = Environment(
            loader=FileSystemLoader(str(template_dir)),
            autoescape=select_autoescape(["html", "xml"]),
        )

    async def send_email(
        self, to_email: str, subject: str, template_name: str, context: dict
    ) -> bool:
        """
        Send an email using a Jinja2 template.

        Args:
            to_email: Recipient email address
            subject: Email subject
            template_name: Name of the template file (e.g., "demand_assigned.html")
            context: Dictionary with variables for template rendering

        Returns:
            bool: True if email was sent successfully, False otherwise
        """
        try:
            # Render template
            template = self.jinja_env.get_template(template_name)
            html_content = template.render(**context)

            # Create message
            message = MIMEMultipart("alternative")
            message["From"] = f"{settings.smtp_from_name} <{settings.smtp_from_email}>"
            message["To"] = to_email
            message["Subject"] = subject

            # Attach HTML content
            html_part = MIMEText(html_content, "html", "utf-8")
            message.attach(html_part)

            # Send email
            await aiosmtplib.send(
                message,
                hostname=settings.smtp_host,
                port=settings.smtp_port,
                username=settings.smtp_username,
                password=settings.smtp_password,
                start_tls=True,  # Required for port 587
            )

            logger.info(f"Email sent successfully to {to_email}")
            return True

        except Exception as e:
            logger.error(f"Failed to send email to {to_email}: {str(e)}")
            return False
