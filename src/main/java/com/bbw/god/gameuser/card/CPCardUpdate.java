package com.bbw.god.gameuser.card;

import lombok.Data;

/**
 * 卡牌升级参数
 *
 * @author suhq
 * @date 2018年11月6日 下午2:08:07
 */
@Data
public class CPCardUpdate {
    /** 数据ID（非必传） **/
    private Long dataId;
    /** 卡牌基础ID（必传） **/
    private int cardId;
    private int goldEle;
    private int woodEle;
    private int waterEle;
    private int fireEle;
    private int earthEle;
    private int cardSoul;
    private int shenSha = 0;// 神砂

    /**
     * 参数有效，数字都在合理范围
     *
     * @return
     */
    public void checkVal() {
        goldEle = Math.max(0, goldEle);
        woodEle = Math.max(0, woodEle);
        waterEle = Math.max(0, waterEle);
        fireEle = Math.max(0, fireEle);
        earthEle = Math.max(0, earthEle);
        cardSoul = Math.max(0, cardSoul);
        shenSha = Math.max(0, shenSha);
    }

    /**
     * 获取升级卡牌元素和灵石总数
     *
     * @return
     */
    public int getTotalEleAndSoul() {
        return goldEle + woodEle + waterEle + fireEle + earthEle + cardSoul + shenSha;
    }

}
