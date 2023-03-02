package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 说明：
 * 守卫
 *
 * @author lwb
 * date 2021-06-04
 */
@Service
public class ShouWeiProcessor extends AbstractMiXianFightProcessor {
    @Autowired
    private CengZhuProcessor cengZhuProcessor;

    @Override
    public boolean isXunShi() {
        return false;
    }

    @Override
    public int fightFailDeductBlood(long uid) {
        return 9;
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        long uid = nightmareMiXian.getGameUserId();
        UserNightmareMiXianEnemy mxdEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = mxdEnemy.getEnemy(posData.getPos(), nightmareMiXian.getCurrentLevel(), posData.getTye());
        MiXianEnemy enemy = null;
        if (!optional.isPresent()) {
            enemy = buildAiCards(uid, nightmareMiXian.getCurrentLevel(), posData);
            mxdEnemy.getMiXianEnemies().add(enemy);
            gameUserService.updateItem(mxdEnemy);
        } else {
            enemy = optional.get();
        }
        rd.setOpponentId(enemy.getEnemyId());
        List<RDFightsInfo.RDFightCard> aiCards = new ArrayList<>();
        for (CCardParam param : enemy.getCardParams()) {
            aiCards.add(RDFightsInfo.RDFightCard.instance(param));
        }
        rd.setAiCards(aiCards);
        rd.setMyCards(nightmareMiXian.getCardGroup());
        List<RDNightmareMxd.CardUsed> cardUseds = new ArrayList<>();
        int gid = gameUserService.getActiveGid(uid);
        Date date = DateUtil.now();
        for (int i = 10; i <= 30; i += 10) {
            if (i == nightmareMiXian.getCurrentLevel()) {
                continue;
            }
            Optional<MiXianEnemy> cengZhuOptional = cengZhuProcessor.getLevelOwner(gid, i, date);
            if (cengZhuOptional.isPresent() && cengZhuOptional.get().getUid() == uid) {
                MiXianEnemy xianEnemy = cengZhuOptional.get();
                for (CCardParam param : xianEnemy.getCardParams()) {
                    RDNightmareMxd.CardUsed cardUsed = new RDNightmareMxd.CardUsed();
                    cardUsed.setId(param.getId());
                    cardUsed.setMxdLevel(i);
                    cardUseds.add(cardUsed);
                }
            }
        }
        rd.setMyUsedCards(cardUseds);
    }

    /**
     * 1）卡组：随机5星卡*5、随机4星卡*5、随机1~5星卡*10。
     * 2）等级：与玩家等级一致
     * 3）玩家在于守卫战斗时，会根据当前层锁定卡牌适用的等级及阶数上限。守卫的卡牌为上限数据，而玩家的卡牌无法超过上限。
     * 层数限制	卡牌等级	卡牌阶数
     * 一阶（10层）	15	6
     * 二阶（20层）	20	8
     * 三阶（30层）	25	10
     *
     * @param uid
     * @param currentMxdLevel
     * @param posData
     * @return
     */
    @Override
    public MiXianEnemy buildAiCards(long uid, int currentMxdLevel, MiXianLevelData.PosData posData) {
        int[] cardRules = {0, 0, 0, 5, 5};
        for (int i = 0; i < 10; i++) {
            int randomStar = PowerRandom.getRandomBySeed(5);
            cardRules[randomStar - 1]++;
        }
        List<CfgCardEntity> cardEntities = new ArrayList<>();
        for (int i = 0; i < cardRules.length; i++) {
            List<CfgCardEntity> list = CardTool.getRandomCard(i + 1, cardRules[i]);
            cardEntities.addAll(list);
        }
        List<CCardParam> cardParams = new ArrayList<>();
        int lv = getCardLv(currentMxdLevel);
        int hv = getCardHv(currentMxdLevel);
        for (CfgCardEntity cardEntity : cardEntities) {
            cardParams.add(CCardParam.init(cardEntity.getId(), lv, hv));
        }
        //需要生成对手
        GameUser gu = gameUserService.getGameUser(uid);
        MiXianEnemy enemy = MiXianEnemy.getInstance(posData, currentMxdLevel);
        enemy.setNickname("迷仙洞" + currentMxdLevel + "层守卫");
        enemy.setCardParams(cardParams);
        enemy.setHead(3150);
        enemy.setLevel(gu.getLevel());
        return enemy;
    }

    private int getCardLv(int currentMxdLevel) {
        return 15 + 5 * (currentMxdLevel / 10 - 1);
    }

    private int getCardHv(int currentMxdLevel) {
        return 6 + 2 * (currentMxdLevel / 10 - 1);
    }

    /**
     * 玩家在于守卫战斗时，会根据当前层锁定卡牌适用的等级及阶数上限。守卫的卡牌为上限数据，而玩家的卡牌无法超过上限。
     *
     * @param uid
     * @return
     */
    @Override
    public List<CCardParam> buildUserFightCardGroup(long uid) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        int currentMxdLevel = nightmareMiXian.getCurrentLevel();
        int lv = getCardLv(currentMxdLevel);
        int hv = getCardHv(currentMxdLevel);
        List<Integer> cardGroup = nightmareMiXian.getCardGroup();
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<UserCard> fightCards = userCards.stream().filter(p -> cardGroup.contains(p.getBaseId()) || cardGroup.contains(CardTool.getDeifyCardId(p.getBaseId()))).collect(Collectors.toList());
        List<CCardParam> cardParams = new ArrayList<>();
        for (UserCard fightCard : fightCards) {
            CCardParam init = CCardParam.init(fightCard);
            init.setHv(hv);
            init.setLv(lv);
            cardParams.add(init);
        }
        return cardParams;
    }

    /**
     * 击败守卫后将会掉落关卡钥匙，返回迷仙洞时，守卫的图标会变淡消失。
     * 并在消失后按照2*2的格式生成3个宝箱+1个特殊宝箱。
     * 30层的守卫会生成2个宝箱格+1个珍贵宝箱+1个特殊宝箱。
     *
     * @param nightmareMiXian
     * @param rd
     * @return
     */
    @Override
    public List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd) {
        List<MiXianLevelData.PosData> datas = nightmareMiXian.getLevelData().getPosDatas();
        int[] boxType = {NightmareMiXianPosEnum.BOX.getType(), NightmareMiXianPosEnum.BOX.getType(), NightmareMiXianPosEnum.BOX.getType(), NightmareMiXianPosEnum.BOX_SPECIAL.getType()};
        if (nightmareMiXian.getCurrentLevel() == 30) {
            boxType[2] = NightmareMiXianPosEnum.BOX_RICH.getType();
        }
        int index = 0;
        for (MiXianLevelData.PosData data : datas) {
            if (data.getTye() == NightmareMiXianPosEnum.SHOU_WEI.getType()) {
                data.setTye(boxType[index]);
                index++;
            }
        }
        return Arrays.asList(Award.instance(11660, AwardEnum.FB, 1));
    }


    /**
     * 未能击败守卫时在返回迷仙洞界面时弹出对应弹窗，在关闭弹窗后减少9点生命值并后退一格。
     *
     * @param uid
     * @param param
     * @param rd
     */
    @Override
    public void fail(long uid, FightSubmitParam param, RDFightResult rd) {
        RDNightmareMxd rdNightmareMxd = RDNightmareMxd.getFightResultInstance(-fightFailDeductBlood(uid));
        rd.setMxd(rdNightmareMxd);
        UserNightmareMiXianEnemy xianEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = xianEnemy.getMiXianEnemies().stream().filter(p -> p.getEnemyId().equals(param.getOpponentId())).findFirst();
        if (!optional.isPresent()) {
            return;
        }
        MiXianEnemy enemy = optional.get();
        int blood = enemy.getBlood() - param.getOppLostBlood();
        if (blood > 0) {
            enemy.setBlood(blood);
        }
        List<Integer> collect = param.getOppKilledCards().stream().map(FightSubmitParam.SubmitCardParam::getId).collect(Collectors.toList());
        enemy.setCardParams(enemy.getCardParams().stream().filter(p -> !collect.contains(p.getId())).collect(Collectors.toList()));
        gameUserService.updateItem(xianEnemy);
    }

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.SHOU_WEI.equals(miXianPosEnum);
    }
}
