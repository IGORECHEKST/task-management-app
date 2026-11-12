package task_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task_management_app.model.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
}

