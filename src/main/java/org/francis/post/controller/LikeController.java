package org.francis.post.controller;

import org.francis.post.constant.PostConstants;
import org.francis.post.entity.Like;
import org.francis.post.schedule.PostTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author Franc1s
 * @date 2022/6/20
 * @apiNote
 */
@RestController
public class LikeController {
    private static final Logger log = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private PostTaskService postTaskService;

    @PostMapping("/like")
    public void like(Long userId, Long postId, Boolean isLike) {
        //进行数据检验 如果是点赞的话查看是否已经点赞过了(Mysql && Redis) 取消点赞也一样
        //valid code....
        //post_set添加postId
        redisTemplate.opsForSet().add(PostConstants.POST_SET_KEY, postId);
        //post_user_like_set_${postId}添加点赞记录用户id
        redisTemplate.opsForSet().add(PostConstants.POST_USER_LIKE_SET_POST_KEY + postId, userId);
        //点赞记录
        Like like = new Like();
        like.setLike(isLike);
        like.setPostId(postId);
        like.setUserId(userId);
        like.setLikeOprTime(LocalDateTime.now());
        //post_user_like_${postId}_${userId} 添加点赞记录
        redisTemplate.opsForHash().put(PostConstants.POST_USER_LIKE_SET_POST_USER_KEY, postId + "_" + userId, like);
        //如果点赞就+1，取消点赞就-1
        if (isLike) {
            redisTemplate.opsForValue().increment(PostConstants.POST_COUNTER_KEY+postId);
        } else {
            redisTemplate.opsForValue().decrement(PostConstants.POST_COUNTER_KEY+postId);
        }
    }

    @PostMapping("/sync")
    public void sync() {
        postTaskService.syncPostLikeToMysql();
    }
}
