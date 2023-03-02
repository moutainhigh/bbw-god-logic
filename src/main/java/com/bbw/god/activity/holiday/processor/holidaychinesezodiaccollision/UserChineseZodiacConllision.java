package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 玩家生肖对碰
 *
 * @author: huanghb
 * @date: 2023/2/9 17:21
 */
@Data
public class UserChineseZodiacConllision extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 1L;
    private long gameUserId;
    /** 地图等级 */
    private Integer mapLevel = 0;
    /** 生肖地图 */
    private String[] chineseZodiacMap;

    /**
     * 初始化
     *
     * @return
     */
    protected static UserChineseZodiacConllision instance(long uid) {
        UserChineseZodiacConllision userChineseZodiacConllision = new UserChineseZodiacConllision();
        userChineseZodiacConllision.setId(ID.INSTANCE.nextId());
        userChineseZodiacConllision.setGameUserId(uid);
        return userChineseZodiacConllision;
    }

    /**
     * 重置
     *
     * @return
     */
    protected void reset() {
        this.mapLevel = 0;
        this.chineseZodiacMap = null;
    }

    /**
     * 是否可以刷新地图
     *
     * @return
     */
    public boolean ifCanRefreshMap() {
        return 0 == mapLevel;
    }

    /**
     * 初始化生肖地图
     *
     * @param chineseZodiacMap
     */
    public void initMap(String[] chineseZodiacMap) {
        Integer level = HolidayChineseZodiaConllisionTool.getMapLevelBySize(chineseZodiacMap.length);
        this.mapLevel = level;
        this.chineseZodiacMap = chineseZodiacMap;
    }

    /**
     * 是否下标越界
     *
     * @param index
     * @return
     */
    public boolean ifIndexOut(int index) {
        return index >= this.chineseZodiacMap.length;
    }

    /**
     * 翻牌
     *
     * @param index
     */
    public void flip(int index) {
        String chineseZodiacInfoStr = this.chineseZodiacMap[index];
        List<String> chineseZodiacInfoList = ListUtil.parseStrToStrs(chineseZodiacInfoStr);
        String chineseZodiacInfo = HolidayChineseZodiaConllisionTool.chineseZodiacInfoListToStr(chineseZodiacInfoList.get(0), FlipStatusEnum.OPEN.getStatus());
        this.chineseZodiacMap[index] = chineseZodiacInfo;
    }

    /**
     * 碰牌失败处理
     *
     * @param
     */
    public void conllisionFailHandle() {
        for (int i = 0; i < chineseZodiacMap.length; i++) {
            String chineseZodiacInfo = chineseZodiacMap[i];
            List<String> chineseZodiacInfoList = ListUtil.parseStrToStrs(chineseZodiacInfo);
            int flipStasus = Integer.parseInt(chineseZodiacInfoList.get(1));
            if (FlipStatusEnum.OPEN.getStatus() != flipStasus) {
                continue;
            }
            chineseZodiacInfo = HolidayChineseZodiaConllisionTool.chineseZodiacInfoListToStr(chineseZodiacInfoList.get(0), FlipStatusEnum.NOT_OPEN.getStatus());
            chineseZodiacMap[i] = chineseZodiacInfo;
        }
    }

    /**
     * 碰牌成功处理
     *
     * @param
     */
    public void conllisionSucessHandle() {
        //更新生肖卡状态
        for (int i = 0; i < chineseZodiacMap.length; i++) {
            String newChineseZodiacInfo = chineseZodiacMap[i];
            List<String> chineseZodiacInfoList = ListUtil.parseStrToStrs(newChineseZodiacInfo);
            int flipStasus = gainFlipStasus(newChineseZodiacInfo);
            if (FlipStatusEnum.OPEN.getStatus() != flipStasus) {
                continue;
            }
            newChineseZodiacInfo = HolidayChineseZodiaConllisionTool.chineseZodiacInfoListToStr(chineseZodiacInfoList.get(0), FlipStatusEnum.COLLISION_SUCESS.getStatus());
            chineseZodiacMap[i] = newChineseZodiacInfo;
        }
    }

    /**
     * 是否可以对碰
     *
     * @param
     */
    public boolean ifCanConllision() {
        int count = gainFlipCount(FlipStatusEnum.OPEN);
        return HolidayChineseZodiaConllisionTool.getCollisionInfo().getCollisionNeedChineseZodiacNum() == count;
    }

    /**
     * 获得翻牌状态
     *
     * @param chineseZodiacInfo
     * @return
     */
    public int gainFlipStasus(String chineseZodiacInfo) {
        List<String> chineseZodiacInfoList = ListUtil.parseStrToStrs(chineseZodiacInfo);
        return Integer.parseInt(chineseZodiacInfoList.get(1));
    }

    /**
     * 对碰
     *
     * @param index
     */
    public boolean conllision(int index) {
        int openChineseZodiacNum = (int) Arrays.stream(this.chineseZodiacMap).filter(chineseZodiacMap[index]::contains).count();
        //是否碰撞成功
        boolean isConllisionSuccess = HolidayChineseZodiaConllisionTool.getCollisionInfo().getCollisionNeedChineseZodiacNum() == openChineseZodiacNum;
        if (!isConllisionSuccess) {
            return false;
        }
        return true;
    }

    /**
     * 是否有错误数据
     *
     * @return
     */
    public boolean ifHasErrorData() {
        int count = gainFlipCount(FlipStatusEnum.COLLISION_SUCESS);
        Integer collisionNeedChineseZodiacNum = HolidayChineseZodiaConllisionTool.getCollisionInfo().getCollisionNeedChineseZodiacNum();
        return 0 != count % collisionNeedChineseZodiacNum;
    }

    /**
     * 获得所有生肖翻牌指定状态计数
     *
     * @param collisionSucess 翻牌状态
     * @return
     */
    public int gainFlipCount(FlipStatusEnum collisionSucess) {
        if (null == this.chineseZodiacMap) {
            return 0;
        }
        return (int) Arrays.stream(this.chineseZodiacMap)
                .filter(tmp -> gainFlipStasus(tmp) == collisionSucess.getStatus()).count();
    }

    /**
     * 获得指定生肖翻牌指定状态计数
     *
     * @param chineseZodiac
     * @param collisionSucess
     * @return
     */
    public int gainFlipCountByChineseZodiac(String chineseZodiac, FlipStatusEnum collisionSucess) {
        if (null == this.chineseZodiacMap) {
            return 0;
        }
        return (int) Arrays.stream(this.chineseZodiacMap)
                .filter(tmp -> tmp.equals(chineseZodiac) && gainFlipStasus(tmp) == collisionSucess.getStatus()).count();
    }

    /**
     * 修复错误数据
     *
     * @return
     */
    public void fixErrorData(int chineseZodiacIndex) {
        String chineseZodiacInfo = chineseZodiacMap[chineseZodiacIndex];
        //不是碰牌成功的不需要修复数据
        int flipStasus = gainFlipStasus(chineseZodiacInfo);
        if (FlipStatusEnum.COLLISION_SUCESS.getStatus() != flipStasus) {
            return;
        }
        List<String> chineseZodiacInfoList = ListUtil.parseStrToStrs(chineseZodiacInfo);
        chineseZodiacInfo = HolidayChineseZodiaConllisionTool.chineseZodiacInfoListToStr(chineseZodiacInfoList.get(0), FlipStatusEnum.NOT_OPEN.getStatus());
        chineseZodiacMap[chineseZodiacIndex] = chineseZodiacInfo;
    }

    /**
     * 检查和修复数据
     *
     * @return true 代表数据不需要修复， flase 数据异常并修复
     */
    public Boolean checkAndHandleLastData() {
        boolean notNeedFixData = true;
        int notConllisionNum = gainFlipCount(FlipStatusEnum.OPEN) + gainFlipCount(FlipStatusEnum.NOT_OPEN);
        Integer collisionNeedChineseZodiacNum = HolidayChineseZodiaConllisionTool.getCollisionInfo().getCollisionNeedChineseZodiacNum();
        //是否是最后一对生肖牌
        if (collisionNeedChineseZodiacNum != notConllisionNum) {
            return notNeedFixData;
        }
        int ownChineseZodiacId = 0;
        int ownChineseZodiacStatus = FlipStatusEnum.NOT_OPEN.getStatus();
        int ownChineseZodiacIndex = 0;
        for (int i = 0; i < this.getChineseZodiacMap().length; i++) {
            String chineseZodiacInfo = chineseZodiacMap[i];
            int flipStasus = gainFlipStasus(chineseZodiacInfo);
            //碰牌成功的不做检测
            if (FlipStatusEnum.COLLISION_SUCESS.getStatus() == flipStasus) {
                continue;
            }
            List<String> chineseZodiacInfoList = ListUtil.parseStrToStrs(chineseZodiacInfo);
            int currentChineseZodiacId = Integer.parseInt(chineseZodiacInfoList.get(0));
            //记录最后一对生肖卡的信息
            if (0 == ownChineseZodiacId) {
                ownChineseZodiacId = currentChineseZodiacId;
                ownChineseZodiacStatus = flipStasus;
                ownChineseZodiacIndex = i;
                continue;
            }
            //生肖id一致检测通过
            if (ownChineseZodiacId == currentChineseZodiacId) {
                continue;
            }
            //该生肖是否是当前生肖地图唯一的一对
            int gainCollisionSuccessCountByChineseZodiac = gainFlipCountByChineseZodiac(chineseZodiacInfo, FlipStatusEnum.COLLISION_SUCESS);
            if (0 == gainCollisionSuccessCountByChineseZodiac) {
                String newChineseZodiacInfo = HolidayChineseZodiaConllisionTool.chineseZodiacInfoListToStr(chineseZodiacInfoList.get(0), flipStasus);
                this.getChineseZodiacMap()[ownChineseZodiacIndex] = newChineseZodiacInfo;
                notNeedFixData = false;
                break;
            }
            //该生肖不是当前生肖地图唯一的一对
            String newChineseZodiacInfo = HolidayChineseZodiaConllisionTool.chineseZodiacInfoListToStr("" + ownChineseZodiacId, ownChineseZodiacStatus);
            this.getChineseZodiacMap()[i] = newChineseZodiacInfo;
            notNeedFixData = false;
            break;
        }
        return notNeedFixData;
    }

    /**
     * 是否全部碰牌成功
     *
     * @return
     */
    public boolean ifConllisionAllSucess() {
        for (int i = 0; i < this.chineseZodiacMap.length; i++) {
            int flipStasus = gainFlipStasus(chineseZodiacMap[i]);
            if (FlipStatusEnum.COLLISION_SUCESS.getStatus() == flipStasus) {
                continue;
            }
            return false;

        }
        return true;
    }
}
