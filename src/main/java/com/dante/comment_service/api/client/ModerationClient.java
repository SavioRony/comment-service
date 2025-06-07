package com.dante.comment_service.api.client;


import com.dante.comment_service.api.model.ModerationInput;
import com.dante.comment_service.api.model.ModerationOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/moderation")
public interface ModerationClient {

    @PostExchange
    ModerationOutput moderate(@RequestBody ModerationInput moderationInput);

}
