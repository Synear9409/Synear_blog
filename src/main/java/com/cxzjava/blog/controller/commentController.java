package com.cxzjava.blog.controller;


import com.cxzjava.blog.pojo.Comment;
import com.cxzjava.blog.pojo.User;
import com.cxzjava.blog.service.BlogService;
import com.cxzjava.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class commentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    ///获取配置文件中的配置的静态资源
    @Value("${comment.avatar}")
    private String avatar;

    /**
     * 获取每个博客的评论信息
     * @param blogId
     * @param model
     * @return
     */
    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model){
        model.addAttribute("comment",commentService.listCommentByBlogId(blogId));
        return "blogs :: commentList";
    }

    /**
     * 评论信息的发布保存
     * @param comment
     * @param session
     * @return
     */
    @PostMapping("/comments")
    public String commentPost(Comment comment, HttpSession session){
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId));

        ///判断管理员是否登录，若登录则可以管理员回复
        User user = (User) session.getAttribute("user");
        if(user != null){
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        }else{
            comment.setAvatar(avatar);
        }

        commentService.saveComment(comment);
        return "redirect:/comments/"+blogId;
    }
}
