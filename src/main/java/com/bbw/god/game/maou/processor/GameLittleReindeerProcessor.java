package com.bbw.god.game.maou.processor;

import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.maou.Enum.ReindeerFoodTypeEnum;
import com.bbw.god.game.maou.cfg.CfgGameMaou;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.game.maou.ctrl.CPGameMaouAttack;
import com.bbw.god.game.maou.ctrl.RDGameMaouAttack;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 小小驯鹿业务处理类
 *
 * @author: huanghb
 * @date: 2022/3/26 16:54
 */
@Slf4j
@Service
public class GameLittleReindeerProcessor extends AbstractGameMaouProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private InsRoleInfoDao insRoleInfoDao;
    @Autowired
    private App app;

    /**
     * 获取本次攻击（投喂）的血量（亲密度）
     *
     * @param uid
     * @param attackParam
     * @param activity
     * @return
     */
    @Override
    public int getBeatedBlood(long uid, CPGameMaouAttack attackParam, IActivity activity) {
        //参数检测
        ReindeerFoodTypeEnum foodType = ReindeerFoodTypeEnum.fromTreasureId(attackParam.getTreasureId());
        if (null == foodType) {
            throw new ExceptionForClientTip("maou.reindeer.food.not.exist");
        }
        //检查法宝是否充足
        TreasureChecker.checkIsEnough(attackParam.getTreasureId(), 1, uid);
        // 打掉血量
        return foodType.getHurtValue();
    }

    /**
     * 卡牌编组
     *
     * @param uid
     * @param maouCards
     * @param activity
     * @return
     */
    @Override
    public RDSuccess setMaouCards(long uid, String maouCards, IActivity activity) {
        return new RDSuccess();
    }

    /**
     * 发送击杀（投喂）奖励
     *
     * @param killer
     * @param activity
     * @param turn
     */
    @Override
    public void sendKilledAward(long killer, IActivity activity, int turn) {
        //获得击杀奖励
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        List<Award> killAwards = GameMaouTool.getKillAwards(gameMaouType, turn);
        //添加奖励邮件
        List<UserMail> userMails = new ArrayList<>();
        String title = LM.I.getMsgByUid(killer, "activity.game.reindeer.title");
        String content = LM.I.getMsgByUid(killer, "activity.game.reindeer.content");
        List<Long> uidsToSend = getUidsToSend();
        for (Long uid : uidsToSend) {
            UserMail userMail = UserMail.newAwardMail(title, content, uid, killAwards);
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }

    /**
     * 攻打（投喂）
     *
     * @param uid
     * @param attackParam
     * @param activity
     * @return
     */
    @Override
    public RDGameMaouAttack attack(long uid, CPGameMaouAttack attackParam, IActivity activity) {
        RDGameMaouAttack rd = super.attack(uid, attackParam, activity);
        TreasureEventPublisher.pubTDeductEvent(uid, attackParam.getTreasureId(), 1, WayEnum.GUAN_ZU, rd);
        return rd;
    }

    /**
     * 获取需要发放奖励的玩家
     *
     * @return
     */
    private List<Long> getUidsToSend() {
        //参与者奖励需要等级
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(GameMaouType.GUAN_ZU);
        Integer needLevel = maouConfig.getKillAwardsNeedLevel();
        //3天之内
        Date endDate = DateUtil.addDays(DateUtil.now(), -3);
        //需要发放奖励的玩家
        return insRoleInfoDao.getUidByLevelAndLastLoginDate(needLevel, DateUtil.toDateInt(endDate));
    }


    @Override
    public boolean isMatch(GameMaouType gameMaouType) {
        return GameMaouType.LITTLE_REINDEER == gameMaouType;
    }
}
