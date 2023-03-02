package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 女娲集市编号服务
 *
 * @author fzj
 * @date 2022/5/9 9:43
 */
@Service
public class GameNvWaMarketNumService {
    private static final String NV_WA_MARKET = "nvWaMarketNum";
    private static final String SLOT_ID = "";
    /** 摊位id，uid：过期时间 */
    @Autowired
    private RedisHashUtil<Integer, String> nvWaMarketNum;

    /**
     * 获得摊位id
     *
     * @param boothNo
     * @return
     */
    public Long getBoothId(Integer boothNo) {
        String filed = nvWaMarketNum.get(NV_WA_MARKET).get(boothNo);
        if (null == filed || SLOT_ID.equals(filed)) {
            return null;
        }
        long expiredDate = Long.parseLong(filed.split(SPLIT)[1]);
        if (isExpired(expiredDate)) {
            return null;
        }
        return Long.valueOf(filed.split(SPLIT)[2]);
    }

    /**
     * 获得玩家不过期摊位id
     *
     * @param uid
     * @return
     */
    public Long getUserUnexpiredBoothId(long uid) {
        Map<Integer, String> allUnexpiredBoothNo = getAllUnexpiredBoothNo();
        if (allUnexpiredBoothNo.isEmpty()) {
            return null;
        }
        return getUserBooth(uid, allUnexpiredBoothNo);
    }

    /**
     * 获得玩家摊位id包括过期的
     *
     * @param uid
     * @return
     */
    public Long getUserBoothId(long uid) {
        Map<Integer, String> allBoothNo = getAllBoothNo();
        if (allBoothNo.isEmpty()) {
            return null;
        }
        return getUserBooth(uid, allBoothNo);
    }

    /**
     * 获得玩家摊位
     *
     * @param uid
     * @param boothNo
     * @return
     */
    private Long getUserBooth(long uid, Map<Integer, String> boothNo) {
        for (Map.Entry<Integer, String> booth : boothNo.entrySet()) {
            String boothValue = booth.getValue();
            if (SLOT_ID.equals(boothValue)) {
                continue;
            }
            long userId = Long.parseLong(boothValue.split(SPLIT)[0]);
            if (userId != uid) {
                continue;
            }
            return Long.parseLong(boothValue.split(SPLIT)[2]);
        }
        return null;
    }

    /**
     * 获得摊位id
     *
     * @param page
     * @return
     */
    public List<Long> getBoothIds(Integer page) {
        List<Long> unexpiredBooth = getAllUnexpiredBoothId();
        if (unexpiredBooth.isEmpty()) {
            return new ArrayList<>();
        }
        int allBoothNum = unexpiredBooth.size();
        int maxNum = 4;
        int start = Math.max((page - 1), 0) * maxNum;
        int end = start + maxNum;
        int allPage = allBoothNum / maxNum;
        if (start == allPage || allPage == (page - 1)) {
            end = start + (allBoothNum % maxNum);
        }
        if (allBoothNum <= maxNum) {
            end = start + allBoothNum;
        }
        return unexpiredBooth.subList(start, end);
    }

    /**
     * 是否过期
     *
     * @param expiredDate
     * @return
     */
    private boolean isExpired(long expiredDate) {
        Date expired = DateUtil.fromDateLong(expiredDate);
        if (DateUtil.now().before(expired)) {
            return false;
        }
        return true;
    }

    /**
     * 更新摊位编号信息
     *
     * @param boothNo
     * @param uid
     * @param date
     */
    public void updateBoothNo(Integer boothNo, long uid, long date, long boothId) {
        String value = uid + SPLIT + date + SPLIT + boothId;
        nvWaMarketNum.putField(NV_WA_MARKET, boothNo, value);
    }

    /**
     * 清空摊位编号信息
     *
     * @param boothNo
     */
    public void emptyBoothNo(Integer boothNo) {
        nvWaMarketNum.putField(NV_WA_MARKET, boothNo, SLOT_ID);
    }

    /**
     * 增加摊位
     *
     * @param uid
     * @param date
     */
    public Integer addBoothNo(long uid, long date, long boothId) {
        String value = uid + SPLIT + date + SPLIT + boothId;
        Map<Integer, String> allBoothNo = getAllBoothNo();
        Integer vacancy = null;
        for (Map.Entry<Integer, String> boothNo : allBoothNo.entrySet()) {
            String numValue = boothNo.getValue();
            if (!SLOT_ID.equals(numValue)) {
                continue;
            }
            vacancy = boothNo.getKey();
            break;
        }
        if (null == vacancy) {
            nvWaMarketNum.putField(NV_WA_MARKET, allBoothNo.size() + 1, value);
            return allBoothNo.size() + 1;
        }
        updateBoothNo(vacancy, uid, date, boothId);
        return vacancy;
    }

    /**
     * 获得所有摊位标识
     *
     * @return
     */
    public Map<Integer, String> getAllBoothNo() {
        return nvWaMarketNum.get(NV_WA_MARKET);
    }

    /**
     * 不过期摊位
     *
     * @return
     */
    public List<Long> getAllUnexpiredBoothId() {
        List<Long> unexpiredBooth = new ArrayList<>();
        Map<Integer, String> allBoothNo = getAllUnexpiredBoothNo();
        for (Map.Entry<Integer, String> booth : allBoothNo.entrySet()) {
            String boothValue = booth.getValue();
            unexpiredBooth.add(Long.parseLong(boothValue.split(SPLIT)[2]));
        }
        return unexpiredBooth;
    }

    /**
     * 不过期摊位id
     *
     * @return
     */
    public List<Long> getAllExpiredBoothId() {
        List<Long> expiredBooth = new ArrayList<>();
        Map<Integer, String> allBoothNo = getAllBoothNo();
        if (allBoothNo.isEmpty()) {
            return expiredBooth;
        }
        for (Map.Entry<Integer, String> booth : allBoothNo.entrySet()) {
            String boothValue = booth.getValue();
            if (SLOT_ID.equals(boothValue)) {
                continue;
            }
            long expiredDate = Long.parseLong(boothValue.split(SPLIT)[1]);
            if (!isExpired(expiredDate)) {
                continue;
            }
            expiredBooth.add(Long.parseLong(boothValue.split(SPLIT)[2]));
        }
        return expiredBooth;
    }

    /**
     * 获得不过期摊位号
     *
     * @return
     */
    public Map<Integer, String> getAllUnexpiredBoothNo() {
        Map<Integer, String> allBoothNo = getAllBoothNo();
        if (allBoothNo.isEmpty()) {
            return new HashMap<>();
        }
        Map<Integer, String> unexpiredBooth = new HashMap<>();
        for (Map.Entry<Integer, String> booth : allBoothNo.entrySet()) {
            String boothValue = booth.getValue();
            if (SLOT_ID.equals(boothValue)) {
                continue;
            }
            long expiredDate = Long.parseLong(boothValue.split(SPLIT)[1]);
            if (isExpired(expiredDate)) {
                continue;
            }
            unexpiredBooth.put(booth.getKey(), boothValue);
        }
        return unexpiredBooth;
    }

    /**
     * 检查是否拥有摊位
     *
     * @param uid
     */
    public void checkBooth(long uid) {
        Map<Integer, String> allBoothNo = getAllBoothNo();
        if (allBoothNo.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, String> boothNo : allBoothNo.entrySet()) {
            String numValue = boothNo.getValue();
            if (SLOT_ID.equals(numValue)) {
                continue;
            }
            long boothUid = Long.parseLong(numValue.split(":")[0]);
            if (uid != boothUid) {
                continue;
            }
            throw new ExceptionForClientTip("nightmareNvWaM.booth.not.exist");
        }
    }
}
