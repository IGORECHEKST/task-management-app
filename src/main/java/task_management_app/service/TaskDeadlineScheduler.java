package task_management_app.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import task_management_app.model.Status;
import task_management_app.model.Task;
import task_management_app.repository.TaskRepository;

@Component
@RequiredArgsConstructor
public class TaskDeadlineScheduler {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional(readOnly = true)
    public void sendDeadlineReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Task> tasksDueTomorrow = taskRepository.findAllByDueDateAndStatusNot(
                tomorrow,
                Status.COMPLETED
        );
        for (Task task : tasksDueTomorrow) {
            notificationService.notifyTaskDeadlineApproaching(task.getAssignee(), task);
        }
    }
}

