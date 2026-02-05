package com.sigesi.sigesi.documentos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.documentos.dtos.DocumentoCreateDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoResponseDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para Documento.
 */
@RestController
@RequestMapping("/api/documentos")
@Tag(name = "documentos")
public class DocumentoController {

  @Autowired
  private DocumentoService documentoService;

  @Autowired
  private DocumentoPdfService documentoPdfService;

  @GetMapping("/")
  public ResponseEntity<List<DocumentoResponseDTO>> listAll() {
    List<DocumentoResponseDTO> documentos = documentoService.getAll();
    return ResponseEntity.ok(documentos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DocumentoResponseDTO> getDocumentoById(@PathVariable Long id) {
    DocumentoResponseDTO documento = documentoService.getDocumentoById(id);
    return ResponseEntity.ok(documento);
  }

  @PostMapping("/")
  public ResponseEntity<DocumentoResponseDTO> createDocumento(
      @Valid @RequestBody DocumentoCreateDTO dto) {
    DocumentoResponseDTO result = documentoService.createDocumento(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DocumentoResponseDTO> updateDocumento(
      @PathVariable Long id, @Valid @RequestBody DocumentoUpdateDTO dto) {
    DocumentoResponseDTO result = documentoService.updateDocumento(id, dto);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDocumento(@PathVariable Long id) {
    documentoService.deleteDocumento(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/pdf")
  public ResponseEntity<byte[]> baixarPdf(@PathVariable Long id) {
    Documento doc = documentoService.getDocumentoEntityById(id);
    byte[] pdf = documentoPdfService.gerarPdfDocumento(doc);

    String filename = "documento_" + doc.getNumero() + ".pdf";

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdf);
  }
}
