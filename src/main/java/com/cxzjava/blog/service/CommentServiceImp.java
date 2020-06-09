package com.cxzjava.blog.service;

import com.cxzjava.blog.dao.CommentRepository;
import com.cxzjava.blog.pojo.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 根据博客id获取评论列表
     * @param blogId
     * @return
     */
    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = Sort.by("createTime");
        ///获取顶级评论，即无父类
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentsNull(blogId,sort);
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId = comment.getParentComments().getId();
        if(parentCommentId != -1){
            /*回复时，将父级评论（被回复的）传递给回复的人，建立一个父子关系*/
            comment.setParentComments(commentRepository.getOne(parentCommentId));
        }else {
            comment.setParentComments(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }





    /**
     * 循环每个顶级的评论节点
     * @param comments
     * @return
     */
    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            ///copy，防止对原数据更改
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        //合并评论的各层子代到第一级子代集合中
        combineChildren(commentsView);
        return commentsView;
    }

    /**
     *
     * @param comments root根节点，blog不为空的对象集合
     * @return
     */
    private void combineChildren(List<Comment> comments) {

        for (Comment comment : comments) {
            ///获取comment的子评论
            List<Comment> replys1 = comment.getReplyComments();
            for(Comment reply1 : replys1) {
                //循环迭代，找出子代，存放在tempReplys中
                recursively(reply1);
            }
            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplyComments(tempReplys);
            //清除临时存放区
            tempReplys = new ArrayList<>();
        }
    }

    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplys = new ArrayList<>();
    /**
     * 递归迭代，剥洋葱
     * @param comment 被迭代的对象
     * @return
     */
    private void recursively(Comment comment) {
        tempReplys.add(comment);//子评论的顶节点添加到临时存放集合
        if (comment.getReplyComments().size()>0) {
            List<Comment> replys = comment.getReplyComments();
            for (Comment reply : replys) {
                ///再将子代的子评论加入临时存放集合
                tempReplys.add(reply);
                if (reply.getReplyComments().size()>0) {
                    ///若有，继续递归
                    recursively(reply);
                }
            }
        }
    }
}
