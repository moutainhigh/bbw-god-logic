package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 生肖对碰活动
 *
 * @author: huanghb
 * @date: 2023/2/10 12:01
 */
@Service
public class HolidayChineseZodiacConllisionProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayChineseZodiacConllisionService holidayChineseZodiacConllisionService;
    @Autowired
    private AwardService awardService;


    public HolidayChineseZodiacConllisionProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.CHINESE_ZODIAC_COLLISION);
    }

    /**
     * 是否在ui中展示
     *
     * @param uid
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivityList = (RDActivityList) super.getActivities(uid, activityType);
        rdActivityList.setRdMallList(null);
        //获得生肖对碰信息
        UserChineseZodiacConllision userChineseZodiacConllision = holidayChineseZodiacConllisionService.getUserHolidayChineseZodiacConllision(uid);
        //检查和修复生肖地图数据
        checkAndFixChineseZodiacConllisionData(userChineseZodiacConllision);
        //封装返回类
        RdChineseZodiacConllision rdChineseZodiacConllision = RdChineseZodiacConllision.instance(userChineseZodiacConllision);
        rdActivityList.setChineseZodiacConllision(rdChineseZodiacConllision);
        return rdActivityList;
    }

    /**
     * 检查和修复生肖地图数据
     *
     * @param userChineseZodiacConllision
     */
    private void checkAndFixChineseZodiacConllisionData(UserChineseZodiacConllision userChineseZodiacConllision) {
        //没有错误数据
        if (!userChineseZodiacConllision.ifHasErrorData()) {
            return;
        }
        //检查和修复数据
        String[] chineseZodiacMap = userChineseZodiacConllision.getChineseZodiacMap();
        for (int i = 0; i < chineseZodiacMap.length; i++) {
            String chineseZodiac = chineseZodiacMap[i];
            //不是碰牌成功直接跳过
            int flipStasus = userChineseZodiacConllision.gainFlipStasus(chineseZodiac);
            if (FlipStatusEnum.COLLISION_SUCESS.getStatus() != flipStasus) {
                continue;
            }
            //该生效翻牌数量无异常
            int flipNum = userChineseZodiacConllision.gainFlipCountByChineseZodiac(chineseZodiac, FlipStatusEnum.COLLISION_SUCESS);
            Integer collisionNeedChineseZodiacNum = HolidayChineseZodiaConllisionTool.getCollisionInfo().getCollisionNeedChineseZodiacNum();
            if (0 == flipNum % collisionNeedChineseZodiacNum) {
                continue;
            }
            //修复数据
            userChineseZodiacConllision.fixErrorData(i);
        }
        //更新数据
        holidayChineseZodiacConllisionService.updateData(userChineseZodiacConllision);
    }

    /**
     * 刷新地图
     *
     * @param uid
     * @param mapLevel 地图等级
     */
    public RdChineseZodiacConllision refreshChineseZodiacMap(long uid, int mapLevel) {
        UserChineseZodiacConllision userHolidayChineseZodiacConllision = holidayChineseZodiacConllisionService.getUserHolidayChineseZodiacConllision(uid);
        if (!userHolidayChineseZodiacConllision.ifCanRefreshMap()) {
            throw new ExceptionForClientTip("activity.chineseZodiacMap.not.refresh");
        }
        //地图初始化
        String[] chineseZodiacMap = HolidayChineseZodiaConllisionTool.initChineseZodiacMapByMapLevel(mapLevel);
        userHolidayChineseZodiacConllision.initMap(chineseZodiacMap);
        //更新数据
        holidayChineseZodiacConllisionService.updateData(userHolidayChineseZodiacConllision);
        return RdChineseZodiacConllision.instance(userHolidayChineseZodiacConllision);
    }

    /**
     * 生肖翻牌
     *
     * @param uid
     * @param index
     */
    public RdChineseZodiacConllision chineseZodiacFlip(long uid, int index) {
        UserChineseZodiacConllision userHolidayChineseZodiacConllision = holidayChineseZodiacConllisionService.getUserHolidayChineseZodiacConllision(uid);
        //地图是否存在
        boolean isMapExist = 0 == userHolidayChineseZodiacConllision.getChineseZodiacMap().length;
        if (isMapExist) {
            throw new ExceptionForClientTip("activity.chineseZodiacMap.not.map");
        }
        //是否下标越界
        if (userHolidayChineseZodiacConllision.ifIndexOut(index)) {
            throw new ExceptionForClientTip("activity.chineseZodiacMap.index.out");
        }
        //翻牌
        userHolidayChineseZodiacConllision.flip(index);
        //获得碰撞配置信息
        CfgChineseZodiacConllision.Collision collisionInfo = HolidayChineseZodiaConllisionTool.getCollisionInfo();
        RdChineseZodiacConllision rd = RdChineseZodiacConllision.instance();
        //是否可以碰牌
        if (!userHolidayChineseZodiacConllision.ifCanConllision()) {
            //道具检查
            for (Award award : collisionInfo.getNeedTreasures()) {
                TreasureChecker.checkIsEnough(award.getAwardId(), award.getNum(), uid);
            }
            //道具扣除
            for (Award award : collisionInfo.getNeedTreasures()) {
                TreasureEventPublisher.pubTDeductEvent(uid, award.getAwardId(), award.getNum(), WayEnum.CHINESE_ZODIAC_COLLISION, rd);
            }
            holidayChineseZodiacConllisionService.updateData(userHolidayChineseZodiacConllision);
            rd.addChineseZodiacConllisionInfo(userHolidayChineseZodiacConllision.getMapLevel(), userHolidayChineseZodiacConllision.getChineseZodiacMap());
            return rd;
        }

        List<Award> needFetchAwards = new ArrayList<>();
        //碰牌
        boolean isConllisionSuccess = userHolidayChineseZodiacConllision.conllision(index);
        if (!isConllisionSuccess) {
            //碰牌失败处理
            userHolidayChineseZodiacConllision.conllisionFailHandle();
            holidayChineseZodiacConllisionService.updateData(userHolidayChineseZodiacConllision);
            rd.addChineseZodiacConllisionInfo(userHolidayChineseZodiacConllision.getMapLevel(), userHolidayChineseZodiacConllision.getChineseZodiacMap());
            return rd;
        }

        //碰牌成功处理
        userHolidayChineseZodiacConllision.conllisionSucessHandle();
        needFetchAwards.addAll(collisionInfo.getSucessAwards());
        //全部碰牌成功奖励
        if (userHolidayChineseZodiacConllision.ifConllisionAllSucess()) {
            CfgChineseZodiacConllision.ChineseZodiacMap chineseZodiacMap = HolidayChineseZodiaConllisionTool
                    .getSingleChineseZodiacMap(userHolidayChineseZodiacConllision.getMapLevel());
            needFetchAwards.addAll(chineseZodiacMap.getAwards());
            //地图重置
            userHolidayChineseZodiacConllision.reset();
        }
        holidayChineseZodiacConllisionService.updateData(userHolidayChineseZodiacConllision);
        awardService.fetchAward(uid, needFetchAwards, WayEnum.CHINESE_ZODIAC_COLLISION, "", rd);
        rd.addChineseZodiacConllisionInfo(userHolidayChineseZodiacConllision.getMapLevel(), userHolidayChineseZodiacConllision.getChineseZodiacMap());
        return rd;
    }
}
