package io.github.flowrapp.infrastructure.jpa.businessbd.entity;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @Size(max = 320)
  @NotNull
  @Column(name = "mail", nullable = false, length = 320)
  private String mail;

  @Size(max = 15)
  @Column(name = "phone", length = 15)
  private String phone;

  @NotNull
  @Column(name = "password_hash", nullable = false, length = Integer.MAX_VALUE)
  private String passwordHash;

  @NotNull
  @ColumnDefault("true")
  @Column(name = "enabled", nullable = false)
  private Boolean enabled;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<BusinessEntity> ownedBusinesses = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<BusinessUserEntity> businessMemberships = new LinkedHashSet<>();

}
