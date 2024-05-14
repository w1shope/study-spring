package com.example.devops.repository;

import com.example.devops.entity.Comment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findTopByPostIdOrderByCreatedDateDesc(Long postId);

    @Query("select c from Comment c join fetch c.user u where c.createdDate > :lastNotifiedTime and c.post.id = :postId")
    List<Comment> getNewNotificationComments(
        @Param("lastNotifiedTime") LocalDateTime lastNotifiedTime,
        @Param("postId") Long postId);

    List<Comment> findAllByPostId(Long postId);
}
