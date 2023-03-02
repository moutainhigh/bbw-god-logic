package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.event.EPSmelt;
import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：
 *  熔炉
 * @author lwb
 * date 2021-05-28
 */
@Service
public class FurnaceProcessor extends AbstractMiXianPosProcessor {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;
    //妖狐尾巴、孔雀羽毛、猿猴獠牙、牛魔尖角
    private static final List<Integer> treasures = Arrays.asList(11620, 11630, 11640, 11650);

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.FURNACE.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
    }

    /**
     * 熔炼:妖狐尾巴、孔雀羽毛、猿猴獠牙、牛魔尖角
     * 点击熔炼时将会按照可熔炼的最大次数进行熔炼。
     *
     * @return
     */
    public RDNightmareMxd smelt(long uid) {
        for (Integer treasure : treasures) {
            TreasureChecker.checkIsEnough(treasure, 1, uid);
        }
        RDNightmareMxd rd = new RDNightmareMxd();
        for (int id : treasures) {
            TreasureEventPublisher.pubTDeductEvent(uid, id, 1, WayEnum.MXD_FURNACE, rd);
        }
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.RONG_LU.getValue(),0);
        List<Award> gainAwards = new ArrayList<>();
        int guaranteeProgress = utr.gainGuaranteProgress(TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getName());
        if (guaranteeProgress >= 129) {
            gainAwards.add(Award.instance(TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getValue(), AwardEnum.FB, 1));
        } else {
            gainAwards = getRandomAwards(uid);
            guaranteeProgress++;
        }

        if (ListUtil.isNotEmpty(gainAwards)) {
            awardService.fetchAward(uid, gainAwards, WayEnum.MXD_FURNACE, WayEnum.MXD_FURNACE.getName(), rd);
            MiXDEventPublisher.pubSmeltEvent(EPSmelt.instance(new BaseEventParam(uid), true));
            for (Award gainAward : gainAwards) {
                if (TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getValue() == gainAward.getAwardId()) {
                    guaranteeProgress = 0;
                    break;
                }
            }
        } else {
            MiXDEventPublisher.pubSmeltEvent(EPSmelt.instance(new BaseEventParam(uid), false));
            rd.setSmeltMsg("熔炼失败！");
        }
        utr.setGuaranteProgress(TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getName(), guaranteeProgress);
        gameUserService.updateItem(utr);
        return rd;
    }

    /**
     * 获取随机奖励
     *
     * @param uid
     * @return
     */
    private List<Award> getRandomAwards(long uid) {
        CfgNightmareMiXian cfg = NightmareMiXianTool.getCfg();
        List<CfgNightmareMiXian.AwardInfo> smeltAwards = cfg.getSmeltAwards();
        int sumProbability = smeltAwards.stream().collect(Collectors.summingInt(CfgNightmareMiXian.AwardInfo::getProbability)).intValue();
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<Integer> userCardIds = userCards.stream().map(UserCard::getBaseId).collect(Collectors.toList());
        List<Integer> gainCardIds = new ArrayList<>();
        //道具扣除完毕，开始随机奖励
        List<Award> gainAwards = new ArrayList<>();
        int seed = PowerRandom.getRandomBySeed(sumProbability);
        int sum = 0;
        List<Award> awards = new ArrayList<>();
        for (CfgNightmareMiXian.AwardInfo smeltAward : smeltAwards) {
            sum += smeltAward.getProbability();
            if (sum >= seed) {
                awards = smeltAward.getAwards();
                break;
            }
        }
        for (Award award : awards) {
            if (award.getItem() == AwardEnum.KP.getValue()) {
                int cardId = award.getAwardId();
                if (gainCardIds.contains(cardId) || userCardIds.contains(cardId) || userCardIds.contains(CardTool.getDeifyCardId(cardId))) {
                    //卡牌不能重复获得
                    continue;
                }
                gainCardIds.add(award.getAwardId());
            }
            gainAwards.add(award);
        }
        return awards;
    }
}
