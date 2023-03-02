package com.bbw;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * 卡牌技能组测试类
 *
 * @author: hzf
 * @create: 2022-08-24 09:27
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LogicServerApplication.class }) // 指定启动类
public class UserCardSkillGroupTest {

    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private UserCardService userCardService;


    /**
     * 测试 账号 95599@qq.com,
     * 角色:210831009600001
     * 卡牌：夔牛； id：541
     * 默认卡组是：skillGroup0
     * 切换卡牌是：skillGroup1
     *
     * type = 40
     * baseId = 461
     * skill = 3149
     */
    @Test
    public void changeSkillGroup(){

//        long uid = 210831009600001L;
//        int cardId = 541;
//        String skillGroupKey = "skillGroup0";
//        UserCard userCard = userCardService.getUserCard(uid, cardId);
//        System.out.println("userCard = " + userCard);
//        if (null != userCard.getStrengthenInfo().gainSkillGroup()) {
//            userCard.getStrengthenInfo().setCurrentSkillGroupKey(skillGroupKey);
//            Map<String, List<Integer>> lastSkillMap = userCard.getStrengthenInfo().getSkillGroups().get(skillGroupKey).getLastSkillMap();
//            System.out.println("lastSkillMap = " + lastSkillMap.size());
//            System.out.println("lastSkillMap = " + lastSkillMap);
////            UserCard.UserCardSkillGroup userCardSkillGroup = new UserCard.UserCardSkillGroup();
////            userCard.getStrengthenInfo().getSkillGroups().put(skillGroupKey, userCardSkillGroup);
//
//        }
////        gameUserService.updateItem(userCard);
        int skillScrollId = TreasureTool.getSkillScrollId(40, 461, 3149);
        System.out.println("skillScrollId = " + skillScrollId);
    }
}
