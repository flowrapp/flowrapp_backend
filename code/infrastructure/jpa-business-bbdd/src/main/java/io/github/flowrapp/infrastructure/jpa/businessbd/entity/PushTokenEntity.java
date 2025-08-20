package io.github.flowrapp.infrastructure.jpa.businessbd.entity;

import java.time.Instant;
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
@Table(name = "push_tokens", schema = "flowrapp_management")
public class PushTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Size(max = 255)
  @NotNull
  @Column(name = "token", nullable = false)
  private UUID token;

  @Size(max = 255)
  @NotNull
  @Column(name = "device_id", nullable = false)
  private String deviceId;

  @Size(max = 50)
  @NotNull
  @Column(name = "platform", nullable = false, length = 50)
  private String platform;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

}
