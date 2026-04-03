package com.bulatewicz.task_application.Controllers;

import com.bulatewicz.task_application.Enums.TaskPriority;
import com.bulatewicz.task_application.Enums.TaskStatus;
import com.bulatewicz.task_application.Tasks.Task;
import com.bulatewicz.task_application.Tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping({"/", "/tasks"})
    public String viewTasks(Model model, Principal principal) {
        List<Task> tasks = taskService.getTasksForUser(principal);

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage" , "tasks");

        return "tasks";
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        model.addAttribute("currentPage" , "history");
        return "history";
    }

    @GetMapping("/recentlyDeleted")
    public String viewRecentlyDeleted(Model model, Principal principal) {
        List<Task> tasks = taskService.getDeletedTasksForUser(principal);

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage" , "recentlyDeleted");
        return "recently_deleted";
    }

    @GetMapping("/settings")
    public String viewSetings(Model model) {
        model.addAttribute("currentPage" , "settings");
        return "settings";
    }

    @PostMapping("/createTask")
    public String addTask(@RequestParam String description,
                          @RequestParam(required = false) String dueDate,
                          @RequestParam String priority,
                          Principal principal) {

        if (principal != null) {
            taskService.createTask(principal, description, dueDate, priority);
        }
        return "redirect:/";
    }

    @PostMapping("/completeTask/{id}")
    public String completeTask(@PathVariable UUID id, Principal principal) {
        if(principal != null) {
            taskService.completeTask(id, principal);
        }

        return "redirect:/";
    }

    @PostMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable UUID id, Principal principal) {
        if(principal != null) {
            taskService.deleteTask(id, principal);
        }

        return "redirect:/";
    }

    @PostMapping("/permDeleteTask/{id}")
    public String permDeleteTask(@PathVariable UUID id, Principal principal) {
        if(principal != null) {
            taskService.permDeleteTask(id, principal);
        }

        return "redirect:/recentlyDeleted";
    }

    @PostMapping("/recoverTask/{id}")
    public String recoverTask(@PathVariable UUID id, Principal principal) {
        if(principal != null) {
            taskService.recoverTask(id, principal);
        }

        return "redirect:/recentlyDeleted";
    }
}
