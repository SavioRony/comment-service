package com.dante.comment_service.api.model;

import lombok.Data;

@Data
public class ModerationOutput {
    private boolean approved;
    private String reason;
}
