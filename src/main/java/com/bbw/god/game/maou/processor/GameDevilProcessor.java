package com.bbw.god.game.maou.processor;

import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.maou.GameMaouAttacker;
import com.bbw.god.game.maou.GameMaouAttackerService;
import com.bbw.god.game.maou.GameMaouJoinerService;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.game.maou.ctrl.CPGameMaouAttack;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.server.maou.attack.MaouAttackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抵抗恶魔业务处理类
 *
 * @author: suhq
 * @date: 2022/1/7 2:34 下午
 */
@Service
public class GameDevilProcessor extends AbstractGameMaouProcessor {
    @Autowired
    private GameMaouAttackerService gameMaouAttackerService;
    @Autowired
    private MaouAttackService maouAttackService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GameMaouJoinerService gameMaouJoinerService;
    @Autowired
    private GameUserService gameUserService;

    @Override
    public int getBeatedBlood(long uid, CPGameMaouAttack attackParam, IActivity activity) {
        //攻击者信息
        GameMaouAttacker attacker = gameMaouAttackerService.getOrCreateAttacker(uid, activity);
        if (ListUtil.isEmpty(attacker.getCards())) {
            throw ExceptionForClientTip.fromi18nKey("maou.has.no.card.group");
        }

        // 打掉魔王血量
        List<Integer> attackCardIds = attacker.getCards();
        List<UserCard> attackCards = userCardService.getUserCards(uid, attackCardIds);
        int beatedBlood = this.maouAttackService.getBeatedBlood(attackCards, TypeEnum.Null.getValue());
        return beatedBlood;
    }

    @Override
    public void sendKilledAward(long killer, IActivity activity, int turn) {
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        List<Award> killAwards = GameMaouTool.getKillAwards(gameMaouType, turn);
        List<UserMail> userMails = new ArrayList<>();
        String title = LM.I.getMsgByUid(killer, "activity.game.maou.title");
        String content = LM.I.getMsgByUid(killer, "activity.game.maou.content");
        List<Long> uidsToSend = getUidsToSend(activity, turn);
        for (Long uid : uidsToSend) {
            UserMail userMail = UserMail.newAwardMail(title, content, uid, killAwards);
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }

    /**
     * 获取需要发放奖励的玩家
     *
     * @param activity
     * @param turn
     * @return
     */
    private List<Long> getUidsToSend(IActivity activity, int turn) {
        List<Long> uidsToSend = new ArrayList<>();
        Map<Long, Integer> allJoiners = gameMaouJoinerService.getAllJoiners(activity, turn);
        for (Map.Entry<Long, Integer> entry : allJoiners.entrySet()) {
            int status = entry.getValue();
            if (status == AwardStatus.UNAWARD.getValue()) {
                continue;
            }
            uidsToSend.add(entry.getKey());
        }
        return uidsToSend;
    }


    @Override
    public boolean isMatch(GameMaouType gameMaouType) {
        return GameMaouType.DEVIL == gameMaouType;
    }
}
