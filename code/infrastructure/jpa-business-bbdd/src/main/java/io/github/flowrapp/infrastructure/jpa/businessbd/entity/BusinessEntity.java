package io.github.flowrapp.infrastructure.jpa.businessbd.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@Table(name = "business")
public class BusinessEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @Size(max = 255)
  @NotNull
  @Column(name = "address", nullable = false)
  private String address;

  @Size(max = 255)
  @NotNull
  @Column(name = "town", nullable = false)
  private String town;

  @Size(max = 255)
  @NotNull
  @Column(name = "city", nullable = false)
  private String city;

  @Size(max = 255)
  @NotNull
  @Column(name = "country", nullable = false)
  private String country;

  @NotNull
  @Column(name = "longitude", nullable = false)
  private Double longitude;

  @NotNull
  @Column(name = "latitude", nullable = false)
  private Double latitude;

  @NotNull
  @Column(name = "area", nullable = false)
  private Double area;

  @NotNull
  @Column(name = "timezone_offset", nullable = false)
  private ZoneId zone;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private UserEntity owner;

  @OneToMany(mappedBy = "business")
  private Set<BusinessUserEntity> members = new LinkedHashSet<>();

  @OneToMany(mappedBy = "business")
  private Set<InvitationEntity> invitations = new LinkedHashSet<>();

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy hb ? hb.getHibernateLazyInitializer()
            .getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy hb ? hb.getHibernateLazyInitializer()
            .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    BusinessEntity that = (BusinessEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy hb
        ? hb.getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
