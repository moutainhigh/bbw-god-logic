package com.bbw.god.login;

import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.businessgang.digfortreasure.RDDigTreasureInfo;
import com.bbw.god.gameuser.businessgang.luckybeast.RDLuckyBeastInfo;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.card.equipment.rd.RdComprehendInfo;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.gameuser.yuxg.rd.RDYuXGFuTu;
import com.bbw.god.rd.RDCommon.RDSpecail;
import com.bbw.god.server.god.ServerGod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGameUser extends RDNoticeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 后续请求客户端请求头需传递的token */
    private String token = null;
    /** token有效期 */
    private Long tokenExpiredTime = null;
    /** 请求访问令牌参数 */
    private List<String> eids = null;

    private Long guId = null;
    private Long serverTime = null;// 服务器时间
    private String shortServerName = null;// 服务器名简称
    //    private Integer needGoldForAttacks = null;// 大魔王5倍攻击需要的元宝
    private Integer sex = null;
    private String nickname = null;
    private Integer head = null;
    private Integer headIcon = null;// 头像框
    private Integer emoticon = null;//表情包
    private Integer level = null;
    private Long experience = null;
    private Integer expRate = null;// 等级分母经验
    private Long copper = null;
    private Integer gold = null;
    /** 钻石 */
    private Integer diamond = null;
    private Integer dices = null;
    private Integer position = null;
    private Integer direction = null;
    private Integer goldEle = null;
    private Integer woodEle = null;
    private Integer waterEle = null;
    private Integer fireEle = null;
    private Integer earthEle = null;
    private Integer defaultDeck = null;// 默认卡组
    //    private Integer ableRefreshDailyTask = null;// 是否可以更新每日任务
    private String myInvitationCode = null;// 我的邀请码
    private Integer useShoe = null;// 是否使用漫步靴
    private Integer preWorldType = null;// 前一次的世界类型
    private Integer curWorldType = null;// 当前所处的世界类型
    /** 各主城属性（轮回世界才有传） */
    private List<Integer> mainCityDefenderTypes;
    private Integer mbxRemainForcross = null;// 是否选择方向
    private Integer guideStatus = null;// 新手引导状态
    private Integer isPassNewerGuide = null;//是否通过新手引导
    private Long nextBeatTime = null;// 过多久可以帮好友打怪
    private Integer pvpTimes = null;// 封神台可挑战次数
    private Integer god = null;// 附体的神仙
    private Integer godRemainStep = null;// 神仙剩余步数
    private Long godRemainTime = null;// 神仙剩余时间
    private Integer godExt = null;//神仙额外参数
    private List<RDEffectTreasure> usedTreasures = null;// 使用中的法宝的信息
    private List<RDGod> gods = null;// 地图上的神仙
    private List<RDSpecail> specials = null;// 拥有的特产
    private List<RDTreasure> userTreasures = null;// 玩家拥有的法宝
    private List<RDCard> cards = null;// 玩家拥有的卡牌
    ///	private List<List<Integer>> decks = null;// 编组卡牌\
    private List<RDCardGroup> decks = null;// 编组卡牌
    private List<RDChengc> manors = null;
    private List<RDChengc> nightmareManors = null;// 梦魇世界城池
    //    private Integer awardTaskNum = null;// 可领奖励成就数
    private List<RDMallPrice> mallPrices = null;
    private Integer firstTodayLogin = 0;// 是否今日首登
    private Long lastDiceIncTime = null;// 最近一次体力增长时间
    private RDNoticeInfo noticeInfo = null;// 通知信息

    // 临时
    private Integer ygBoxOpenTimes = null;// 野怪开箱子次数
    private Integer bxOpenTimes = null;// 宝箱打开次数
    private List<Integer> cardPoolOpenTimes = null;// 卡池打开次数，依次金、木、水、火、土、万物

    private List<Integer> listHeadIcons = null;// 玩家拥有的所有头像框
    private List<Integer> listHeads = null;// 玩家拥有的所有头像

    private List<String> accountTagsList = null;// 该账号拥有的所有账号标签
    private Integer isBacker = null;// 是否显示新功能
    private Integer bagLimit = null;// 背包格子
    private Integer bagBuyTimes = 0;// 背包格子购买次数
    /** 妖族信息 */
    private List<RDYaoZu> yaoZus = new ArrayList<>();
    /** 是否通关妖族 */
    private boolean passYaoZu = false;
    /** 轮回挑战成功次数 */
    private Integer transmigrationSuccessNum;
    /** 玉虚宫符图信息 */
    private List<RDYuXGFuTu> fuTus;
    /** 法坛总等级 */
    private Integer faTanTotalLv;
    /** 招财兽位置 */
    private List<RDLuckyBeastPos> luckyBeasts = new ArrayList<>();
    /** 挖宝信息 */
    private RDDigTreasureInfo digTreasureInfo = null;
    /** 至宝信息 */
    private List<RdCardZhiBao> cardZhiBaos = new ArrayList<>();

    /**
     * 使用中的法宝的信息
     *
     * @author suhq
     * @date 2019年2月26日 下午5:04:18
     */
    @Data
    public static class RDEffectTreasure implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer treasureId;
        private Integer steps;
        //最近使用时间
        private Date effectTime;

        public RDEffectTreasure(UserTreasureEffect utEffect) {
            this.treasureId = utEffect.getBaseId();
            this.steps = utEffect.getRemainEffect();
            this.effectTime = utEffect.getEffectTime();
        }

    }

    /**
     * 地图上的神仙
     *
     * @author suhq
     * @date 2019年2月26日 下午5:05:52
     */
    @Data
    public static class RDGod implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer position;

        public RDGod(ServerGod sGod) {
            this.id = sGod.getGodId();
            // 兼容
            if (this.id == 520) {
                this.id = 510;
            }
            this.position = sGod.getPosition();
        }

    }

    /**
     * 地图上的妖族
     */
    @Data
    public static class RDYaoZu implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer position;

        public RDYaoZu(UserYaoZuInfo yaoZuInfo) {
            this.id = yaoZuInfo.getBaseId();
            this.position = yaoZuInfo.getPosition();
        }

    }

    /**
     * 地图上的招财兽
     */
    @Data
    public static class RDLuckyBeastPos implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer position;

        public RDLuckyBeastPos(RDLuckyBeastInfo rdLuckyBeastInfo) {
            this.id = rdLuckyBeastInfo.getLuckyBeastId();
            this.position = rdLuckyBeastInfo.getPosition();
        }

    }

    /**
     * 玩家法宝
     *
     * @author suhq
     * @date 2019年2月26日 下午5:10:54
     */
    @Data
    public static class RDTreasure implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer baseId;
        private Integer quantity;
        private Integer type = null;
        private Integer star;

        public RDTreasure(UserTreasure uTreasure) {
            this.baseId = uTreasure.getBaseId();
            this.quantity = uTreasure.gainTotalNum();
            CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(this.baseId);
            this.star = treasureEntity.getStar();
            if (treasureEntity != null && treasureEntity.ifSkillScroll()) {
                this.type = treasureEntity.getType();
            }
        }

    }

    /**
     * 玩家至宝
     *
     * @author: huanghb
     * @date: 2022/9/21 9:31
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RdCardZhiBao implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 唯一 */
        private Long zhiBaoDataId;
        /** 卡牌id */
        private Integer cardId;
        /** 装备ID */
        private Integer zhiBaoId;
        /** 五行属性 */
        private Integer property;
        /** 加成 */
        private List<CardEquipmentAddition> additions = new ArrayList<>();
        /** 技能组 */
        private Integer[] skillGroup;
        /** 是否装上 0表示未装备 1表示装备 */
        private Integer putedOn = 0;

        public static RdCardZhiBao instance(UserCardZhiBao userCardZhiBao) {
            RdCardZhiBao info = new RdCardZhiBao();
            info.setZhiBaoId(userCardZhiBao.getZhiBaoId());
            info.setZhiBaoDataId(userCardZhiBao.getId());
            info.setCardId(userCardZhiBao.getCardId());
            info.setProperty(userCardZhiBao.getProperty());
            info.setAdditions(userCardZhiBao.gainAdditions());
            info.setSkillGroup(userCardZhiBao.getSkillGroup());
            info.setPutedOn(userCardZhiBao.ifPutOn());
            return info;

        }
    }

    /**
     * 玩家仙诀
     *
     * @author: huanghb
     * @date: 2022/9/21 9:31
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RdCardXianJue implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 仙诀数据id */
        private Long xianJueDataId;
        /** 仙诀类型 */
        private Integer xianJueType;
        /** 卡牌id */
        private Integer cardId;
        /** 强化等级 */
        private Integer level = 0;
        /** 品质 */
        private Integer quality = QualityEnum.NONE.getValue();
        /** 星图进度 */
        private Integer starMapProgress = 0;
        /** 参悟值 */
        private List<RdComprehendInfo> comprehendValue;

        public static RdCardXianJue instance(UserCardXianJue userCardXianJue) {
            RdCardXianJue info = new RdCardXianJue();
            info.setCardId(userCardXianJue.getCardId());
            info.setXianJueDataId(userCardXianJue.getId());
            info.setXianJueType(userCardXianJue.getXianJueType());
            info.setLevel(userCardXianJue.getLevel());
            info.setQuality(userCardXianJue.getQuality());
            info.setStarMapProgress(userCardXianJue.getStarMapProgress());
            //获得参悟值信息
            List<RdComprehendInfo> rdCardXianJueInfos = userCardXianJue.gainAdditions()
                    .stream().map(RdComprehendInfo::instance).collect(Collectors.toList());

            info.setComprehendValue(rdCardXianJueInfos);
            return info;

        }
    }

    /**
     * 玩家卡牌
     *
     * @author suhq
     * @date 2019年2月26日 下午5:23:59
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDCard implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long dataId = null;
        private Integer baseId = null;
        private Integer level = null;
        private Integer soul = null;
        private Integer hierarchy = null;
        private Long experience = null;
        private Integer skill0;
        private Integer skill5;
        private Integer skill10;
        private Integer attackSymbol = 0;// 攻击符箓
        private Integer defenceSymbol = 0;// 防御符箓
        private Integer isUseSkillScroll;// 是否使用技能卷轴
        private Integer usst = 0;// 使用技能卷轴的次数
        private Integer sex;
        private Integer star;
        private Integer property;
        private Integer fashion;
        private Integer atk;//总攻击
        private Integer hp;//总防御
        /** 至宝 */
        private List<RdCardZhiBao> zhiBaos;
        /** 仙诀 */
        private List<RdCardXianJue> xianJues;

        public static RDCard instance(UserCard uc) {
            RDCard rd = new RDCard();
            rd.setDataId(uc.getId());
            rd.setBaseId(uc.getBaseId());
            rd.setLevel(uc.getLevel());
            rd.setSoul(uc.getLingshi());
            rd.setHierarchy(uc.getHierarchy());
            long exp = uc.getExperience() - CardExpTool.getExpByLevel(uc.gainCard(), uc.getLevel());
            rd.setExperience(exp);
            rd.setSkill0(uc.gainSkill0());
            rd.setSkill5(uc.gainSkill5());
            rd.setSkill10(uc.gainSkill10());
            rd.setDefenceSymbol(uc.gainDefenceSymbol());
            rd.setAttackSymbol(uc.gainAttackSymbol());
            rd.setIsUseSkillScroll(uc.ifUseSkillScroll() ? 1 : 0);
            int usst = null == uc.getStrengthenInfo() ? 0 : uc.getStrengthenInfo().gainUseSkillScrollTimes();
            rd.setUsst(usst);
            return rd;

        }

        public static RDCard instanceForPvpRoboter(UserCard uc) {
            RDCard rd = new RDCard();
            rd.setBaseId(uc.getBaseId());
            rd.setLevel(uc.getLevel());
            rd.setHierarchy(uc.getHierarchy());
            CfgCardEntity cardEntity = CardTool.getCardById(uc.getBaseId());
            rd.setSkill0(cardEntity.getZeroSkill());
            rd.setSkill5(cardEntity.getFiveSkill());
            rd.setSkill10(cardEntity.getTenSkill());
            rd.setIsUseSkillScroll(0);
            return rd;
        }

        public static RDCard instance(UserLeaderCard leaderCard) {
            RDCard card = new RDCard();
            card.setBaseId(leaderCard.getBaseId());
            card.setHierarchy(leaderCard.getHv());
            card.setLevel(leaderCard.getLv());
            card.setSex(leaderCard.getSex());
            card.setProperty(leaderCard.getProperty());
            card.setStar(leaderCard.getStar());
            card.setFashion(leaderCard.getFashion());
            card.setHp(leaderCard.settleTotalHpWithEquip());
            card.setAtk(leaderCard.settleTotalAtkWithEquip());
            card.setIsUseSkillScroll(1);
            return card;
        }
    }

    /**
     * 玩家城池
     *
     * @author suhq
     * @date 2019年2月26日 下午5:25:47
     */
    @Data
    public static class RDChengc implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer updateFull;
        private Integer hierarchy;
        /** 法坛 */
        private Integer ft;

        public RDChengc(UserCity uCity) {
            this.id = uCity.getBaseId();
            this.updateFull = uCity.ifUpdateFull() ? 1 : 0;
            this.hierarchy = uCity.getHierarchy();
            this.ft = uCity.getFt();
        }

    }

    /**
     * 商城物品价格
     *
     * @author suhq
     * @date 2019年2月26日 下午5:32:34
     */
    @Data
    public static class RDMallPrice implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer price;

        public RDMallPrice(int id, int price) {
            this.id = id;
            this.price = price;
        }

    }

    @Data
    public static class RDCardGroup implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long cardGroupId;
        private String name;//卡组名称
        private int deck;//卡组索引
        private List<Integer> cardIds;//卡牌
        private Integer fuCeId = 0;
    }
}
