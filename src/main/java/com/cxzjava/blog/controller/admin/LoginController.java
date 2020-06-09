package com.cxzjava.blog.controller.admin;


import com.cxzjava.blog.pojo.User;
import com.cxzjava.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String loginPage(){
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession httpSession,
                        RedirectAttributes attributes){
        User user = userService.checkUser(username,password);
        if(user != null){
            ///登陆成功后，将session中用户的密码设置为空，防止被非法获取
            user.setPassword(null);
            httpSession.setAttribute("user",user);
            return "redirect:/admin/main";
        }else{
            /*这里也可以用Model来传递*/
            attributes.addFlashAttribute("message","用户名和密码错误");
            return "redirect:/admin";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/admin";
    }

}
