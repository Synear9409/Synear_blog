package com.cxzjava.blog.controller.admin;

import com.cxzjava.blog.pojo.Tag;
import com.cxzjava.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * springboot JPA自带的分页功能
     * @param pageable
     * @return
     */
    @GetMapping("/tag")
    public String tags(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC)
                       Pageable pageable, Model model){
        model.addAttribute("page",tagService.listTag(pageable));
        return "admin/tags";
    }

    /**
     * 载入新增页面
     * @param model
     * @return
     */
    @GetMapping("/tag/input")
    public String input(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }

    /**
     * 载入更新操作页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/tag/{id}/input")
    public String editPost(Model model,@PathVariable Long id){
        model.addAttribute("tag",tagService.getTag(id));
        return "admin/tags-input";
    }

    /**
     * 分类的新增、修改操作
     * @param tag
     * @param result
     * @param id
     * @param attributes
     * @return
     */
    @PostMapping("/tag/save{id}")
    public String postTag(@Valid Tag tag,
                          BindingResult result,
                          @PathVariable Long id,
                          RedirectAttributes attributes){
        Tag t1 = tagService.getTagByName(tag.getName());
        if(t1 != null){
            result.rejectValue("name","nameError","该标签已存在");
        }
        if(result.hasErrors())
            return "admin/tags-input";

        //可插入数据也可以修改保存数据
        Tag tag1 = tagService.saveTag(tag);

        if(tag1 == null)
            attributes.addFlashAttribute("message","操作失败!");
        else
            attributes.addFlashAttribute("message","操作成功!");
        return "redirect:/admin/tag";
    }

    @GetMapping("/tag/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message", "删除成功!");
        return "redirect:/admin/tag";
    }
}
