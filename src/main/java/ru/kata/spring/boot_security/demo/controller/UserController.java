package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }


    @GetMapping(value = "/")
    public String hello() {
        return "/hello";
    }

    @GetMapping(value = "/user")
    public String showUser(Principal principal, Model model) {
        for (User user : service.findAll()) {
            if (user.getUsername().equals(principal.getName())) {
                model.addAttribute("userId", user);
                return "user";
            }
        }
        return "notUser";
    }


    @GetMapping(value = "/admin")
    public String showAdmin(ModelMap model, Principal principal) {
        for (User user : service.findAll()) {
            if (user.getUsername().equals(principal.getName())) {
                model.addAttribute("userId", user);
            }
        }
        model.addAttribute("users", service.findAll());
        model.addAttribute("new_user", new User());
        model.addAttribute("new_user_role", new Role());
        return "admin";
    }

    @PostMapping(value = "/saveUser")
    public String saveUser(@ModelAttribute("new_user") User user, @ModelAttribute("new_user_role") Role role) {
        List<Role> list = new ArrayList<>();
        list.add(role);
        service.saveUser(new User(user.getUsername(), user.getPassword(), list));
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/edit")
    public String getUserEdit(Model model, @RequestParam long id) {
        User userId = service.findById(id).get();
        model.addAttribute("userId", userId);
        Role role = new Role(userId.getRoles().toString());
        model.addAttribute("new_user_role", role);
        return "edit";
    }
    @PostMapping(value = "/update")
    public String editUser(@ModelAttribute("new_user") User user, @ModelAttribute("new_user_role") Role role) {
        List<Role> list = new ArrayList<>();
        list.add(role);
        user.setRoles(list);
        service.mergeUser(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/delete")
    public String getUserDelete(Model model, @RequestParam long id) {
        User userId = service.findById(id).get();
        model.addAttribute("userId", userId);
        Role role = new Role(userId.getRoles().toString());
        model.addAttribute("new_user_role", role);
        return "delete";
    }
    @PostMapping(value = "/delete")
    public String deleteUser(@ModelAttribute("new_user") User user) {
        service.deleteById(user.getId());
        return "redirect:/admin";
    }


}
