package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.CardSkillRecommend;

import java.util.List;

public interface CardSkillRecommendService extends IService<CardSkillRecommend> {

    List<CardSkillRecommend> queryAllByCardId(int cardId);
}
