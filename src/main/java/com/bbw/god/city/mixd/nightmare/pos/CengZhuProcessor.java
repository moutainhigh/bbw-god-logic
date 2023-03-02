package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 说明：
 * 层主
 *
 * @author lwb
 * date 2021-06-04
 */
@Service
public class CengZhuProcessor extends AbstractMiXianFightProcessor {
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private RedisHashUtil<String, MiXianEnemy> redisHashUtil;
    @Autowired
    private MailService mailService;

    @Override
    public boolean isXunShi() {
        return false;
    }

    @Override
    public int fightFailDeductBlood(long uid) {
        return 0;
    }


    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        for (MiXianLevelData.PosData data : nightmareMiXian.getLevelData().getPosDatas()) {
            if (data.getTye() == NightmareMiXianPosEnum.SHOU_WEI.getType()) {
                throw new ExceptionForClientTip("mxd.need.kill.shouwei");
            }
        }
        long uid = nightmareMiXian.getGameUserId();
        MiXianEnemy enemy = buildAiCards(uid, nightmareMiXian.getCurrentLevel(), posData);
        if (enemy.getUid().equals(nightmareMiXian.getGameUserId())) {
            throw new ExceptionForClientTip("mxd.self.is.level.owner");
        }
        rd.setLevelOwnerName(enemy.getNickname());
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
            Optional<MiXianEnemy> optional = getLevelOwner(gid, i, date);
            if (optional.isPresent() && optional.get().getUid() == uid) {
                MiXianEnemy xianEnemy = optional.get();
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
        Optional<MiXianEnemy> levelOwner = getLevelOwner(gameUserService.getActiveGid(uid), currentMxdLevel, new Date());
        if (levelOwner.isPresent()) {
            return levelOwner.get();
        }
        return getAiEnemy(uid, currentMxdLevel, posData);
    }

    /**
     * 获取AI层主
     *
     * @param uid
     * @param currentMxdLevel
     * @param posData
     * @return
     */
    private MiXianEnemy getAiEnemy(long uid, int currentMxdLevel, MiXianLevelData.PosData posData) {
        UserNightmareMiXianEnemy mxdEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = mxdEnemy.getEnemy(posData.getPos(), currentMxdLevel, posData.getTye());
        if (optional.isPresent()) {
            return optional.get();
        }
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
        int lv = 15 + 5 * (currentMxdLevel / 10 - 1);
        int hv = 6 + 2 * (currentMxdLevel / 10 - 1);
        for (CfgCardEntity cardEntity : cardEntities) {
            cardParams.add(CCardParam.init(cardEntity.getId(), lv, hv));
        }
        //需要生成对手
        GameUser gu = gameUserService.getGameUser(uid);
        MiXianEnemy enemy = MiXianEnemy.getInstance(posData, currentMxdLevel);
        enemy.setNickname("层主-藏宝守卫");
        enemy.setCardParams(cardParams);
        enemy.setLevel(gu.getLevel());

        mxdEnemy.getMiXianEnemies().add(enemy);
        gameUserService.updateItem(mxdEnemy);
        return enemy;
    }

    /**
     * 击败层主后，返回迷仙洞时，层主的图标会变淡消失。并在消失后按照2*2的格式生成3个宝箱+1个特殊宝箱。
     *
     * @param nightmareMiXian
     * @param rd
     * @return
     */
    @Override
    public List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd) {
        List<MiXianLevelData.PosData> datas = nightmareMiXian.getLevelData().getPosDatas();
        //有原来的3个普通+1个特殊 调整为4个普通。
        int[] boxType = {NightmareMiXianPosEnum.BOX.getType(), NightmareMiXianPosEnum.BOX.getType(), NightmareMiXianPosEnum.BOX.getType(), NightmareMiXianPosEnum.BOX.getType()};
        int index = 0;
        for (MiXianLevelData.PosData data : datas) {
            if (data.getTye() == NightmareMiXianPosEnum.LEVEL_LEADER.getType()) {
                data.setTye(boxType[index]);
                index++;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void handleFightAward(long uid, long aiId, RDFightResult rd) {
        super.handleFightAward(uid, aiId, rd);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        String baseKey = getBaseKey(gameUserService.getActiveGid(uid));
        String filedKey = getFiledKey(nightmareMiXian.getCurrentLevel(), DateUtil.now());
        GameUser gu = gameUserService.getGameUser(uid);
        MiXianEnemy newLevelOwner = MiXianEnemy.getInstance(nightmareMiXian.getPostData(nightmareMiXian.getPos()), nightmareMiXian.getCurrentLevel());
        newLevelOwner.setEnemyId(NightmareMiXianTool.buildMxdAiId(nightmareMiXian.getCurrentLevel(), nightmareMiXian.getPos(), NightmareMiXianPosEnum.LEVEL_LEADER.getType()));
        newLevelOwner.setLevel(gu.getLevel());
        newLevelOwner.setHead(gu.getRoleInfo().getHead());
        newLevelOwner.setHeadIcon(gu.getRoleInfo().getHeadIcon());
        newLevelOwner.setNickname("层主-" + ServerTool.getServerShortName(gu.getServerId()) + gu.getRoleInfo().getNickname());
        newLevelOwner.setUid(gu.getId());
        List<Integer> cardGroup = nightmareMiXian.getCardGroup();
        List<CCardParam> cardParams = new ArrayList<>();
        for (int cardId : cardGroup) {
            UserCard userCard = userCardService.getUserCard(uid, cardId);
            cardParams.add(CCardParam.init(userCard));
        }
        newLevelOwner.setCardParams(cardParams);
        MiXianEnemy levelOwner = redisHashUtil.getField(baseKey, filedKey);
        if (levelOwner != null && levelOwner.getUid() > 0) {
            beatAwards(nightmareMiXian, rd);
            gameUserService.updateItem(nightmareMiXian);
            //有玩家层主
            String loserTitle = LM.I.getMsgByUid(levelOwner.getUid(), "mail.nightmare.mxd.guard.lose.title", nightmareMiXian.getCurrentLevel());
            String loserContent = LM.I.getMsgByUid(levelOwner.getUid(), "mail.nightmare.mxd.guard.lose.content", ServerTool.getServerShortName(gu.getServerId()), gu.getRoleInfo().getNickname());
            mailService.sendSystemMail(loserTitle, loserContent, levelOwner.getUid());
            MiXDEventPublisher.pubCZBiteTheDustEvent(new BaseEventParam(levelOwner.getUid()));
        }
        redisHashUtil.putField(baseKey, filedKey, newLevelOwner);
    }

    /**
     * 层主只能挑战一次，失败后无法再战
     *
     * @param uid
     * @param param
     * @param rd
     */
    @Override
    public void fail(long uid, FightSubmitParam param, RDFightResult rd) {
        rd.setMxd(RDNightmareMxd.getFightResultInstance(fightFailDeductBlood(uid)));
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        for (MiXianLevelData.PosData posData : nightmareMiXian.getLevelData().getPosDatas()) {
            if (posData.getTye() == NightmareMiXianPosEnum.LEVEL_LEADER.getType()) {
                posData.setTye(NightmareMiXianPosEnum.EMPTY.getType());
            }
        }
        String baseKey = getBaseKey(gameUserService.getActiveGid(uid));
        String filedKey = getFiledKey(nightmareMiXian.getCurrentLevel(), DateUtil.now());
        MiXianEnemy levelOwner = redisHashUtil.getField(baseKey, filedKey);
        if (levelOwner != null && levelOwner.getUid() > 0) {
            gameUserService.updateItem(nightmareMiXian);
            GameUser gu = gameUserService.getGameUser(uid);
            String title = LM.I.getMsgByUid(levelOwner.getUid(), "mail.nightmare.mxd.guard.award.title", nightmareMiXian.getCurrentLevel());
            String content = LM.I.getMsgByUid(levelOwner.getUid(), "mail.nightmare.mxd.guard.award.content", ServerTool.getServerShortName(gu.getServerId()), gu.getRoleInfo().getNickname());
            mailService.sendAwardMail(title, content, levelOwner.getUid(), Arrays.asList(Award.instance(11670, AwardEnum.FB, 1)));
            MiXDEventPublisher.pubCZBeatDefierEvent(new BaseEventParam(levelOwner.getUid()));
        }
    }

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.LEVEL_LEADER.equals(miXianPosEnum);
    }

    /**
     * 最外层的KEY
     *
     * @param gid
     * @return
     */
    public static String getBaseKey(int gid) {
        if (gid == 17) {
            gid = 16;
        }
        return GameRedisKey.getDataTypeKey(gid, "mxdLevelOwner");
    }

    /**
     * 层数据的KEY
     *
     * @param level
     * @param date
     * @return
     */
    public static String getFiledKey(int level, Date date) {
        int dateInt = DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(date));
        return dateInt + "_" + level;
    }

    public Optional<MiXianEnemy> getLevelOwner(int gid, int mxdLevel, Date date) {
        String baseKey = getBaseKey(gid);
        String filedKey = getFiledKey(mxdLevel, date);
        MiXianEnemy levelOwner = redisHashUtil.getField(baseKey, filedKey);
        if (levelOwner != null) {
            return Optional.of(levelOwner);
        }
        return Optional.empty();
    }

    /**
     * 重置层主信息
     */
    public void ResetLevelOwnerInfo(int gid) {
        String baseKey = getBaseKey(gid);
        for (int i = 10; i <= 30; i += 10) {
            String filedKey = getFiledKey(i, new Date());
            redisHashUtil.putField(baseKey, filedKey, null);
        }

    }

    /**
     * 设置层主卡组
     *
     * @param uid
     * @param cards
     * @return
     */
    public RDNightmareMxd saveLevelOwnerCards(long uid, List<Integer> cards) {
        List<CCardParam> cardParams = new ArrayList<>();
        List<UserCard> userCards = userCardService.getUserCards(uid, cards);
        for (UserCard userCard : userCards) {
            cardParams.add(CCardParam.init(userCard));
        }
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        int level = nightmareMiXian.getCurrentLevel();
        String baseKey = getBaseKey(gameUserService.getActiveGid(uid));
        String filedKey = getFiledKey(level, new Date());
        MiXianEnemy levelOwner = redisHashUtil.getField(baseKey, filedKey);
        if (levelOwner != null && levelOwner.getSaveTimes() == 0 && levelOwner.getUid() == uid) {
            levelOwner.setCardParams(cardParams);
            levelOwner.setSaveTimes(1);
            redisHashUtil.putField(baseKey, filedKey, levelOwner);
        }
        return new RDNightmareMxd();
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
        List<Integer> cardGroup = nightmareMiXian.getCardGroup();
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<UserCard> fightCards = userCards.stream().filter(p -> cardGroup.contains(p.getBaseId()) || cardGroup.contains(CardTool.getDeifyCardId(p.getBaseId()))).collect(Collectors.toList());
        List<CCardParam> cardParams = new ArrayList<>();
        for (UserCard fightCard : fightCards) {
            CCardParam init = CCardParam.init(fightCard);
            cardParams.add(init);
        }
        return cardParams;
    }
}
