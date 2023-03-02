package com.bbw.god.gameuser;

import com.bbw.common.DateUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.pool.PlayerPool;
import com.bbw.god.game.config.*;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.monitor.MonitorUser;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.gameuser.dice.UserDiceService;
import com.bbw.god.gameuser.guide.v1.NewerGuideEnum;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.treasure.UserTimeLimitTreasureService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;

import static com.bbw.god.gameuser.treasure.processor.YuQLProcessor.YuQL_DICE_MAX_ADD;

/**
 * 重构说明： 玩家登录设备信息、IP转移到用户明细 去除无用的字段：blood,weekCopper,nwmCopper,goldBought,firstBought,goldForRank,goldBoughtPeriod 登录天数的记录转移到UserLoginInfo
 * 用户限玩转移到UserBehaviorLimit 合并原有的GameUser和ViceUser 神仙附体记录转移到UserGod 道具效果转移到UserTreasureEffect 帮好友打怪（nextBeatMonster）转移到UserMonsterHelp
 * pvp转移到UserPvp 新手任务和进阶任务转移到Task和UserTask 诛仙阵转移到UserZxz 道具使用次数转移到UserTreasureRecord 主线任务转移到Task和UserTask
 * 移除无用的字段：awardValues,remainBuyTime,eventKey,randomValues,own*,specialSoldNum,
 * monsterTimes,helpMonsterTimes,trainingTimes,collectCards,collectConsumeEles, daily* 注意：!!!实体以get、is为前缀的方法返回的数据都会被转化成json,并持久化到Redis
 *
 * @author suhq 2018年9月30日 上午10:17:38
 */
@Slf4j
@Data
public class GameUser {
    private static GameUserRedisUtil userRedis = SpringContextUtil.getBean(GameUserRedisUtil.class);
    private static PlayerPool updatePool = SpringContextUtil.getBean(PlayerPool.class);
    private static MonitorUser monitor = SpringContextUtil.getBean(MonitorUser.class);
    private static UserTimeLimitTreasureService userTimeLimitTreasureService = SpringContextUtil.getBean(UserTimeLimitTreasureService.class);
    private static UserTreasureService userTreasureService = SpringContextUtil.getBean(UserTreasureService.class);
    private static UserDiceService userDiceService = SpringContextUtil.getBean(UserDiceService.class);
    private static UserPayInfoService userPayInfoService = SpringContextUtil.getBean(UserPayInfoService.class);
    private static final int DICE_INC_MAX = 2500000;
    private static final int ELE_INC_MAX = 103 * 10000;
    private static final int GOLD_INC_MAX = 5 * 10000;
    private static final int DIAMOND_INC_MAX = 5 * 10000;
    private static final int EXP_INC_MAX = Integer.MAX_VALUE / 2;
    private static final int COPPER_INC_MAX = 30000 * 10000;
    private static final Long COPPER_DEC_MAX = Long.MAX_VALUE;
    private Long id;// 6位日期(yyMMdd)+4位原始区服ID+5位区服玩家计数器。GameUserRedisUtil.getNewPlayerId
    private Integer serverId;// 合服ID
    /* 玩家资源 */
    private Integer level = 1;// 20;
    private Long experience = 0L;// 187185;
    private Integer gold = 0;
    private Integer diamond = 0;
    private Long copper = 0L;// 铜钱
    private Integer dice = 0;// 体力
    private Integer goldEle = 0;
    private Integer woodEle = 0;
    private Integer waterEle = 0;
    private Integer fireEle = 0;
    private Integer earthEle = 0;// 土

    // 位置信息
    private Location location = Location.init();
    // 游戏状态信息
    private Status status = new Status();
    // 玩家自定义设置
    private Setting setting = new Setting();
    // 角色账号相关信息
    private RoleInfo roleInfo = new RoleInfo();

    public void updateServerId(int sid) {
        this.serverId = sid;
        userRedis.updateServerId(this.id, sid);
        updateMe();
    }

    private void updateMe() {
        monitor.monitor(this);
        updatePool.addToUpdatePool(this.id);
    }

    /**
     * 如果有对Status属性进行修改，需要调用updateStatus方法
     *
     * @return
     */
    public Status getStatus() {
        if (this.status.unload) {
            Status tmp = userRedis.getUserStatus(this.id);
            if (null != tmp) {
                this.status = tmp;
            }
            this.status.unload = false;
        }
        return this.status;
    }

    public void updateStatus() {
        userRedis.updateStatus(this.id, this.status);
        updateMe();
    }

    /**
     * 如果有对Setting属性进行修改，需要调用updateSetting方法
     *
     * @return
     */
    public Setting getSetting() {
        if (this.setting.unload) {
            Setting tmp = userRedis.getUserSetting(this.id);
            if (null != tmp) {
                this.setting = tmp;
            }
            this.setting.unload = false;
        }
        return this.setting;
    }

    public void updateSetting() {
        userRedis.updateSetting(this.id, this.setting);
        updateMe();
    }

    /**
     * 如果有对RoleInfo属性进行修改，需要调用updateRoleInfo方法
     *
     * @return
     */
    public RoleInfo getRoleInfo() {
        if (this.roleInfo.unload) {
            RoleInfo tmp = userRedis.getUserRoleInfo(this.id);
            if (null != tmp) {
                this.roleInfo = tmp;
            }
            this.roleInfo.unload = false;
        }
        return this.roleInfo;
    }

    public void updateRoleInfo() {
        userRedis.updateRoleInfo(this.id, this.roleInfo);
        updateMe();
    }

    public void incLevel(int add) {
        this.level = userRedis.incLevel(this.id, add).intValue();
        updateMe();
    }

    public void incLevel() {
        this.level = userRedis.incLevel(this.id, 1).intValue();
        updateMe();
    }

    public void addExperience(long add) {
        if (notInRange(add, EXP_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "经验值增加异常！增加量:" + add);
        }
        this.experience = userRedis.incExperience(this.id, add);
        updateMe();
    }

    public void addExperienceForGm(long add) {
        this.experience = userRedis.incExperience(this.id, add);
        updateMe();
    }

    /**
     * 增加元宝
     *
     * @param addedGold
     */
    public void addGold(int addedGold) {
        if (notInRange(addedGold, GOLD_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "元宝增加异常！增加量:" + addedGold);
        }
        this.gold = userRedis.incGold(this.id, addedGold).intValue();
        updateMe();
    }

    /**
     * 扣除元宝
     *
     * @param deductedGold
     */
    public void deductGold(int deductedGold) {
        if (notInRange(deductedGold, GOLD_INC_MAX)) {
            log.error(this.id + "," + this.getRoleInfo().getNickname() + "消耗大量元宝！扣除量:" + deductedGold);
        }
        if (this.gold < deductedGold) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "元宝不够！扣除量:" + deductedGold);
        }
        this.gold = userRedis.incGold(this.id, -deductedGold).intValue();
        updateMe();
    }

    /**
     * 增加钻石
     *
     * @param addedDiamond
     */
    public void addDiamond(int addedDiamond) {
        if (notInRange(addedDiamond, DIAMOND_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "钻石增加异常！增加量:" + addedDiamond);
        }
        this.diamond = userRedis.incDiamond(this.id, addedDiamond).intValue();
        updateMe();
    }

    /**
     * 扣除钻石
     *
     * @param deductedDiamond
     */
    public void deductDiamond(int deductedDiamond) {
        if (notInRange(deductedDiamond, DIAMOND_INC_MAX)) {
            log.error(this.id + "," + this.getRoleInfo().getNickname() + "消耗大量钻石！扣除量:" + deductedDiamond);
        }
        if (this.diamond < deductedDiamond) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "钻石不够！扣除量:" + deductedDiamond);
        }
        this.diamond = userRedis.incDiamond(this.id, -deductedDiamond).intValue();
        updateMe();
    }

    /**
     * 增加铜钱
     *
     * @param addedCopper：
     */
    public void addCopper(long addedCopper, WayEnum way) {
        if (way != WayEnum.LT && notInRange(addedCopper, COPPER_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "铜钱增加异常！增加量:" + addedCopper);
        }
        this.copper = userRedis.incCopper(this.id, addedCopper);
        updateMe();
    }

    /**
     * 扣除铜钱
     *
     * @param deductedCopper：
     */
    public void deductCopper(long deductedCopper) {
        if (notInRange(deductedCopper, COPPER_DEC_MAX)) {
            log.error(this.id + "," + this.getRoleInfo().getNickname() + "消耗大量铜钱！扣除量:" + deductedCopper);
        }
        if (this.copper < deductedCopper) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "铜钱不够！扣除量:" + deductedCopper);
        }
        this.copper = userRedis.incCopper(this.id, -deductedCopper);
        updateMe();
    }

    /**
     * 自动增长体力
     *
     * @param addedDice
     */
    public void aotuAddDice(int addedDice, Date update) {
        if (0 == addedDice) {
            userDiceService.updateLastIncTime(id, update);
            return;
        }
        if (notInRange(addedDice, DICE_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "体力增加异常！增加量:" + addedDice);
        }
        userDiceService.updateLastIncTime(id, update);
        this.dice = userRedis.incDice(this.id, addedDice).intValue();
        updateMe();
    }

    /**
     * 增加体力
     *
     * @param addedDice
     */
    public void addDice(int addedDice) {
        // 加值是否有效
        if (notInRange(addedDice, DICE_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "体力增加异常！增加量:" + addedDice);
        }
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        // 超过体力上限不加体力
        if (this.dice >= config.getMaxDice()) {
            return;
        }
        // 体力不超过上限
        if ((this.dice + addedDice) > config.getMaxDice()) {
            addedDice = addedDice - (this.dice + addedDice - config.getMaxDice());
        }
        // 加体力
        this.dice = userRedis.incDice(this.id, addedDice).intValue();
        updateMe();
    }

    /**
     * 兼容旧有体力数据
     */
    // TODO 一个月后去掉
    public void repairDice() {
        int newDice = this.dice * 6;
        int addedDice = newDice - this.dice;
        // 加体力
        this.dice = userRedis.incDice(this.id, addedDice).intValue();
        updateMe();
    }

    public void deductDice(int deductedDice) {
        if (notInRange(deductedDice, DICE_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "体力扣除异常！扣除量:" + deductedDice);
        }
        if (this.dice < deductedDice) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "体力不够！扣除量:" + deductedDice);
        }
        this.dice = userRedis.incDice(this.id, -deductedDice).intValue();
        updateMe();
    }

    public void addEle(int type, int addedEle, WayEnum way) {
        if (way != WayEnum.LT && notInRange(addedEle, ELE_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "元素增加异常！增加量:" + addedEle);
        }
        opEle(type, addedEle);
        updateMe();
    }

    public void deductEle(int type, int deductedEle) {
        if (notInRange(deductedEle, ELE_INC_MAX)) {
            throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "元素扣除异常！扣除量:" + deductedEle);
        }
        opEle(type, -deductedEle);
        updateMe();
    }

    private void opEle(int type, int addedEle) {
        TypeEnum eleType = TypeEnum.fromValue(type);
        if (null == eleType) {
            throw CoderException.fatal("[" + type + "]是无效的元素类型。");
        }
        switch (eleType) {
            case Gold:
                if (this.goldEle < 0) {
                    this.goldEle = 0;
                }
                if (addedEle + this.goldEle < 0) {
                    throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "金元素不够！扣除量:" + -addedEle);
                }
                this.goldEle = userRedis.incGoldEle(this.id, addedEle).intValue();
                break;
            case Wood:
                if (this.woodEle < 0) {
                    this.woodEle = 0;
                }
                if (addedEle + this.woodEle < 0) {
                    throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "木元素不够！扣除量:" + -addedEle);
                }
                this.woodEle = userRedis.incWoodEle(this.id, addedEle).intValue();
                break;
            case Water:
                if (this.waterEle < 0) {
                    this.waterEle = 0;
                }
                if (addedEle + this.waterEle < 0) {
                    throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "水元素不够！扣除量:" + -addedEle);
                }
                this.waterEle = userRedis.incWaterEle(this.id, addedEle).intValue();
                break;
            case Fire:
                if (this.fireEle < 0) {
                    this.fireEle = 0;
                }
                if (addedEle + this.fireEle < 0) {
                    throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "火元素不够！扣除量:" + -addedEle);
                }
                this.fireEle = userRedis.incFireEle(this.id, addedEle).intValue();
                break;
            case Earth:
                if (this.earthEle < 0) {
                    this.earthEle = 0;
                }
                if (addedEle + this.earthEle < 0) {
                    throw CoderException.fatal(this.id + "," + this.getRoleInfo().getNickname() + "土元素不够！扣除量:" + -addedEle);
                }
                this.earthEle = userRedis.incEarthEle(this.id, addedEle).intValue();
                break;
        }
    }

    /**
     * 获取元素数量
     *
     * @param eleType
     * @return
     */
    public int getEleCount(TypeEnum eleType) {
        switch (eleType) {
            case Gold:
                return this.goldEle;
            case Wood:
                return this.woodEle;
            case Water:
                return this.waterEle;
            case Fire:
                return this.fireEle;
            case Earth:
                return this.earthEle;
        }
        throw CoderException.high("不存在的元素类型");
    }

    /**
     * 漫步靴是否开启
     *
     * @return
     */
    public boolean ifMbxOpen() {
        return getSetting().getActiveMbx() != null && getSetting().getActiveMbx() > 0;
    }

    /**
     * 获得可领取的俸禄天数
     *
     * @return
     */
    public int gainSalaryUnawardDays() {
        Date salaryTime = getStatus().getSalaryCopperTime();
        if (salaryTime == null) {
            return 1;
        } else {
            int unAwardDays = DateUtil.getDaysBetween(salaryTime, DateUtil.now());
            int maxSalaryDay = GameUserConfig.bean().getMaxSalaryDay();
            unAwardDays = unAwardDays > maxSalaryDay ? maxSalaryDay : unAwardDays;
            return unAwardDays;
        }
    }

    /**
     * 获取角色当前位置
     *
     * @return
     */
    public CfgCityEntity gainCurCity() {
        return CityTool.getCityByRoadId(getLocation().getPosition());
    }

    /**
     * 玩家移动到当前位置
     *
     * @param position
     * @param direction
     */
    public void moveTo(int position, int direction) {
        this.location.lastPosition = this.location.position;
        this.location.lastDirection = this.location.direction;
        this.location.position = position;
        this.location.direction = direction;
        userRedis.updateLocation(this.id, this.location);
        updateMe();
    }

    public boolean ifMaxDice() {
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        return this.dice >= config.getMaxDice();
    }

    /**
     * 玉麒麟批量使用体力上限检测
     *
     * @param YuQLUseTimes
     * @return
     */
    public boolean ifMaxDice(Integer YuQLUseTimes) {
        //玉麒麟使用一次加60体力
        return this.dice + YuQLUseTimes * TreasureTool.getTreasureConfig().getTreasureEffectYQL() > YuQL_DICE_MAX_ADD;
    }

    private static boolean notInRange(long value, long maxValue) {
        return !(value > 0 && value <= maxValue);
    }

    /**
     * 修正头像ID
     */
    public void repaireHead() {
        int index = this.roleInfo.head - 10000;
        if (index >= 0) {
            // 更新已设置的头像 特殊头像ID由原来的10000多 转至3000多
            this.roleInfo.head = 3000 + index * 10;
        }
        userRedis.updateRoleInfo(this.id, this.roleInfo);
        updateMe();
    }

    @Data
    public static class Location {
        private int position = NewerGuideEnum.START.getPos();// 当前位置
        private int direction = NewerGuideEnum.START.getDir();// 方向
        private int lastPosition = NewerGuideEnum.START.getPos();
        ;// 上一个位置
        private int lastDirection = NewerGuideEnum.START.getDir();

        /**
         * 新的位置信息
         *
         * @return
         */
        public static Location init() {
            return new Location();
        }
    }

    /* 玩家状态 */
    @Data
    public static class Status {
        private Long uid;
        private int satisfaction = 0;// 女娲满意度
        private String ygWin = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";// 野怪情况记录
        private Integer guideStatus = NewerGuideEnum.START.getStep();// 旧新手引导状态
        /** 是否首充(直冲产品不算首充) 不可删除 */
        @Deprecated
        private boolean isFirstBought = true;
        /** 月卡结束时间 不可删除 */
        @Deprecated
        private Date ykEndTime;
        /** 月卡奖励领取时间 不可删除 */
        @Deprecated
        private Date ykAwardTime;
        /** 季卡结束时间 不可删除 */
        @Deprecated
        private Date jkEndTime;
        /** 季卡奖励领取时间 不可删除 */
        @Deprecated
        private Date jkAwardTime;
        /** 速战卡购买时间 不可删除 */
        @Deprecated
        private Date endFightBuyTime;
        private Date salaryCopperTime;// 俸禄领取时间
        /** 体力购买纪录 不可删除 */
        @Deprecated
        private Integer diceBuyTimes = 0;
        /** 体力最近一次购买时间 不可删除 */
        @Deprecated
        private Date diceLastBuyTime;
        /** 体力最近体力增长时间 不可删除 */
        @Deprecated
        private Date diceLastIncTime;
        private Optional<Long> attachingGod = Optional.empty();// 当前依附的usergod的ID
        private boolean growTaskCompleted = false;// 新手任务已经完成
        private Date lastTianlingAwardTime;// 天灵礼包领取时间
        /** 地灵礼包领取时间 */
        private Date lastDilingAwardTime;
        private transient boolean unload = true;// 未载入
        private Integer preWordType;// 当前世界的类型，对应WorldType.enum中的枚举value
        private Integer curWordType = WorldType.NORMAL.getValue();// 当前世界的类型，对应WorldType.enum中的枚举value
        private Boolean isUseZhuJiDan = false;// 是否使用筑基丹

        public Integer getPreWordType() {
            if (null == preWordType) {
                return curWordType;
            }
            return preWordType;
        }

        /**
         * 是否进入到了梦魇世界
         *
         * @return
         */
        public boolean intoNightmareWord() {
            return getCurWordType() == WorldType.NIGHTMARE.getValue();
        }

        /**
         * 是否在轮回世界
         *
         * @return
         */
        public boolean ifInTransmigrateWord() {
            return getCurWordType() == WorldType.TRANSMIGRATION.getValue();
        }

        /**
         * 非封神大陆
         *
         * @return
         */
        public boolean ifNotInFsdlWorld() {
            return getCurWordType() != WorldType.NORMAL.getValue();
        }
    }

    @Data
    public static class Setting {
        /* 配置值 */
        private Long uid;
        private int defaultDeck = 1;// 默认编组
        private String attackMaouInfo;// 魔王三卡牌编组。卡牌ID,卡牌ID,卡牌ID
        // private boolean isHideName = false;// 是否隐藏昵称
        private Integer activeMbx = 0;// 是否开启漫步靴
        private transient boolean unload = true;// 未载入

    }

    @Data
    public static class RoleInfo {
        /* 玩家基本属性 */
        private Long uid;
        private String userName;
        private String nickname;
        private int channelId;// 渠道ID
        private int head = 1;// 头像:1男51女
        private int headIcon = TreasureEnum.HEAD_ICON_Normal.getValue();// 头像框 默认无1000
        private int emoticon = 0;
        private int sex = 1;// 1男2女
        private int country = 10;// 五行属性
        private String enterInvitationCode = "";// 输入的邀请码
        private String myInvitationCode = "";// 我的邀请码
        // private int invitateNum;// 邀请人数
        private Date lastShareTime;// 最近一次分享时间
        private Date regTime;// 注册时间
        private transient boolean unload = true;// 未载入

        public int getHeadIcon() {
            if (userTimeLimitTreasureService.isTimeLimit(headIcon, WayEnum.NONE)) {
                UserTreasure ut = userTreasureService.getUserTreasure(uid, headIcon);
                if (ut != null && ut.gainTotalNum() == 0) {
                    headIcon = TreasureEnum.HEAD_ICON_Normal.getValue();
                }
            }
            return headIcon;
        }

    }
}
