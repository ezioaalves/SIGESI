package com.sigesi.sigesi.auditoria;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Auditoria")
public class GenericAuditController {

  @Autowired
  private GenericAuditService auditService;

  @GetMapping("/revisions")
  public List<AuditLogDTO> getRevisions(
      @RequestParam AuditableEntity entity,
      @RequestParam(required = false) Long id) {
    try {
      Class<?> entityClass = Class.forName(entity.getFullPath());
      return auditService.getRevisions(entityClass, id);
    } catch (ClassNotFoundException e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Entidade não configurada corretamente: " + entity.name());
    }
  }
}
