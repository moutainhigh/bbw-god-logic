package com.bbw.common;

import com.bbw.god.gameuser.card.UserCard;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CloneUtilTest {

    @Test
    public void testCloneList() {
        List<UserCard> userCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UserCard userCard = new UserCard();
            userCard.setLevel(10);
            userCard.setHierarchy(10);
            userCards.add(userCard);
        }
        List<UserCard> cloneCards = CloneUtil.cloneList(userCards);
        for (UserCard cloneCard : cloneCards) {
            cloneCard.setLevel(5);
            cloneCard.setHierarchy(5);
        }
        Assert.assertTrue(10 == userCards.get(0).getLevel());
        Assert.assertTrue(5 == cloneCards.get(0).getLevel());
    }
}