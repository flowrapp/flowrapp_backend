package io.github.flowrapp.infrastructure.jpa.neonazure.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "users_roles", schema = "flowrapp_management")
public class UsersRole {

  @SequenceGenerator(name = "users_roles_id_gen", sequenceName = "users_id_seq1", allocationSize = 1)
  @EmbeddedId
  private UsersRoleId id;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @MapsId("businessId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "business_id", nullable = false)
  private Business business;

  @Size(max = 50)
  @NotNull
  @Column(name = "role", nullable = false, length = 50)
  private String role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invited_by")
  private User invitedBy;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "joined_at", nullable = false)
  private OffsetDateTime joinedAt;

}