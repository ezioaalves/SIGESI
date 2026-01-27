package com.sigesi.sigesi.arquivos;

import com.sigesi.sigesi.storage.MinioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import
org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static
org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static
org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static
org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static
org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* Integration tests for ArquivoController.
*/
@SpringBootTest
@AutoConfigureMockMvc
class ArquivoControllerTest {

@Autowired
private MockMvc mockMvc;

@MockitoBean
private MinioService minioService;

@Test
@WithMockUser
void testUploadFile() throws Exception {
// Mock MinIO upload to avoid needing a real MinIO instance
when(minioService.uploadFile(any(),
anyString())).thenReturn("mocked-storage-key");

MockMultipartFile file = new MockMultipartFile(
"file",
"test.pdf",
"application/pdf",
"Test content".getBytes()
);

mockMvc.perform(multipart("/api/arquivos/upload")
.file(file)
.param("categoria", "test"))
.andExpect(status().isCreated())
.andExpect(jsonPath("$.nomeOriginal").value("test.pdf"))
.andExpect(jsonPath("$.contentType").value("application/pdf"));
}

@Test
@WithMockUser
void testUploadFileInvalidType() throws Exception {
MockMultipartFile file = new MockMultipartFile(
"file",
"test.exe",
"application/x-msdownload",
"Test content".getBytes()
);

mockMvc.perform(multipart("/api/arquivos/upload")
.file(file))
.andExpect(status().isBadRequest());
}

@Test
@WithMockUser
void testGetAllFiles() throws Exception {
mockMvc.perform(get("/api/arquivos/"))
.andExpect(status().isOk());
}
}
