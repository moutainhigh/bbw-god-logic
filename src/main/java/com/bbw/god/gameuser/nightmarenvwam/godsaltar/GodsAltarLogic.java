package com.bbw.god.gameuser.nightmarenvwam.godsaltar;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 封神祭坛
 *
 * @author fzj
 * @date 2022/5/10 14:03
 */
@Service
@Slf4j
public class GodsAltarLogic {
    @Autowired
    private UserGodsAltarService userGodsAltarService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 进入封神祭坛
     *
     * @param uid
     */
    public RDGodsAltar enterGodsAltar(long uid) {
        UserGodsAltarInfo godsAltarInfo = userGodsAltarService.getOrCreatGodsAltarInfo(uid);
        List<UserGodsAltarInfo.ExchangeProgress> progressList = godsAltarInfo.getProgressList();
        RDGodsAltar rd = new RDGodsAltar();
        List<RDGodsAltar.RDGodHeadInfo> rdGodHeadInfos = new ArrayList<>();
        List<Integer> cardIds = NightmareNvWamCfgTool.getGodHeadCardId();
        for (Integer cardId : cardIds) {
            UserGodsAltarInfo.ExchangeProgress progress = progressList.stream()
                    .filter(p -> cardId.equals(p.getCardId())).findFirst().orElse(null);
            if (null == progress) {
                RDGodsAltar.RDGodHeadInfo godHeadInfo = RDGodsAltar.getInstance(cardId);
                rdGodHeadInfos.add(godHeadInfo);
                continue;
            }
            RDGodsAltar.RDGodHeadInfo godHeadInfo = RDGodsAltar.getInstance(progress);
            rdGodHeadInfos.add(godHeadInfo);
        }
        rd.setGodHeadInfos(rdGodHeadInfos);
        return rd;
    }

    /**
     * 消耗令牌
     *
     * @param uid
     * @param treasureId
     */
    public RDCommon consumeBrand(long uid, int treasureId) {
        //判断是否属于神格牌
        NightmareNvWamCfgTool.checkGodHeadCard(treasureId);
        int num = 1;
        TreasureChecker.checkIsEnough(treasureId, num, uid);
        UserGodsAltarInfo godsAltarInfo = userGodsAltarService.getOrCreatGodsAltarInfo(uid);
        Integer cardId = NightmareNvWamCfgTool.getCardIdByTreasure(treasureId);
        UserGodsAltarInfo.ExchangeProgress progresses = godsAltarInfo.getProgressList().stream()
                .filter(g -> cardId.equals(g.getCardId())).findFirst().orElse(null);
        RDCommon rd = new RDCommon();
        //第一次加进度
        if (null == progresses) {
            UserGodsAltarInfo.ExchangeProgress exchangeProgress = UserGodsAltarInfo.getInstance(cardId);
            exchangeProgress.getProgress().add(treasureId);
            godsAltarInfo.getProgressList().add(exchangeProgress);
            gameUserService.updateItem(godsAltarInfo);
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, num, WayEnum.GODS_ALTAR_MALL, rd);
            return rd;
        }
        //检查兑换进度
        Integer limitTimes = NightmareNvWamCfgTool.getNightmareNvm().getExchangeLimitTimes();
        int times = progresses.getHasExchangeTimes();
        if (times >= limitTimes) {
            throw new ExceptionForClientTip("city.mytyf.convert.over");
        }
        //检查是否已经存在神格牌
        List<Integer> currentProgress = progresses.getProgress();
        if (currentProgress.contains(treasureId)) {
            throw new ExceptionForClientTip("exchange.not.valid");
        }
        currentProgress.add(treasureId);
        //满足兑换条件
        Integer godheadTotalPro = NightmareNvWamCfgTool.getNightmareNvm().getGodheadTotalProgress();
        if (currentProgress.size() == godheadTotalPro) {
            //加兑换次数
            godsAltarInfo.addLimitTimes(progresses);
            if (times == 0) {
                //清空兑换进度
                progresses.setProgress(new ArrayList<>());
            }
            Integer godHeadCard = NightmareNvWamCfgTool.getGodHeadCard(cardId);
            TreasureEventPublisher.pubTAddEvent(uid, godHeadCard, num, WayEnum.GODS_ALTAR_MALL, rd);
        } else {
            //更新进度
            progresses.setProgress(currentProgress);
        }
        gameUserService.updateItem(godsAltarInfo);
        TreasureEventPublisher.pubTDeductEvent(uid, treasureId, num, WayEnum.GODS_ALTAR_MALL, rd);
        return rd;
    }

}
