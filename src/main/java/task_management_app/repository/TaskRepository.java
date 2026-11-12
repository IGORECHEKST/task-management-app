package task_management_app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management_app.model.Status;
import task_management_app.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Page<Task> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Optional<Task> findById(Long id);

    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Page<Task> findAllByProjectId(Long projectId, Pageable pageable);

    @EntityGraph(attributePaths = {"assignee", "project"})
    List<Task> findAllByDueDateAndStatusNot(LocalDate dueDate, Status status);
}
