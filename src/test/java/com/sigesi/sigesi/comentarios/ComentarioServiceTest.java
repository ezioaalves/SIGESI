package com.sigesi.sigesi.comentarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.demandas.DemandaService;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

/**
 * Testes unitarios para ComentarioService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ComentarioService Tests")
class ComentarioServiceTest {

  @Mock
  private ComentarioRepository comentarioRepository;

  @Mock
  private ComentarioMapper comentarioMapper;

  @Mock
  private DemandaService demandaService;

  @Mock
  private UsuarioService usuarioService;

  @InjectMocks
  private ComentarioService comentarioService;

  private Comentario comentario;
  private ComentarioResponseDTO responseDTO;
  private Demanda demanda;
  private Usuario autor;

  @BeforeEach
  void setUp() {
    demanda = Demanda.builder().id(1L).build();
    autor = Usuario.builder().id(1L).email("test@test.com").name("Test User").build();
    comentario = Comentario.builder()
        .id(1L).demanda(demanda).autor(autor).texto("Comentario teste")
        .build();
    responseDTO = ComentarioResponseDTO.builder()
        .id(1L).demandaId(1L).autor(autor).texto("Comentario teste")
        .build();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando nao ha comentarios")
  void testGetAllRetornaListaVazia() {
    when(comentarioRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<ComentarioResponseDTO> resultado = comentarioService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
  }

  @Test
  @DisplayName("Deve retornar lista de comentarios")
  void testGetAllRetornaLista() {
    Comentario c1 = mock(Comentario.class);
    Comentario c2 = mock(Comentario.class);

    when(comentarioRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(c1, c2));
    when(comentarioMapper.toDto(any())).thenReturn(mock(ComentarioResponseDTO.class));

    List<ComentarioResponseDTO> resultado = comentarioService.getAll();

    assertEquals(2, resultado.size());
  }

  @Test
  @DisplayName("Deve retornar comentario por ID")
  void testGetComentarioByIdComSucesso() {
    when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
    when(comentarioMapper.toDto(comentario)).thenReturn(responseDTO);

    ComentarioResponseDTO resultado = comentarioService.getComentarioById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException quando ID nao encontrado")
  void testGetComentarioByIdNaoEncontrado() {
    when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> comentarioService.getComentarioById(999L));
  }

  @Test
  @DisplayName("Deve retornar comentarios por demanda")
  void testGetComentariosByDemandaRetornaLista() {
    Comentario c1 = mock(Comentario.class);

    when(comentarioRepository.findByDemandaIdOrderByCriadoEmAsc(1L))
        .thenReturn(List.of(c1));
    when(comentarioMapper.toDto(any())).thenReturn(mock(ComentarioResponseDTO.class));

    List<ComentarioResponseDTO> resultado = comentarioService.getComentariosByDemanda(1L);

    assertEquals(1, resultado.size());
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando demanda nao tem comentarios")
  void testGetComentariosByDemandaRetornaListaVazia() {
    when(comentarioRepository.findByDemandaIdOrderByCriadoEmAsc(999L))
        .thenReturn(List.of());

    List<ComentarioResponseDTO> resultado = comentarioService.getComentariosByDemanda(999L);

    assertTrue(resultado.isEmpty());
  }

  @Test
  @DisplayName("Deve criar comentario com sucesso")
  void testCreateComentarioComSucesso() {
    ComentarioCreateDTO createDTO = new ComentarioCreateDTO(1L, 1L, "Novo comentario");

    when(demandaService.getDemandaEntityById(1L)).thenReturn(demanda);
    when(usuarioService.getUsuarioById(1L)).thenReturn(autor);
    when(comentarioMapper.toEntity(createDTO)).thenReturn(comentario);
    when(comentarioRepository.save(comentario)).thenReturn(comentario);
    when(comentarioMapper.toDto(comentario)).thenReturn(responseDTO);

    ComentarioResponseDTO resultado = comentarioService.createComentario(createDTO);

    assertNotNull(resultado);
    verify(comentarioRepository, times(1)).save(comentario);
  }

  @Test
  @DisplayName("Deve deletar comentario com sucesso")
  void testDeleteComentarioComSucesso() {
    when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));

    comentarioService.deleteComentario(1L);

    verify(comentarioRepository, times(1)).delete(comentario);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao deletar comentario inexistente")
  void testDeleteComentarioNaoEncontrado() {
    when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> comentarioService.deleteComentario(999L));
    verify(comentarioRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade Comentario por ID")
  void testGetComentarioEntityByIdComSucesso() {
    when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));

    Comentario resultado = comentarioService.getComentarioEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para entidade inexistente")
  void testGetComentarioEntityByIdNaoEncontrado() {
    when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> comentarioService.getComentarioEntityById(999L));
  }
}
