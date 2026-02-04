package com.sigesi.sigesi.documentos;

import com.sigesi.sigesi.arquivos.Arquivo;
import com.sigesi.sigesi.arquivos.ArquivoService;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.documentos.dtos.DocumentoCreateDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoResponseDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoUpdateDTO;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servico para Documento.
 */
@Service
public class DocumentoService {

  @Autowired
  private DocumentoRepository documentoRepository;

  @Autowired
  private DocumentoMapper documentoMapper;

  @Autowired
  private PessoaService pessoaService;

  @Autowired
  private ArquivoService arquivoService;

  public List<DocumentoResponseDTO> getAll() {
    return documentoRepository.findAllByOrderByIdAsc()
        .stream()
        .map(documentoMapper::toDto)
        .collect(Collectors.toList());
  }

  public DocumentoResponseDTO getDocumentoById(Long id) {
    Documento documento = this.getDocumentoEntityById(id);
    return documentoMapper.toDto(documento);
  }

  public DocumentoResponseDTO createDocumento(DocumentoCreateDTO dto) {
    Pessoa assinante = pessoaService.getPessoEntityById(dto.getAssinanteId());
    Pessoa interessado = pessoaService.getPessoEntityById(dto.getInteressadoId());

    Documento entity = documentoMapper.toEntity(dto);
    entity.setAssinante(assinante);
    entity.setInteressado(interessado);

    this.resolveAnexos(dto.getAnexoIds(), entity);

    Documento saved = documentoRepository.save(entity);
    return documentoMapper.toDto(saved);
  }

  public DocumentoResponseDTO updateDocumento(Long id, DocumentoUpdateDTO dto) {
    Documento documento = this.getDocumentoEntityById(id);

    documentoMapper.updateFromDto(dto, documento);

    if (dto.getAssinanteId() != null) {
      Pessoa assinante = pessoaService.getPessoEntityById(dto.getAssinanteId());
      documento.setAssinante(assinante);
    }

    if (dto.getInteressadoId() != null) {
      Pessoa interessado = pessoaService.getPessoEntityById(dto.getInteressadoId());
      documento.setInteressado(interessado);
    }

    this.resolveAnexos(dto.getAnexoIds(), documento);

    Documento updated = documentoRepository.save(documento);
    return documentoMapper.toDto(updated);
  }

  public void deleteDocumento(Long id) {
    Documento documento = this.getDocumentoEntityById(id);
    documentoRepository.delete(documento);
  }

  public Documento getDocumentoEntityById(Long id) {
    return documentoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Documento nao encontrado com id " + id));
  }

  private void resolveAnexos(List<Long> anexoIds, Documento documento) {
    if (anexoIds != null && !anexoIds.isEmpty()) {
      List<Arquivo> anexos = anexoIds.stream()
          .map(anexoId -> arquivoService.getArquivoEntityById(anexoId))
          .collect(Collectors.toList());
      documento.setAnexos(anexos);
    }
  }
}
