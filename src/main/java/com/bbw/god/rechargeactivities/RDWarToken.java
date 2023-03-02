package com.bbw.god.rechargeactivities;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.mall.store.RDStoreGoodsInfo;
import com.bbw.god.rd.item.RDAchievableItem;
import com.bbw.god.rechargeactivities.wartoken.CfgWarTokenLevelAward;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：战令
 *
 * @author lwb
 * date 2021-06-02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RDWarToken extends RDRechargeActivity{
    private Integer tokenLevel;
    private Integer supToken;
    private Integer tokenExp;
    private List<LevelAwardStatus> levelAwards;
    //任务
    private List<RDAchievableItem> tasks;
    private Integer weekTaskExp;//本周任务经验
    private Integer weekTaskMaxExp;//本周任务经验上限
    // 返回商店商品
    private List<RDStoreGoodsInfo> goodsInfoList = null;
    private Integer refreshTimes;
    private RDAchievableItem task;
    /** 基础大奖 */
    private List<RDAward> baseBigAwards;
    /** 进阶大奖 */
    private List<RDAward> supBigAwards;

    public static RDWarToken getInstance(UserWarToken userWarToken){
        RDWarToken rd=new RDWarToken();
        rd.setTokenLevel(userWarToken.getLevel());
        rd.setSupToken(userWarToken.getSupToken());
        rd.setTokenExp(userWarToken.getCurrentLevelExp());
        return rd;
    }

    @Data
    public static class LevelAwardStatus implements Serializable{
        private Integer tokenLevel;
        private Integer status;
        /**
         * 基础奖励
         */
        private List<Award> baseAwards=null;
        /**
         * 进阶奖励
         */
        private List<Award> supAwards=null;

        /**
         * 实例化 仅有战令等级和状态
         * @param tokenLevel
         * @param status
         * @return
         */
        public static LevelAwardStatus getInstance(int tokenLevel, AwardStatus status){
            LevelAwardStatus levelAwardStatus=new LevelAwardStatus();
            levelAwardStatus.setTokenLevel(tokenLevel);
            levelAwardStatus.setStatus(status.getValue());
            return levelAwardStatus;
        }

        /**
         * 完整实例化
         * @param award
         * @param status
         * @return
         */
        public static LevelAwardStatus getInstance(CfgWarTokenLevelAward award, AwardStatus status){
            LevelAwardStatus levelAwardStatus= getInstance(award.getTokenLevel(),status);
            levelAwardStatus.setBaseAwards(award.getBaseAwards());
            levelAwardStatus.setSupAwards(award.getSupAwards());
            return levelAwardStatus;
        }
    }

}
