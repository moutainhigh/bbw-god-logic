package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;

/**
 * 生肖碰撞活动
 *
 * @author: huanghb
 * @date: 2023/2/10 11:56
 */
@Data
public class RdChineseZodiacConllision extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 地图等级 */
    private Integer mapLevel;
    /** 生肖地图 */
    private String[] chineseZodiacMap;

    @Data
    public static class rdWishInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 唯一标识 */
        private long id;
        /*心愿礼物*/
        private Integer wishGift;
        /*npcId*/
        private Integer npcId;

    }

    /**
     * 初始化
     *
     * @return
     */
    public static RdChineseZodiacConllision instance() {
        RdChineseZodiacConllision rdChineseZodiacConllision = new RdChineseZodiacConllision();
        return rdChineseZodiacConllision;
    }

    /**
     * 初始化
     *
     * @param userchinesezodiacconllision
     * @return
     */
    public static RdChineseZodiacConllision instance(UserChineseZodiacConllision userchinesezodiacconllision) {
        RdChineseZodiacConllision rdChineseZodiacConllision = new RdChineseZodiacConllision();
        rdChineseZodiacConllision.setMapLevel(userchinesezodiacconllision.getMapLevel());
        rdChineseZodiacConllision.setChineseZodiacMap(userchinesezodiacconllision.getChineseZodiacMap());
        return rdChineseZodiacConllision;
    }

    /**
     * 添加生肖对碰信息
     *
     * @param mapLevel         地图等级
     * @param chineseZodiacMap 生肖地图
     * @return
     */
    public void addChineseZodiacConllisionInfo(int mapLevel, String[] chineseZodiacMap) {
        this.mapLevel = mapLevel;
        this.chineseZodiacMap = chineseZodiacMap;
    }


}
