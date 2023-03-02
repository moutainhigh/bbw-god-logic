package com.bbw.god.server.maou.bossmaou.attackinfo;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import lombok.Data;

import java.util.Date;

/**
 * 打魔王记录信息（一条记录代表一次攻击）
 *
 * @author suhq
 * @date 2019年1月8日 上午10:17:15
 */
@Data
public class BossMaouAttackDetail {
    private Long id;
    private Long maouId;//魔王ID
    private Long guId;// 玩家ID
    private Integer maouRound;//魔王回合
    private Integer beatedBlood;// 本次打掉的血量
    private Integer attackTimes = 1;// 本次攻击次数 1/100
    private Date attackTime;// 本次攻击时间190201120500
    private int attackType = 0;//使用元宝数攻击

    public static BossMaouAttackDetail instance(Long guId, Long mwId, int maouRound, int beatedBlood, int attackTimes, int attackType) {
        BossMaouAttackDetail attackDetail = new BossMaouAttackDetail();
        attackDetail.setId(ID.INSTANCE.nextId());
        attackDetail.setMaouId(mwId);
        attackDetail.setGuId(guId);
        attackDetail.setMaouRound(maouRound);
        attackDetail.setBeatedBlood(beatedBlood);
        attackDetail.setAttackTimes(attackTimes);
        attackDetail.setAttackTime(DateUtil.now());
        attackDetail.setAttackType(attackType);
        return attackDetail;
    }
}
