package com.sigesi.sigesi.arquivos;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import com.sigesi.sigesi.arquivos.dtos.FileUrlResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for file operations.
 */
@RestController
@RequestMapping("/api/arquivos")
@Tag(name = "arquivos")
public class ArquivoController {

  @Autowired
  private ArquivoService arquivoService;

  /**
   * Upload a file.
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ArquivoResponseDTO> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "categoria", required = false) String categoria) {

    ArquivoResponseDTO result = arquivoService.uploadFile(file, categoria);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  /**
   * Get all files.
   */
  @GetMapping("/")
  public ResponseEntity<List<ArquivoResponseDTO>> listAll() {
    List<ArquivoResponseDTO> arquivos = arquivoService.getAllFiles();
    return ResponseEntity.ok(arquivos);
  }

  /**
   * Get file metadata by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ArquivoResponseDTO> getFileById(@PathVariable Long id) {
    ArquivoResponseDTO arquivo = arquivoService.getFileMetadata(id);
    return ResponseEntity.ok(arquivo);
  }

  /**
   * Get download URL for file.
   */
  @GetMapping("/{id}/url")
  public ResponseEntity<FileUrlResponseDTO> getFileUrl(@PathVariable Long id) {
    FileUrlResponseDTO response = arquivoService.generateDownloadUrl(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Download file content via proxy.
   */
  @GetMapping("/{id}/download")
  public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable Long id) {
    System.out.println("Processing download request for file ID: " + id);
    ArquivoResponseDTO metadata = arquivoService.getFileMetadata(id);
    java.io.InputStream inputStream = arquivoService.downloadFile(id);

    org.springframework.core.io.InputStreamResource resource = new org.springframework.core.io.InputStreamResource(
        inputStream);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(metadata.getContentType()))
        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + metadata.getNomeOriginal() + "\"")
        .body(resource);
  }

  /**
   * Delete file.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
    arquivoService.deleteFile(id);
    return ResponseEntity.noContent().build();
  }
}
