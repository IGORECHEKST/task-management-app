package task_management_app.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task_management_app.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateRequestDto {
    @NotNull
    private Role.RoleName role;
    private Set<Role.RoleName> roles;

    public UserRoleUpdateRequestDto(Role.RoleName role) {
        this.role = role;
        this.roles = role != null ? Set.of(role) : Collections.emptySet();
    }

    public UserRoleUpdateRequestDto(Set<Role.RoleName> roles) {
        this.roles = roles;
        if (roles != null && !roles.isEmpty()) {
            this.role = roles.iterator().next();
        }
    }

    public void setRole(Role.RoleName role) {
        this.role = role;
        if (this.roles == null || this.roles.isEmpty()) {
            this.roles = role != null ? Set.of(role) : Collections.emptySet();
        }
    }

    public void setRoles(Set<Role.RoleName> roles) {
        this.roles = roles;
        if (this.role == null && roles != null && !roles.isEmpty()) {
            this.role = roles.iterator().next();
        }
    }

    public Set<Role.RoleName> getRoles() {
        if (roles != null && !roles.isEmpty()) {
            return roles;
        }
        if (role != null) {
            return Set.of(role);
        }
        return Collections.emptySet();
    }
}
