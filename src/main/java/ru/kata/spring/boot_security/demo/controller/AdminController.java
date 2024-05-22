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
import java.util.Optional;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = {"/users", ""})
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
    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult, Model model) {
        Optional<User> userByEmail = userService.findByUsername(user.getUsername());
        if (userByEmail.isPresent()) {
            bindingResult.rejectValue("email", "error.email",
                    "This email is already in use");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "/admin/user-create";
        }

        this.userService.saveUser(user);
        return "redirect:/admin/users/";
    }

    @GetMapping(value = "/edit")
    public String showEditPage(@RequestParam("id") Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/edit-user";
    }

    @GetMapping("/users/edit")
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        Optional<User> userById = Optional.ofNullable(userService.getById(id));

        if (userById.isPresent()) {
            model.addAttribute("user", userById.get());
            model.addAttribute("listRoles", roleService.getAllRoles());
            return "/admin/edit-user";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PatchMapping("/users/edit")
    public String editUser(@ModelAttribute("user") @Valid User updatedUser,
                           BindingResult bindingResult, Model model) {
        Optional<User> userByEmail = userService.findByUsername(updatedUser.getUsername());
        if (userByEmail.isPresent() && (!userByEmail.get().getId().equals(updatedUser.getId()))) {
            bindingResult.rejectValue("username", "error.username",
                    "This username is already taken");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("listRoles", roleService.getAllRoles());
            return "/admin/edit-user";
        }

        userService.saveUser(updatedUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/save")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/user-create";
        }
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        if (userService.getById(id).isCredentialsNonExpired()) {
            userService.deleteById(id);
        }
        return "redirect:/admin/users";
    }
}
