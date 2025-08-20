package io.github.flowrapp.infrastructure.jpa.businessbd.entity;

import java.io.Serializable;
import java.time.LocalDate;
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
public class ReportIdEntity implements Serializable {
  private static final long serialVersionUID = 4268134098785297417L;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @NotNull
  @Column(name = "business_id", nullable = false)
  private Integer businessId;

  @NotNull
  @Column(name = "clock_day", nullable = false)
  private LocalDate clockDay;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    ReportIdEntity entity = (ReportIdEntity) o;
    return Objects.equals(this.businessId, entity.businessId)
        &&
        Objects.equals(this.clockDay, entity.clockDay) &&
        Objects.equals(this.userId, entity.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessId, clockDay, userId);
  }

}
