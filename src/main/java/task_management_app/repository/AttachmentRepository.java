package task_management_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import task_management_app.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @EntityGraph(attributePaths = "task")
    List<Attachment> findAllByTaskId(Long taskId);
}

