package io.github.flowrapp.infrastructure.jpa.businessbd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class ReportEntity {

  @EmbeddedId
  private ReportId id;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "business_id", nullable = false)
  private BusinessEntity business;

  @NotNull
  @Column(name = "hours", nullable = false)
  private Double hours;

}
