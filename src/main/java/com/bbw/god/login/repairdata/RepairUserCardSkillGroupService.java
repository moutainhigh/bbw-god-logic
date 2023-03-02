package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardConstant;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_SKILL_GROUP;

/**
 * 迁移卡牌技能到技能组
 *
 * @author: hzf
 * @create: 2022-08-23 15:49
 **/
@Slf4j
@Service
public class RepairUserCardSkillGroupService implements BaseRepairDataService {

    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private UserCardService userCardService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(REPAIR_SKILL_GROUP)) {
            List<UserCard> userCardList = userCardService.getUserCards(gu.getId());
            List<UserCard> newUserCards = new ArrayList<>();
            for (UserCard userCard : userCardList) {

                if (userCard.getStrengthenInfo() == null) {
                    continue;
                }

                log.info("{}卡牌[{}]技能[{}]数据迁移。{}", gu.getId(), userCard.getBaseId(), userCard.gainSkills(), userCard.toString());
                UserCard.SkillGroup skillGroup = userCard.getStrengthenInfo().getSkillGroups().get(CardConstant.SKILL_GROUP_1);
                if (null == skillGroup) {
                    skillGroup = new UserCard.SkillGroup();
                    userCard.getStrengthenInfo().getSkillGroups().put(CardConstant.SKILL_GROUP_1, skillGroup);
                }
                //构建技能组
                if (userCard.getStrengthenInfo().getSkill0() != null) {
                    skillGroup.setS0(userCard.getStrengthenInfo().getSkill0());
                    userCard.getStrengthenInfo().setSkill0(null);
                }
                if (userCard.getStrengthenInfo().getSkill5() != null) {
                    skillGroup.setS5(userCard.getStrengthenInfo().getSkill5());
                    userCard.getStrengthenInfo().setSkill5(null);
                }
                if (userCard.getStrengthenInfo().getSkill10() != null) {
                    skillGroup.setS10(userCard.getStrengthenInfo().getSkill10());
                    userCard.getStrengthenInfo().setSkill10(null);
                }
                if (userCard.getStrengthenInfo().getLastSkillMap() != null) {
                    skillGroup.setLastSkills(userCard.getStrengthenInfo().getLastSkillMap());
                    userCard.getStrengthenInfo().setLastSkillMap(null);
                }
                if (userCard.getStrengthenInfo().getUsingSkillScrolls() != null) {
                    skillGroup.setUsingSkillScrolls(userCard.getStrengthenInfo().getUsingSkillScrolls());
                    userCard.getStrengthenInfo().setUsingSkillScrolls(null);
                }
                if (userCard.getStrengthenInfo().getUseSkillScrollTimes() != null) {
                    skillGroup.setUseSkillScrollTimes(userCard.getStrengthenInfo().getUseSkillScrollTimes());
                    userCard.getStrengthenInfo().setUseSkillScrollTimes(null);
                }
                newUserCards.add(userCard);
            }
            gameUserService.updateItems(newUserCards);
        }
    }
}
