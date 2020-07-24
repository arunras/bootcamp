package com.arunx.boxapi.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("artist")
public class Artist {

  @Id
  private Integer id;
  
  @NotNull
  @NotEmpty(message = "name cannot be empty")
  private String name;

}
