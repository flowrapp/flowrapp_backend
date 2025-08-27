package io.github.flowrapp.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import io.github.flowrapp.utils.NumberUtils;

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
    return NumberUtils.secondsToHours(this.seconds);
  }

}
