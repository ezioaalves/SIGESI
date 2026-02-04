package com.sigesi.sigesi.documentos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sigesi.sigesi.arquivos.Arquivo;
import com.sigesi.sigesi.arquivos.ArquivoService;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.documentos.dtos.DocumentoCreateDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoResponseDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoUpdateDTO;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testes para DocumentoService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentoService Tests")
class DocumentoServiceTest {

  @Mock
  private DocumentoRepository documentoRepository;

  @Mock
  private DocumentoMapper documentoMapper;

  @Mock
  private PessoaService pessoaService;

  @Mock
  private ArquivoService arquivoService;

  @InjectMocks
  private DocumentoService documentoService;

  private DocumentoCreateDTO documentoCreateDTO;
  private DocumentoResponseDTO documentoResponseDTO;
  private Documento documento;
  private Pessoa assinante;
  private Pessoa interessado;

  @BeforeEach
  void setUp() {
    assinante = Pessoa.builder().id(1L).nome("Assinante").build();
    interessado = Pessoa.builder().id(2L).nome("Interessado").build();

    documentoCreateDTO = new DocumentoCreateDTO();
    documentoCreateDTO.setSubject("Test Subject");
    documentoCreateDTO.setBody("Test Body");
    documentoCreateDTO.setTipo(DocumentoTipo.OFICIO);
    documentoCreateDTO.setAssinanteId(1L);
    documentoCreateDTO.setInteressadoId(2L);

    documento = Documento.builder()
        .id(1L)
        .subject("Test Subject")
        .body("Test Body")
        .tipo(DocumentoTipo.OFICIO)
        .assinante(assinante)
        .interessado(interessado)
        .build();

    documentoResponseDTO = DocumentoResponseDTO.builder()
        .id(1L)
        .subject("Test Subject")
        .body("Test Body")
        .tipo(DocumentoTipo.OFICIO)
        .build();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando nao ha documentos")
  void testGetAllRetornaListaVazia() {
    when(documentoRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<DocumentoResponseDTO> resultado = documentoService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(documentoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista com documentos existentes")
  void testGetAllRetornaListaComDocumentos() {
    Documento doc1 = mock(Documento.class);
    Documento doc2 = mock(Documento.class);

    when(documentoRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(doc1, doc2));
    when(documentoMapper.toDto(any())).thenReturn(mock(DocumentoResponseDTO.class));

    List<DocumentoResponseDTO> resultado = documentoService.getAll();

    assertEquals(2, resultado.size());
    verify(documentoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar documento quando buscar por ID existente")
  void testGetDocumentoByIdComSucesso() {
    when(documentoRepository.findById(1L)).thenReturn(Optional.of(documento));
    when(documentoMapper.toDto(documento)).thenReturn(documentoResponseDTO);

    DocumentoResponseDTO resultado = documentoService.getDocumentoById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Test Subject", resultado.getSubject());
  }

  @Test
  @DisplayName("Deve lancar excecao 404 quando buscar por ID inexistente")
  void testGetDocumentoByIdLancaExcecaoQuandoNaoEncontrado() {
    when(documentoRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      documentoService.getDocumentoById(999L);
    });

    assertTrue(exception.getMessage().contains("Documento nao encontrado"));
  }

  @Test
  @DisplayName("Deve criar documento com sucesso")
  void testCreateDocumentoComSucesso() {
    when(pessoaService.getPessoEntityById(1L)).thenReturn(assinante);
    when(pessoaService.getPessoEntityById(2L)).thenReturn(interessado);
    when(documentoMapper.toEntity(documentoCreateDTO)).thenReturn(documento);
    when(documentoRepository.save(any(Documento.class))).thenReturn(documento);
    when(documentoMapper.toDto(documento)).thenReturn(documentoResponseDTO);

    DocumentoResponseDTO resultado = documentoService.createDocumento(documentoCreateDTO);

    assertNotNull(resultado);
    verify(pessoaService, times(1)).getPessoEntityById(1L);
    verify(pessoaService, times(1)).getPessoEntityById(2L);
    verify(documentoRepository, times(1)).save(any(Documento.class));
  }

  @Test
  @DisplayName("Deve atualizar documento com sucesso")
  void testUpdateDocumentoComSucesso() {
    DocumentoUpdateDTO updateDTO = new DocumentoUpdateDTO();
    updateDTO.setSubject("Updated Subject");

    when(documentoRepository.findById(1L)).thenReturn(Optional.of(documento));
    when(documentoRepository.save(any(Documento.class))).thenReturn(documento);
    when(documentoMapper.toDto(documento)).thenReturn(documentoResponseDTO);

    DocumentoResponseDTO resultado = documentoService.updateDocumento(1L, updateDTO);

    assertNotNull(resultado);
    verify(documentoMapper, times(1)).updateFromDto(updateDTO, documento);
    verify(documentoRepository, times(1)).save(documento);
  }

  @Test
  @DisplayName("Deve deletar documento com sucesso")
  void testDeleteDocumentoComSucesso() {
    when(documentoRepository.findById(1L)).thenReturn(Optional.of(documento));
    doNothing().when(documentoRepository).delete(documento);

    assertDoesNotThrow(() -> documentoService.deleteDocumento(1L));

    verify(documentoRepository, times(1)).delete(documento);
  }

  @Test
  @DisplayName("Deve lancar excecao ao deletar documento inexistente")
  void testDeleteDocumentoLancaExcecaoQuandoNaoEncontrado() {
    when(documentoRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      documentoService.deleteDocumento(999L);
    });

    assertTrue(exception.getMessage().contains("Documento nao encontrado"));
  }

  @Test
  @DisplayName("Deve retornar entidade documento quando buscar por ID")
  void testGetDocumentoEntityByIdComSucesso() {
    when(documentoRepository.findById(1L)).thenReturn(Optional.of(documento));

    Documento resultado = documentoService.getDocumentoEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    verify(documentoRepository, times(1)).findById(1L);
  }
}
