package com.bbw.god.game.zxz.entity;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵符图
 * @author: hzf
 * @create: 2022-09-24 09:31
 **/
@Data
public class ZxzFuTu implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 符图Id */
    private Integer fuTuId;
    /** 符图等级 */
    private Integer fuTuLv;
    /** 位置 */
    private Integer pos;

    @Override
    public String toString() {
        return fuTuId + ZxzConstant.SPLIT_CHAR + fuTuLv + ZxzConstant.SPLIT_CHAR + pos ;
    }

    /**
     * 获取符图加成
     * @param runes
     * @return
     */
    public List<CombatBuff> gainFuTuCombatBuff(List<ZxzFuTu> runes){
        List<CombatBuff> combatBuffs = new ArrayList<>();
        if (ListUtil.isEmpty(runes)) {
            return combatBuffs;
        }
        for (ZxzFuTu fuTu : runes) {
            CombatBuff fuTuCombatBuff = new CombatBuff();
            fuTuCombatBuff.setRuneId(fuTu.getFuTuId());
            fuTuCombatBuff.setLevel(fuTu.getFuTuLv());
            combatBuffs.add(fuTuCombatBuff);
        }
        return combatBuffs;
    }

    /**
     * 构建诛仙阵
     * @param fuTuPos
     * @param userFuTu
     * @return
     */
    public String instance(Integer fuTuPos, UserFuTu userFuTu){
        ZxzFuTu fuTu = new ZxzFuTu();
        fuTu.setPos(fuTuPos);
        fuTu.setFuTuId(userFuTu.getBaseId());
        fuTu.setFuTuLv(userFuTu.getLv());
        return fuTu.toString();
    }
}
