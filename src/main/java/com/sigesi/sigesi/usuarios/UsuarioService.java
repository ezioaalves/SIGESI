package com.sigesi.sigesi.usuarios;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.config.ConflictException;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.usuarios.dtos.CadastroCidadaoDTO;
import com.sigesi.sigesi.usuarios.dtos.UsuarioUpdateDTO;
import com.sigesi.sigesi.usuarios.enums.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioMapper usuarioMapper;

  @Autowired
  private PessoaService pessoaService;

  @Autowired
  private EnderecoService enderecoService;

  private void validarUsuarioEditavel(Long id) {
    if (id == 1) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Não é permitido realizar essa ação para este usuário");
    }
  }

  public List<Usuario> getAll() {
    return usuarioRepository.findByIdNot(1L);
  }

  public Usuario getUsuarioById(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id " + id));
  }

  public Optional<Usuario> findByPessoaId(Long pessoaId) {
    return usuarioRepository.findByPessoaId(pessoaId);
  }

  public Usuario vincularPessoa(Usuario usuario, Pessoa pessoa) {
    usuario.setPessoa(pessoa);
    return usuarioRepository.save(usuario);
  }

  /**
   * Conclui o primeiro cadastro do cidadão autenticado e o vincula à pessoa.
   */
  @Transactional
  public PessoaResponseDTO cadastrarPessoa(Usuario usuario, CadastroCidadaoDTO dto) {
    if (usuario.getPessoa() != null) {
      throw new ConflictException("O cadastro de cidadão já foi configurado");
    }

    Optional<Pessoa> pessoaExistente = pessoaService.findPessoaEntityByCpf(dto.getCpf());
    if (pessoaExistente.isPresent()) {
      Pessoa pessoa = pessoaExistente.get();
      usuarioRepository.findByPessoaId(pessoa.getId())
          .filter(outroUsuario -> !outroUsuario.getId().equals(usuario.getId()))
          .ifPresent(outroUsuario -> {
            throw new ConflictException("CPF já vinculado a outro usuário");
          });

      vincularPessoa(usuario, pessoa);
      return pessoaService.toResponseDto(pessoa);
    }

    EnderecoResponseDTO endereco = enderecoService.createEndereco(dto.getEndereco());
    PessoaCreateDTO pessoaDto = new PessoaCreateDTO(
        dto.getNome(), dto.getCpf(), dto.getSexo(), endereco.getId());
    PessoaResponseDTO pessoaCriada = pessoaService.createPessoa(pessoaDto);
    Pessoa pessoa = pessoaService.getPessoaEntityById(pessoaCriada.getId());
    vincularPessoa(usuario, pessoa);
    return pessoaCriada;
  }

  public Usuario toggleUsuarioAtivo(Long id) {
    validarUsuarioEditavel(id);

    Usuario usuario = this.getUsuarioById(id);
    usuario.setAtivo(!usuario.getAtivo());
    return usuarioRepository.save(usuario);
  }

  public Usuario updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO) {
    validarUsuarioEditavel(id);

    Usuario usuario = this.getUsuarioById(id);

    usuarioMapper.updateFromDto(usuarioUpdateDTO, usuario);

    return usuarioRepository.save(usuario);
  }

  public Usuario processOAuthPostLogin(OAuth2User oAuth2User) {
    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String picture = oAuth2User.getAttribute("picture");

    Usuario user = usuarioRepository.findByEmail(email)
        .map(u -> {
          u.setName(name);
          u.setPictureUrl(picture);
          return u;
        })
        .orElse(Usuario.builder()
            .email(email)
            .name(name)
            .pictureUrl(picture)
            .provider("google")
            .ativo(true)
            .role(Role.CIDADAO)
            .build());

    return usuarioRepository.save(user);
  }
}
