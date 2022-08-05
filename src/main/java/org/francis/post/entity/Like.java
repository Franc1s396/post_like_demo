package org.francis.post.entity;

import java.time.LocalDateTime;

/**
 * @author Franc1s
 * @date 2022/6/20
 * @apiNote
 */
public class Like {
    private Long postId;
    private Long userId;
    private Boolean like;
    private LocalDateTime likeOprTime;

    @Override
    public String toString() {
        return "Like{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", like=" + like +
                ", likeOprTime=" + likeOprTime +
                '}';
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public LocalDateTime getLikeOprTime() {
        return likeOprTime;
    }

    public void setLikeOprTime(LocalDateTime likeOprTime) {
        this.likeOprTime = likeOprTime;
    }
}
