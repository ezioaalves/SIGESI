package com.sigesi.sigesi.pessoas;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.ConflictException;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoRepository;
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaUpdateDTO;

@Service
public class PessoaService {

  @Autowired
  private PessoaRepository pessoaRepository;

  @Autowired
  private EnderecoRepository enderecoRepository;

  @Autowired
  private PessoaMapper pessoaMapper;

  public List<PessoaResponseDTO> getAll() {
    return pessoaRepository.findAllByOrderByIdAsc()
        .stream()
        .map(pessoaMapper::toDto)
        .collect(Collectors.toList());
  }

  public PessoaResponseDTO getPessoaById(Long id) {
    Pessoa pessoa = pessoaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pessoa não encontrada com ID " + id));

    return pessoaMapper.toDto(pessoa);
  }

  public PessoaResponseDTO getPessoaByCpf(String cpf) {
    Pessoa pessoa = pessoaRepository.findByCpf(cpf)
        .orElseThrow(() -> new NotFoundException("Pessoa não encontrada com CPF " + cpf));

    return pessoaMapper.toDto(pessoa);
  }

  public PessoaResponseDTO createPessoa(PessoaCreateDTO pessoaDTO) {
    Endereco endereco = enderecoRepository.findById(pessoaDTO.getEnderecoId())
        .orElseThrow(() -> new NotFoundException("Endereço não encontrado"));

    Optional<Pessoa> pessoa = pessoaRepository.findByCpf(pessoaDTO.getCpf());

    if (pessoa.isPresent()) {
      throw new ConflictException("CPF já cadastrado");
    }

    Pessoa entity = pessoaMapper.toEntity(pessoaDTO);
    entity.setEndereco(endereco);
    pessoaRepository.save(entity);

    return pessoaMapper.toDto(entity);
  }

  public PessoaResponseDTO updatePessoa(Long id, PessoaUpdateDTO pessoaDTO) {
    Pessoa entity = pessoaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

    pessoaMapper.updateFromDto(pessoaDTO, entity);

    if (pessoaDTO.getEnderecoId() != null) {
      Endereco endereco = enderecoRepository.findById(pessoaDTO.getEnderecoId())
          .orElseThrow(() -> new NotFoundException("Endereço não encontrado"));
      entity.setEndereco(endereco);
    }

    pessoaRepository.save(entity);
    return pessoaMapper.toDto(entity);
  }

  public void deletePessoa(Long id) {
    Pessoa pessoa = pessoaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com id " + id));

    pessoaRepository.delete(pessoa);
  }
}
