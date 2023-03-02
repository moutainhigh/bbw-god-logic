package com.bbw.god.game.zxz.controller;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.zxz.rd.*;
import com.bbw.god.game.zxz.service.ZxzLogic;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
   新诛仙阵 接口相关
 * @author: hzf
 * @create: 2022-09-14 17:48
 **/
@RestController
public class ZxzController extends AbstractController {


    @Autowired
    private ZxzLogic zxzLogic;



    /**
     * 进入诛仙阵
     * @return
     */
    @GetMapping(CR.Zxz.ENTER)
    public RdZxzLevel enter() {
        return zxzLogic.enter(getUserId());
    }

    /**
     * 进入难度
     * @param difficulty 难度
     * @param regionId 区域Id
     * @return
     */
    @GetMapping(CR.Zxz.ENTER_LEVEL)
    public RdZxzRegion enterLevel(Integer difficulty, Integer regionId){
        return zxzLogic.enterLevel(getUserId(),difficulty,regionId);
    }

    /**
     * 进入区域
     * @param regionId 区域Id
     * @return
     */
    @GetMapping(CR.Zxz.ENTER_REGION)
    public RdZxzRegionDefender enterRegion(Integer regionId){
        return zxzLogic.enterRegion(getUserId(),regionId);
    }

    /**
     * 诛仙阵：编辑用户卡组
     * @param cardIds 卡牌ids
     * @param regionId 区域id
     * @return
     */
    @GetMapping(CR.Zxz.EDIT_CARD_GROUP)
    public RDSuccess editCardGroup(String cardIds,Integer regionId){
        return zxzLogic.editCardGroup(getUserId(),cardIds,regionId);
    }

    /**
     * 设置符册
     * @param regionId
     * @param fuCeDataId
     * @return
     */
    @GetMapping(CR.Zxz.SET_FU_CE)
    public RDSuccess setFuCe(Integer regionId,long fuCeDataId){
        return zxzLogic.setFuCe(getUserId(),regionId,fuCeDataId);
    }

    /**
     * 编辑词条
     * @param entries id@等级,id@等级,id@等级
     * @param regionId 区域Id
     * @param unEntries  卸载 词条id@等级,词条id@等级,词条id@等级
     * @return
     */
    @GetMapping(CR.Zxz.EDIT_ENTRY)
    public RdEntry editEntry(String entries,Integer regionId,String unEntries){
        return zxzLogic.editEntry(getUserId(),entries,regionId,unEntries);
    }

    /**
     * 扫荡
     * @param difficulty 难度类型
     * @return
     */
    @GetMapping(CR.Zxz.MOP_UP)
    public RDCommon mopUp(Integer difficulty){
        return zxzLogic.mopUp(getUserId(),difficulty);
    }

    /**
     * 敌方配置
     * @param regionId 区域Id
     * @return
     */
    @GetMapping(CR.Zxz.ENEMY_CONFIG)
    public RdEnemyRegion enemyRegion(Integer regionId){
        return zxzLogic.enemyRegion(regionId);
    }

    /**
     * 查看词条
     * @param difficulty
     * @return
     */
    @GetMapping(CR.Zxz.GET_ENTRY)
    public RdZxzEntry getEntry(Integer difficulty){
        return zxzLogic.getEntry(getUserId(),difficulty);
    }

    /**
     * 查看用户卡组
     * @param regionId
     * @return
     */
    @GetMapping(CR.Zxz.GET_USER_CARD_GROUP)
    public RdUserCardGroup getUserCardGroup(Integer regionId){
        return zxzLogic.getUserCardGroup(getUserId(),regionId);
    }

    /**
     * 查看上榜用户卡组
     * @param uid
     * @param regionId
     * @param beginDate
     * @return
     */
    @GetMapping(CR.Zxz.GET_USER_RANK_CARD_GROUP)
    public RdUserRankCardGroup getUserRankCardGroup(long uid, Integer regionId, Integer beginDate){
        return zxzLogic.getUserRankCardGroup(uid,regionId,beginDate);
    }
    /**
     * 开宝箱
     * @param defenderId
     * @return
     */
    @GetMapping(CR.Zxz.OPEN_BOX)
    public RDCommon openBox(Integer defenderId){
        return zxzLogic.openBox(getUserId(),defenderId);
    }

    /**
     * 领取全通宝箱
     * @param difficulty
     * @return
     */
    @GetMapping(CR.Zxz.OPEN_DIFFICULTY_PASSBOX)
    public RDCommon openDifficultyPassBox(Integer difficulty){
        return zxzLogic.openDifficultyPassBox(getUserId(),difficulty);
    }

    /**
     * 获取区域榜单
     * @param difficulty
     * @param regionId
     * @param beginDate 开始时间
     * @param startRank
     * @param endRank
     * @return
     */
    @GetMapping(CR.Zxz.GET_ZXZ_RANK)
    public RdZxzRank getZxzRank(Integer difficulty,Integer regionId,Integer beginDate, Integer startRank, Integer endRank){
        return zxzLogic.getZxzRank(getUserId(),difficulty,regionId,beginDate,startRank,endRank);
    }

    /**
     * 根据难度获取诅咒效果
     * @param difficulty
     * @return
     */
    @GetMapping(CR.Zxz.get_Zu_Zhou)
    public RdZxzZuZhou getZuZhou(Integer difficulty){
        return zxzLogic.getZuZhou(getUserId(),difficulty);

    }


}
