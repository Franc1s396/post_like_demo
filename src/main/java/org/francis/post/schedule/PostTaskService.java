package org.francis.post.schedule;

import org.francis.post.constant.PostConstants;
import org.francis.post.entity.Like;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author Franc1s
 * @date 2022/8/5
 * @apiNote
 */
@Service
public class PostTaskService {
    private static final Logger log = LoggerFactory.getLogger(PostTaskService.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 定时将Redis中点赞数据刷回MySQL
     * MySQL
     * post_like(记录用户点赞数)
     * post_user_like(记录用户点赞记录 包含user_id,post_id)
     */

    public void syncPostLikeToMysql() {
        //1.从post_set中pop出postId
        Set<Object> postSet = redisTemplate.opsForSet().members(PostConstants.POST_SET_KEY);
        log.info("开始遍历post_set");
        postSet.forEach(o -> {
            Long postId = ((Integer) o).longValue();
            log.info("获取到postId:{}", postId);
            //2.从根据pop出的postId去post_user_like_set_${postId}中pop出userId
            Set<Object> postUserLikeSet = redisTemplate.opsForSet().members(PostConstants.POST_USER_LIKE_SET_POST_KEY + postId);
            log.info("开始遍历post_user_like_set");
            postUserLikeSet.forEach(o1 -> {
                Long userId = ((Integer) o1).longValue();
                log.info("获取到userId:{}", userId);
                //3.根据postId,userId去post_user_like_${postId}_${userId}中查找点赞记录
                Map<Object, Object> likeMap = redisTemplate.opsForHash().entries(PostConstants.POST_USER_LIKE_SET_POST_USER_KEY);
                //4.根据点赞记录情况操作MySQL(点赞或取消点赞)
                Like like = (Like) likeMap.get(postId + "_" + userId);
                log.info("{}",like);
                //如果是点赞 就添加点赞记录到post_user_like表
                if (like.getLike()) {
                    //MySQL操作...
                }
                //如果是取消点赞,就删除点赞记录
                //MySQL中删除点赞记录...
                //**删除Redis记录
                log.info("删除redis记录");
                redisTemplate.opsForSet().remove(PostConstants.POST_USER_LIKE_SET_POST_KEY + postId, userId);
                redisTemplate.opsForHash().delete(PostConstants.POST_USER_LIKE_SET_POST_USER_KEY,postId + "_" + userId);
            });
            //**删除Redis记录
            log.info("删除redis记录");
            redisTemplate.opsForSet().remove(PostConstants.POST_SET_KEY, postId);
            //5.将post_counter_${postId}的数据刷回post_like表
            Integer likeCount = (Integer) redisTemplate.opsForValue().get(PostConstants.POST_COUNTER_KEY + postId);
            log.info("post_counter_{} 点赞数:{}", postId, likeCount);
            //MySQL操作...
            //**删除Redis记录
            log.info("删除redis记录");
            redisTemplate.opsForValue().getAndDelete(PostConstants.POST_COUNTER_KEY + postId);
        });
    }
}
