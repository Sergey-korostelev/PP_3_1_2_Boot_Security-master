package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<Role> set = new HashSet<>();
        set.add(role);
        userService.saveUser(new User(user.getUsername(), user.getPassword(), set));
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/edit")
    public String getUserEdit(Model model, @RequestParam long id) {
        User userId = userService.findById(id).get();
        model.addAttribute("userId", userId);
        Role role = new Role(userId.getRoles().toString());
        model.addAttribute("new_user_role", role);
        return "edit";
    }
    @PostMapping(value = "/update")
    public String editUser(@ModelAttribute("new_user") User user, @ModelAttribute("new_user_role") Role role) {
        Set<Role> set = new HashSet<>();
        set.add(role);
        user.setRoles(set);
        userService.mergeUser(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/delete")
    public String getUserDelete(Model model, @RequestParam long id) {
        User userId = userService.findById(id).get();
        model.addAttribute("userId", userId);
        Role role = new Role(userId.getRoles().toString());
        model.addAttribute("new_user_role", role);
        return "delete";
    }
    @PostMapping(value = "/delete")
    public String deleteUser(@ModelAttribute("new_user") User user) {
        userService.deleteById(user.getId());
        return "redirect:/admin";
    }


}
