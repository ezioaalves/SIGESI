package com.sigesi.sigesi.auditoria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.AuditQueryCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigesi.sigesi.config.UsuarioRevisionEntity;

import jakarta.persistence.EntityManager;

/**
 * Testes unitarios para GenericAuditService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GenericAuditService Tests")
class GenericAuditServiceTest {

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private GenericAuditService genericAuditService;

  private UsuarioRevisionEntity createRevisionEntity() {
    UsuarioRevisionEntity rev = new UsuarioRevisionEntity();
    rev.setId(1L);
    rev.setTimestamp(System.currentTimeMillis());
    rev.setUsuarioNome("Admin");
    rev.setUsuarioEmail("admin@test.com");
    return rev;
  }

  private void setupAuditMocks(
      MockedStatic<AuditReaderFactory> mockedFactory,
      AuditReader auditReader,
      AuditQuery auditQuery) {
    AuditQueryCreator queryCreator = mock(AuditQueryCreator.class);

    mockedFactory.when(() -> AuditReaderFactory.get(entityManager))
        .thenReturn(auditReader);
    when(auditReader.createQuery()).thenReturn(queryCreator);
    when(queryCreator.forRevisionsOfEntity(any(), anyBoolean(), anyBoolean()))
        .thenReturn(auditQuery);
    when(auditQuery.addOrder(any())).thenReturn(auditQuery);
  }

  private List<?> createResultList(RevisionType revType) {
    Object[] row = new Object[]{new Object(), createRevisionEntity(), revType};
    List<Object[]> results = new ArrayList<>();
    results.add(row);
    return results;
  }

  @Test
  @DisplayName("Deve retornar lista de revisoes")
  void testGetRevisionsRetornaLista() {
    try (MockedStatic<AuditReaderFactory> mockedFactory =
        Mockito.mockStatic(AuditReaderFactory.class)) {

      AuditReader auditReader = mock(AuditReader.class);
      AuditQuery auditQuery = mock(AuditQuery.class);
      setupAuditMocks(mockedFactory, auditReader, auditQuery);
      when(auditQuery.getResultList()).thenReturn(createResultList(RevisionType.ADD));

      List<AuditLogDTO> result = genericAuditService.getRevisions(Object.class, null);

      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals("INSERT", result.get(0).action());
      assertEquals("Admin", result.get(0).usuarioNome());
    }
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando nao ha revisoes")
  void testGetRevisionsRetornaListaVazia() {
    try (MockedStatic<AuditReaderFactory> mockedFactory =
        Mockito.mockStatic(AuditReaderFactory.class)) {

      AuditReader auditReader = mock(AuditReader.class);
      AuditQuery auditQuery = mock(AuditQuery.class);
      setupAuditMocks(mockedFactory, auditReader, auditQuery);
      when(auditQuery.getResultList()).thenReturn(Collections.emptyList());

      List<AuditLogDTO> result = genericAuditService.getRevisions(Object.class, null);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  @DisplayName("Deve filtrar por entity ID quando fornecido")
  void testGetRevisionsComEntityId() {
    try (MockedStatic<AuditReaderFactory> mockedFactory =
        Mockito.mockStatic(AuditReaderFactory.class)) {

      AuditReader auditReader = mock(AuditReader.class);
      AuditQuery auditQuery = mock(AuditQuery.class);
      setupAuditMocks(mockedFactory, auditReader, auditQuery);
      when(auditQuery.add(any())).thenReturn(auditQuery);
      when(auditQuery.getResultList()).thenReturn(createResultList(RevisionType.MOD));

      List<AuditLogDTO> result = genericAuditService.getRevisions(Object.class, 1L);

      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals("UPDATE", result.get(0).action());
    }
  }

  @Test
  @DisplayName("Deve mapear DELETE corretamente")
  void testGetRevisionsComDelete() {
    try (MockedStatic<AuditReaderFactory> mockedFactory =
        Mockito.mockStatic(AuditReaderFactory.class)) {

      AuditReader auditReader = mock(AuditReader.class);
      AuditQuery auditQuery = mock(AuditQuery.class);
      setupAuditMocks(mockedFactory, auditReader, auditQuery);
      when(auditQuery.getResultList()).thenReturn(createResultList(RevisionType.DEL));

      List<AuditLogDTO> result = genericAuditService.getRevisions(Object.class, null);

      assertEquals("DELETE", result.get(0).action());
    }
  }
}
