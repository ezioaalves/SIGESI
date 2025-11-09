package com.sigesi.sigesi.pessoas;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.ConflictException;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaUpdateDTO;

@Service
public class PessoaService {

  @Autowired
  private PessoaRepository pessoaRepository;

  @Autowired
  private EnderecoService enderecoService;

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
    this.checkPessoaConflict(pessoaDTO.getCpf());

    Endereco endereco = enderecoService.getEnderecoEntityById(pessoaDTO.getEnderecoId());

    Pessoa pessoa = pessoaMapper.toEntity(pessoaDTO);
    pessoa.setEndereco(endereco);

    pessoa = pessoaRepository.save(pessoa);

    return pessoaMapper.toDto(pessoa);
  }

  public PessoaResponseDTO updatePessoa(Long id, PessoaUpdateDTO pessoaDTO) {
    Pessoa pessoa = this.getPessoEntityById(id);

    pessoaMapper.updateFromDto(pessoaDTO, pessoa);

    if (pessoaDTO.getEnderecoId() != null) {
      Endereco endereco = enderecoService.getEnderecoEntityById(id);
      pessoa.setEndereco(endereco);
    }

    pessoaRepository.save(pessoa);
    return pessoaMapper.toDto(pessoa);
  }

  public void deletePessoa(Long id) {
    Pessoa pessoa = this.getPessoEntityById(id);

    pessoaRepository.delete(pessoa);
  }

  public Pessoa getPessoEntityById(Long id) {
    return pessoaRepository.findById(id).orElseThrow(() -> new NotFoundException("pessoa não encontrada com id " + id));
  }

  public void checkPessoaConflict(String cpf) {
    if (pessoaRepository.existsByCpf(cpf)) {
      throw new ConflictException("CPF já cadastrado");
    }
  }

}
