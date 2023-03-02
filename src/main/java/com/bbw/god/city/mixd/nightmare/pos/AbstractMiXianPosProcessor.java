package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
public abstract class AbstractMiXianPosProcessor {
    @Autowired
    protected NightmareMiXianService nightmareMiXianService;
    @Autowired
    private UserCardService userCardService;
    /**
     * 匹配service
     * @param miXianPosEnum
     * @return
     */
    public abstract boolean match(NightmareMiXianPosEnum miXianPosEnum);

    /**
     * 到达位置
     * @param nightmareMiXian
     * @param rd
     * @param
     * @return
     */
    public abstract void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData);


    public List<Award> checkBoxAwards(List<Award> awards,long uid){
        List<Award> rdAwardList=new ArrayList<>();
        for (Award award : awards) {
            switch (AwardEnum.fromValue(award.getItem())) {
                case TQ:
                case YB:
                    rdAwardList.add(CloneUtil.clone(award));
                    break;
                case KP:
                    //有具体卡牌
                    if (award.getAwardId() != 0) {
                        rdAwardList.add(CloneUtil.clone(award));
                        break;
                    }
                    //没有具体卡牌
                    List<UserCard> userCards = userCardService.getUserCards(uid);
                    List<UserCard> collect = userCards.stream().filter(p -> p.gainCard().getStar().equals(award.getStar())).collect(Collectors.toList());
                    if (ListUtil.isEmpty(collect)) {
                        rdAwardList.add(Award.instance(0, AwardEnum.TQ, 10000));
                        break;
                    }
                    int index = PowerRandom.getRandomBetween(1, collect.size()) - 1;
                    rdAwardList.add(Award.instance(collect.get(index).getBaseId(), AwardEnum.KP, award.getNum()));
                    break;
                case FB:
                    //有具体法宝
                    if (award.getAwardId() != 0) {
                        rdAwardList.add(CloneUtil.clone(award));
                        break;
                    }

                    // 查询对应 星级 所有法宝
                    List<CfgTreasureEntity> allTe = TreasureTool.getTreasureConfig().getOldTreasureMapByStar().get(award.getStar());
                    // 过滤玉麒麟
                    List<CfgTreasureEntity> noYqlArr = allTe.stream().filter(treasureEnt ->
                            !treasureEnt.getName().equals(TreasureEnum.YQL.getName())).collect(Collectors.toList());
                    // 随机一个不为玉麒麟的法宝
                    CfgTreasureEntity treasureEntity = noYqlArr.get(PowerRandom.randomInt(noYqlArr.size()));
                    // 添加法宝
                    rdAwardList.add(Award.instance(treasureEntity.getId(), AwardEnum.FB, award.getNum()));
                    break;
                case YS:
                    //有具体元素
                    if (award.getAwardId() != 0) {
                        rdAwardList.add(CloneUtil.clone(award));
                        break;
                    }
                    rdAwardList.add(Award.instance(PowerRandom.getRandomBetween(1, 5) * 10, AwardEnum.YS, award.getNum()));
                    break;
                default:
            }
        }
        return rdAwardList;
    }

}