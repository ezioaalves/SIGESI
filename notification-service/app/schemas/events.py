"""Pydantic schemas for RabbitMQ events."""
from datetime import date

from pydantic import BaseModel


class DemandEvent(BaseModel):
    """Event received from SIGESI via RabbitMQ."""

    event_type: str  # "created", "assigned", "status_changed"
    demand_id: int
    solicitacao_id: int
    assunto: str  # BURACO, ESGOTO, ILUMINACAO, LIMPEZA, OUTROS

    # Responsible person (agent/operator)
    responsavel_id: int
    responsavel_email: str
    responsavel_nome: str

    # Status (for status_changed events)
    old_status: str | None = None
    new_status: str | None = None

    # Deadline
    prazo: date | None = None
