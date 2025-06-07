package com.dante.comment_service.api.controller;

import com.dante.comment_service.api.client.ModerationClient;
import com.dante.comment_service.api.model.CommentInput;
import com.dante.comment_service.api.model.CommentOutput;
import com.dante.comment_service.api.model.ModerationInput;
import com.dante.comment_service.api.model.ModerationOutput;
import com.dante.comment_service.common.IdGenerator;
import com.dante.comment_service.domain.model.Comment;
import com.dante.comment_service.domain.model.CommentId;
import com.dante.comment_service.domain.repository.CommentRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ModerationClient moderationClient;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @PostMapping
    public CommentOutput create(@RequestBody CommentInput commentInput){
        CommentId idComment = new CommentId(IdGenerator.generateTSID());
        ModerationOutput moderate = moderationClient.moderate(convertToModeration(commentInput, idComment));
        if(!moderate.isApproved()){
            logger.warn("Comentario não aprovador: {}", moderate.getReason());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Comment commentSaved = commentRepository.save(convertToDomain(commentInput, idComment));
        return convertToOutput(commentSaved);
    }

    @GetMapping("/{commentId}")
    public CommentOutput getById(@PathVariable TSID commentId){
        CommentId id = new CommentId(commentId);
        Comment commentSaved = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToOutput(commentSaved);
    }

    @GetMapping
    public Page<CommentOutput> search(@PageableDefault Pageable pageable){
        Page<Comment> comments = commentRepository.findAll(pageable);
        return comments.map(this::convertToOutput);
    }


    private ModerationInput convertToModeration(CommentInput commentInput, CommentId commentId){
        return ModerationInput.builder()
                .text(commentInput.getText())
                .commentId(commentId.getValue())
                .build();
    }

    private Comment convertToDomain(CommentInput commentInput, CommentId commentId){
        return Comment.builder()
                .id(commentId)
                .author(commentInput.getAuthor())
                .text(commentInput.getText())
                .createdAt(LocalDateTime.now().toString())
                .build();
    }

    private CommentOutput convertToOutput(Comment comment){
        return CommentOutput.builder()
                .id(comment.getId().getValue())
                .author(comment.getAuthor())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
