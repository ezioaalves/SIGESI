package com.sigesi.sigesi.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for MinIO file operations.
 */
@Service
public class MinioService {

  private final MinioClient minioClient;

  @Value("${minio.bucket-name}")
  private String bucketName;

  @Autowired
  public MinioService(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  /**
   * Initialize bucket if it doesn't exist.
   */
  public void initBucket() {
    try {
      boolean found = minioClient.bucketExists(
          BucketExistsArgs.builder()
              .bucket(bucketName)
              .build()
      );

      if (!found) {
        minioClient.makeBucket(
            MakeBucketArgs.builder()
                .bucket(bucketName)
                .build()
        );
      }
    } catch (Exception e) {
      throw new StorageException("Não foi possível inicializar o armazenamento", e);
    }
  }

  /**
   * Upload file to MinIO.
   */
  public String uploadFile(MultipartFile file, String objectName) {
    try (InputStream inputStream = file.getInputStream()) {
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .stream(inputStream, file.getSize(), -1)
              .contentType(file.getContentType())
              .build()
      );
      return objectName;
    } catch (Exception e) {
      throw new StorageException("Não foi possível enviar o arquivo", e);
    }
  }

  /**
   * Download file from MinIO.
   */
  public InputStream downloadFile(String objectName) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .build()
      );
    } catch (Exception e) {
      throw new StorageException("Não foi possível baixar o arquivo", e);
    }
  }

  /**
   * Generate presigned URL for temporary access.
   */
  public String getPresignedUrl(String objectName, int expiryMinutes) {
    try {
      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucketName)
              .object(objectName)
              .expiry(expiryMinutes, TimeUnit.MINUTES)
              .build()
      );
    } catch (Exception e) {
      throw new StorageException("Não foi possível gerar o endereço para download", e);
    }
  }

  /**
   * Delete file from MinIO.
   */
  public void deleteFile(String objectName) {
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .build()
      );
    } catch (Exception e) {
      throw new StorageException("Não foi possível excluir o arquivo", e);
    }
  }

  /**
   * Check if file exists.
   */
  public boolean fileExists(String objectName) {
    try {
      minioClient.statObject(
          StatObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .build()
      );
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
