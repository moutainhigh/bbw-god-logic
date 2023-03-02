package com.bbw.god.gamer.helpabout;

import com.bbw.LogicServerApplication;
import com.bbw.common.JSONUtil;
import com.bbw.god.gameuser.card.skill.recommend.CardSkillRecommendLogic;
import com.bbw.god.gameuser.card.skill.recommend.RDCardSkillRecommend;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
* @author lwb  
* @date 2019年4月10日  
* @version 1.0  
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogicServerApplication.class)
@WebAppConfiguration
public class Test {

    @Autowired
    private CardSkillRecommendLogic cardSkillRecommendLogic;

    @org.junit.Test
    public void getTest(){
        RDCardSkillRecommend list = cardSkillRecommendLogic.list(201026009600002L, 101);
        System.err.println(JSONUtil.toJson(list));
    }
}
