package cn.glh.alumni.service;

import cn.glh.alumni.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: Administrator
 * @Date: 2022/2/15 12:56
 * Description 点赞接口实现类
 */
@Service
public class LikeService {

    @Resource
    private RedisTemplate<String, Integer> redisTemplate;


    /**
     * 点赞
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param fromUserId 点赞人Id
     */
    public void like(String targetType, int targetId, Integer fromUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //当前目标的点赞
                String targetLikeKey = RedisKeyUtil.getTargetLikeKey(targetType, targetId);

                // 判断用户是否已经点过赞了
                boolean isMember = redisOperations.opsForSet().isMember(targetLikeKey, fromUserId);

                redisOperations.multi(); // 开启事务

                if (isMember) {
                    // 如果用户已经点过赞，点第二次则取消赞
                    redisOperations.opsForSet().remove(targetLikeKey, fromUserId);
                }
                else {
                    redisTemplate.opsForSet().add(targetLikeKey, fromUserId);
                }

                return redisOperations.exec(); // 提交事务
            }
        });
    }

    /**
     * 获取点赞数量
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 点赞数量
     */
    public long findTargetLikeCount(String targetType, int targetId) {
        String targetLikeKey = RedisKeyUtil.getTargetLikeKey(targetType, targetId);
        return redisTemplate.opsForSet().size(targetLikeKey);
    }

    /**
     * 查询某个用户对某个目标的点赞状态（是否已赞）
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param fromUserId 点赞的人
     * @return 点赞状态 1:已赞，0:未赞
     */
    public int findTargetLikeStatus(String targetType, int targetId, Integer fromUserId) {
        String entityLikeKey = RedisKeyUtil.getTargetLikeKey(targetType, targetId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, fromUserId) ? 1 : 0;
    }

    public void deleteTargetLike(String targetType, int targetId){
        //当前目标的点赞
        String targetLikeKey = RedisKeyUtil.getTargetLikeKey(targetType, targetId);
        redisTemplate.delete(targetLikeKey);
    }



}
