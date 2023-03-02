package com.bbw.god.game.award.giveback;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认返还池实现
 *
 * @author: suhq
 * @date: 2022/5/26 3:17 下午
 */
@Slf4j
@Service
public class GiveBackPool extends AbstractGiveBackPool {

    @Autowired
    protected RedisHashUtil<String, GiveBackAwards> giveBackPoolRedisUtil;
    @Autowired
    private GameUserService gameUserService;

    @Override
    public String getPoolType() {
        return "default";
    }

    @Override
    protected void doGiveBack() {
        log.info("--奖励返还开始(" + getPoolType() + ")-------------------------------------------------");
        String lastPoolKey = getLastPoolKey();
        Map<String, GiveBackAwards> poolValues = giveBackPoolRedisUtil.get(lastPoolKey);
        if (null == poolValues) {
            return;
        }
        if (poolValues.size() > 3000) {
            log.error("[" + this.getPoolType() + "]待返还奖励超过3000条");
        }
        Long begin = System.currentTimeMillis();
        log.info("==================将待发放奖励保存到日志");
        StringBuilder dataToLog = new StringBuilder();
        for (GiveBackAwards value : poolValues.values()) {
            dataToLog.append(value.toString());
            dataToLog.append("\n");
        }
        log.info(dataToLog.toString());
        log.info("==================待发放奖励保存到日志结束,耗时：{}", System.currentTimeMillis() - begin);
        try {
            begin = System.currentTimeMillis();
            List<UserMail> giveBackMails = poolValues.values().stream()
                    .map((tmp) -> UserMail.newAwardMail(tmp.getTitle(), tmp.getContent(), tmp.getUid(), tmp.getAwards()))
                    .collect(Collectors.toList());
            log.info("==================待发放奖励邮件构建完成。耗时：{}", System.currentTimeMillis() - begin);
            begin = System.currentTimeMillis();
            //先删除池
            giveBackPoolRedisUtil.delete(lastPoolKey);
            log.info("==================删除待发放奖励池{}", lastPoolKey);
            //再发放邮件
            log.info("==================开始发放返还奖励邮件");
            gameUserService.addItems(giveBackMails);
            Long end = System.currentTimeMillis();
            long usedTime = end - begin;
            log.info("[{}]返还奖励邮件发放完成！成功[{}]条，失败[0]。耗时：{}", getPoolType(), poolValues.size(), usedTime);
            log.info("--奖励返还结束(" + getPoolType() + ")-------------------------------------------------");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 将待返还的奖励加入池内
     *
     * @param poolValues
     */
    public void toGiveBackPool(List<GiveBackAwards> poolValues) {
        Map<String, GiveBackAwards> valuesToRedis = toMap(poolValues);
        giveBackPoolRedisUtil.putAllField(getKey(), valuesToRedis);
    }

    /**
     * 将待返还的奖励加入池内
     *
     * @param poolValue
     */
    public void toGiveBackPool(GiveBackAwards poolValue) {
        giveBackPoolRedisUtil.putField(getKey(), poolValue.gainFieldKey(), poolValue);
    }

    /**
     * 将返还奖励的集合转换成待持久化到Redis的map
     *
     * @param values
     * @return
     */
    private Map<String, GiveBackAwards> toMap(List<GiveBackAwards> values) {
        Map<String, GiveBackAwards> valuesToRedis = new HashMap<>();
        for (GiveBackAwards value : values) {
            if (null == value) {
                continue;
            }
            String field = value.gainFieldKey();
            valuesToRedis.put(field, value);
        }
        return valuesToRedis;
    }
}
