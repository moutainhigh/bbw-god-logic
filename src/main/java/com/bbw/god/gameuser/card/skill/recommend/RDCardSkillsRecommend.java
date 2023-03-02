package com.bbw.god.gameuser.card.skill.recommend;

import com.bbw.god.db.entity.CardSkillRecommend;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 返回客户端技能推荐
 *
 * @author fzj
 * @date 2021/9/27 17:19
 */
@Data
public class RDCardSkillsRecommend extends RDSuccess {
    /** 技能推荐 */
    private List<RecommendInfo> cardSkills;
    /** 更新时间 */
    private Integer statisticDate;

    @Data
    public static class RecommendInfo{
        private List<Integer> skills;
    }
    public static RecommendInfo instanceRecommendInfo(List<Integer> cardAllSkills){
        RecommendInfo info=new RecommendInfo();
        info.setSkills(cardAllSkills);
        return info;
    }
}
