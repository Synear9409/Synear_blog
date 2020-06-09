package com.cxzjava.blog.service;

import com.cxzjava.blog.dao.BlogRepository;
import com.cxzjava.blog.exception.NotFoundException;
import com.cxzjava.blog.pojo.Blog;
import com.cxzjava.blog.pojo.BlogQuery;
import com.cxzjava.blog.pojo.Type;
import com.cxzjava.blog.util.MarkdownUtils;
import com.cxzjava.blog.util.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImp implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.getOne(id);
    }

    /**
     *
     * @param id
     * @return
     */
    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        /*更新浏览次数*/
        blogRepository.updateViews(id);

        Blog blog = blogRepository.getOne(id);
        if(blog == null){
            throw new NotFoundException("该博客资源不存在");
        }
        Blog b = new Blog();
        ///复制一个blog对象，防止操作对原数据产生影响
        BeanUtils.copyProperties(blog,b);
        String content = b.getContent();
        ////将博客详情的markdown内容转换为html显示
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        return b;
    }

    /**
     *   特定分页       例如首页的分页、后台博客管理、后台搜索后的分页、分类、标签的分页
     * @param pageable
     * @param blog   根据参数blog进行的分页
     * @return
     */
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(cb.like(root.<String>get("title"), "%"+blog.getTitle()+"%"));
                }
                if (blog.getTypeId() != null) {
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                if (blog.isRecommend()) {
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    /**
     * 首页博客的模糊查询
     * @param query
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    /**
     * 根据tagId的分页
     * @param tagId
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                ///选中哪个tag 则更改原来分页方式
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    /**
     * 获取博客的前几条数据设为最新推荐
     * @param size
     * @return
     */
    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable = PageRequest.of(0,size,sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    /**
     * findGroupYear()方法将年份倒序排序
     * @return
     */
    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        ///这里要用new LinkedHashMap，不然直接new HashMap 遍历时会随机取值
        Map<String,List<Blog>> map = new LinkedHashMap<>();
        for(String year : years){
            map.put(year,blogRepository.findByYear(year));
        }
        return map;
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        ///若不存在id则是新增操作 反之为修改
        if(blog.getId() == null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else{
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.getOne(id);
        if (b == null)
            throw new NotFoundException("不存在该博客");
        //public static void copyProperties(@NotNull Object source,
        //                                  @NotNull Object target,
        //                                  String... ignoreProperties)-----想要忽略的数据
        //                                  即将【MyBeanUtils.getNullPropertyNames(blog)】里面的属性全部忽略
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

}
