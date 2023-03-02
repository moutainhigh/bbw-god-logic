package com.bbw.god.game.zxz.treasure;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.zxz.cfg.CfgRegionConfig;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.game.zxz.entity.ZxzAbstractCardGroup;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.service.ZxzService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import com.bbw.god.game.zxz.entity.UserZxzCardGroupInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 复活草
 * @author: hzf
 * @create: 2022-09-22 22:24
 **/
@Service
public class RevivalGrassProcessor extends TreasureUseProcessor {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;


    public RevivalGrassProcessor() {
        this.treasureEnum = TreasureEnum.ZXZ_REVIVAL_GRASS;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        if (null != param.getRegionId() && 0 != param.getRegionId()) {
            effectZxz(gu.getId(), param.getRegionId());
        } else {
            effectZxzFourSaints(gu.getId(), param.getChallengeType());
        }

    }

    private void effectZxzFourSaints(long uid, Integer challengeType) {
        //复活次数限制
        Integer fuHCLimitNum = CfgFourSaintsTool.getCfg().getFuHCLimitNum();
        //获取四圣挑战区域
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        //当前使用的次数
        Integer surviceTimes = userZxzFourSaints.getSurviceTimes();
        if (surviceTimes >= fuHCLimitNum){
            throw new ExceptionForClientTip("zxz.reviveFrequency.achieve.max");
        }
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid,challengeType);
        if (userCardGroup.getHp() > 0 && userCardGroup.getHp().equals(userCardGroup.getMaxHp())) {
            throw  new ExceptionForClientTip("zxz.userHp.full");
        }
        revivalGrassEffect(uid,userCardGroup);
        //添加复活次数
        userZxzFourSaints.addSurviceTimes();
        gameUserService.updateItem(userZxzFourSaints);
    }

    private void effectZxz(long uid,Integer regionId){
        //判断区域
        ZxzTool.ifRegion(regionId);

        CfgRegionConfig regionConfig = ZxzTool.getRegionConfig();
        //每个区域的复活次数最大值
        Integer reviveFrequency = regionConfig.getReviveFrequency();
        //获取区域
        UserZxzRegionInfo userRegion = zxzService.getUserZxzRegion(uid,regionId);
        //当前使用的次数
        Integer surviceTimes = userRegion.getSurviceTimes();
        if (surviceTimes >= reviveFrequency){
            throw new ExceptionForClientTip("zxz.reviveFrequency.achieve.max");
        }
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid,regionId);
        if (userCardGroup.getHp() > 0 && userCardGroup.getHp().equals(userCardGroup.getMaxHp())) {
            throw  new ExceptionForClientTip("zxz.userHp.full");
        }
        revivalGrassEffect(uid,userCardGroup);
        //添加复活次数
        userRegion.addSurviceTimes();
        gameUserService.updateItem(userRegion);
    }

    /**
     * 复活草作用
     * @param uid
     * @param userCardGroup
     * @param <T>
     */
    private < T extends ZxzAbstractCardGroup> void revivalGrassEffect(long uid,T userCardGroup){
        //恢复到上一次的血量
        userCardGroup.recoverHpToLastHp(uid);
        gameUserService.updateItem(userCardGroup);
    }
}
