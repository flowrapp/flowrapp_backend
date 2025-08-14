package io.github.flowrapp.infrastructure.jpa.neonazure.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
public class UsersRoleId implements Serializable {

  @Serial
  private static final long serialVersionUID = -2585556766504571519L;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @NotNull
  @Column(name = "business_id", nullable = false)
  private Integer businessId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    UsersRoleId entity = (UsersRoleId) o;
    return Objects.equals(this.businessId, entity.businessId)
        &&
        Objects.equals(this.userId, entity.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessId, userId);
  }

}
