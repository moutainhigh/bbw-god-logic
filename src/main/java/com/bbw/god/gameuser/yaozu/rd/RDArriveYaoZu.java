package com.bbw.god.gameuser.yaozu.rd;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 触发妖族返回客户端的信息
 *
 * @author fzj
 * @date 2021/9/14 14:13
 */
@Slf4j
@Data
public class RDArriveYaoZu extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -2194371713246181712L;
    /** 妖族buff集合 */
    private  List<Integer> buffs = Arrays.asList(RunesEnum.YGZQ.getRunesId(), RunesEnum.FWFS.getRunesId(), RunesEnum.TDLHI.getRunesId(), RunesEnum.YZXM.getRunesId());
    /** 妖族id */
    private Integer yaoZuId = null;
    /** 属性 10-金 20木 30水 40火 50土 */
    private Integer type = null;
    /** 护符id */
    private List<Integer> runes = null;
    /** 妖族成就信息 */
    private List<RDArriveYaoZu.AchievementInfos> achievementInfos = null;
    /** 是否遇到妖族 0未遇见 1遇见*/
    private Integer meetYaoZu = 0;

    public RDArriveYaoZu() {
        super();
    }

    @Data
    public static class AchievementInfos implements Serializable {
        /** 成就id */
        private Integer id;
        /** 成就进度 */
        private Integer process;

        public static RDArriveYaoZu.AchievementInfos instance(int id, int process) {
            RDArriveYaoZu.AchievementInfos info = new RDArriveYaoZu.AchievementInfos();
            info.setId(id);
            info.setProcess(process);
            return info;
        }
    }
}
