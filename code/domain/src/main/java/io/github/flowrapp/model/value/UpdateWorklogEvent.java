package io.github.flowrapp.model.value;

import io.github.flowrapp.model.Worklog;

import org.springframework.context.ApplicationEvent;

/**
 * Represents an event that is triggered when a worklog is updated.
 */
public class UpdateWorklogEvent extends ApplicationEvent {

  public UpdateWorklogEvent(Worklog updatedWorklog) {
    super(updatedWorklog);
  }

  public static UpdateWorklogEvent of(Worklog updatedWorklog) {
    return new UpdateWorklogEvent(updatedWorklog);
  }

}
