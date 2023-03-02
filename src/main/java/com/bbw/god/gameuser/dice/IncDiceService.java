package com.bbw.god.gameuser.dice;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.dice.EPDiceFull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-28 21:22
 */
@Slf4j
@Service
public class IncDiceService {
    @Autowired
    private GameUserService userService;
    @Autowired
    private UserDiceService userDiceService;

    /**
     * 体力上限
     */
    public static int maxDiceLimitByLevel(int level) {
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        return level / 2 * config.getDiceOneShake() + config.getBaseDiceIncLimit();
    }

    /**
     * <pre>
     * 恢复体力。恢复的体力受等级限制。
     * 正常情况下，体力每15分钟恢复1点。
     * 仙长作用下，每10分钟恢复1点。
     * 衰神作用下，每20分钟恢复1点。
     * </pre>
     *
     * @param user
     * @return 返回恢复后的体力值
     */
    public int limitIncDice(GameUser user) {
        // 体力已经达到上限，结算完成，结算结果是不增长，记录结算时间。
        if (user.getDice().intValue() >= maxDiceLimitByLevel(user.getLevel())) {
            UserDiceInfo userDiceInfo = userDiceService.getUserDiceInfo(user.getId());
            int addDice = settleAddDice(userDiceInfo.getDiceLastIncTime(), user.getId());
            if (addDice > 0) {
                ResEventPublisher.pubDiceFullEvent(EPDiceFull.instance(user.getId(), addDice));
                userDiceInfo.setDiceLastIncTime(DateUtil.now());
                userService.updateItem(userDiceInfo);
            }
            return user.getDice().intValue();
        }
        Date now = DateUtil.now();
        // 上一次体力恢复时间
        Date lastIncDiceTime = getDiceLastIncTime(user);
        long seconds = DateUtil.getSecondsBetween(lastIncDiceTime, now);
        // 最快也得离上一次恢复时间的DiceSpeedEnum.FAST.getSeconds()秒才需要恢复体力，未到结算时间，不结算。
        if (seconds + 2 < DiceSpeedEnum.FAST.getSeconds()) {// 离上一次结算时间还不到体力恢复最短时间（fast秒），误差2秒。
            return user.getDice();
        }
        // ----------------------开始结算-----------------------------
        // 下一次结算最快可能的时间（离上一次结算DiceSpeedEnum.FAST.getSeconds()秒后）
        Date maybeFirstIncDiceTime = DateUtil.addSeconds(lastIncDiceTime, DiceSpeedEnum.FAST.getSeconds());
        Optional<UserGod> effectGod = getEffectUserGod(user.getId(), maybeFirstIncDiceTime);
        // 最后一次增长体力后，没有神仙附体，则每15分钟增加1个体力
        if (!effectGod.isPresent()) {
            return normalInc(user);
        }
        // 最后一次增长体力后，有神仙附体
        return godInc(user, effectGod.get());
    }

    public int settleAddDice(Date lastIncDiceTime, long uid) {
        Date now = DateUtil.now();
        // 上一次体力恢复时间
        if (lastIncDiceTime == null) {
            lastIncDiceTime = DateUtil.addDays(DateUtil.now(), -1);
        }
        long seconds = DateUtil.getSecondsBetween(lastIncDiceTime, now);
        // 最快也得离上一次恢复时间的DiceSpeedEnum.FAST.getSeconds()秒才需要恢复体力，未到结算时间，不结算。
        if (seconds + 2 < DiceSpeedEnum.FAST.getSeconds()) {// 离上一次结算时间还不到体力恢复最短时间（fast秒），误差2秒。
            return 0;
        }
        // ----------------------开始结算-----------------------------
        // 下一次结算最快可能的时间（离上一次结算DiceSpeedEnum.FAST.getSeconds()秒后）
        Date maybeFirstIncDiceTime = DateUtil.addSeconds(lastIncDiceTime, DiceSpeedEnum.FAST.getSeconds());
        Optional<UserGod> effectGodOp = getEffectUserGod(uid, maybeFirstIncDiceTime);
        if (effectGodOp.isPresent()) {
            UserGod god = effectGodOp.get();
            Date godLeveTime = god.getAttachEndTime();
            // 到现在神仙还没离开,按照神仙增长速度计算
            if (godLeveTime.after(now)) {
                Date end = now;
                int godSpeed = getSpeedByGod(god);
                return getAddDicNum(godSpeed, lastIncDiceTime, end);
            }
            // 如果神仙已经离开，先计算神仙离开前到增长，后续再按照正常增长计算
            Date end = godLeveTime;
            int godSpeed = getSpeedByGod(god);
            return getAddDicNum(godSpeed, lastIncDiceTime, end) + getAddDicNum(DiceSpeedEnum.NORMAL.getSeconds(), lastIncDiceTime, now);
        } else {
            return getAddDicNum(DiceSpeedEnum.NORMAL.getSeconds(), lastIncDiceTime, now);
        }
    }

    private int godInc(GameUser user, UserGod effectGod) {
        Date begin = getDiceLastIncTime(user);
        Date now = DateUtil.now();
        Date godLeveTime = effectGod.getAttachEndTime();
        // 到现在神仙还没离开,按照神仙增长速度计算
        if (godLeveTime.after(now)) {
            Date end = now;
            int godSpeed = getSpeedByGod(effectGod);
            return incWithMaxLimit(user, godSpeed, begin, end);
        }
        // 如果神仙已经离开，先计算神仙离开前到增长，后续再按照正常增长计算
        Date end = godLeveTime;
        int godSpeed = getSpeedByGod(effectGod);
        incWithMaxLimit(user, godSpeed, begin, end);
        //
        return normalInc(user);
    }

    private int normalInc(GameUser user) {
        Date begin = getDiceLastIncTime(user);
        Date end = DateUtil.now();
        return incWithMaxLimit(user, DiceSpeedEnum.NORMAL.getSeconds(), begin, end);
    }

    /**
     * @param user                玩家
     * @param speed:增长速度
     * @param beginIncTime:计算开始时间
     * @param endIncTime:计算结算时间
     * @return
     */
    private int incWithMaxLimit(GameUser user, int speed, Date beginIncTime, Date endIncTime) {
        int maxLimit = maxDiceLimitByLevel(user.getLevel());
        if (user.getDice() >= maxLimit) {
            user.aotuAddDice(0, DateUtil.now());// 使用自增，避免多处读写造成覆盖
            return user.getDice();
        }
        long intervalSeconds = DateUtil.getSecondsBetween(beginIncTime, endIncTime);
        long incNum = intervalSeconds / speed;
        // 还没到需要恢复到时间，不需要恢复
        if (0 == (int) incNum) {
            return user.getDice();
        }
        // 体力可以恢复到满
        if (user.getDice() + incNum >= maxLimit) {
            Long overflow = Math.max(0, user.getDice() + incNum - maxLimit);
            user.aotuAddDice(maxLimit - user.getDice(), DateUtil.now());// 使用自增，避免多处读写造成覆盖
            try {
                if (overflow > 0) {
                    ResEventPublisher.pubDiceFullEvent(EPDiceFull.instance(user.getId(), overflow.intValue()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("体力溢出加入到体力罐异常！");
            }
            return maxLimit;
        }
        // 无法加满
        int offset = (int) intervalSeconds % speed;
        Date lastIncTime = DateUtil.addSeconds(endIncTime, -offset);
        user.aotuAddDice((int) incNum, lastIncTime);
        return user.getDice();
    }

    private int getAddDicNum(int speed, Date beginIncTime, Date endIncTime) {
        long intervalSeconds = DateUtil.getSecondsBetween(beginIncTime, endIncTime);
        long incNum = intervalSeconds / speed;
        return (int) incNum;
    }

    // 获取可能生效的神仙
    private Optional<UserGod> getEffectUserGod(Long uid, Date mayBefirstIncDiceTime) {
        // 这里的数据已经按照时间正序排列
        List<UserGod> attachGods = userService.getMultiItems(uid, UserGod.class);
        if (attachGods.isEmpty()) {
            return Optional.empty();
        }
        // 在第一次结算时候，还没离开的，对体力恢复有影响的神仙，理论上只有1个
        List<UserGod> effectGods = attachGods.stream().filter(god -> god.getAttachEndTime() != null && god.getAttachEndTime().after(mayBefirstIncDiceTime) && isEffectDiceGod(god.getBaseId())).collect(Collectors.toList());
        if (effectGods.isEmpty()) {
            return Optional.empty();
        }
        // 取最后一条记录
        return Optional.of(effectGods.get(effectGods.size() - 1));
    }

    // 留下对体力恢复有影响的仙长,衰神
    private boolean isEffectDiceGod(int godId) {
        // 仙长,衰神
        if (godId == GodEnum.XZ.getValue() || godId == GodEnum.SS.getValue()) {
            return true;
        }
        return false;
    }


    @Getter
    @AllArgsConstructor
    private enum DiceSpeedEnum implements Serializable {
        FAST("仙长", 90), // 90秒数
        NORMAL("正常", 120), // 120秒数
        SLOW("衰神", 150); // 150秒数

        private String name;
        private int seconds;

    }

    private int getSpeedByGod(UserGod userGod) {
        GodEnum god = GodEnum.fromValue(userGod.getBaseId());
        switch (god) {
            case XZ:
                if (userGod.getAttachWay() != null && WayEnum.HEXAGRAM.equals(userGod.getAttachWay())) {
                    return DiceSpeedEnum.FAST.getSeconds() / 4;
                }
                return DiceSpeedEnum.FAST.getSeconds();
            case SS:
                return DiceSpeedEnum.SLOW.getSeconds();
            default:
                return DiceSpeedEnum.NORMAL.getSeconds();
        }
    }

    private Date getDiceLastIncTime(GameUser user) {
        UserDiceInfo userDiceInfo = userDiceService.getUserDiceInfo(user.getId());
        Date lastIncDiceTime = userDiceInfo.getDiceLastIncTime();
        if (null == lastIncDiceTime) {
            log.error("uid=[{}] 昵称=[{}]的 玩家没有上次体力增长时间。默认为1天前。");
            lastIncDiceTime = DateUtil.addDays(DateUtil.now(), -1);
            userDiceInfo.setDiceLastIncTime(lastIncDiceTime);
            userService.updateItem(userDiceInfo);
        }
        return lastIncDiceTime;
    }
}
