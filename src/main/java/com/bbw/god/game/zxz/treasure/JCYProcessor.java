package com.bbw.god.game.zxz.treasure;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.ZxzAbstractCardGroup;
import com.bbw.god.game.zxz.entity.UserZxzCard;
import com.bbw.god.game.zxz.entity.UserZxzCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.service.ZxzService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 金创药
 * @author: hzf
 * @create: 2022-09-23 11:37
 **/
@Service
public class JCYProcessor  extends TreasureUseProcessor {

    @Autowired
    private ZxzService zxzService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;

    @Autowired
    private GameUserService gameUserService;

    public JCYProcessor() {
        this.treasureEnum = TreasureEnum.ZXZ_JCY;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        if (null != param.getRegionId() && 0 != param.getRegionId()) {
            effectZxz(gu.getId(),param);
        } else {
            effectZxzFourSaints(gu.getId(),param);
        }

    }

    /**
     * 作用于诛仙阵-四圣
     * @param uid
     * @param param
     */
    private void effectZxzFourSaints(long uid, CPUseTreasure param) {
        // 获取用户四圣信息
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, param.getChallengeType());
        //金创药使用次数限制
        Integer jinCYLimitNum = CfgFourSaintsTool.getCfg().getJinCYLimitNum();
        //是否达到次数上限
        boolean ifNumLimit = userZxzFourSaints.getJinCyTimes() >= jinCYLimitNum;

        if (ifNumLimit){
            throw new ExceptionForClientTip("zxz.four.saints.jinCy.num.limit");

        }
        //获取用户卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, param.getChallengeType());
        //金创药的作用
        jCYEffect(userCardGroup,param);
        //更新金创药使用次数
        userZxzFourSaints.addJinCyTime();
        gameUserService.updateItem(userZxzFourSaints);

    }
    /**
     * 作用于诛仙阵上
     * @param uid
     * @param param
     */
    private void effectZxz(long uid,CPUseTreasure param){
        //判断区域
        ZxzTool.ifRegion(param.getRegionId());
        //获取用户卡组
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, param.getRegionId());
        jCYEffect(userCardGroup,param);

  }

    /**
     * 金创药的作用
     * @param userCardGroup
     * @param param
     * @param <T>
     */
  private < T extends ZxzAbstractCardGroup> void jCYEffect(T userCardGroup, CPUseTreasure param){
      //判断卡牌id是否为空
      boolean isEffectOnCard = StringUtils.isNotBlank(param.getCardId());
      if (isEffectOnCard) {
          userCardGroup.resurrectionCard(Integer.parseInt(param.getCardId()));
          gameUserService.updateItem(userCardGroup);
          return;
      }
      userCardGroup.recoverHp(0.3);
      gameUserService.updateItem(userCardGroup);
  }
}
