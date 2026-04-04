package com.bulatewicz.task_application.Controllers;

import com.bulatewicz.task_application.Enums.TaskPriority;
import com.bulatewicz.task_application.Enums.TaskStatus;
import com.bulatewicz.task_application.Tasks.Task;
import com.bulatewicz.task_application.Tasks.TaskService;
import com.bulatewicz.task_application.Users.User;
import com.bulatewicz.task_application.Users.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Autowired
    private UserService userService;

    @GetMapping({"/", "/tasks"})
    public String viewTasks(Model model, @AuthenticationPrincipal User user) {
        if(user == null) {
            return "redirect:/settings";
        }

        List<Task> tasks = taskService.getTasksForUser(user);

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage" , "tasks");

        return "tasks";
    }

    @GetMapping("/history")
    public String viewHistory(Model model, @AuthenticationPrincipal User user) {
        List<Task> tasks = taskService.getAllTasksForUser(user);

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage" , "history");
        return "history";
    }

    @GetMapping("/recentlyDeleted")
    public String viewRecentlyDeleted(Model model, @AuthenticationPrincipal User user) {
        List<Task> tasks = taskService.getDeletedTasksForUser(user);

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
                          @AuthenticationPrincipal User user) {

        if (user != null) {
            taskService.createTask(user, description, dueDate, priority);
        }
        return "redirect:/";
    }

    @PostMapping("/completeTask/{id}")
    public String completeTask(@PathVariable UUID id) {
        taskService.completeTask(id);

        return "redirect:/";
    }

    @PostMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);

        return "redirect:/";
    }

    @PostMapping("/permDeleteTask/{id}")
    public String permDeleteTask(@PathVariable UUID id) {
        taskService.permDeleteTask(id);

        return "redirect:/recentlyDeleted";
    }

    @PostMapping("/removeFromHistory/{id}")
    public String removeFromHistory(@PathVariable UUID id) {
        taskService.permDeleteTask(id);

        return "redirect:/history";
    }

    @PostMapping("/recoverTask/{id}")
    public String recoverTask(@PathVariable UUID id) {
        taskService.recoverTask(id);

        return "redirect:/recentlyDeleted";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        userService.registerUser(username, password);

        System.out.println(username + " " + password);

        return "redirect:/settings?registered=true";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(@AuthenticationPrincipal User user, HttpServletRequest request) throws ServletException {
        userService.deleteUser(user);

        request.logout();

        return "redirect:/settings?deleted=true";
    }
}
