package task_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task_management_app.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}

