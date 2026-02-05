"""Tests for email service."""
import pytest

from app.services.email_service import EmailService


@pytest.mark.asyncio
async def test_email_service_template_rendering():
    """Test that email templates can be rendered."""
    service = EmailService()

    # Test template rendering (without actually sending)
    template = service.jinja_env.get_template("demand_assigned.html")
    context = {
        "recipient_name": "Test User",
        "demand_id": 123,
        "assunto": "BURACO",
        "prazo": "31/12/2024",
    }
    html = template.render(**context)

    assert "Test User" in html
    assert "123" in html
    assert "BURACO" in html
    assert "31/12/2024" in html


@pytest.mark.asyncio
async def test_email_service_status_changed_template():
    """Test status changed email template rendering."""
    service = EmailService()

    template = service.jinja_env.get_template("demand_status_changed.html")
    context = {
        "recipient_name": "Test User",
        "demand_id": 456,
        "old_status": "PENDENTE",
        "new_status": "EM_ANDAMENTO",
    }
    html = template.render(**context)

    assert "Test User" in html
    assert "456" in html
    assert "PENDENTE" in html
    assert "EM_ANDAMENTO" in html
