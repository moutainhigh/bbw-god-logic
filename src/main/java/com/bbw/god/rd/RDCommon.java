package com.bbw.god.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.special.UserSpecial;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCommon extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    // 资源信息
    private Integer addedGold = null;// 元宝
    private List<RDResAddInfo> addGolds = null;
    /** 钻石 */
    private Integer addedDiamond = null;
    private List<RDResAddInfo> addDiamonds = null;
    /** 铜钱 */
    private Long addedCopper = null;// 铜钱
    private List<RDResAddInfo> addCoppers = null;
    private Integer addedDices = null;// 体力
    private Integer deductedDices = null;//体力扣除
    private RDLevelAward updateAward = null;// 升级奖励
    private Integer cszCopper = null;// 财神珠
    private Integer addedGoldEle = null;// 金元素
    private Integer addedWoodEle = null;// 木元素
    private Integer addedWaterEle = null;// 水元素
    private Integer addedFireEle = null;// 火元素
    private Integer addedEarthEle = null;// 土元素
    private List<RDCardInfo> cards = null;// 卡牌
    private List<RDTreasureInfo> treasures = null;// 法宝
    private List<RDCardToLinshi> cardToLinshis = null;
    private List<RDTreasureInfo> deductedTreasures = null;// 扣除法宝
    private List<RDSpecail> specials = null;
    private List<Long> reduceSpecial = null;//减少的特产的dataId
    private Integer cszRemain = null;// 财神珠剩余步数
    private Integer lbRemain = null;// 落宝金钱剩余步数
    private List<String> redNotices = new ArrayList<>();
    //战令礼包使用信息
    private Integer giftUseTimes = null;
    // 神仙附体
    private Integer godRemainCell = null;// 附体剩余步数
    private Integer godRemainTime = null;// 附体剩余时间
    private Integer attachedGod = null;// 附体神仙

    // 玩家升级信息
    private Integer expRate = null;
    private Long guExp = null;
    private Long addedGuExp = null;
    private List<RDResAddInfo> addedGuExps = null;
    private Integer guLevel = null;

    // 卡牌升级信息
    private Integer card = null;
    private Integer cardLevel = null;
    private Long cardExp = null;

    // 通知用
    private List<RDAchievementId> taskIds = null;// 达成的成就
    private Integer dailyTaskStatus = null;// 每日任务达成通知
    private Integer growTaskStatus = null;// 新手进阶任务通知
    private Integer mainTaskStatus = null;// 主线任务达成通知
    private Integer dailyHeroBackTaskStatus = null;// 英雄回归每日任务达成通知
    // private Integer activeZLLB = null;// 激活的助力礼包
    //活跃度
    private Integer addedHY = null;
    // 巅峰值
    private Integer addedDFZ = null;
    private Integer addAloneMaouFreeTimes = null;
    private Integer freeTimes = null;//野怪宝箱免费次数

    private List<RDCardLingshi> deductedCardLingshi = null;// 扣除卡牌灵石

    public void addAloneMaouFreeTimes(int addedNum) {
        this.addAloneMaouFreeTimes = this.addAloneMaouFreeTimes == null ?
                addedNum : this.addAloneMaouFreeTimes + addedNum;
    }

    public void addDailyHy(int addedNum) {
        this.addedHY = this.addedHY == null ? addedNum : this.addedHY + addedNum;
    }

    public void addDailyDfz(int addedNum) {
        this.addedDFZ = this.addedDFZ == null ? addedNum : this.addedDFZ + addedNum;
    }

    public void addDeductedTreasures(RDTreasureInfo info) {
        if (this.deductedTreasures == null) {
            this.deductedTreasures = new ArrayList<RDCommon.RDTreasureInfo>();
        }
        this.deductedTreasures.add(info);
    }

    public void addCopper(long addedNum) {
        this.addedCopper = this.addedCopper == null ? addedNum : this.addedCopper + addedNum;
    }

    public void setAddCoppers(List<RDResAddInfo> addCoppers) {
        if (ListUtil.isEmpty(addCoppers)) {
            return;
        }
        if (this.addCoppers == null) {
            this.addCoppers = new ArrayList<RDCommon.RDResAddInfo>();
        }
        this.addCoppers.addAll(addCoppers);
    }

    public void addCSZCopper(int addedNum) {
        this.cszCopper = this.cszCopper == null ? addedNum : this.cszCopper + addedNum;
    }

    public void addGold(int addedNum) {
        this.addedGold = this.addedGold == null ? addedNum : this.addedGold + addedNum;
    }

    /**
     * 添加钻石
     *
     * @param addedDiamond
     */
    public void addDiamond(int addedDiamond) {
        this.addedDiamond = this.addedDiamond == null ? addedDiamond : this.addedDiamond + addedDiamond;
    }

    public void addDice(int addedNum) {
        this.addedDices = this.addedDices == null ? addedNum : this.addedDices + addedNum;
    }

    public void addEle(int type, int addedNum) {
        TypeEnum typeEnum = TypeEnum.fromValue(type);
        switch (typeEnum) {
            case Gold:
                this.addedGoldEle = this.addedGoldEle == null ? addedNum : this.addedGoldEle + addedNum;
                return;
            case Wood:
                this.addedWoodEle = this.addedWoodEle == null ? addedNum : this.addedWoodEle + addedNum;
                return;
            case Water:
                this.addedWaterEle = this.addedWaterEle == null ? addedNum : this.addedWaterEle + addedNum;
                break;
            case Fire:
                this.addedFireEle = this.addedFireEle == null ? addedNum : this.addedFireEle + addedNum;
                return;
            case Earth:
                this.addedEarthEle = this.addedEarthEle == null ? addedNum : this.addedEarthEle + addedNum;
                return;
            default:
                return;
        }
    }

    public void addCard(RDCardInfo rdCardInfo) {
        if (this.cards == null) {
            this.cards = new ArrayList<>();
        }
        this.cards.add(rdCardInfo);
    }

    public void addTreasure(RDTreasureInfo rdTreasureInfo) {
        if (this.treasures == null) {
            this.treasures = new ArrayList<>();
        }
        /*RDTreasureInfo rdInfo = this.treasures.stream().filter(tmp -> tmp.getId().equals(rdTreasureInfo.getId())).findFirst().orElse(null);
        if (rdInfo == null) {
            this.treasures.add(rdTreasureInfo);
        } else {
            rdInfo.setNum(rdInfo.getNum() + rdTreasureInfo.getNum());
        }*/
        this.treasures.add(rdTreasureInfo);
    }

    public void addDeductTreasure(RDTreasureInfo rdTreasureInfo) {
        if (this.treasures == null) {
            this.treasures = new ArrayList<>();
        }
        RDTreasureInfo rdInfo = this.deductedTreasures.stream().filter(tmp -> tmp.getId().equals(rdTreasureInfo.getId())).findFirst().orElse(null);
        if (rdInfo == null) {
            this.deductedTreasures.add(rdTreasureInfo);
        } else {
            rdInfo.setNum(rdInfo.getNum() + rdTreasureInfo.getNum());
        }

    }

    public void addSpecial(UserSpecial special) {
        if (this.specials == null) {
            this.specials = new ArrayList<>();
        }
        this.specials.add(RDSpecail.fromUserSpecail(special));
    }

    /**
     * 添加新成就
     *
     * @param achievementId
     */
    public void addAchievement(int achievementId) {
        if (this.taskIds == null) {
            this.taskIds = new ArrayList<>();
        }
        this.taskIds.add(new RDAchievementId(achievementId));
    }

    public RDLevelAward gainLevelAward() {
        if (this.updateAward == null) {
            this.updateAward = new RDLevelAward();
        }
        return this.updateAward;
    }

    public void setGodAttachInfo(int baseGodId) {
        setAttachedGod(baseGodId);
        GodEnum godEnum = GodEnum.fromValue(baseGodId);
        if ((godEnum.getType() == 10 || godEnum.getType() == 30) && getGodRemainCell() == null) {
            setGodRemainCell(godEnum.getEffect());
        } else if (godEnum.getType() == 20 && getGodRemainTime() == null) {
            setGodRemainTime(godEnum.getEffect() * 1000);
        }
    }

    public void addCardToLinshi(int treasureId, int cardId) {
        if (cardToLinshis == null) {
            cardToLinshis = new ArrayList<>();
        }
        cardToLinshis.add(RDCardToLinshi.instance(treasureId, cardId));
    }

    /**
     * 卡牌获得信息
     *
     * @author suhq
     * @date 2019年3月22日 上午9:41:07
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDCardInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long dataId = null;
        private Integer card = null;
        private Integer addSoul = null;
        private Integer soulNum = null;

        public RDCardInfo() {
        }

        public RDCardInfo(int cardId, int addSoul, int soulNum) {
            this.card = cardId;
            this.addSoul = addSoul;
            this.soulNum = soulNum;
        }

        public static int makeAddSoul(int cfgCardId) {
            return cfgCardId + 1000;
        }

    }

    /**
     * num为0，表示获得的法宝立即生效
     *
     * @author suhq
     * @date 2018年10月22日 下午3:56:17
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDTreasureInfo implements Serializable {

        private static final long serialVersionUID = 1L;
        private Integer id = null;
        /** 目前符图道具需要使用 */
        private Long dataId = null;
        private Integer num = null;
        private Integer type = null;
        /** 是否展示 */
        private Integer isShow;
        /** 需要展示数量 */
        private Integer needShowNum;

        public RDTreasureInfo(Integer id, Integer num, Integer type) {
            this.id = id;
            this.num = num;
            this.type = type;
        }

        public RDTreasureInfo(Integer id, Integer num, Integer type, Integer isShow, Integer needShowNum) {
            this.id = id;
            this.num = num;
            this.type = type;
            this.isShow = isShow;
            this.needShowNum = needShowNum;
        }

        public RDTreasureInfo(Integer id, Long dataId, Integer num, Integer type) {
            this.id = id;
            this.dataId = dataId;
            this.num = num;
            this.type = type;
        }

        public RDTreasureInfo(Integer id, Integer num) {
            this.id = id;
            this.num = num;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDLevelAward implements Serializable {

        private static final long serialVersionUID = 1L;
        private Integer addedGold = null;
        private Integer addedDices = null;
    }

    @Data
    @AllArgsConstructor
    public static class RDAchievementId implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer taskId;

    }

    @Data
    @AllArgsConstructor
    public static class RDResAddInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer wayType;
        private Long value;
    }

    /**
     * 特产信息
     *
     * @author lwb
     */
    @Data
    public static class RDSpecail implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer baseId;//基础ID
        private Long dataId;//存储ID
        private Integer type;//0 为未上锁特产，1为上锁特产

        public static RDSpecail fromUserSpecail(UserSpecial special) {
            RDSpecail rdSpecail = new RDSpecail();
            rdSpecail.setBaseId(special.getBaseId());
            rdSpecail.setDataId(special.getId());
            rdSpecail.setType(0);//0 为未上锁特产，1为上锁特产
            return rdSpecail;
        }

        public static RDSpecail instancePocketSpecail(UserSpecial special) {
            RDSpecail rd = fromUserSpecail(special);
            rd.setType(1);
            return rd;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDCardToLinshi implements Serializable {

        private static final long serialVersionUID = 1L;
        private Integer treasureId = null;
        private Integer cardId = null;
        private Integer num = 1;

        public static RDCardToLinshi instance(int treasureId, int cardId) {
            RDCardToLinshi rd = new RDCardToLinshi();
            rd.setCardId(cardId);
            rd.setTreasureId(treasureId);
            return rd;
        }

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDCardLingshi implements Serializable {
        private static final long serialVersionUID = -2818123046985843266L;
        private Integer cardId;
        private Integer num;

        public static RDCardLingshi instance(int cardId, int num) {
            RDCardLingshi rd = new RDCardLingshi();
            rd.setCardId(cardId);
            rd.setNum(num);
            return rd;
        }

    }
}
