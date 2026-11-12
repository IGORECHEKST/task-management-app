package task_management_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import task_management_app.model.Comment;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from:noreply@taskmanagement.com}")
    private String fromEmail;

    @Value("${notification.mail.enabled:false}")
    private boolean mailEnabled;

    @Async
    @Override
    public void notifyTaskAssigned(User assignee, Task task) {
        if (assignee == null || task == null) {
            return;
        }
        String subject = "New Task Assigned: " + task.getName();
        String body = String.format(
                "Hello %s,%n%nYou have been assigned to task '%s' (Due date: %s, Priority: %s).",
                assignee.getUsername(),
                task.getName(),
                task.getDueDate(),
                task.getPriority()
        );
        sendEmail(assignee.getEmail(), subject, body);
    }

    @Async
    @Override
    public void notifyNewComment(User assignee, Task task, Comment comment) {
        if (assignee == null || task == null || comment == null) {
            return;
        }
        String subject = "New Comment on Task: " + task.getName();
        String body = String.format(
                "Hello %s,%n%nA new comment was added to task '%s':%n\"%s\"",
                assignee.getUsername(),
                task.getName(),
                comment.getText()
        );
        sendEmail(assignee.getEmail(), subject, body);
    }

    @Async
    @Override
    public void notifyTaskDeadlineApproaching(User assignee, Task task) {
        if (assignee == null || task == null) {
            return;
        }
        String subject = "Task Deadline Approaching: " + task.getName();
        String body = String.format(
                "Hello %s,%n%nReminder: Task '%s' is due on %s.",
                assignee.getUsername(),
                task.getName(),
                task.getDueDate()
        );
        sendEmail(assignee.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            log.debug("Skipping notification because recipient email is blank for subject: {}", subject);
            return;
        }
        if (!mailEnabled) {
            log.info("Notification (mail disabled) to {}: [{}] {}", to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            log.warn("Failed to send email notification to {}: {}", to, e.getMessage());
        }
    }
}

