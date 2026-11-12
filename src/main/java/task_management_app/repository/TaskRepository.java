package task_management_app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import task_management_app.model.Priority;
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

    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    @Query("SELECT t FROM Task t WHERE "
            + "(:projectId IS NULL OR t.project.id = :projectId) AND "
            + "(:status IS NULL OR t.status = :status) AND "
            + "(:priority IS NULL OR t.priority = :priority) AND "
            + "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND "
            + "(:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(COALESCE(t.description, '')) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findAllWithFilters(
            @Param("projectId") Long projectId,
            @Param("status") Status status,
            @Param("priority") Priority priority,
            @Param("assigneeId") Long assigneeId,
            @Param("search") String search,
            Pageable pageable
    );
}
