package com.bbw.god.city.mixd.nightmare;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.*;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.dice.UserDiceInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 说明：
 * 玩家的梦魇迷仙洞的数据
 *
 * @author lwb
 * date 2021-05-26
 */
@Data
public class UserNightmareMiXian extends UserSingleObj {
    /**
     * 血量
     */
    private Integer blood = 0;
    /**
     * 是否受到伤害
     */
    private boolean beInjured = false;
    /**
     * 失败
     */
    private boolean toFail = false;
    /**
     * 正在战斗的类型（格子类型）
     */
    public Integer fightingType = 0;
    /**
     * 击杀巡视数量
     */
    private Integer killXunShiNum = 0;
    /**
     * 关卡数据
     */
    private MiXianLevelData levelData;

    /**
     * 宝库生成概率 以10000为100%
     */
    private Integer treasureHousePsb = 0;
    /**
     * 宝库数据
     */
    private MiXianLevelData treasureHouseData;
    /**
     * 在宝库
     */
    private boolean inTreasureHouse = false;
    /**
     * 在宝库是否获取宝藏
     */
    private boolean gainTreasureHourse = false;
    /**
     * 背包
     */
    private List<Award> bag = new ArrayList<>();
    /**
     * 下次初始化时的层
     */
    private int nextInitLevel = 1;
    /**
     * 宝箱随机到元宝奖励
     */
    private boolean findYbBox = false;
    /**
     * 玩家连续闯关
     */
    private Integer continuePassLevel = 1;

    private List<Integer> cardGroup = new ArrayList<>();

    /**
     * 珍贵宝箱未获得技能计数
     */
    private Integer richBoxNotGainSkill = 0;

    /**
     * 初始化每日累计元宝的时间（格式yyyy-MM-dd）
     */
    private String initDailyGoldNumTime;

    /**
     * 每日获得元宝累计数量
     */
    private Integer dailyGoldNum = 0;

    /**
     * 剩余挑战层数
     */
    private Integer remainChallengeLayers;

    /**
     * 最大挑战层数
     */
    private Integer maxChallengeLayers;

    /**
     * 挑战层数最近增长时间
     */
    private Date layersLastIncTime;

    /**
     * 挑战层数下一次增长时间
     */
    private Date layersNextIncTime;
    /**
     * 失败
     *
     * @return
     */
    public boolean ifFail() {
        return this.blood <= 0 || toFail;
    }

    /**
     * 进入新关卡层
     */
    public void intoNewLevel() {
        this.blood = 10;
    }

    /**
     * 扣血
     * 没血时到达的层
     * 当前层	减少层数
     * 1~10	0
     * 11~20	2
     * 21~30	3
     *
     * @param val
     */
    public void incBlood(int val) {
        this.blood += val;
        if (this.blood <= 0) {
            int level = getLevelData().getLevel();
            this.nextInitLevel = 1;
            if (level <= 10) {
                this.nextInitLevel = 1;
            } else if (level <= 20) {
                this.nextInitLevel = 11;
            } else if (level <= 30) {
                this.nextInitLevel = 21;
            }
        }
    }

    /**
     * 实例化
     *
     * @param uid
     * @return
     */
    public static UserNightmareMiXian getInstance(long uid) {
        UserNightmareMiXian userNightmareMiXian = new UserNightmareMiXian();
        userNightmareMiXian.setGameUserId(uid);
        userNightmareMiXian.setId(ID.INSTANCE.nextId());
        // 初始化每日累计元宝的时间（格式yyyy-MM-dd）
        userNightmareMiXian.setInitDailyGoldNumTime(DateUtil.toDateString(new Date()));
        // 初始化挑战层数信息
        CfgNightmareMiXian cfg = NightmareMiXianTool.getCfg();
        Date now = DateUtil.now();
        userNightmareMiXian.setLayersLastIncTime(now);
        userNightmareMiXian.setRemainChallengeLayers(cfg.getMaxChallengeLayers());
        userNightmareMiXian.setMaxChallengeLayers(cfg.getMaxChallengeLayers());
        userNightmareMiXian.setLayersNextIncTime(DateUtil.addSeconds(now, cfg.getIncLayersSpeedSecond()));
        return userNightmareMiXian;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_NIGHTMARE_MIXIAN;
    }

    /**
     * 玩家是否有上次挑战层数增长时间
     *
     * @return true-有，false-没有
     */
    public boolean hadChallengeLayersLastIncTime(){
        return getLayersLastIncTime() != null;
    }

    /**
     * 将奖励加入到背包：必须是具体的奖励 不能是随机XXX的
     *
     * @param awards
     */
    public void addAwardToBag(List<Award> awards) {
        if (ListUtil.isEmpty(awards)) {
            return;
        }
        for (Award award : awards) {
            Optional<Award> optional = bag.stream().filter(p -> p.getItem() == award.getItem() && p.getAwardId().equals(award.getAwardId())).findFirst();
            if (optional.isPresent()) {
                Award award1 = optional.get();
                award1.setGainDate(new Date());
                award1.setNum(award1.getNum() + award.getNum());
            } else {
                Award clone = CloneUtil.clone(award);
                clone.setGainDate(new Date());
                bag.add(clone);
            }
        }
    }

    /**
     * 检查背包中道具的 数量
     *
     * @param treasureId
     * @return
     */
    public int awardNumInBag(int treasureId) {
        if (treasureId == 0) {
            return 0;
        }
        Optional<Award> awardsOp = bag.stream().filter(p -> p.getAwardId() == treasureId).findFirst();
        if (awardsOp.isPresent()) {
            return awardsOp.get().getNum();
        }
        return 0;
    }

    /**
     * 拥有关卡钥匙
     *
     * @return
     */
    public boolean ifHasGateKey() {
        return awardNumInBag(TreasureEnum.MXD_LEVEL_KEY.getValue()) > 0;
    }

    /**
     * 获取位置对应数据
     *
     * @param pos
     * @return
     */
    public MiXianLevelData.PosData getPostData(int pos) {
        if (isInTreasureHouse()) {
            return getTreasureHouseData().getPosData(pos);
        }
        return getLevelData().getPosData(pos);
    }

    /**
     * 更新位置信息
     *
     * @param pos
     * @param passPath
     */
    public void updatePosData(int pos, String passPath) {
        if (isInTreasureHouse()) {
            this.getTreasureHouseData().setPos(pos);
        } else {
            this.getLevelData().setPos(pos);
            showCurrentPos(pos);
            if (StrUtil.isBlank(passPath)) {
                return;
            }
            String[] split = passPath.split(";");
            for (String posStr : split) {
                int pass = Integer.parseInt(posStr);
                showCurrentPos(pass);
            }
        }
    }

    public void addTreasureHousePsb(int val) {
        this.treasureHousePsb += val;
    }

    /**
     * 把当前位置 改变为空白格子
     */
    public void takeCurrentPosToEmptyType() {
        if (isInTreasureHouse()) {
            this.getTreasureHouseData().takePosToEmpty(this.getTreasureHouseData().getPos());
        } else {
            this.getLevelData().takePosToEmpty(this.getLevelData().getPos());
        }
    }

    /**
     * 显示格子:
     * 宝库内默认都是显示的
     *
     * @param pos
     */
    public void showCurrentPos(int pos) {
        if (!isInTreasureHouse()) {
            this.getLevelData().showPos(pos);
        }
    }

    /**
     * 随机到烛龙巡使
     * 当玩家累计击杀200名巡使后，接下来每次遇到巡使，有0.5%几率变为特殊巡使-烛龙
     * 当玩家累计击杀400名巡使后，接下来每次遇到巡使，有1%几率变为特殊巡使-烛龙
     * 当玩家累计击杀600名巡使后，接下来每次遇到巡使，有3%几率变为特殊巡使-烛龙
     *
     * @return
     */
    public boolean hitZhuLongXunShi() {
        int hit = 0;
        if (killXunShiNum >= 600) {
            hit = 300;
        } else if (killXunShiNum >= 400) {
            hit = 100;
        } else if (killXunShiNum >= 200) {
            hit = 50;
        }
        if (hit == 0) {
            return false;
        }
        return PowerRandom.hitProbability(hit, 10000);
    }

    /**
     * 获取当前所在层
     *
     * @return
     */
    @JSONField(serialize = false)
    public Integer getCurrentLevel() {
        return levelData.getLevel();
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    @JSONField(serialize = false)
    public Integer getPos() {
        if (inTreasureHouse) {
            return treasureHouseData.getPos();
        }
        return levelData.getPos();
    }

    /**
     * 是否在宝库
     *
     * @return
     */
    public boolean isInTreasureHouse() {
        if (treasureHouseData == null) {
            return false;
        }
        return inTreasureHouse;
    }

    /**
     * 是否击败了当前层的所有巡使
     *
     * @param
     * @return
     */
    public boolean ifKillAllXunShi() {
        for (MiXianLevelData.PosData posData : getLevelData().getPosDatas()) {
            if (posData.ifXunShiPos()) {
                return false;
            }
        }
        return true;
    }

    public void setNextInitLevel(int nextInitLevel) {
        this.nextInitLevel = nextInitLevel;
        if (this.nextInitLevel <= 0 || this.nextInitLevel > 30) {
            this.nextInitLevel = 1;
        }
    }
}