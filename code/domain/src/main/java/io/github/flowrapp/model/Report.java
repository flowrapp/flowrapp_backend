package io.github.flowrapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.With;
import org.springframework.util.Assert;

@Builder(toBuilder = true)
@With
public record Report(
    Integer id,
    User user,
    Business business,
    LocalDate day,
    BigDecimal hours) {

  public static Report fromWorklog(Worklog worklog) {
    Assert.isTrue(worklog.isValid(), "Invalid worklog");

    return Report.builder()
        .user(worklog.user())
        .business(worklog.business())
        .day(worklog.getDay())
        .hours(worklog.getHours())
        .build();
  }

  public Report sum(BigDecimal hours) {
    return this.withHours(
        this.hours.add(hours));
  }

  public Report minus(BigDecimal hours) {
    return this.withHours(
        this.hours.subtract(hours));
  }

}
