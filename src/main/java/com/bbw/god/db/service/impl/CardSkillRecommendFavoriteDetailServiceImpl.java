package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.CardSkillRecommendFavoriteDetailDao;
import com.bbw.god.db.entity.CardSkillRecommendFavoriteDetail;
import com.bbw.god.db.service.CardSkillRecommendFavoriteDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardSkillRecommendFavoriteDetailServiceImpl extends ServiceImpl<CardSkillRecommendFavoriteDetailDao,CardSkillRecommendFavoriteDetail> implements CardSkillRecommendFavoriteDetailService {
    @Override
    public List<CardSkillRecommendFavoriteDetail> queryByRecommendId(int recommendId) {
        EntityWrapper<CardSkillRecommendFavoriteDetail> ew=new EntityWrapper<>();
        ew.eq("recommend_id",recommendId);
        return this.selectList(ew);
    }

    @Override
    public boolean existMyDetail(long uid, int recommendId) {
        EntityWrapper<CardSkillRecommendFavoriteDetail> ew=new EntityWrapper<>();
        ew.eq("recommend_id",recommendId).eq("uid",uid);
        return this.selectCount(ew)>0;
    }

    @Override
    public void insertDetail(CardSkillRecommendFavoriteDetail detail) {
        this.insert(detail);
    }
}
