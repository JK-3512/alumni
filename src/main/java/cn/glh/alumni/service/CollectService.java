package cn.glh.alumni.service;

import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2022/2/17 9:58
 * Description 收藏接口
 * userID: (targetType, targetId)
 *         (targetType, targetId)
 */
public interface CollectService {

    /**
     * 收藏
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param fromUserId 收藏的人
     */
    void collect(String targetType, int targetId, Integer fromUserId);

    /**
     * 获取收藏数量
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏数量
     */
    long findTargetCollectCount(String targetType, int targetId);

    /**
     * 获取收藏状态
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param fromUserId 收藏的人
     */
    int findTargetCollectStatus(String targetType, int targetId, Integer fromUserId);

    /**
     * 查询某个用户所有的收藏对象
     * @param targetType 目标类型
     * @param fromUserId 当前用户
     * @return 存放目标ID的List集合
     */
    List<Integer> findCollector(String targetType, Integer fromUserId);
}
