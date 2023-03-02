package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.ListUtil;
import com.bbw.god.db.dao.CardSkillRecommendDao;
import com.bbw.god.db.entity.CardSkillRecommend;
import com.bbw.god.db.service.CardSkillRecommendService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardSkillRecommendServiceImpl extends ServiceImpl<CardSkillRecommendDao,CardSkillRecommend> implements CardSkillRecommendService {

    @Override
    public List<CardSkillRecommend> queryAllByCardId(int cardId) {
        EntityWrapper<CardSkillRecommend> ew=new EntityWrapper<>();
        ew.eq("card_id",cardId);
        List<CardSkillRecommend> recommends = this.selectList(ew);
        if (ListUtil.isNotEmpty(recommends)){
            return recommends;
        }
        return new ArrayList<>();
    }
}
