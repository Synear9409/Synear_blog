package com.cxzjava.blog.controller.admin;

import com.cxzjava.blog.pojo.Type;
import com.cxzjava.blog.service.TypeService;
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
public class TypeController {

    @Autowired
    private TypeService typeService;

    /**
     * 根据Type的分页
     * @param pageable
     * @return
     */
    @GetMapping("/type")
    public String types(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model){
        model.addAttribute("page",typeService.listType(pageable));
        return "admin/types";
    }

    /**
     * 载入新增页面
     * @param model
     * @return
     */
    @GetMapping("/type/input")
    public String input(Model model){
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }

    /**
     * 载入更新操作页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/type/{id}/input")
    public String editInput(@PathVariable Long id , Model model){
        model.addAttribute("type",typeService.getType(id));
        return "admin/types-input";
    }

    /**
     * 分类的新增、修改操作
     * @param type
     * @param result
     * @param attributes
     * @return
     */
    @PostMapping("/type/save")
    public String posType(@Valid Type type,
                          BindingResult result,
                          RedirectAttributes attributes){
        Type t1 = typeService.getTypeByName(type.getName());
        if(t1 != null){
            result.rejectValue("name","nameError","该分类已存在");
        }
        if(result.hasErrors()){
            return "admin/types-input";
        }
        ///可插入数据也可以修改保存数据
        Type sType = typeService.saveType(type);

        if(sType == null)
            attributes.addFlashAttribute("message", "操作失败!");
        else
            attributes.addFlashAttribute("message", "操作成功!");
        return "redirect:/admin/type";
    }

    @GetMapping("/type/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "删除成功!");
        return "redirect:/admin/type";
    }
}
