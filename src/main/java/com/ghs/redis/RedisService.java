package com.ghs.redis;

/**
 * @Author WenS
 * @Date 2022/5/112/2 23:58
 * @Version 1.0
 * @Describe
 */
public interface RedisService {

    /**
     * 判断key是否存在
     */
    Boolean hasKey(String key);
}
