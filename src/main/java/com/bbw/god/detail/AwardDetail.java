package com.bbw.god.detail;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-26 20:35
 */
@Data
public class AwardDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Integer NO_ID = 0;// 没有ID
    private AwardEnum awardType; // 资源类型。
    private Integer awardId = 0; // 资源ID
    private String awardName = ""; // 名称
    private Long valueChange; // 变化数量。正数为加，负数为扣除。

    private AwardDetail(AwardEnum awardType, Integer awardId, String awardName) {
        this.awardType = awardType;
        this.awardId = awardId;
        this.awardName = awardName;
    }

    private AwardDetail(AwardEnum awardType, Integer awardId) {
        this.awardType = awardType;
        this.awardId = awardId;
        this.awardName = awardType.getName();
    }

    /**
     * 体力
     *
     * @param valueChange
     * @return
     */
    public static AwardDetail fromDice(long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.TL, NO_ID);
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 元宝
     *
     * @param valueChange
     * @return
     */
    public static AwardDetail fromGold(long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.YB, NO_ID);
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 钻石
     *
     * @param valueChange
     * @return
     */
    public static AwardDetail fromDiamond(long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.ZS, NO_ID);
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 铜钱
     *
     * @param valueChange
     * @return
     */
    public static AwardDetail fromCopper(long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.TQ, NO_ID);
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 元素
     *
     * @param typeEnum
     * @param valueChange
     * @return
     */
    public static AwardDetail fromEle(TypeEnum typeEnum, long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.YS, typeEnum.getValue(), typeEnum.getName() + AwardEnum.YS.getName());
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 卡牌
     *
     * @param userCard
     * @param valueChange
     * @return
     */
    public static AwardDetail fromUserCard(UserCard userCard, long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.KP, userCard.getBaseId(), userCard.getName());
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 法宝
     *
     * @param treasureId
     * @param valueChange
     * @return
     */
    public static AwardDetail fromTreasure(int treasureId, long valueChange) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureId);
        AwardDetail detail = new AwardDetail(AwardEnum.FB, treasureId, treasure.getName());
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 法宝
     *
     * @param treasure
     * @param valueChange
     * @return
     */
    public static AwardDetail fromTreasure(TreasureEnum treasure, long valueChange) {
        AwardDetail detail = new AwardDetail(AwardEnum.FB, treasure.getValue(), treasure.getName());
        detail.setValueChange(valueChange);
        return detail;
    }

    /**
     * 符图
     *
     * @param userFuTu
     * @param valueChange
     * @return
     */
    public static AwardDetail fromFuTu(UserFuTu userFuTu, long valueChange) {
        Integer fuTuId = userFuTu.getBaseId();
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(fuTuId);
        CfgFuTuEntity fuTuInFo = YuXGTool.getFuTuInFo(fuTuId);
        String fuTu = treasure.getName() + userFuTu.getLv() + "级" + fuTuInFo.getQuality() + "阶";
        AwardDetail detail = new AwardDetail(AwardEnum.FB, fuTuId, fuTu);
        detail.setValueChange(valueChange);
        return detail;
    }
}
