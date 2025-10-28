package com.sigesi.sigesi.pessoas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.enderecos.EnderecoService;

@Service
public class PessoaService {

  @Autowired
  private PessoaRepository pessoaRepository;

  @Autowired
  private EnderecoService enderecoService;

  public List<Pessoa> getAll() {
    return pessoaRepository.findAll();
  }

  public Pessoa getPessoaById(Long id) {
    return pessoaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com id " + id));
  }

  public Pessoa getPessoaByCpf(String cpf) {
    return pessoaRepository.findByCpf(cpf)
        .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com CPF " + cpf));
  }

  public Pessoa createPessoa(Pessoa pessoa) {
    if (pessoa.getEndereco() != null && pessoa.getEndereco().getId() != null) {
      enderecoService.getEnderecoById(pessoa.getEndereco().getId());
    }
    return pessoaRepository.save(pessoa);
  }

  public Pessoa updatePessoa(Long id, Pessoa pessoaAtualizada) {
    Pessoa pessoa = pessoaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com id " + id));

    pessoa.setNome(pessoaAtualizada.getNome());
    pessoa.setCpf(pessoaAtualizada.getCpf());
    pessoa.setSexo(pessoaAtualizada.getSexo());

    if (pessoaAtualizada.getEndereco() != null
        && pessoaAtualizada.getEndereco().getId() != null) {
      enderecoService.getEnderecoById(pessoaAtualizada.getEndereco().getId());
      pessoa.setEndereco(pessoaAtualizada.getEndereco());
    }

    return pessoaRepository.save(pessoa);
  }

  public void deletePessoa(Long id) {
    Pessoa pessoa = pessoaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com id " + id));

    pessoaRepository.delete(pessoa);
  }
}
