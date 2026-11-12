package task_management_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import task_management_app.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE "
            + "(:status IS NULL OR p.status = :status) AND "
            + "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> findAllWithFilters(
            @Param("status") Project.Status status,
            @Param("search") String search,
            Pageable pageable
    );
}
