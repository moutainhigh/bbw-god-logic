package com.bbw.god.server.maou.attack;

import com.bbw.BaseTest;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.server.maou.attack.skill.SkillPerformResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MaouAttackServiceTest extends BaseTest {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MaouAttackService maouAttackService;
    @Autowired
    private UserCardService userCardService;

    @Test
    public void getBeatedBlood() {
        List<UserCard> userCards = userCardService.getUserCards(UID);
        userCards = userCards.subList(0, 3);
        List<SkillPerformResult> results = this.maouAttackService.attack(userCards);
        results.forEach(System.out::println);
    }
}