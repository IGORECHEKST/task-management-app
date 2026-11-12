package task_management_app.service;

import task_management_app.model.Comment;
import task_management_app.model.Task;
import task_management_app.model.User;

public interface NotificationService {
    void notifyTaskAssigned(User assignee, Task task);

    void notifyNewComment(User assignee, Task task, Comment comment);

    void notifyTaskDeadlineApproaching(User assignee, Task task);
}

