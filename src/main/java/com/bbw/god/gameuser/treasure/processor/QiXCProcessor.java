package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserShakeLogic;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.shake.EPShake;
import com.bbw.god.gameuser.shake.ShakeEvent;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.road.PathRoad;
import com.bbw.god.road.RoadPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 七香车
 *
 * @author suhq
 * @date 2018年11月29日 上午9:07:06
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QiXCProcessor extends TreasureUseProcessor {
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private GameUserShakeLogic gameUserShakeLogic;
    @Autowired
    private HexagramBuffService hexagramBuffService;

    public QiXCProcessor() {
        this.treasureEnum = TreasureEnum.QXC;
        this.isAutoBuy = false;
    }

    @Override
    public boolean isSelfToDeductTreasure(long uid) {
        return hexagramBuffService.isHexagramBuff(uid, HexagramBuffEnum.HEXAGRAM_3.getId());
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        // 体力是否足够
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        ResChecker.checkDice(gu, config.getDiceOneShake());
        int count = param.gainCount();
        // 检查骰子数是否有效
        if (this.userTreasureEffectService.isTreasureEffect(gu.getId(), TreasureEnum.QL.getValue())) {
            if (count > 18) {
                throw new ExceptionForClientTip("gu.dice.not.valid");
            }
            int num = count / 3;
            SpringContextUtil.publishEvent(new ShakeEvent(new EPShake(Arrays.asList(num, num, num), new BaseEventParam(gu.getId(), WayEnum.TREASURE_USE))));
        } else if (this.userTreasureEffectService.isTreasureEffect(gu.getId(), TreasureEnum.SBX.getValue())) {
            if (count > 12) {
                throw new ExceptionForClientTip("gu.dice.not.valid");
            }
            int num = count / 2;
            SpringContextUtil.publishEvent(new ShakeEvent(new EPShake(Arrays.asList(num, num), new BaseEventParam(gu.getId(), WayEnum.TREASURE_USE))));
        } else {
            SpringContextUtil.publishEvent(new ShakeEvent(new EPShake(Arrays.asList(count), new BaseEventParam(gu.getId(), WayEnum.TREASURE_USE))));
        }
        // 新手引导七香车的步数
//        if (!this.newerGuideService.isPassNewerGuide(gu.getId()) && count != NewerGuideEnum.XIANRENDONG.getNextStepNum()) {
//            throw new ExceptionForClientTip("request.param.not.valid");
//        }
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        int count = param.gainCount();
        List<PathRoad> roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), count);
        this.gameUserShakeLogic.getRoads(gu, roadPath, WayEnum.TREASURE_USE, rd);
//        this.gameUserLogic.getRoads(gu, count, gu.getLocation().getDirection(), WayEnum.TREASURE_USE, rd);
        // 骰子点数
        List<Integer> randoms = new ArrayList<Integer>();
        if (count > 12) {
            randoms.add(6);
            randoms.add(6);
            randoms.add(count - 12);
        } else if (count > 6) {
            randoms.add(6);
            randoms.add(count - 6);
        } else {
            randoms.add(count);
        }
        rd.setRandoms(randoms);
        if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_3.getId())) {
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId(), WayEnum.TREASURE_USE, rd), HexagramBuffEnum.HEXAGRAM_3.getId(), 1);
        }
    }

}
