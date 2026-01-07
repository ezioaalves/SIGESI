package com.sigesi.sigesi.arquivos;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import com.sigesi.sigesi.arquivos.dtos.FileUrlResponseDTO;
import com.sigesi.sigesi.arquivos.util.StorageKeyGenerator;
import com.sigesi.sigesi.arquivos.validation.FileValidator;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.storage.MinioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for file operations.
 */
@Service
public class ArquivoService {

  @Autowired
  private ArquivoRepository arquivoRepository;

  @Autowired
  private ArquivoMapper arquivoMapper;

  @Autowired
  private MinioService minioService;

  /**
   * Upload file to MinIO and save metadata.
   */
  @Transactional
  public ArquivoResponseDTO uploadFile(MultipartFile file, String categoria) {
    // Validate file
    FileValidator.validateFile(file);

    // Generate unique storage key
    String storageKey;
    if (categoria != null && !categoria.isEmpty()) {
      storageKey = StorageKeyGenerator.generateCategoryKey(categoria, file.getOriginalFilename());
    } else {
      storageKey = StorageKeyGenerator.generateKey(file.getOriginalFilename());
    }

    // Upload to MinIO first
    try {
      minioService.uploadFile(file, storageKey);
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to storage", e);
    }

    // Save metadata
    Arquivo arquivo;
    try {
      arquivo = Arquivo.builder()
          .nomeOriginal(file.getOriginalFilename())
          .storageKey(storageKey)
          .contentType(file.getContentType())
          .tamanho(file.getSize())
          .categoria(categoria)
          .ativo(true)
          .build();

      arquivo = arquivoRepository.save(arquivo);
    } catch (Exception e) {
      // Rollback: delete file from MinIO if DB save fails
      try {
        minioService.deleteFile(storageKey);
      } catch (Exception cleanupEx) {
        // Log cleanup failure
      }
      throw new RuntimeException("Failed to save file metadata", e);
    }

    return arquivoMapper.toDto(arquivo);
  }

  /**
   * Get file metadata by ID.
   */
  @Transactional(readOnly = true)
  public ArquivoResponseDTO getFileMetadata(Long id) {
    Arquivo arquivo = arquivoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Arquivo não encontrado com id " + id));
    return arquivoMapper.toDto(arquivo);
  }

  /**
   * Get all active files.
   */
  @Transactional(readOnly = true)
  public List<ArquivoResponseDTO> getAllFiles() {
    List<Arquivo> arquivos = arquivoRepository.findByAtivoTrueOrderByUploadedAtDesc();
    return arquivoMapper.toDtoList(arquivos);
  }

  /**
   * Generate presigned download URL.
   */
  @Transactional(readOnly = true)
  public FileUrlResponseDTO generateDownloadUrl(Long id) {
    Arquivo arquivo = arquivoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Arquivo não encontrado com id " + id));

    // Generate URL valid for 1 hour
    String url = minioService.getPresignedUrl(arquivo.getStorageKey(), 60);

    return FileUrlResponseDTO.builder()
        .fileId(id)
        .url(url)
        .expiresInSeconds(3600L)
        .nomeOriginal(arquivo.getNomeOriginal())
        .build();
  }

  /**
   * Delete file (metadata and storage).
   */
  @Transactional
  public void deleteFile(Long id) {
    Arquivo arquivo = arquivoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Arquivo não encontrado com id " + id));

    // Delete from MinIO
    try {
      minioService.deleteFile(arquivo.getStorageKey());
    } catch (Exception e) {
      // Log error but continue with metadata deletion
    }

    // Delete metadata
    arquivoRepository.delete(arquivo);
  }

  /**
   * Get Arquivo entity by ID (internal use).
   */
  public Arquivo getArquivoEntityById(Long id) {
    return arquivoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Arquivo não encontrado com id " + id));
  }
}
