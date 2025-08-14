package io.github.flowrapp.infrastructure.jpa.neonazure.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "business")
public class Business {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "business_id_gen")
  @SequenceGenerator(name = "business_id_gen", sequenceName = "business_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "altitude")
  private Double altitude;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "area")
  private Double area;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

}