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
import io.github.flowrapp.port.output.MailSenderPort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

  @MockitoBean
  private MailSenderPort mailSender; // mock mail sender to avoid sending real emails during tests

  @Test
  void testClockInAfterCurrentTimestamp_shouldSucceed() {
    // Clock-in doesn't validate timestamp - it accepts future timestamps
    // The validation happens during clock-out
    var clockInRequest = new ClockInRequestDTO()
        .clockIn(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).plusHours(1));

    var response = testRestTemplate.exchange(
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
    var clockInRequest = new ClockInRequestDTO()
        .clockIn(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusHours(2));

    var clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    var worklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out with future timestamp (should fail)
    var clockOutRequest = new ClockOutRequestDTO()
        .clockOut(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).plusHours(1));

    var clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + worklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        String.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testClockOutWithDurationMoreThanOneDay_shouldReturn400() {
    // Step 1: Clock-in 25 seconds ago
    var clockInRequest = new ClockInRequestDTO()
        .clockIn(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusHours(25));

    var clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    var worklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out now (duration > 1 day, should fail)
    var clockOutRequest = new ClockOutRequestDTO()
        .clockOut(OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusMinutes(1)); // Just before current time to ensure it's valid
                                                                                   // timestamp

    var clockOutResponse = testRestTemplate.exchange(
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
    var clockInTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusHours(8);
    var clockInRequest = new ClockInRequestDTO()
        .clockIn(clockInTime);

    var clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    var worklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out with valid timestamp
    var clockOutTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid")).minusMinutes(1);
    var clockOutRequest = new ClockOutRequestDTO()
        .clockOut(clockOutTime);

    var clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + worklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(OK);

    // Verify worklog is closed
    var worklog = worklogJpaRepository.findById(worklogId.intValue());
    assertThat(worklog).isPresent();
    assertThat(worklog.get().getClockOut()).isNotNull();

    // Verify report record exists for that day
    // Note: Using user ID 1 (admin) and business ID 1 as per test data
    var clockDay = clockInTime.toLocalDate();
    Thread.sleep(1000); // Wait for report generation

    // Check if any report exists for this user, business, and day
    var allReports = reportJpaRepository.findAll();
    var dayReport = allReports.stream()
        .filter(r -> r.getUser().getId().equals(1))
        .filter(r -> r.getBusiness().getId().equals(1))
        .filter(r -> r.getId().getClockDay().equals(clockDay))
        .findFirst();

    assertThat(dayReport).isPresent();
  }

  @Test
  void testCrossDayWorklog_shouldCreateTwoWorklogsAndTwoReports() throws InterruptedException {
    // Step 1: Clock-in late at night (yesterday 23:30)
    var clockInTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid"))
        .minusDays(1)
        .withHour(23)
        .withMinute(30)
        .withSecond(0)
        .withNano(0);

    var clockInRequest = new ClockInRequestDTO()
        .clockIn(clockInTime);

    var clockInResponse = testRestTemplate.exchange(
        post("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/clock-in")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockInRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockInResponse.getStatusCode()).isEqualTo(OK);
    var originalWorklogId = clockInResponse.getBody().getId();

    // Step 2: Clock-out early morning (today 07:30)
    var clockOutTime = OffsetDateTime.now(ZoneId.of("Europe/Madrid"))
        .withHour(7)
        .withMinute(30)
        .withSecond(0)
        .withNano(0);

    var clockOutRequest = new ClockOutRequestDTO()
        .clockOut(clockOutTime);

    var clockOutResponse = testRestTemplate.exchange(
        put("/api/v1/businesses/" + BUSINESS_ID + "/worklogs/" + originalWorklogId + "/clock-out")
            .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(clockOutRequest),
        ClockIn200ResponseDTO.class);

    assertThat(clockOutResponse.getStatusCode()).isEqualTo(OK);

    // Verify two worklogs were created (original gets split by day)
    var allWorklogs = worklogJpaRepository.findAll();
    var crossDayWorklogs = allWorklogs.stream()
        .filter(w -> w.getUser().getId().equals(1) && w.getBusiness().getId().equals(1))
        .filter(w -> w.getClockIn().toLocalDate().equals(clockInTime.toLocalDate())
            ||
            w.getClockIn().toLocalDate().equals(clockOutTime.toLocalDate()))
        .toList();

    assertThat(crossDayWorklogs).hasSize(2);

    // Verify two report records were created (one for each day)
    Thread.sleep(1000); // Wait for reports to be generated
    var allReports = reportJpaRepository.findAll();

    var yesterdayReport = allReports.stream()
        .filter(r -> r.getUser().getId().equals(1))
        .filter(r -> r.getBusiness().getId().equals(1))
        .filter(r -> r.getId().getClockDay().equals(clockInTime.toLocalDate()))
        .findFirst();

    var todayReport = allReports.stream()
        .filter(r -> r.getUser().getId().equals(1))
        .filter(r -> r.getBusiness().getId().equals(1))
        .filter(r -> r.getId().getClockDay().equals(clockOutTime.toLocalDate()))
        .findFirst();

    assertThat(yesterdayReport).isPresent();
    assertThat(todayReport).isPresent();
  }
}
