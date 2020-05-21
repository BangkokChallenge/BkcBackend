package us.dev.backend.AdminManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/manager")
public class AdminManagerController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostRepository postRepository;

    @GetMapping
    public String managerLogin(Model model, HttpServletRequest req) {
        model.addAttribute("message",req.getServletContext());
        return "managerLogin";

    }


    @GetMapping("/home")
    public String managerPage(Model model) {
        System.out.println("###########");
        ManagerNotice notices = new ManagerNotice();
        model.addAttribute("notices",notices);


        return "managerPage";
    }

    @GetMapping("/accountList")
    public String accountList(Model model) {
        List<Account> accountList = accountRepository.findAll();
        accountList.stream().forEach(e -> e.getId());
        model.addAttribute("accountList", accountList);

        return "accountList";
    }

    @GetMapping("/postList")
    public String postList(Model model) {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
        System.out.println(postList.size());
        model.addAttribute("postList",postList);

        return "postList";

    }

    @GetMapping("/404")
    public String page404(){

        return "404";
    }






}
