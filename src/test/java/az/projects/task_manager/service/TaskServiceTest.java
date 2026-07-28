package az.projects.task_manager;

import az.projects.task_manager.dto.request.TaskRequest;
import az.projects.task_manager.dto.response.TaskResponse;
import az.projects.task_manager.exception.TaskNotFoundException;
import az.projects.task_manager.model.Task;
import az.projects.task_manager.model.TaskStatus;
import az.projects.task_manager.model.User;
import az.projects.task_manager.repository.TaskRepository;
import az.projects.task_manager.repository.UserRepository;
import az.projects.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("test@example.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            TaskRequest request = new TaskRequest();
            request.setTitle("Write tests");
            request.setDescription("Cover the service layer");
            request.setStatus(TaskStatus.TODO);

            Task savedTask = new Task();
            savedTask.setId(1L);
            savedTask.setTitle("Write tests");
            savedTask.setDescription("Cover the service layer");
            savedTask.setStatus(TaskStatus.TODO);
            savedTask.setUser(testUser);

            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            TaskResponse response = taskService.createTask(request);

            assertThat(response.getTitle()).isEqualTo("Write tests");
            assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
            verify(taskRepository, times(1)).save(any(Task.class));
        }
    }

    @Test
    void getTaskById_shouldThrowException_whenTaskNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("test@example.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(taskRepository.findByIdAndUserEmail(99L, "test@example.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTaskById(99L))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Test
    void deleteTask_shouldCallRepositoryDelete_whenTaskExists() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("test@example.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Task existingTask = new Task();
            existingTask.setId(1L);

            when(taskRepository.findByIdAndUserEmail(1L, "test@example.com"))
                    .thenReturn(Optional.of(existingTask));

            taskService.deleteTask(1L);

            verify(taskRepository, times(1)).delete(existingTask);
        }
    }
}