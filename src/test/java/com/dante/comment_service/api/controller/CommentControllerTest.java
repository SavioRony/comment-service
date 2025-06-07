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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @InjectMocks
    CommentController commentController;
    @Mock
    ModerationClient moderationClient;
    @Mock
    CommentRepository commentRepository;

    CommentId idComment = new CommentId(IdGenerator.generateTSID());
    Comment commentSaved = new Comment(idComment,"texto","author","2025-06-07T06:14:05.145085300");

    @Nested
    class CadastroComment{

        @Test
        void Dado_um_comentario_valido_Quando_criar_Entao_deve_retorna_um_id_de_cadastro(){
            //Arrange
            ModerationOutput moderationOutput = new ModerationOutput();
            moderationOutput.setApproved(true);
            moderationOutput.setReason("Texto valido");
            when(moderationClient.moderate(Mockito.any(ModerationInput.class))).thenReturn(moderationOutput);
            when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(commentSaved);

            //Act
            CommentOutput commentOutput = commentController.create(new CommentInput());

            //Assert
            assertEquals(commentOutput.getId().toString(),commentSaved.getId().toString());
            assertEquals(commentOutput.getAuthor(), commentSaved.getAuthor());
            assertEquals(commentOutput.getText(), commentSaved.getText());
            assertEquals(commentOutput.getCreatedAt(), commentSaved.getCreatedAt());

            InOrder inOrder = Mockito.inOrder(moderationClient, commentRepository);
            inOrder.verify(moderationClient).moderate(Mockito.any(ModerationInput.class));
            inOrder.verify(commentRepository).save(Mockito.any(Comment.class));

            Mockito.verify(moderationClient, Mockito.times(1)).moderate(Mockito.any(ModerationInput.class));
            Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));

        }

        @Test
        void Dado_um_comentario_inapropriado_Quando_criar_Entao_deve_retorna_uma_exececao(){
            //Arrange
            ModerationOutput moderationOutput = new ModerationOutput();
            moderationOutput.setApproved(false);
            moderationOutput.setReason("invalido");
            when(moderationClient.moderate(Mockito.any(ModerationInput.class))).thenReturn(moderationOutput);

            //Act & Assert
            assertThrows(ResponseStatusException.class, () -> commentController.create(new CommentInput()));

        }
    }

    @Nested
    class BuscarCommentPorId{
        CommentId idComment = new CommentId(IdGenerator.generateTSID());

        @Test
        void Dado_um_id_comentario_valido_Quando_buscar_por_id_Entao_deve_retorna_um_comentario(){

            //Arrange
            when(commentRepository.findById(Mockito.any(CommentId.class))).thenReturn(Optional.of(commentSaved));

            //Act
            CommentOutput commentOutput = commentController.getById(idComment.getValue());

            //Assert
            assertEquals(commentOutput.getId().toString(), commentSaved.getId().toString());
            assertEquals(commentOutput.getAuthor(), commentSaved.getAuthor());
            assertEquals(commentOutput.getText(), commentSaved.getText());
            assertEquals(commentOutput.getCreatedAt(), commentSaved.getCreatedAt());

            Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.any(CommentId.class));

        }

        @Test
        void Dado_um_comentario_inapropriado_Quando_criar_Entao_deve_retorna_uma_exececao(){
            //Arrange
            when(commentRepository.findById(Mockito.any(CommentId.class))).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ResponseStatusException.class, () -> commentController.getById(idComment.getValue()));

        }
    }

    @Nested
    class BuscarCommentPaginado{
        CommentId idComment = new CommentId(IdGenerator.generateTSID());

        @Test
        void Quando_buscar_todos_comentarios_Entao_deve_retorna_uma_lista_comentario(){

            //Arrange
            List<Comment> commentList = List.of(commentSaved);
            Page<Comment> commentPage = new PageImpl<>(commentList);
            Pageable pageable = PageRequest.of(0, 10);
            when(commentRepository.findAll(pageable)).thenReturn(commentPage);

            //Act
            Page<CommentOutput> result = commentController.search(pageable);

            //Assert
            assertEquals(1, result.getTotalElements());
            assertEquals("texto", result.getContent().get(0).getText());

        }

    }
}