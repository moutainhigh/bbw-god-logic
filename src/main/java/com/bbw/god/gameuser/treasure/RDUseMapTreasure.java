package com.bbw.god.gameuser.treasure;

import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.leadercard.RDLeaderCardInfo;
import com.bbw.god.rd.RDAdvance;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 使用地图法宝（包括开宝箱等）返回的数据
 *
 * @author suhq
 * @date 2019年3月12日 下午10:08:25
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDUseMapTreasure extends RDAdvance implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer useNum = null;// 使用法宝消耗的数量
    private Integer pvpTimes;// 封神台次数
    private Integer updateFull = null;// 建筑是否升满
    private Integer direction = null;// 使用山河社稷图、风火轮的新方向

    private Integer cardId;
    private Integer skill0;
    private Integer skill5;
    private Integer skill10;

    private Integer maouDiceNo;

    private RdDeifyCardInfo deifyCardInfo = null;//封神卡信息
    private List<RdDeifyCardInfo> deifyCardInfos = null;//多个封神卡信息
    private Integer digEmpty = null;


    private RDLeaderCardInfo leaderCardInfo = null;
    private Integer leaderCardFreePoint = null;

    /** 经验丹过期时间 */
    private Date doubleExpRemainTime;


    public void setStrengthenInfo(UserCard uc) {
        this.cardId = uc.getBaseId();
        this.skill0 = uc.gainSkill0();
        this.skill5 = uc.gainSkill5();
        this.skill10 = uc.gainSkill10();
    }
    @Data
    public static class RdDeifyCardInfo{
        private Integer cardId;
        private Integer hv;
        private Integer lv;
        private Integer attackSymbol=0;//攻击符箓
        private Integer defenseSymbol=0;//防御符箓

        public static RdDeifyCardInfo instance(int cardId,int lv,int hv){
            RdDeifyCardInfo rd=new RdDeifyCardInfo();
            rd.setCardId(cardId);
            rd.setLv(lv);
            rd.setHv(hv);
            return rd;
        }
    }
}
