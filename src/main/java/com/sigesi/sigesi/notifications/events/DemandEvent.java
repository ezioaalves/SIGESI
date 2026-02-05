package com.sigesi.sigesi.notifications.events;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event object for demand-related notifications.
 * Sent to RabbitMQ for processing by notification microservice.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DemandEvent {

    /**
     * Event type: "assigned", "status_changed".
     */
    private String eventType;

    /**
     * ID of the demand.
     */
    private Long demandId;

    /**
     * ID of the solicitacao (parent entity).
     */
    private Long solicitacaoId;

    /**
     * Subject of the solicitacao (BURACO, ESGOTO, ILUMINACAO, LIMPEZA, OUTROS).
     */
    private String assunto;

    /**
     * ID of the responsible user (agent/operator).
     */
    private Long responsavelId;

    /**
     * Email of the responsible user.
     */
    private String responsavelEmail;

    /**
     * Name of the responsible user.
     */
    private String responsavelNome;

    /**
     * Old status (for status_changed events).
     */
    private String oldStatus;

    /**
     * New status (for status_changed events).
     */
    private String newStatus;

    /**
     * Deadline for the demand.
     */
    private LocalDate prazo;
}
