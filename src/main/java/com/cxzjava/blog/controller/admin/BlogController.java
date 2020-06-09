package com.cxzjava.blog.controller.admin;


import com.cxzjava.blog.pojo.*;
import com.cxzjava.blog.service.BlogService;
import com.cxzjava.blog.service.TagService;
import com.cxzjava.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {
    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blog";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    /**
     * 获取分页数据并返回到前端
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/blog")
    public String blog(@PageableDefault(size = 3,sort = {"updateTime"},direction = Sort.Direction.DESC)Pageable pageable,
                       Model model){
        model.addAttribute("type",typeService.listType());
        model.addAttribute("page",blogService.listBlog(pageable));
        return LIST;
    }

    /**
     * 模糊查询
     * @param pageable
     * @param blog
     * @param model
     * @return
     */
    @PostMapping("/blog/search")
    public String search(@PageableDefault(size = 3,sort = {"updateTime"},direction = Sort.Direction.DESC)Pageable pageable,
                         BlogQuery blog, Model model){
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs :: blogList";
    }

    /**
     * 载入博客新增页面
     * @param model
     * @return
     */
    @GetMapping("/blog/input")
    public String input(Model model){
        setTypeandTag(model);
        model.addAttribute("blog",new Blog());
        return INPUT;
    }

    /**
     * 页面初始化数据（type列表、tag列表）
     * @param model
     */
    private void setTypeandTag(Model model){
        model.addAttribute("type",typeService.listType());
        model.addAttribute("tag",tagService.listTag());
    }

    /**
     * 载入博客修改页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/blog/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        setTypeandTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog",blog);
        return INPUT;
    }

    /**
     * 修改及新增
     * @param blog
     * @param attributes
     * @param session
     * @return
     */
    @PostMapping("/blog/save")
    public String post(Blog blog, BindingResult result, RedirectAttributes attributes, HttpSession session){
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));

        /**
         * 不建议再在这加这个标题验证了，会给修改博客时造成小bug
         */
        /*
        Blog b1;
        b1 = blogService.getBlogByTitle(blog.getTitle());
        if(b1 != null){
            result.rejectValue("title","titleError","该博客标题已存在");
        }
        if(result.hasErrors())
            return INPUT;
        */
        Blog b;
        if (blog.getId() == null) {
            b =  blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }

        if (b == null ) {
            attributes.addFlashAttribute("message", "操作失败");
        } else {
            attributes.addFlashAttribute("message", "操作成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blog/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功!");
        return REDIRECT_LIST;
    }
}
