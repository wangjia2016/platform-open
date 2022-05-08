package com.platform.mall.service.basic.impl;

import com.platform.common.enums.ThreadPoolNameEnum;
import com.platform.common.exception.BusinessException;
import com.platform.mall.service.basic.BasicMallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description
 * @ClassName BasicMallServiceImpl
 * @Author wangjia
 * @date 2022.05.08 11:55
 */
@Service("basicMallService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class BasicMallServiceImpl implements BasicMallService {

    private final RedisTemplate redisTemplate;

    @Override
    public Boolean updateThreadPoolConfig(Integer corePoolSize, Integer maximumPoolSize, ThreadPoolNameEnum poolName) {
        final Object o = redisTemplate.opsForValue().get(poolName);
        if (ObjectUtils.isEmpty(o)) {
            log.error("线程池为空");
            throw new BusinessException("线程池为空");
        }
        try {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) o;

            if (!ObjectUtils.isEmpty(corePoolSize)) {
                pool.setCorePoolSize(corePoolSize);
            }
            if (!ObjectUtils.isEmpty(maximumPoolSize)) {
                pool.setMaximumPoolSize(maximumPoolSize);
            }
            log.info("核心线程大小:{},最大线程大小:{}",pool.getCorePoolSize(),pool.getMaximumPoolSize());
        }catch (Exception e){
            log.error("线程池更新异常,{}",e.getMessage());
            throw new BusinessException("线程池更新异常");
        }
        return true;
    }
}
