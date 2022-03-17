package cn.glh.alumni.service;

import cn.glh.alumni.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: Administrator
 * @Date: 2022/2/17 11:13
 * Description
 */
@Service
public class CollectService {

    @Resource
    private RedisTemplate<String, Integer> redisTemplate;


    /**
     * 收藏
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @param fromUserId 收藏人Id
     */
    public void collect(String targetType, int targetId, Integer fromUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 生成 Redis 的 key
                //实体被收藏
                String collectKey = RedisKeyUtil.getCollectKey(targetType, targetId);
                //用户查看收藏
                String collectorKey = RedisKeyUtil.getCollectorKey(fromUserId, targetType);

                // 判断用户是否已经收藏了
                boolean isMember = redisOperations.opsForSet().isMember(collectKey, fromUserId);

                // 开启事务管理
                redisOperations.multi();

                if (isMember) {
                    // 如果用户已经收藏，点第二次则取消收藏
                    redisOperations.opsForSet().remove(collectKey, fromUserId);
                    redisOperations.opsForSet().remove(collectorKey, targetId);
                }
                else {
                    redisOperations.opsForSet().add(collectKey, fromUserId);
                    redisOperations.opsForSet().add(collectorKey, targetId);
                }

                // 提交事务
                return redisOperations.exec();
            }
        });
    }

    /**
     * 获取目标收藏数量
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @return 收藏数量
     */
    public long findTargetCollectCount(String targetType, int targetId) {
        String collectKey = RedisKeyUtil.getCollectKey(targetType, targetId);
        return redisTemplate.opsForSet().size(collectKey);
    }

    /**
     * 查询某个用户对某个目标的收藏状态（是否已收藏）
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @param fromUserId 收藏的人
     * @return 收藏状态 1:已收藏，0:未收藏
     */
    public int findTargetCollectStatus(String targetType, int targetId, Integer fromUserId) {
        String collectorKey = RedisKeyUtil.getCollectKey(targetType, targetId);
        return redisTemplate.opsForSet().isMember(collectorKey, fromUserId) ? 1 : 0;
    }


    /**
     * 查询某个用户所有的收藏对象
     * @param targetType 目标类型
     * @param fromUserId 当前用户
     * @return 存放目标ID的List集合
     */
    public List<Integer> findCollector(String targetType, Integer fromUserId) {

        String collectorKey = RedisKeyUtil.getCollectorKey(fromUserId, targetType);
        //拿到了用户在当前类型下的收藏对象
        Set<Integer> targetIds = redisTemplate.opsForSet().members(collectorKey);
        //用户在当前类型下没有收藏对象
        if (targetIds == null) {
            return null;
        }
        return new ArrayList<>(targetIds);
    }

    public void deleteTargetCollect(String targetType, int targetId){
        //当前目标的点赞
        String targetLikeKey = RedisKeyUtil.getCollectKey(targetType, targetId);
        redisTemplate.delete(targetLikeKey);
    }

    /**
     * 清除用户收藏列表下已经删除的内容
     * @param targetType
     * @param targetId
     * @param fromUserId
     */
    public void deleteTargetCollector(String targetType, int targetId,Integer fromUserId){
        //当前目标的点赞
        String collectorKey = RedisKeyUtil.getCollectorKey(fromUserId, targetType);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 开启事务管理
                redisOperations.multi();
                redisOperations.opsForSet().remove(collectorKey, targetId);
                // 提交事务
                return redisOperations.exec();
            };
        });
    }
}
