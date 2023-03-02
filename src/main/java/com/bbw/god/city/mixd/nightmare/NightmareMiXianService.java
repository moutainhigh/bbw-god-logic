package com.bbw.god.city.mixd.nightmare;

import com.bbw.common.DateUtil;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.RDCardStrengthen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-26
 */
@Service
public class NightmareMiXianService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NightmareMiXianLogic nightmareMiXianLogic;

    /**
     * 获取玩家迷仙洞数据  不存在则创建对象
     * @param uid
     * @return
     */
    public UserNightmareMiXian getAndCreateUserNightmareMiXian(long uid){
        UserNightmareMiXian item = gameUserService.getSingleItem(uid, UserNightmareMiXian.class);
        if (item==null){
            item=UserNightmareMiXian.getInstance(uid);
            gameUserService.addItem(uid,item);
        }
        return item;
    }
    /**
     * 获取玩家迷仙洞AI数据  不存在则创建对象
     * @param uid
     * @return
     */
    public UserNightmareMiXianEnemy getAndCreateUserNightmareMiXianEnemy(long uid){
        UserNightmareMiXianEnemy item = gameUserService.getSingleItem(uid, UserNightmareMiXianEnemy.class);
        if (item==null){
            item=UserNightmareMiXianEnemy.getInstance(uid);
            gameUserService.addItem(uid,item);
        }
        return item;
    }

    /**
     * 增加挑战层数
     *
     * @param user 梦魇迷仙洞用户数据
     * @return 返回当前可挑战层数
     */
    public int incChallengeLayers(UserNightmareMiXian user) {
        CfgNightmareMiXian cfgNightmareMiXian = NightmareMiXianTool.getCfg();
        // 获取用户最后一次挑战层数增加的时间
        if (!user.hadChallengeLayersLastIncTime()){
            // 不存在 则初始化挑战层数信息
            initChallengeLayers(user, cfgNightmareMiXian);
        }
        Date layersLastIncTime = user.getLayersLastIncTime();
        Date now = DateUtil.now();
        // 获取挑战层数恢复速度
        Integer speedSecond = cfgNightmareMiXian.getIncLayersSpeedSecond();
        // 获取时间差值（秒）
        long seconds = DateUtil.getSecondsBetween(layersLastIncTime, now);
        // 最快也得离上一次恢复时间的DiceSpeedEnum.FAST.getSeconds()秒才需要恢复体力，未到结算时间，不结算。误差2秒
        if (seconds + 2 < speedSecond) {
            return user.getRemainChallengeLayers();
        }
        // 无法加满
        int offset = (int) seconds % speedSecond;
        Date newlyIncTime = DateUtil.addSeconds(now, -offset);
        long incNum = seconds / speedSecond;
        // 获取最大挑战层数配置
        Integer cfgMaxLayers = cfgNightmareMiXian.getMaxChallengeLayers();
        if (!cfgMaxLayers.equals(user.getMaxChallengeLayers())){
            // 同步玩家最大挑战层数
            user.setMaxChallengeLayers(cfgMaxLayers);
        }
        // 获取玩家当前挑战层数
        Integer remainLayers = user.getRemainChallengeLayers();
        if (remainLayers + incNum > cfgMaxLayers) {
            // 体力可以恢复到满
            remainLayers = cfgMaxLayers;
        }else {
            remainLayers += (int) incNum;
        }
        user.setLayersLastIncTime(newlyIncTime);
        // 设置下一次恢复的时间
        user.setLayersNextIncTime(DateUtil.addSeconds(newlyIncTime, cfgNightmareMiXian.getIncLayersSpeedSecond()));
        user.setRemainChallengeLayers(remainLayers);
        gameUserService.updateItem(user);
        return user.getRemainChallengeLayers();
    }

    /**
     * 减少挑战层数
     *
     * @param user 梦魇迷仙洞用户数据
     * @return 返回当前可挑战层数
     */
    public int redChallengeLayers(UserNightmareMiXian user) {
        CfgNightmareMiXian cfgNightmareMiXian = NightmareMiXianTool.getCfg();
        if (user.getRemainChallengeLayers().equals(user.getMaxChallengeLayers())){
            // 玩家当前层数 等于 最大层数，在扣除挑战层数时，初始化最近层数增加的时间
            Date now = DateUtil.now();
            user.setLayersLastIncTime(now);
            user.setLayersNextIncTime(DateUtil.addSeconds(now, cfgNightmareMiXian.getIncLayersSpeedSecond()));
        }
        // 层数数检疫
        user.setRemainChallengeLayers(user.getRemainChallengeLayers() - 1);
        return user.getRemainChallengeLayers();
    }

    /**
     * 初始化玩家挑战层数信息
     *
     * @param user 迷仙洞玩家信息
     * @param cfgNightmareMiXian 迷仙洞配置信息
     */
    private void initChallengeLayers(UserNightmareMiXian user, CfgNightmareMiXian cfgNightmareMiXian){
        Date now = DateUtil.now();
        user.setLayersLastIncTime(now);
        user.setRemainChallengeLayers(cfgNightmareMiXian.getMaxChallengeLayers());
        user.setMaxChallengeLayers(cfgNightmareMiXian.getMaxChallengeLayers());
        user.setLayersNextIncTime(DateUtil.addSeconds(now, cfgNightmareMiXian.getIncLayersSpeedSecond()));
        gameUserService.updateItem(user);
    }

    /**
     * 查看敌方卡牌详情
     * @param cardId
     * @param oppIdAndUid
     * @return
     */
    public RDCardStrengthen getCardInfo(Integer cardId, String oppIdAndUid) {
        RDCardStrengthen rd = new RDCardStrengthen();
        String[] split = oppIdAndUid.split(",");
        long oppId = Long.parseLong(split[0]);
        long uid = Long.parseLong(split[1]);
        MiXianEnemy enemy = nightmareMiXianLogic.getEnemyById(uid, oppId);
        CCardParam param = enemy.getCardParams().stream().filter(tmp -> tmp.getId() == cardId).findFirst().orElse(null);
        if (null == param) {
            return rd;
        }
        rd.setCardId(cardId);
        rd.setSkill0(param.getSkills().get(0));
        rd.setSkill5(param.getSkills().get(1));
        rd.setSkill10(param.getSkills().get(2));
        rd.setAttackSymbol(0);
        rd.setDefenceSymbol(0);
        rd.setIsUseSkillScroll(param.getIsUseSkillScroll());
        return rd;
    }
}