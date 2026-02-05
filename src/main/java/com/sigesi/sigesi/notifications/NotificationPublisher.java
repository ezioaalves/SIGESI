package com.sigesi.sigesi.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.RabbitMQConfig;
import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.demandas.DemandaStatus;
import com.sigesi.sigesi.notifications.events.DemandEvent;

/**
 * Service for publishing notification events to RabbitMQ.
 */
@Service
public class NotificationPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Publish an event when a demand is assigned to an agent.
     *
     * @param demanda The demand that was assigned
     */
    public void publishDemandAssigned(Demanda demanda) {
        try {
            DemandEvent event = DemandEvent.builder()
                .eventType("assigned")
                .demandId(demanda.getId())
                .solicitacaoId(demanda.getSolicitacao().getId())
                .assunto(demanda.getSolicitacao().getAssunto().name())
                .responsavelId(demanda.getResponsavel().getId())
                .responsavelEmail(demanda.getResponsavel().getEmail())
                .responsavelNome(demanda.getResponsavel().getName())
                .prazo(demanda.getPrazo())
                .build();

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ASSIGNED,
                event
            );

            LOGGER.info("Published demand assigned event for demand ID: {}", demanda.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish demand assigned event: {}", e.getMessage(), e);
        }
    }

    /**
     * Publish an event when a demand's status changes.
     *
     * @param demanda The demand with updated status
     * @param oldStatus The previous status
     */
    public void publishDemandStatusChanged(Demanda demanda, DemandaStatus oldStatus) {
        try {
            DemandEvent event = DemandEvent.builder()
                .eventType("status_changed")
                .demandId(demanda.getId())
                .solicitacaoId(demanda.getSolicitacao().getId())
                .assunto(demanda.getSolicitacao().getAssunto().name())
                .responsavelId(demanda.getResponsavel().getId())
                .responsavelEmail(demanda.getResponsavel().getEmail())
                .responsavelNome(demanda.getResponsavel().getName())
                .oldStatus(oldStatus.name())
                .newStatus(demanda.getStatus().name())
                .prazo(demanda.getPrazo())
                .build();

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_STATUS_CHANGED,
                event
            );

            LOGGER.info(
                "Published demand status changed event for demand ID: {} ({}->{})",
                demanda.getId(),
                oldStatus.name(),
                demanda.getStatus().name()
            );
        } catch (Exception e) {
            LOGGER.error("Failed to publish demand status changed event: {}", e.getMessage(), e);
        }
    }
}
