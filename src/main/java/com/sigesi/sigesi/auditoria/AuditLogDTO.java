package com.sigesi.sigesi.auditoria;

import java.util.Map;

public record AuditLogDTO(
    Map<String, Object> entity,
    String usuarioNome,
    String usuarioEmail,
    Long timestamp,
    String action) {
}
