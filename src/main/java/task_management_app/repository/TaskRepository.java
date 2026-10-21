package task_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management_app.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}

