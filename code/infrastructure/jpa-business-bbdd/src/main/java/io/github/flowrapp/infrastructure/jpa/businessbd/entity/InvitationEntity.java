package io.github.flowrapp.infrastructure.jpa.businessbd.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "invitations")
public class InvitationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "invited")
  private UserEntity invited;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "invited_by")
  private UserEntity invitedBy;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "business_id", nullable = false)
  private BusinessEntity business;

  @NotNull
  @Column(name = "token", nullable = false)
  private UUID token;

  @Size(max = 50)
  @NotNull
  @Column(name = "role", nullable = false, length = 50)
  private String role;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @NotNull
  @ColumnDefault("(now() + '7 days'::interval)")
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Size(max = 20)
  @NotNull
  @ColumnDefault("'PENDING'")
  @Column(name = "status", nullable = false, length = 20)
  private String status;

}
