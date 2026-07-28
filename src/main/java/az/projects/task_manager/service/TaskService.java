package az.projects.task_manager.service;

import az.projects.task_manager.dto.request.TaskRequest;
import az.projects.task_manager.dto.response.TaskResponse;
import az.projects.task_manager.exception.TaskNotFoundException;
import az.projects.task_manager.model.Task;
import az.projects.task_manager.model.User;
import az.projects.task_manager.repository.TaskRepository;
import az.projects.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskResponse createTask(TaskRequest taskRequest) {
        User currentUser = getCurrentUser();

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setUser(currentUser);

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findByUserEmail(currentUserEmail())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findByIdAndUserEmail(id, currentUserEmail())
                .orElseThrow(() -> new TaskNotFoundException(id));
        return toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findByIdAndUserEmail(id, currentUserEmail())
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());

        Task updated = taskRepository.save(task);
        return toResponse(updated);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findByIdAndUserEmail(id, currentUserEmail())
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }

    private String currentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(currentUserEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    // change this to MapStruct
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }
}