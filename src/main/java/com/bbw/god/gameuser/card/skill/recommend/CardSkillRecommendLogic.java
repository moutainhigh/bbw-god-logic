package com.bbw.god.gameuser.card.skill.recommend;

import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CardSkillRecommend;
import com.bbw.god.db.entity.CardSkillRecommendFavoriteDetail;
import com.bbw.god.db.entity.StatisticInCardSkillEntity;
import com.bbw.god.db.service.CardSkillRecommendFavoriteDetailService;
import com.bbw.god.db.service.CardSkillRecommendService;
import com.bbw.god.statistics.CardSkillStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author：lwb
 * @date: 2021/1/11 9:49
 * @version: 1.0
 */
@Service
public class CardSkillRecommendLogic {

    @Autowired
    private CardSkillRecommendService recommendService;
    @Autowired
    private CardSkillRecommendFavoriteDetailService detailService;
    @Autowired
    private CardSkillStatisticService cardSkillStatisticService;

    public RDCardSkillRecommend list(long uid,int cardId){
        RDCardSkillRecommend rd=new RDCardSkillRecommend();
        List<CardSkillRecommend> recommendList = recommendService.queryAllByCardId(cardId);
        List<RDCardSkillRecommend.RecommendInfo> list=new ArrayList<>();
        if (ListUtil.isNotEmpty(recommendList)){
            for (CardSkillRecommend recommend : recommendList) {
                RDCardSkillRecommend.RecommendInfo info=RDCardSkillRecommend.instanceRecommendInfo(recommend);
                List<CardSkillRecommendFavoriteDetail> details = detailService.queryByRecommendId(recommend.getId());
                if (ListUtil.isNotEmpty(details)){
                    Long count = details.stream().filter(p -> p.getStatus() == -1).count();
                    info.setBad(count.intValue());
                    info.setGood(details.size()-info.getGood());
                    Optional<CardSkillRecommendFavoriteDetail> optional = details.stream().filter(p -> p.getUid() == uid).findFirst();
                    if (optional.isPresent()){
                        info.setMyStatus(optional.get().getStatus());
                    }
                }
                list.add(info);
            }
        }
        rd.setList(list);
        return rd;
    }
    /**
     * 获取卡牌前五条技能推荐
     *
     * @param cardId
     */
    public RDCardSkillsRecommend getCardsAllSkills(int cardId){
        List<StatisticInCardSkillEntity> cardSkillEntities = cardSkillStatisticService.getCardSkillEntities(cardId);
        List<List<Integer>> cardsAllSkills = cardSkillStatisticService.getCardsAllSkills(cardId);
        List<RDCardSkillsRecommend.RecommendInfo> cardAllSkills = new ArrayList<>();
        RDCardSkillsRecommend rd = new RDCardSkillsRecommend();
        if (ListUtil.isNotEmpty(cardsAllSkills)) {
            for (int i = 0; i < cardsAllSkills.size(); i++){
                List<Integer> cardSkillsList = cardsAllSkills.get(i);
                RDCardSkillsRecommend.RecommendInfo recommendInfo = RDCardSkillsRecommend.instanceRecommendInfo(cardSkillsList);
                cardAllSkills.add(recommendInfo);
            }
            rd.setStatisticDate(cardSkillEntities.get(0).getStatisticDate());
        }
        rd.setCardSkills(cardAllSkills);
        return rd;
    }

    public RDCardSkillRecommend comment(long uid,int recommendId,int status){
        RDCardSkillRecommend rd=new RDCardSkillRecommend();
        if (detailService.existMyDetail(uid,recommendId)){
            return rd;
        }
        CardSkillRecommendFavoriteDetail detail=new CardSkillRecommendFavoriteDetail();
        detail.setRecommend_id(recommendId);
        detail.setUid(uid);
        detail.setStatus(status==-1?-1:1);
        detailService.insertDetail(detail);
        return rd;
    }
}
