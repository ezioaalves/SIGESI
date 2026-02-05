"""Pydantic schemas for RabbitMQ events."""
from datetime import date

from pydantic import BaseModel, ConfigDict, field_validator
from pydantic.alias_generators import to_camel


class DemandEvent(BaseModel):
    """Event received from SIGESI via RabbitMQ."""

    model_config = ConfigDict(
        populate_by_name=True,
        alias_generator=to_camel,
    )

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

    @field_validator("prazo", mode="before")
    @classmethod
    def parse_date_array(cls, v):
        """Handle Jackson's LocalDate array format [year, month, day]."""
        if isinstance(v, list) and len(v) == 3:
            return date(v[0], v[1], v[2])
        return v
