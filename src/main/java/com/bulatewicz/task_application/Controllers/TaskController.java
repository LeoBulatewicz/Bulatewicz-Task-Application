package com.bulatewicz.task_application.Controllers;

import com.bulatewicz.task_application.Enums.TaskPriority;
import com.bulatewicz.task_application.Enums.TaskStatus;
import com.bulatewicz.task_application.Tasks.Task;
import com.bulatewicz.task_application.Tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping({"/", "/tasks"})
    public String viewTasks(Model model, Principal principal) {
        List<Task> tasks = taskService.getTasksForUser(principal);
        //System.out.println(tasks);

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
    public String viewRecentlyDeleted(Model model) {
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
                          @RequestParam String dueDate,
                          @RequestParam String priority,
                          Principal principal) {

        if (principal != null) {
            taskService.createTask(principal, description, dueDate, priority);
        }
        return "redirect:/";
    }
}
