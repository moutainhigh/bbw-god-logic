package com.bbw.god.game.maou.event;

import com.bbw.god.activity.IActivity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.maou.GameMaouActivityService;
import com.bbw.god.game.maou.GameMaouInfoService;
import com.bbw.god.game.maou.GameMaouJoinerService;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.game.maou.processor.GameMaouProcessorFactory;
import com.bbw.god.gameuser.level.EPGuLevelUp;
import com.bbw.god.gameuser.level.GuLevelUpEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 跨服魔王时间监听器
 *
 * @author: suhq
 * @date: 2021/12/17 4:20 下午
 */
@Slf4j
@Component
public class GameMaouListener {

    @Autowired
    private GameMaouInfoService gameMaouInfoService;

    @Autowired
    private GameMaouProcessorFactory gameMaouProcessorFactory;
    @Autowired
    private GameMaouActivityService gameMaouActivityService;
    @Autowired
    private GameMaouJoinerService gameMaouJoinerService;

    /**
     * 击杀魔王发放奖励
     *
     * @param event
     */
    @Async
    @EventListener
    public void killMaou(GameMaouKilledEvent event) {
        EPGameMaouKilled ep = event.getEP();
        IActivity activity = ep.getActivity();
        gameMaouProcessorFactory.getMaouProcessor(activity).sendKilledAward(ep.getGuId(), activity, ep.getTurn());
    }

    /**
     * 监听等级变化四件，用于更新是否可以获得魔王击杀奖励
     *
     * @param event
     */
    @Async
    @EventListener
    public void levelUp(GuLevelUpEvent event) {
        EPGuLevelUp ep = event.getEP();

        IActivity maouActivity = gameMaouActivityService.getMaouActivity(ep.getGuId());
        if (null == maouActivity || gameMaouInfoService.getRemainTimeToBegin(maouActivity) > 0) {
            return;
        }
        GameMaouType gameMaouType = GameMaouType.fromActivity(maouActivity.gainType());
        Integer killAwardsMinLevel = GameMaouTool.getKillAwardsNeedLevel(gameMaouType);
        Integer oldLevel = ep.getOldLevel();
        Integer newLevel = ep.getNewLevel();
        if (oldLevel >= killAwardsMinLevel || newLevel < killAwardsMinLevel) {
            return;
        }
        Integer curTurn = gameMaouInfoService.getCurTurn(maouActivity);
        gameMaouJoinerService.updateJoinerStatus(ep.getGuId(), maouActivity, curTurn, AwardStatus.ENABLE_AWARD);
    }


}
