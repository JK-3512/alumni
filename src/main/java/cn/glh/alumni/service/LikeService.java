package cn.glh.alumni.service;

/**
 * @Author: Administrator
 * @Date: 2022/2/15 12:50
 * Description 点赞接口
 */
public interface LikeService {

    /**
     * 点赞
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param fromUserId 点赞的人
     */
    void like(String targetType, int targetId, Integer fromUserId);

    /**
     * 获取点赞数量
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 点赞数量
     */
    long findTargetLikeCount(String targetType, int targetId);

    /**
     * 获取点赞状态
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param fromUserId 点赞的人
     * @return 点赞状态 1:已赞，0:未赞
     */
    int findTargetLikeStatus(String targetType, int targetId, Integer fromUserId);

    /**
     * 获取用户点赞的所有目标
     */
}
