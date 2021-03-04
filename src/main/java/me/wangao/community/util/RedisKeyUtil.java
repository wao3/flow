package me.wangao.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_CAPTCHA = "captcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";
    private static final String PREFIX_COUNTER = "counter";

    private static final String COUNTER_KEY_USER = "user";
    private static final String COUNTER_KEY_POST = "post";
    private static final String COUNTER_KEY_COMMENT = "comment";
    private static final String COUNTER_KEY_NODE_TOPIC = COUNTER_KEY_POST + SPLIT + "node";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set<userId>
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某用户获得的赞
    // like:user:UserId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset<entityId, datetime>
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset<userId, datetime>
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录验证码
    public static String getCaptchaKey(String owner) {
        return PREFIX_CAPTCHA + SPLIT + owner;
    }

    // 登录凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // 单日UV
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间uv
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 统计帖子分数
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

    // 用户计数器
    public static String getUserCounterKey() {
        return PREFIX_COUNTER + SPLIT + COUNTER_KEY_USER;
    }

    // 话题计数器
    public static String getPostCounterKey() {
        return PREFIX_COUNTER + SPLIT + COUNTER_KEY_POST;
    }

    // 回复计数器
    public static String getCommentCounterKey() {
        return PREFIX_COUNTER + SPLIT + COUNTER_KEY_COMMENT;
    }

    // 节点话题计数器
    public static String getNodePostCounterKey(int nodeId) {
        return PREFIX_COUNTER + SPLIT + COUNTER_KEY_NODE_TOPIC + SPLIT + nodeId;
    }
}
