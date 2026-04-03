package com.bulatewicz.task_application.Tasks;

import com.bulatewicz.task_application.Enums.TaskPriority;
import com.bulatewicz.task_application.Enums.TaskStatus;
import com.bulatewicz.task_application.Repositories.TaskRepository;
import com.bulatewicz.task_application.Repositories.UserRepository;
import com.bulatewicz.task_application.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;


    /**
     * Retrieves all tasks that should be displayed for a given {@link User} class and a {@link List} of {@link Task} classes. These tasks are filtered by:<br>
     * - Status: {@link TaskStatus#OVERDUE} tasks are prioritized<br>
     * - Due Date: The closer tasks {@link Task#getDueDate()}, the higher priority they have<br>
     * - Priority: Tasks with a higher {@link com.bulatewicz.task_application.Enums.TaskPriority} are prioritized<br>
     * - Description: Alphabetical order of the task description<br>
     */
    public List<Task> getTasksForUser(Principal principal) {
        if (principal == null) { return null; }
        User activeUser = userRepository.findByUsername(principal.getName());
        if (activeUser == null) { return null; }
        Collection<TaskStatus> activeStatuses = Set.of(
                TaskStatus.IN_PROGRESS,
                TaskStatus.OVERDUE
        );

        List<Task> tasks = taskRepository.findByOwnerAndStatusIn(activeUser, activeStatuses);
        tasks.forEach(task -> {
            if (task.getDueDate() != null &&
                    task.getStatus().equals(TaskStatus.IN_PROGRESS) &&
                    task.getDueDate().isBefore(LocalDate.now())) {
                task.setStatus(TaskStatus.OVERDUE);
            }
        });

        return tasks.stream()
                .sorted(Comparator
                        .comparing((Task task) -> task.getStatus() == TaskStatus.OVERDUE ? 0 : 1)
                        .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(task ->
                                switch(task.getPriority()) {
                                    case HIGH -> 1;
                                    case MEDIUM -> 2;
                                    case LOW -> 3;
                                    default -> 4;
                                })
                        .thenComparing(Task::getDescription, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<Task> getDeletedTasksForUser(Principal principal) {
        if (principal == null) { return null; }
        User activeUser = userRepository.findByUsername(principal.getName());
        if (activeUser == null) { return null; }
        Collection<TaskStatus> deletedStatuses = Set.of(TaskStatus.DELETED);
        List<Task> tasks = taskRepository.findByOwnerAndStatusIn(activeUser, deletedStatuses);

        tasks.forEach(task -> {
            if(task.getDeleteDate() != null &&
                    LocalDate.now().isAfter(task.getDeleteDate().plusDays(30))) {
                taskRepository.delete(task);
            }
        });

        return tasks.stream()
                .sorted(Comparator
                        .comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(task ->
                                switch(task.getPriority()) {
                                    case HIGH -> 1;
                                    case MEDIUM -> 2;
                                    case LOW -> 3;
                                    default -> 4;
                                })
                        .thenComparing(Task::getDescription, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public void createTask(Principal user, String description, String dueDate, String priority) {
        String username = user.getName();
        Task task = new Task();
        LocalDate date = (dueDate == null || dueDate.isEmpty()) ? null : LocalDate.parse(dueDate);
        task.setOwner(userRepository.findByUsername(username));
        task.setDescription(description);
        task.setDueDate(date);
        task.setCreationDate(LocalDate.now());
        task.setPriority(TaskPriority.valueOf(priority));
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);
    }

    public void deleteTask(UUID id, Principal user) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(TaskStatus.DELETED);
            task.setDeleteDate(LocalDate.now());
            taskRepository.save(task);
        });
    }

    public void completeTask(UUID id, Principal user) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.save(task);
        });
    }

    public void recoverTask(UUID id, Principal user) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setDeleteDate(null);
            taskRepository.save(task);
        });
    }

    public void permDeleteTask(UUID id, Principal user) {
        taskRepository.findById(id).ifPresent(task -> {
            taskRepository.delete(task);
        });
    }
}
