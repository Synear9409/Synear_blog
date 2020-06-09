package com.cxzjava.blog.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogQuery {

    private String title;
    private Long typeId;
    private boolean recommend;

    public BlogQuery() {
    }
}
