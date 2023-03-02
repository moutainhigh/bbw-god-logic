package com.bbw.god.gameuser.card.skill.recommend;

import com.bbw.god.db.entity.CardSkillRecommend;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author：lwb
 * @date: 2021/1/11 9:50
 * @version: 1.0
 */
@Data
public class RDCardSkillRecommend extends RDSuccess {

    private List<RecommendInfo> list;

    @Data
    public static class RecommendInfo{
        private Integer id;
        private Integer cardId;
        private List<Integer> skills;
        private Integer good=0;
        private Integer bad=0;
        private Integer myStatus=0;
    }

    public static RecommendInfo instanceRecommendInfo(CardSkillRecommend recommend){
        RecommendInfo info=new RecommendInfo();
        info.setId(recommend.getId());
        info.setCardId(recommend.getCardId());
        info.setSkills(Arrays.asList(recommend.getZeroSkill(),recommend.getFiveSkill(),recommend.getTenSkill()));
        return info;
    }
}
