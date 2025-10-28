package com.sigesi.sigesi.enderecos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

  @Autowired
  private EnderecoRepository enderecoRepository;

  public List<Endereco> getAll() {
    return enderecoRepository.findAll();
  }

  public Endereco getEnderecoById(Long id) {
    return enderecoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Endereço não encontrado com id " + id));
  }

  public Endereco createEndereco(Endereco endereco) {
    return enderecoRepository.save(endereco);
  }

  public Endereco updateEndereco(Long id, Endereco enderecoAtualizado) {
    Endereco endereco = enderecoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Endereço não encontrado com id " + id));

    endereco.setLogradouro(enderecoAtualizado.getLogradouro());
    endereco.setNumero(enderecoAtualizado.getNumero());
    endereco.setBairro(enderecoAtualizado.getBairro());
    endereco.setReferencia(enderecoAtualizado.getReferencia());

    return enderecoRepository.save(endereco);
  }

  public void deleteEndereco(Long id) {
    Endereco endereco = enderecoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Endereço não encontrado com id " + id));

    enderecoRepository.delete(endereco);
  }
}
