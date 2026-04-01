package com.bulatewicz.task_application.Debugging;

import com.bulatewicz.task_application.Enums.TaskPriority;
import com.bulatewicz.task_application.Enums.TaskStatus;
import com.bulatewicz.task_application.Repositories.TaskRepository;
import com.bulatewicz.task_application.Repositories.UserRepository;
import com.bulatewicz.task_application.Tasks.Task;
import com.bulatewicz.task_application.Users.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, TaskRepository taskRepository) {
        return args -> {
            if (userRepository.findByUsername("Dev") == null) {
                User devUser = User.builder()
                        .username("Dev")
                        .password("password")
                        .build();
                userRepository.save(devUser);

                Task task1 = Task.builder()
                        .description("DO SOMETHING")
                        .dueDate(LocalDate.now().plusDays(2))
                        .status(TaskStatus.IN_PROGRESS)
                        .priority(TaskPriority.HIGH)
                        .owner(devUser)
                        .build();

                Task task2 = Task.builder()
                        .description("KKKJCW")
                        .dueDate(LocalDate.now().minusDays(1)) //OVERDUE
                        .status(TaskStatus.IN_PROGRESS)
                        .priority(TaskPriority.MEDIUM)
                        .owner(devUser)
                        .build();

                taskRepository.saveAll(List.of(task1, task2));
            }
        };
    }
}
