package us.dev.backend.AdminManager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/manager")
public class AdminManagerController {

    @GetMapping
    public String managerLogin(Model model, HttpServletRequest req) {
        model.addAttribute("message",req.getServletContext());
        return "managerLogin";

    }


    @GetMapping("/home")
    public String managerPage(Model model) {


        return "managerPage";
    }




}
