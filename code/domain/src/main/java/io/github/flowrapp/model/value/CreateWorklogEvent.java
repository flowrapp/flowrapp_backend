package io.github.flowrapp.model.value;

import io.github.flowrapp.model.Worklog;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Represents an event that is triggered when a worklog is updated.
 */
@Getter
public class CreateWorklogEvent extends ApplicationEvent {

  private final Worklog worklog;

  private final Worklog previous;

  private final EventType eventType;

  public CreateWorklogEvent(Worklog updatedWorklog, EventType eventType) {
    this(updatedWorklog, null, eventType);
  }

  public CreateWorklogEvent(Worklog updatedWorklog, Worklog previous, EventType eventType) {
    super(updatedWorklog);
    this.worklog = updatedWorklog;
    this.previous = previous;
    this.eventType = eventType;
  }

  public static CreateWorklogEvent of(Worklog updatedWorklog, EventType eventType) {
    return new CreateWorklogEvent(updatedWorklog, eventType);
  }

  public static CreateWorklogEvent created(Worklog updatedWorklog) {
    return new CreateWorklogEvent(updatedWorklog, EventType.CREATE);
  }

  public static CreateWorklogEvent updated(Worklog updatedWorklog, Worklog previous) {
    return new CreateWorklogEvent(updatedWorklog, previous, EventType.UPDATE);
  }

  public static CreateWorklogEvent deleted(Worklog updatedWorklog) {
    return new CreateWorklogEvent(updatedWorklog, EventType.DELETE);
  }

  public enum EventType {
    CREATE, UPDATE, DELETE
  }

}
