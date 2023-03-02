package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.CardSkillRecommendFavoriteDetail;

import java.util.List;

public interface CardSkillRecommendFavoriteDetailService extends IService<CardSkillRecommendFavoriteDetail> {

    List<CardSkillRecommendFavoriteDetail> queryByRecommendId(int recommendId);

    boolean existMyDetail(long uid,int recommendId);

    void insertDetail(CardSkillRecommendFavoriteDetail detail);
}
