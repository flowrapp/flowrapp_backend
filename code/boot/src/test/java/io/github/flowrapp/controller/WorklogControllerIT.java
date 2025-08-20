package io.github.flowrapp.controller;

import static io.github.flowrapp.TestUtils.basicAuth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.http.RequestEntity.put;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import io.github.flowrapp.Application;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockIn200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockInRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockOutRequestDTO;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.ReportJpaRepository;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.WorklogJpaRepository;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
@InitDatabase
class WorklogControllerIT {

  private static final String ADMIN_EMAIL = "admin@admin.com";

  private static final String ADMIN_PASSWORD = "1234";

  private static final Long BUSINESS_ID = 1L;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private WorklogJpaRepository worklogJpaRepository;

  @Autowired
  private ReportJpaRepository reportJpaRepository;

  @Test
  void testClockInAfterCurrentTimestamp_shouldSucceed() {
    // Clock-in doesn't validate timestamp - it accepts future timestamps
    // The validation happens during clock-out
    val clockInRequest = new ClockInRequestDTO()
        .clockIn(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).plusHours(1));

    val response = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(response)
        .returns(OK, ResponseEntity::getStatusCode);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(ClockIn200ResponseDTO::getId)
        .isNotNull();
  }

  @Test
  void testClockOutAfterCurrentTimestamp_shouldReturn400() {
    // Step 1: Clock-in with past timestamp
    val clockInRequest = new ClockInRequestDTO()
        .clockIn(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusHours(2));

    val clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    val worklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out with future timestamp (should fail)
    val clockOutRequest = new ClockOutRequestDTO()
        .clockOut(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).plusHours(1));

    val clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + worklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        String.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testClockOutWithDurationMoreThanOneDay_shouldReturn400() {
    // Step 1: Clock-in 25 hours ago
    val clockInRequest = new ClockInRequestDTO()
        .clockIn(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusHours(25));

    val clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    val worklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out now (duration > 1 day, should fail)
    val clockOutRequest = new ClockOutRequestDTO()
        .clockOut(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusMinutes(1)); // Just before current time to ensure it's valid timestamp

    val clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + worklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        String.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testSuccessfulClockInAndClockOut_shouldCreateReportRecord() throws InterruptedException {
    // Step 1: Clock-in with past timestamp
    val clockInTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusHours(8);
    val clockInRequest = new ClockInRequestDTO()
        .clockIn(clockInTime);

    val clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    val worklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out with valid timestamp
    val clockOutTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusMinutes(1);
    val clockOutRequest = new ClockOutRequestDTO()
        .clockOut(clockOutTime);

    val clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + worklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(OK);

    // Verify worklog is closed
    val worklog = worklogJpaRepository.findById(worklogId.intValue());
    assertThat(worklog).isPresent();
    assertThat(worklog.get().getClockOut()).isNotNull();

    // Verify report record exists for that day
    // Note: Using user ID 1 (admin) and business ID 1 as per test data
    val clockDay = clockInTime.toLocalDate();
    Thread.sleep(1000); // Wait for report generation

    // Check if any report exists for this user, business, and day
    val allReports = reportJpaRepository.findAll();
    val dayReport = allReports.stream()
        .filter(r -> r.getUser().getId().equals(1))
        .filter(r -> r.getBusiness().getId().equals(1))
        .filter(r -> r.getId().getClockDay().equals(clockDay))
        .findFirst();

    assertThat(dayReport).isPresent();
  }

  @Test
  void testCrossDayWorklog_shouldCreateTwoWorklogsAndTwoReports() throws InterruptedException {
    // Step 1: Clock-in late at night (yesterday 23:30)
    val clockInTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid"))
        .minusDays(1)
        .withHour(23)
        .withMinute(30)
        .withSecond(0)
        .withNano(0);

    val clockInRequest = new ClockInRequestDTO()
        .clockIn(clockInTime);

    val clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    val originalWorklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out early morning (today 07:30)
    val clockOutTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid"))
        .withHour(7)
        .withMinute(30)
        .withSecond(0)
        .withNano(0);

    val clockOutRequest = new ClockOutRequestDTO()
        .clockOut(clockOutTime);

    val clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + originalWorklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(OK);

    // Verify two worklogs were created (original gets split by day)
    val allWorklogs = worklogJpaRepository.findAll();
    val crossDayWorklogs = allWorklogs.stream()
        .filter(w -> w.getUser().getId().equals(1) && w.getBusiness().getId().equals(1))
        .filter(w -> w.getClockIn().toLocalDate().equals(clockInTime.toLocalDate())
            ||
            w.getClockIn().toLocalDate().equals(clockOutTime.toLocalDate()))
        .toList();

    assertThat(crossDayWorklogs).hasSize(2);

    // Verify two report records were created (one for each day)
    Thread.sleep(1000); // Wait for reports to be generated
    val allReports = reportJpaRepository.findAll();

    val yesterdayReport = allReports.stream()
        .filter(r -> r.getUser().getId().equals(1))
        .filter(r -> r.getBusiness().getId().equals(1))
        .filter(r -> r.getId().getClockDay().equals(clockInTime.toLocalDate()))
        .findFirst();

    val todayReport = allReports.stream()
        .filter(r -> r.getUser().getId().equals(1))
        .filter(r -> r.getBusiness().getId().equals(1))
        .filter(r -> r.getId().getClockDay().equals(clockOutTime.toLocalDate()))
        .findFirst();

    assertThat(yesterdayReport).isPresent();
    assertThat(todayReport).isPresent();
  }
}
