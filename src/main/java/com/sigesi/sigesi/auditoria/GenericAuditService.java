// @formatter:off
package com.sigesi.sigesi.auditoria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sigesi.sigesi.config.UsuarioRevisionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GenericAuditService {

    @PersistenceContext
    private EntityManager em;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                @Override
                public boolean hasIgnoreMarker(AnnotatedMember m) {
                    String name = m.getName();
                    return super.hasIgnoreMarker(m)
                            || name.equals("hibernateLazyInitializer")
                            || name.equals("handler");
                }
            });

    public List<AuditLogDTO> getRevisions(Class<?> entityClass, Long id) {
        AuditReader reader = AuditReaderFactory.get(em);

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .addOrder(AuditEntity.revisionNumber().desc());

        if (id != null) {
            query.add(AuditEntity.id().eq(id));
        }

        List<?> results = query.getResultList();

        return results.stream()
                .map(r -> {
                    Object[] arr = (r instanceof Object[] o) ? o 
                            : new Object[]{r};

                    Object entity = arr[0];
                    UsuarioRevisionEntity rev = (UsuarioRevisionEntity) arr[1];
                    RevisionType rt = (arr.length > 2) 
                            ? (RevisionType) arr[2] : null;

                    return new AuditLogDTO(
                            toMap(entity),
                            rev.getUsuarioNome(),
                            rev.getUsuarioEmail(),
                            rev.getTimestamp(),
                            mapAction(rt));
                })
                .collect(Collectors.toList());
    }

    private String mapAction(RevisionType rt) {
        if (rt == null) {
            return "UNKNOWN";
        }
        return switch (rt) {
            case ADD -> "INSERT";
            case MOD -> "UPDATE";
            case DEL -> "DELETE";
        };
    }

    private Map<String, Object> toMap(Object entity) {
        try {
            return objectMapper.convertValue(entity, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao serializar: " + e.getMessage());
            return Map.of("error", "Erro na serialização");
        }
    }
}
