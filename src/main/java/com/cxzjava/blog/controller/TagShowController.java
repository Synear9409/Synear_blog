package com.cxzjava.blog.controller;

import com.cxzjava.blog.pojo.BlogQuery;
import com.cxzjava.blog.pojo.Tag;
import com.cxzjava.blog.service.BlogService;
import com.cxzjava.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagsService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id, Model model){
        ///相当于获取了全部标签，标签不可能多到10000条
        List<Tag> tags = tagsService.listTagTop(10000);
        if(id == -1){
            ///获取第一个标签的id
            id = tags.get(0).getId();
        }
        model.addAttribute("tag",tags);
        model.addAttribute("page",blogService.listBlog(id,pageable));
        model.addAttribute("activeTagId",id);
        return "tags";
    }

}
