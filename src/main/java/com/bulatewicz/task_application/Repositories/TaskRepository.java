package com.bulatewicz.task_application.Repositories;

import com.bulatewicz.task_application.Enums.TaskStatus;
import com.bulatewicz.task_application.Tasks.Task;
import com.bulatewicz.task_application.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByOwner(User user);
    List<Task> findByStatusIn(Collection<TaskStatus> statuses);
    List<Task> findByOwnerAndStatusIn(User user, Collection<TaskStatus> statuses);
}
