package com.bbw.god.gameuser.card.skill.recommend;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author：lwb
 * @date: 2021/1/11 9:48
 * @version: 1.0
 */
@RestController
public class CardSkillRecommendController extends AbstractController {
    @Autowired
    private CardSkillRecommendLogic cardSkillRecommendLogic;

    /**
     * 获取推荐列表
     * @param cardId
     * @return
     */
    @RequestMapping(CR.CardSkillRecommend.RecommendList)
    public RDCardSkillRecommend list(Integer cardId){
        return cardSkillRecommendLogic.list(getUserId(),cardId);
    }

    /**
     * 获取技能推荐列表
     * @param cardId
     * @return
     */
    @RequestMapping(CR.CardSkillRecommend.CARD_SKILLS_RECOMMEND)
    public RDCardSkillsRecommend getCardsAllSkills(Integer cardId){
        return cardSkillRecommendLogic.getCardsAllSkills(cardId);
    }

    /**
     * 点赞、踩
     * @param recommendId
     * @param status
     * @return
     */
    @RequestMapping(CR.CardSkillRecommend.RecommendComment)
    public RDCardSkillRecommend comment(Integer recommendId,Integer status){
        return cardSkillRecommendLogic.comment(getUserId(),recommendId,status);
    }

}
