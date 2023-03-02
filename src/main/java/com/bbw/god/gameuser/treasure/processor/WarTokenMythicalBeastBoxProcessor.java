package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.treasure.*;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 战令神兽礼包
 *
 * @author fzj
 * @date 2021/12/1 17:36
 */
@Service
public class WarTokenMythicalBeastBoxProcessor extends TreasureUseProcessor {

    @Autowired
    UserTreasureRecordService userTreasureRecordService;
    @Autowired
    UserTreasureService userTreasureService;
    @Autowired
    BoxService boxService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    AwardService awardService;

    /** 保底次数 */
    private static final int MIN_GUARANTEE_NUM = 200;

    public WarTokenMythicalBeastBoxProcessor() {
        this.treasureEnum = TreasureEnum.WAR_TOKEN_MB_BOX;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {

    }

    /**
     * 保底次数
     *
     * @return
     */
    @Override
    public Integer minGuaranteeNum() {
        return MIN_GUARANTEE_NUM;
    }

    /**
     * 开启宝箱
     *
     * @param uid
     * @param num
     * @param rd
     */
    public void open(long uid, int num, RDCommon rd) {
        //获取保底记录
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.WAR_TOKEN_MB_BOX.getValue(), 0);
        Integer usedTimes = utr.getUseTimes();
        //获得神兽id集合
        List<Integer> mythicalBeastList = TreasureTool.getAllTreasures().stream().filter(t -> t.getType() == TreasureType.BEAST_FTXS.getValue() || t.getType() == TreasureType.BEAST_XJLS.getValue())
                .map(CfgTreasureEntity::getId).collect(Collectors.toList());
        //获取宝箱中神兽奖励集合
        List<Integer> awardMythicalBeasts = boxService.getBoxAllGoods(uid, TreasureEnum.WAR_TOKEN_MB_BOX.getValue())
                .stream().filter(b -> mythicalBeastList.contains(b.getAwardId())).map(Award::gainAwardId).collect(Collectors.toList());
        //获取玩家未拥有宝箱神兽的集合
        List<Integer> notOwnMythicalBeasts = getNotOwnMythicalBeast(uid, awardMythicalBeasts);
        //奖励集合
        List<Award> boxAwards = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            usedTimes++;
            if (!notOwnMythicalBeasts.isEmpty() && usedTimes == MIN_GUARANTEE_NUM) {
                Integer randomMythicalBeasts = PowerRandom.getRandomFromList(notOwnMythicalBeasts);
                boxAwards.add(Award.instance(randomMythicalBeasts, AwardEnum.FB, 1));
                //获得神兽,保底记录清零
                usedTimes = 0;
                notOwnMythicalBeasts.removeIf(m -> m.equals(randomMythicalBeasts));
                continue;
            }
            //如果已拥有宝箱里面的全部神兽，不增加保底次数
            if (notOwnMythicalBeasts.isEmpty()) {
                usedTimes = 0;
            }
            //获得宝箱奖励
            Award award = boxService.getAward(uid, TreasureEnum.WAR_TOKEN_MB_BOX.getValue()).stream().findFirst().orElse(null);
            if (null == award) {
                continue;
            }
            //奖励是否为神兽
            boolean isMythicalBeasts = awardMythicalBeasts.contains(award.gainAwardId());
            //如果奖励为神兽且玩家没有该神兽
            if (isMythicalBeasts && notOwnMythicalBeasts.contains(award.gainAwardId())) {
                boxAwards.add(award);
                //获得神兽,保底记录清零
                notOwnMythicalBeasts.removeIf(m -> m.equals(award.getAwardId()));
                usedTimes = 0;
                continue;
            }
            //如果奖励为神兽且玩家拥有该神兽
            if (isMythicalBeasts) {
                boxAwards.add(Award.instance(TreasureEnum.XZY.getValue(), AwardEnum.FB, 1));
                continue;
            }
            boxAwards.add(award);
        }
        awardService.sendNeedMergedAwards(uid, boxAwards, WayEnum.WAR_TOKEN_MB_BOX, "", rd);
        //添加礼包使用次数
        rd.setGiftUseTimes(usedTimes);
        utr.setUseTimes(usedTimes);
        gameUserService.updateItem(utr);
    }

    /**
     * 获得宝箱卡牌中未拥有的神兽集合
     *
     * @param uid
     * @param awardMythicalBeasts
     * @return
     */
    private List<Integer> getNotOwnMythicalBeast(long uid, List<Integer> awardMythicalBeasts) {
        List<Integer> ownMythicalBeasts = userTreasureService.getAllUserTreasures(uid).stream()
                .map(UserCfgObj::getBaseId).collect(Collectors.toList());
        return awardMythicalBeasts.stream().filter(a -> !ownMythicalBeasts.contains(a)).collect(Collectors.toList());
    }
}
