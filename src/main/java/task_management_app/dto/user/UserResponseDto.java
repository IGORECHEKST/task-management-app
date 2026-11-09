package task_management_app.dto.user;

import java.util.Set;
import lombok.Data;
import task_management_app.model.Role;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role.RoleName> roles;
}