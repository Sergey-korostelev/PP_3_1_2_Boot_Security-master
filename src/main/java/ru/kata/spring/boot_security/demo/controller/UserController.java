package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {
    @Autowired
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService service, RoleService roleService) {
        this.userService = service;
        this.roleService = roleService;
    }


    @GetMapping(value = "/")
    public String hello() {
        return "/hello";
    }

    @GetMapping(value = "/user")
    public String showUser(Principal principal, Model model) {
        for (User user : userService.findAll()) {
            if (user.getUsername().equals(principal.getName())) {
                model.addAttribute("userId", user);
                return "user";
            }
        }
        return "notUser";
    }


    @GetMapping(value = "/admin")
    public String showAdmin(ModelMap model, Principal principal) {
        for (User user : userService.findAll()) {
            if (user.getUsername().equals(principal.getName())) {
                model.addAttribute("userId", user);
            }
        }
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("new_user", new User());
        model.addAttribute("new_user_role", new Role());
        return "admin";
    }

    @PostMapping(value = "/saveUser")
    public String saveUser(@ModelAttribute("new_user") User user, @ModelAttribute("new_user_role") Role role) {
        if (role.getName() != null) {
            Set<Role> set = Arrays.stream(role.getName().split(",")).map(Role::new).collect(Collectors.toSet());
            userService.saveUser(new User(user.getUsername(), user.getPassword(), set));
        }
        return "redirect:/admin";
    }

    @GetMapping("/findOne")
    @ResponseBody
    public User findOne(Long id) {
        return userService.findById(id).get();
    }

    @PostMapping(value = "/update")
    public String editUser(@ModelAttribute("new_user") User user, @ModelAttribute("new_user_role") Role role) {
        if (role.getName() != null) {
            Set<Role> set = Arrays.stream(role.getName().split(",")).map(Role::new).collect(Collectors.toSet());
            user.setRoles(set);
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/delete")
    public String deleteUser(@ModelAttribute("new_user") User user) {
        userService.deleteById(user.getId());
        return "redirect:/admin";
    }
}
