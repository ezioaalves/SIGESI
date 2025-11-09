package com.sigesi.sigesi.jazigos.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class JazigoUpdateDTO {
  @Schema(description = "ID do cemiterio", example = "1")
  private Long cemiterio;

  @Schema(description = "Largura do jazigo", example = "1.33")
  private Double largura;

  @Schema(description = "Comprimeoto do jazigo", example = "2.0")
  private Double comprimento;

  @Schema(description = "Numero da quadra", example = "2")
  private Integer quadra;

  @Pattern(regexp = ".*\\S.*", message = "A rua não pode ser vazio ou composto apenas de espaços")
  @Schema(description = "Nome da rua", example = "Dr. Pedro Silva")
  private String rua;

  @Pattern(regexp = ".*\\S.*", message = "O lote não pode ser vazio ou composto apenas de espaços")
  @Schema(description = "Lote", example = "620")
  private String lote;
}
