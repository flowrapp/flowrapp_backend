package io.github.flowrapp.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;

import lombok.Builder;
import lombok.With;
import org.springframework.util.Assert;

@Builder(toBuilder = true)
@With
public record Report(
    User user,
    Business business,
    LocalDate day,
    BigInteger seconds) {

  public static Report fromWorklog(Worklog worklog) {
    Assert.isTrue(worklog.isValid(), "Invalid worklog");

    return Report.builder()
        .user(worklog.user())
        .business(worklog.business())
        .day(worklog.getDay())
        .seconds(worklog.getSeconds())
        .build();
  }

  public Report sum(BigInteger seconds) {
    return this.withSeconds(
        this.seconds.add(seconds));
  }

  public Report minus(BigInteger seconds) {
    return this.withSeconds(
        this.seconds.subtract(seconds));
  }

  public BigDecimal hours() {
    return new BigDecimal(seconds).divide(BigDecimal.valueOf(3600), 2, RoundingMode.DOWN);
  }

}
