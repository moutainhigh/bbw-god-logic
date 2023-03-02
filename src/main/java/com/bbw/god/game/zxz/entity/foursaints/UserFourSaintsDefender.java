package com.bbw.god.game.zxz.entity.foursaints;

import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.enums.ZxzDefenderEnum;
import com.bbw.god.game.zxz.enums.ZxzFourSaintsDefenderKindEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家诛仙阵四圣关卡
 * @author: hzf
 * @create: 2022-12-27 17:06
 **/
@Data
public class UserFourSaintsDefender {
    /** 关卡id */
    private Integer defenderId;
    /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
    private Integer status;
    /** 诛仙阵：野怪种类信息：参考 ZxzFourSaintsDefenderKindEnum 枚举类 */
    private Integer kind;

    public static UserFourSaintsDefender getInstance(CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule defenderCardRule){
        UserFourSaintsDefender saintsDefender = new UserFourSaintsDefender();
        saintsDefender.setDefenderId(defenderCardRule.getDefenderId());
        saintsDefender.setKind(defenderCardRule.getKind());
        //获取第几关
        Integer defender = CfgFourSaintsTool.defender(defenderCardRule.getDefenderId());
        //判断是不是第一关
        if (defender == ZxzDefenderEnum.DEFENDER_1.getDefenderId()) {
            saintsDefender.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        } else {
            saintsDefender.setStatus(ZxzStatusEnum.NOT_OPEN.getStatus());
        }
        return saintsDefender;

    }
    /**
     * 判断是不是圣兽野怪
     * @return
     */
    public boolean ifKingTherion(){
        return kind.equals(ZxzFourSaintsDefenderKindEnum.KIND_20.getKind());
    }

    public static List<UserFourSaintsDefender> initFourSaintsDefender(Integer challengeType) {
        List<UserFourSaintsDefender> userFourSaintsDefenders = new ArrayList<>();
        List<CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule> defenderCardRules = CfgFourSaintsTool.getDefenderCardRules(challengeType);
        for (CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule defenderCardRule : defenderCardRules) {
            UserFourSaintsDefender saintsDefender = UserFourSaintsDefender.getInstance(defenderCardRule);
            userFourSaintsDefenders.add(saintsDefender);
        }
        return userFourSaintsDefenders;
    }
}
