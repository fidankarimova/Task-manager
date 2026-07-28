package az.projects.task_manager.dto.request;

import az.projects.task_manager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Message cannot be longer than 255")
    private String title;

    @Size(max = 1000, message = "Description cannot be longer than 1000")
    private String description;

    private TaskStatus status;
}
