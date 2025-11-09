package com.sigesi.sigesi.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @InjectMocks
  private UsuarioService usuarioService;

  private Usuario usuarioMock;

  @BeforeEach
  void setUp() {
    usuarioMock = Usuario.builder()
        .id(1L)
        .email("usuario@example.com")
        .name("João Silva")
        .pictureUrl("https://example.com/pic.jpg")
        .provider("google")
        .ativo(true)
        .build();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há usuários")
  void testGetAllRetornaListaVazia() {
    when(usuarioRepository.findAll()).thenReturn(List.of());

    List<Usuario> resultado = usuarioService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(usuarioRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Deve retornar lista com usuários existentes")
  void testGetAllRetornaListaComUsuarios() {
    Usuario u1 = mock(Usuario.class);
    Usuario u2 = mock(Usuario.class);
    Usuario u3 = mock(Usuario.class);

    when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2, u3));

    List<Usuario> resultado = usuarioService.getAll();

    assertEquals(3, resultado.size());
    verify(usuarioRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Deve retornar usuário quando buscar por ID existente")
  void testGetUsuarioByIdComSucesso() {
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

    Usuario resultado = usuarioService.getUsuarioById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("usuario@example.com", resultado.getEmail());
    assertEquals("João Silva", resultado.getName());
  }

  @Test
  @DisplayName("Deve lançar exceção quando buscar por ID inexistente")
  void testGetUsuarioByIdLancaExcecaoQuandoNaoEncontrado() {
    when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      usuarioService.getUsuarioById(999L);
    });

    assertTrue(exception.getMessage().contains("Usuário não encontrado"));
  }

  @Test
  @DisplayName("Deve alternar status ativo de usuário ativo para inativo")
  void testToggleUsuarioAtivoDeAtivoParaInativo() {
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

    Usuario resultado = usuarioService.toggleUsuarioAtivo(1L);

    assertNotNull(resultado);
    verify(usuarioRepository, times(1)).save(usuarioMock);
  }

  @Test
  @DisplayName("Deve alternar status ativo de usuário inativo para ativo")
  void testToggleUsuarioAtivoDeInativoParaAtivo() {
    usuarioMock.setAtivo(false);
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

    Usuario resultado = usuarioService.toggleUsuarioAtivo(1L);

    assertNotNull(resultado);
    verify(usuarioRepository, times(1)).save(usuarioMock);
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar alternar ativo de usuário inexistente")
  void testToggleUsuarioAtivoLancaExcecaoQuandoNaoEncontrado() {
    when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      usuarioService.toggleUsuarioAtivo(999L);
    });

    assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    verify(usuarioRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar usuário existente no processamento OAuth2")
  void testProcessOAuthPostLoginAtualizaUsuarioExistente() {
    OAuth2User oAuth2User = mock(OAuth2User.class);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "usuario@example.com");
    attributes.put("name", "João Silva Atualizado");
    attributes.put("picture", "https://example.com/new-pic.jpg");

    when(oAuth2User.getAttribute("email")).thenReturn("usuario@example.com");
    when(oAuth2User.getAttribute("name")).thenReturn("João Silva Atualizado");
    when(oAuth2User.getAttribute("picture")).thenReturn("https://example.com/new-pic.jpg");

    when(usuarioRepository.findByEmail("usuario@example.com")).thenReturn(Optional.of(usuarioMock));
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

    Usuario resultado = usuarioService.processOAuthPostLogin(oAuth2User);

    assertNotNull(resultado);
    verify(usuarioRepository, times(1)).findByEmail("usuario@example.com");
    verify(usuarioRepository, times(1)).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Deve criar novo usuário no processamento OAuth2 quando não existe")
  void testProcessOAuthPostLoginCriaNovoUsuario() {
    OAuth2User oAuth2User = mock(OAuth2User.class);

    when(oAuth2User.getAttribute("email")).thenReturn("novo@example.com");
    when(oAuth2User.getAttribute("name")).thenReturn("Novo Usuário");
    when(oAuth2User.getAttribute("picture")).thenReturn("https://example.com/novo.jpg");

    when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

    Usuario resultado = usuarioService.processOAuthPostLogin(oAuth2User);

    assertNotNull(resultado);
    verify(usuarioRepository, times(1)).findByEmail("novo@example.com");
    verify(usuarioRepository, times(1)).save(any(Usuario.class));
  }
}
