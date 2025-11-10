package com.capstone.ticketing_system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/AllEvents.html";
    }

    @GetMapping("/AllEvents")
    public String allEvents() {
        return "redirect:/AllEvents.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/register.html";
    }

    @GetMapping("/admin-dashboard")
    public String admin_dashboard() {
        return "redirect:/admin-dashboard.html";
    }

    @GetMapping("/customer-dashboard")
    public String customer_dashboard() {
        return "redirect:/customer-dashboard.html";
    }

    @GetMapping("/organizer-dashboard")
    public String organizer_dashboard() {
        return "redirect:/organizer-dashboard.html";
    }

    @GetMapping("/userProfile")
    public String user_profile() {
        return "redirect:/userProfile.html";
    }

    @GetMapping("/ApproveOrg")
    public String ApproveOrg() {
        return "redirect:/ApproveOrg.html";
    }

    @GetMapping("/ViewEvent")
    public String ViewEvent() {
        return "redirect:/ViewEvent.html";
    }

    @GetMapping("/booking")
    public String booking() {
        return "redirect:/booking.html";
    }

    @GetMapping("/MyBooking")
    public String MyBooking() {
        return "redirect:/MyBooking.html";
    }

    @GetMapping("/tracksales")
    public String tracksales() {
        return "redirect:/tracksales.html";
    }

    @GetMapping("/report")
    public String report() {
        return "redirect:/report.html";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "redirect:/reset-password.html";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "redirect:/change-password.html";
    }

}