package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 双倍经验丹
 *
 * @author fzj
 * @date 2021/12/3 11:17
 */
@Service
public class DoubleExperienceMedicineProcessor extends TreasureUseProcessor {
    @Autowired
    UserTreasureEffectService userTreasureEffectService;
    @Autowired
    GameUserService gameUserService;

    public DoubleExperienceMedicineProcessor() {
        this.treasureEnum = TreasureEnum.DOUBLE_EXPERIENCE_MEDICINE;
        this.isAutoBuy = true;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        UserTreasureEffect utEffect = userTreasureEffectService.getEffect(gu.getId(), TreasureEnum.DOUBLE_EXPERIENCE_MEDICINE.getValue());
        //默认生效秒数
        int effectSeconds = 3600;
        if (utEffect == null) {
            utEffect = UserTreasureEffect.instance(gu.getId(), TreasureEnum.DOUBLE_EXPERIENCE_MEDICINE.getValue(), effectSeconds);
            rd.setDoubleExpRemainTime(DateUtil.now());
            userTreasureEffectService.addTreasureEffect(utEffect);
            return;
        }
        //最近失效时间
        Date superimposeDate = DateUtil.addSeconds(utEffect.getEffectTime(), effectSeconds);
        //判断上次使用双倍经验丹是否过期
        if (DateUtil.now().after(superimposeDate)) {
            utEffect.setRemainEffect(effectSeconds);
        } else {
            //没有过期则叠加
            //计算已经使用的秒数
            int usedSecond = Math.toIntExact(DateUtil.getSecondsBetween(utEffect.getEffectTime(), DateUtil.now()));
            //更新剩余时间
            int secondsRemaining = utEffect.getRemainEffect() + effectSeconds - usedSecond;
            utEffect.setRemainEffect(secondsRemaining);
        }
        rd.setDoubleExpRemainTime(DateUtil.now());
        utEffect.setEffectTime(DateUtil.now());
        gameUserService.updateItem(utEffect);
    }
}
