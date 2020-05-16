package us.dev.backend.AdminManager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/admin")
public class AdminManagerController {

    @GetMapping
    public String DashBoard(Model model) {

        return "adminPage";

    }
}
