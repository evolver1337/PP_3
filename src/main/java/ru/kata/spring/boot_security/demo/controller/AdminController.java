package ru.kata.spring.boot_security.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String getUsersList(Model model) {
        model.addAttribute("users", userService.allUsers());
        return "/admin/user-list";
    }

    @GetMapping("/users/new")
    public String showNewUserPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/user-create";
    }

    @GetMapping(value = "/edit")
    public String showEditPage(@RequestParam("id") long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/edit-user";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user
            , BindingResult bindingResult
            , @ModelAttribute("roles") List<Role> roles) {
        if (bindingResult.hasErrors()) {
            roles.addAll(roleService.getAllRoles());
            return "/admin/user-create";
        } else {
            userService.saveUser(user);
            return "redirect:/admin";
        }
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        if (userService.getById(id) != null) {
            userService.deleteById(id);
        }
        return "redirect:/admin/users";
    }
}
