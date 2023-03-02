package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 派遣模式处理类
 *
 * @author: suhq
 * @date: 2022/12/9 11:42 上午
 */
@Service
public class UserDispatchModelProcessor10 extends AbstractDispatchModeProcessor {

    @Autowired
    private UserCardService userCardService;
    @Autowired
    private PrivilegeService privilegeService;

    @Override
    public boolean isMatch(DispatchModeEnum dispatchMode) {
        return DispatchModeEnum.SUCCESS_RATE_MODE.equals(dispatchMode);
    }

    @Override
    public int getSuccessRate(UserTimeLimitTask dispatchTask) {
        long uid = dispatchTask.getGameUserId();
        //未派遣
        if (ListUtil.isEmpty(dispatchTask.getDispatchCards())) {
            if (privilegeService.isOwnTianLing(uid)) {
                //拥有天灵印，初始概率为10
                return 10;
            }
            return 0;
        }
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(dispatchTask.getGroup());
        int successRate = 0;
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroup);
        CfgDispatchTaskRules dispatchRule = TimeLimitTaskTool.getDispatchRule(taskGroup, dispatchTask.getBaseId());
        boolean isMatchStar = false;
        List<CfgCardEntity> cards = CardTool.getCards(dispatchTask.getDispatchCards());

        for (CfgCardEntity card : cards) {
            //获得卡牌星级
            int star = card.getStar();
            //获得4.5星封神卡牌
            List<Integer> fourStarHalfDeifyCards = CardTool.getFourStarHalfDeifyCards();
            if (fourStarHalfDeifyCards.contains(card.getId())) {
                star++;
            }
            if (star == dispatchRule.getNeedStar()) {
                isMatchStar = true;
            }
            //卡牌星级成功率
            successRate += rules.getStarSuccessRate().get(card.getStar() - 1);
        }
        //星级条件成功率
        if (isMatchStar) {
            successRate += rules.getConditionRate();
        }
        List<UserCard> ucs = userCardService.getUserCards(dispatchTask.getGameUserId(), dispatchTask.getDispatchCards());
        List<Integer> skills = new ArrayList<>();
        for (UserCard uc : ucs) {
            skills.addAll(uc.gainActivedSkills());
            //卡牌星级成功率
            successRate += uc.getLevel() / rules.getCardLevelSpacing() * rules.getCardLevelRate();
        }
        //技能条件成功率
        List<Integer> extraSkills = dispatchTask.getExtraSkills();
        for (Integer extraSkill : extraSkills) {
            if (skills.contains(extraSkill)) {
                successRate += rules.getConditionRate();
            }
        }
        //如果拥有天灵印，加百分之十的成功率
        if (privilegeService.isOwnTianLing(uid)) {
            successRate += 10;
        }
        return successRate;
    }

    /**
     * 获取派遣时间
     *
     * @param dispatchTask
     * @return
     */
    @Override
    public Date getDispatchDate(UserTimeLimitTask dispatchTask) {
        int dispatchTime = getDispatchMinute(dispatchTask);
        Date now = DateUtil.now();
        return DateUtil.addMinutes(now, dispatchTime);
    }

    /**
     * 获得派遣分钟数
     *
     * @param dispatchTask
     * @return
     */
    @Override
    public int getDispatchMinute(UserTimeLimitTask dispatchTask) {
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(dispatchTask.getGroup());
        int dispatchTime = TimeLimitTaskTool.getDispatchTime(taskGroup, dispatchTask.getBaseId());
        return dispatchTime;
    }
}
