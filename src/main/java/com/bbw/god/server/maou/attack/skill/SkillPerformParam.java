package com.bbw.god.server.maou.attack.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import lombok.Data;

import java.util.List;

/**
 * @author suhq
 * @description: 技能执行参数
 * @date 2019-12-31 13:40
 **/
@Data
public class SkillPerformParam {
    private MaouAttackCard performCard;
    private Integer performSkill;
    private List<MaouAttackCard> attackCards;

    public static SkillPerformParam getInstance(MaouAttackCard performCard, Integer performSkill, List<MaouAttackCard> attackCards) {
        SkillPerformParam param = new SkillPerformParam();
        param.setPerformCard(performCard);
        param.setPerformSkill(performSkill);
        param.setAttackCards(attackCards);
        return param;
    }

    /**
     * 获取随机卡牌
     * @param num
     * @return
     */
    public List<MaouAttackCard> getRandomFromList(int num){
        //attackCards 不能被改变顺序 否则会导致卡牌释放异常情况，即某张卡会重复释放技能
        List<MaouAttackCard> copyList = ListUtil.copyList(attackCards, MaouAttackCard.class);
        return PowerRandom.getRandomsFromList(num,copyList);
    }

    public MaouAttackCard getRandomFromList(){
        int seed = PowerRandom.getRandomBySeed(3);
        System.err.println(seed);
        return attackCards.get(seed-1);
    }

}
