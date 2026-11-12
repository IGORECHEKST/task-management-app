package task_management_app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TaskManagementAppApplicationTests {

    @Test
    void applicationClassLoads() {
        TaskManagementAppApplication application = new TaskManagementAppApplication();
        assertNotNull(application);
    }
}
