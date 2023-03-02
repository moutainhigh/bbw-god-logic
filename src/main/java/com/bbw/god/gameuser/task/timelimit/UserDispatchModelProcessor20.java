package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
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
public class UserDispatchModelProcessor20 extends AbstractDispatchModeProcessor {

    @Autowired
    private UserCardService userCardService;
    @Autowired
    private PrivilegeService privilegeService;

    @Override
    public boolean isMatch(DispatchModeEnum dispatchMode) {
        return DispatchModeEnum.DISPATCH_TIME_MODE.equals(dispatchMode);
    }

    @Override
    public int getSuccessRate(UserTimeLimitTask dispatchTask) {
        return 100;
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
        //获得任务派遣规则
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(dispatchTask.getGroup());
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroup);
        //获得派遣卡牌配置
        List<CfgCardEntity> cards = CardTool.getCards(dispatchTask.getDispatchCards());
        int dispatchTime = TimeLimitTaskTool.getDispatchTime(taskGroup, dispatchTask.getBaseId());
        int finalDispatchTime = CloneUtil.clone(dispatchTime);
        for (CfgCardEntity card : cards) {
            //获得卡牌星级
            int star = card.getStar();
            //获得4.5星封神卡牌
            List<Integer> fourStarHalfDeifyCards = CardTool.getFourStarHalfDeifyCards();
            if (!fourStarHalfDeifyCards.contains(card.getId())) {
                star--;
            }
            //派遣时间星级加成
            finalDispatchTime = dispatchTime - dispatchTime * rules.getStarReduceTime().get(star) / 100;
        }
        //获得用户卡牌
        List<UserCard> ucs = userCardService.getUserCards(dispatchTask.getGameUserId(), dispatchTask.getDispatchCards());
        //获得玩家激活技能
        List<Integer> skills = new ArrayList<>();
        for (UserCard uc : ucs) {
            skills.addAll(uc.gainActivedSkills());
        }
        //派遣任务需要技能
        List<Integer> extraSkills = dispatchTask.getExtraSkills();
        //扣除匹配技能减少时间
        for (Integer extraSkill : extraSkills) {
            if (skills.contains(extraSkill)) {
                finalDispatchTime = dispatchTime - dispatchTime * rules.getConditionRate() / 100;
            }
        }

        return finalDispatchTime;
    }
}
