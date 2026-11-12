package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import task_management_app.model.Comment;
import task_management_app.model.Priority;
import task_management_app.model.Status;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.TaskRepository;
import task_management_app.service.impl.EmailNotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private EmailNotificationServiceImpl notificationService;

    private User assignee;
    private Task task;
    private Comment comment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "fromEmail", "noreply@taskmanagement.com");
        ReflectionTestUtils.setField(notificationService, "mailEnabled", true);

        assignee = new User();
        assignee.setId(1L);
        assignee.setUsername("john_doe");
        assignee.setEmail("john@example.com");

        task = new Task();
        task.setId(10L);
        task.setName("Prepare release");
        task.setPriority(Priority.HIGH);
        task.setStatus(Status.IN_PROGRESS);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setAssignee(assignee);

        comment = new Comment();
        comment.setId(100L);
        comment.setTask(task);
        comment.setUser(assignee);
        comment.setText("Almost done!");
        comment.setTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("notifyTaskAssigned: sends SimpleMailMessage when mailEnabled is true")
    void notifyTaskAssigned_MailEnabled_SendsEmail() {
        notificationService.notifyTaskAssigned(assignee, task);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals("john@example.com", sentMessage.getTo()[0]);
        assertEquals("New Task Assigned: Prepare release", sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
        assertTrue(sentMessage.getText().contains("Prepare release"));
    }

    @Test
    @DisplayName("notifyNewComment: sends SimpleMailMessage with comment text when mailEnabled is true")
    void notifyNewComment_MailEnabled_SendsEmail() {
        notificationService.notifyNewComment(assignee, task, comment);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals("john@example.com", sentMessage.getTo()[0]);
        assertEquals("New Comment on Task: Prepare release", sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
        assertTrue(sentMessage.getText().contains("Almost done!"));
    }

    @Test
    @DisplayName("notifyTaskDeadlineApproaching: sends SimpleMailMessage when mailEnabled is true")
    void notifyTaskDeadlineApproaching_MailEnabled_SendsEmail() {
        notificationService.notifyTaskDeadlineApproaching(assignee, task);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals("john@example.com", sentMessage.getTo()[0]);
        assertEquals("Task Deadline Approaching: Prepare release", sentMessage.getSubject());
    }

    @Test
    @DisplayName("notifyTaskAssigned: does not call JavaMailSender when mailEnabled is false")
    void notifyTaskAssigned_MailDisabled_DoesNotSendEmail() {
        ReflectionTestUtils.setField(notificationService, "mailEnabled", false);

        notificationService.notifyTaskAssigned(assignee, task);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("TaskDeadlineScheduler: notifies assignees for tasks due tomorrow")
    void sendDeadlineReminders_FindsTasksAndNotifiesAssignees() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(taskRepository.findAllByDueDateAndStatusNot(tomorrow, Status.COMPLETED))
                .thenReturn(List.of(task));

        TaskDeadlineScheduler scheduler = new TaskDeadlineScheduler(taskRepository, notificationService);
        scheduler.sendDeadlineReminders();

        verify(taskRepository).findAllByDueDateAndStatusNot(tomorrow, Status.COMPLETED);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}

