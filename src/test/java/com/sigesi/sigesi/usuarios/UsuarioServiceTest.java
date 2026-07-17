package com.sigesi.sigesi.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import com.sigesi.sigesi.config.ConflictException;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;
import com.sigesi.sigesi.pessoas.SexoEnum;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.usuarios.dtos.CadastroCidadaoDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private UsuarioMapper usuarioMapper;

  @Mock
  private PessoaService pessoaService;

  @Mock
  private EnderecoService enderecoService;

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
  @DisplayName("Deve retornar lista vazia quando não há usuários (exceto root)")
  void testGetAllRetornaListaVazia() {
    when(usuarioRepository.findByIdNot(1L)).thenReturn(List.of());

    List<Usuario> resultado = usuarioService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());

    verify(usuarioRepository, times(1)).findByIdNot(1L);
  }

  @Test
  @DisplayName("Deve retornar lista com usuários existentes (exceto usuário root)")
  void testGetAllRetornaListaComUsuarios() {
    Usuario u1 = mock(Usuario.class);
    Usuario u2 = mock(Usuario.class);
    Usuario u3 = mock(Usuario.class);

    when(usuarioRepository.findByIdNot(1L))
        .thenReturn(List.of(u1, u2, u3));

    List<Usuario> resultado = usuarioService.getAll();

    assertEquals(3, resultado.size());
    assertEquals(List.of(u1, u2, u3), resultado);
    verify(usuarioRepository, times(1)).findByIdNot(1L);
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
    when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioMock));
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

    Usuario resultado = usuarioService.toggleUsuarioAtivo(2L);

    assertNotNull(resultado);
    verify(usuarioRepository, times(1)).save(usuarioMock);
  }

  @Test
  @DisplayName("Deve alternar status ativo de usuário inativo para ativo")
  void testToggleUsuarioAtivoDeInativoParaAtivo() {
    usuarioMock.setAtivo(false);
    when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioMock));
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

    Usuario resultado = usuarioService.toggleUsuarioAtivo(2L);

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

  @Test
  @DisplayName("Deve concluir o primeiro cadastro do cidadão")
  void testCadastrarPessoaComSucesso() {
    CadastroCidadaoDTO dto = new CadastroCidadaoDTO(
        "João Silva", "12345678900", SexoEnum.MASCULINO,
        new EnderecoCreateDTO("Rua A", "10", "Centro", "Praça"));
    EnderecoResponseDTO endereco = EnderecoResponseDTO.builder().id(5L).build();
    Pessoa pessoa = Pessoa.builder().id(8L).nome("João Silva").cpf("12345678900")
        .sexo(SexoEnum.MASCULINO).build();
    PessoaResponseDTO response = new PessoaResponseDTO(
        8L, "João Silva", "12345678900", "MASCULINO", null);

    when(pessoaService.findPessoaEntityByCpf("12345678900")).thenReturn(Optional.empty());
    when(enderecoService.createEndereco(dto.getEndereco())).thenReturn(endereco);
    when(pessoaService.createPessoa(any())).thenReturn(response);
    when(pessoaService.getPessoaEntityById(8L)).thenReturn(pessoa);
    when(usuarioRepository.save(usuarioMock)).thenReturn(usuarioMock);

    PessoaResponseDTO result = usuarioService.cadastrarPessoa(usuarioMock, dto);

    assertEquals(8L, result.getId());
    assertEquals(pessoa, usuarioMock.getPessoa());
    verify(usuarioRepository).save(usuarioMock);
  }

  @Test
  @DisplayName("Deve rejeitar usuário que já concluiu o cadastro")
  void testCadastrarPessoaJaConfigurada() {
    usuarioMock.setPessoa(Pessoa.builder().id(8L).build());
    CadastroCidadaoDTO dto = new CadastroCidadaoDTO();

    assertThrows(ConflictException.class,
        () -> usuarioService.cadastrarPessoa(usuarioMock, dto));
    verifyNoInteractions(enderecoService);
  }
}
