package com.dante.comment_service.api.model;

import lombok.Data;

@Data
public class CommentInput {
    private String text;
    private String author;
}
