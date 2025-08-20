package io.github.flowrapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.output.ReportRepositoryOutput;
import io.github.flowrapp.value.CreateWorklogEvent;

import lombok.val;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class WorkLogListenerProcessorTest {

  @Mock
  private ReportRepositoryOutput reportRepositoryOutput;

  @InjectMocks
  private WorkLogListenerProcessor workLogListenerProcessor;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void processCreationWorkLogEvent_notExists() {
    // GIVEN
    val event = CreateWorklogEvent.created(this.createValidWorklog());

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput).save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(event.getWorklog().user().id(), report -> report.user().id())
        .returns(event.getWorklog().business().id(), report -> report.business().id())
        .returns(event.getWorklog().getHours(), Report::hours)
        .returns(event.getWorklog().getDay(), Report::day)));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void processCreationWorkLogEvent_Exists(Report alreadyExists) {
    // GIVEN
    val event = CreateWorklogEvent.created(this.createValidWorklog());
    when(reportRepositoryOutput.getByDay(
        event.getWorklog().user().id(), event.getWorklog().business().id(), event.getWorklog().getDay()))
            .thenReturn(Optional.of(alreadyExists));

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput).save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(alreadyExists.user().id(), report -> report.user().id())
        .returns(alreadyExists.business().id(), report -> report.business().id())
        .returns(alreadyExists.hours().add(event.getWorklog().getHours()), Report::hours)
        .returns(alreadyExists.day(), Report::day)));
  }

  @ParameterizedTest
  @InstancioSource
  void processUpdateWorkLogEvent(Worklog previous, Report alreadyExists) {
    // GIVEN
    val worklog = this.createValidWorklog();
    val event = CreateWorklogEvent.updated(worklog, previous);

    when(reportRepositoryOutput.getByDay(
        event.getWorklog().user().id(), event.getWorklog().business().id(), event.getWorklog().getDay()))
            .thenReturn(Optional.of(alreadyExists));

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput).save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(alreadyExists.user().id(), report -> report.user().id())
        .returns(alreadyExists.business().id(), report -> report.business().id())
        .returns(alreadyExists.hours().subtract(previous.getHours()).add(worklog.getHours()), Report::hours)
        .returns(alreadyExists.day(), Report::day)));
  }

  @ParameterizedTest
  @InstancioSource
  void processUpdateWorkLogEvent_notExists(Worklog previous) {
    // GIVEN
    val worklog = this.createValidWorklog();
    val event = CreateWorklogEvent.updated(worklog, previous);

    when(reportRepositoryOutput.getByDay(
        event.getWorklog().user().id(), event.getWorklog().business().id(), event.getWorklog().getDay()))
            .thenReturn(Optional.empty());

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput, never()).save(any());
  }

  @ParameterizedTest
  @InstancioSource
  void processDeleteWorkLogEvent(Report alreadyExists) {
    // GIVEN
    val worklog = this.createValidWorklog();
    val event = CreateWorklogEvent.deleted(worklog);

    when(reportRepositoryOutput.getByDay(
        event.getWorklog().user().id(), event.getWorklog().business().id(), event.getWorklog().getDay()))
            .thenReturn(Optional.of(alreadyExists));

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput).save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(alreadyExists.user().id(), report -> report.user().id())
        .returns(alreadyExists.business().id(), report -> report.business().id())
        .returns(alreadyExists.hours().subtract(worklog.getHours()), Report::hours)
        .returns(alreadyExists.day(), Report::day)));
  }

  @ParameterizedTest
  @InstancioSource
  void processDeleteWorkLogEvent_notExists() {
    // GIVEN
    val worklog = this.createValidWorklog();
    val event = CreateWorklogEvent.deleted(worklog);

    when(reportRepositoryOutput.getByDay(
        event.getWorklog().user().id(), event.getWorklog().business().id(), event.getWorklog().getDay()))
            .thenReturn(Optional.empty());

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput, never()).save(any());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void processDeleteWorkLogEvent_notValid(Worklog worklog) {
    // GIVEN
    val event = CreateWorklogEvent.deleted(
        worklog.withClockOut(null));

    // WHEN
    workLogListenerProcessor.listenToWorkLogEvents(event);

    // THEN
    verify(reportRepositoryOutput, never()).save(any());
  }

  private Worklog createValidWorklog() {
    var clockIn = Instancio.gen().temporal().offsetDateTime().max(OffsetDateTime.now().minusDays(2))
        .min(OffsetDateTime.now().minusDays(3)).get();
    return Instancio.of(Worklog.class)
        .set(field(Worklog::clockIn), clockIn)
        .generate(field(Worklog::clockOut), gen -> gen.temporal().offsetDateTime().range(clockIn, clockIn.plusDays(1)))
        .create();
  }

}
