package com.bbw.god.gameuser.treasure;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTimeLimitTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.sxdh.SxdhDateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserTimeLimitTreasureService {
    /** 神仙大会头像 */
    private final static List<Integer> TIME_LIMIT_SXDH_HEAD_ICON = Arrays.asList(31070, 31071, 31072, 31073, 31074, 31075, 31076);
    /** 金豆和仙豆 */
    private final static List<Integer> GOLDEN_BEANS_AND_FAIRY_BEANS = Arrays.asList(TreasureEnum.XIAN_DOU.getValue(), TreasureEnum.GOLD_BEAN.getValue());
    @Autowired
    private SxdhDateService sxdhDateService;
    @Autowired
    private DfdjDateService dfdjDateService;

    /**
     * 是否限时法宝
     *
     * @param treasureId
     * @param way
     * @return
     */
    public boolean isTimeLimit(int treasureId, WayEnum way) {
        //是否是过期道具
        List<Integer> timeLimitTreasureList = getCfgTimeLimitTreasure().stream()
                .map(CfgTimeLimitTreasureEntity::getTreasureId).collect(Collectors.toList());
        if (timeLimitTreasureList.contains(treasureId)) {
            return true;
        }
        // 仙豆和金豆
        if (GOLDEN_BEANS_AND_FAIRY_BEANS.contains(treasureId)) {
            return true;
        }
        // 神仙大会头像框
        if (TIME_LIMIT_SXDH_HEAD_ICON.contains(treasureId)) {
            return true;
        }
        return false;
    }

    /**
     * 获得过期时间
     *
     * @param uid
     * @param treasureId
     * @return 如果永久，返回null
     */
    public Date getExpireDate(long uid, int treasureId) {
        //获取过期时间
        CfgTimeLimitTreasureEntity limitTreasure = getCfgTimeLimitTreasure().stream()
                .filter(t -> t.getTreasureId() == treasureId).findFirst().orElse(null);
        if (null != limitTreasure) {
            return DateUtil.fromDateTimeString(limitTreasure.getTimeLimit());
        }
        // 仙豆
        if (treasureId == TreasureEnum.XIAN_DOU.getValue()) {
            return sxdhDateService.getBeanExpireDate();
        }
        // 金豆
        if (treasureId == TreasureEnum.GOLD_BEAN.getValue()) {
            return dfdjDateService.getBeanExpireDate();
        }
        // 神仙大会头像
        if (TIME_LIMIT_SXDH_HEAD_ICON.contains(treasureId)) {
            return sxdhDateService.getHeadIconExpireDate(uid);
        }
        return null;
    }

    /**
     * 获取配置类
     *
     * @return
     */
    public List<CfgTimeLimitTreasureEntity> getCfgTimeLimitTreasure() {
        return Cfg.I.get(CfgTimeLimitTreasureEntity.class);
    }
}
