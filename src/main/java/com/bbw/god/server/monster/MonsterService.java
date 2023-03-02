package com.bbw.god.server.monster;

import com.alibaba.fastjson.JSON;
import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.buddy.BuddyService;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.unique.UserMonster;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.god.GodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 好友野怪服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 10:02
 */
@Slf4j
@Service
public class MonsterService {
    // 普通友怪选牌策略ID
    private static final String[] YG_FRIEND_STRATEGY_KEYS = {RandomKeys.YEGUAI_FRIEND_CARD_1, RandomKeys.YEGUAI_FRIEND_CARD_2, RandomKeys.YEGUAI_FRIEND_CARD_3, RandomKeys.YEGUAI_FRIEND_CARD_4, RandomKeys.YEGUAI_FRIEND_CARD_5};
    // 精英友怪选牌策略ID
    private static final String[] YG_ELITE_FRIEND_STRATEGY_KEYS = {RandomKeys.YEGUAI_ELITE__FRIEND_CARD_1, RandomKeys.YEGUAI_ELITE__FRIEND_CARD_2, RandomKeys.YEGUAI_ELITE__FRIEND_CARD_3};

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private BuddyService buddyService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private MailService mailService;
    @Autowired
    private GodService godService;
    @Autowired
    private UserCardRandomService userCardRandomService;

    /**
     * 添加野怪到服务器列表
     *
     * @param sid
     * @param monster
     */
    public void addMonsterToList(int sid, ServerMonster monster) {
        serverService.addServerData(sid, monster);
    }

    /**
     * 更新野怪信息
     *
     * @param monster
     */
    public void updateMonster(ServerMonster monster) {
        serverService.updateServerData(monster);
    }

    // 获取区服所有好友野怪
    private List<ServerMonster> getAllMonsters(int sid) {
        // 获取服务器所有野怪
        List<ServerMonster> monsterList = serverService.getServerDatas(sid, ServerMonster.class);
        // 清理超时和已经被打败的野怪
        for (ServerMonster mster : monsterList) {
            //怪物已失效，且过期超过10分钟，则删除怪物
            if (mster.ifUnvalid() && DateUtil.getMinutesBetween(mster.getEacapeTime(), DateUtil.now()) > 10) {
                serverService.deleteServerData(mster);
            }
        }
        monsterList = monsterList.stream().filter(mster -> !mster.ifUnvalid()).collect(Collectors.toList());
        return monsterList;

    }

    /**
     * 获得好友未打赢的野怪。取最近生成的为被击败的，且不超过20个
     *
     * @param sid
     * @param uid
     * @return
     */
    private List<ServerMonster> getBuddyMonstersLimit20(int sid, long uid) {
        List<ServerMonster> monsterList = getAllMonsters(sid);
        if (monsterList.isEmpty()) {
            return monsterList;
        }
        // 获取玩家到好友
        Set<Long> buddyIds = buddyService.getFriendUids(uid);
        // log.info("{}好友{}", uid, buddyIds);
        // 挑选好友的野怪
        List<ServerMonster> buddyMonsterList = new ArrayList<>();
        for (ServerMonster mster : monsterList) {
            if (null == mster || null == mster.getGuId()) {
                continue;
            }
            if (!mster.getJoinYouGuai()) {
                continue;
            }
            for (Long buddyId : buddyIds) {
                if (null == buddyId) {
                    continue;
                }
                if (mster.getGuId().longValue() == buddyId.longValue()) {
                    buddyMonsterList.add(mster);
                }
            }
        }
        // 获得好友未打赢的野怪。取最近生成的为被击败的，且不超过20个
        return ListUtil.subListForPage(buddyMonsterList, 1, 20);
    }

    /**
     * 获取好友野怪
     *
     * @param guId
     * @return
     */
    public List<ServerMonster> getBuddyMonsters(long guId, int sid) {
        List<ServerMonster> sMonsters = getBuddyMonstersLimit20(sid, guId);

        List<ServerMonster> monstersToShow = new ArrayList<>();
        if (ListUtil.isNotEmpty(sMonsters)) {
            // log.info("{}好友野怪数{}", guId, sMonsters.size());
            // int monsterDefaultLevel =
            // Cfg.I.getUniqueConfig(CfgMonster.class).getMonsterDefaultLevel();
            for (ServerMonster monster : sMonsters) {
                if (monster.getBeDefeated() || monster.getBlood() <= 0) {
                    continue;
                }
                Long remainTime = monster.getEacapeTime().getTime() - System.currentTimeMillis();
                if (remainTime < 1000) {
                    continue;
                }
                // JSONObject soliders =
                // JSONObject.parseObject(monster.getSoliders());
                // int monsterLevel = soliders.getIntValue("level");// 怪物等级
                // 系统自动为玩家生成的怪，只有该玩家可以看到
                // if (monsterLevel == monsterDefaultLevel) {
                if (monster.getBeLong() > 0 && monster.getBeLong() != guId) {
                    continue;
                }
                // }
                monstersToShow.add(monster);
            }

        }
        return monstersToShow;
    }

    /**
     * 友怪剩余多久可以打
     *
     * @param guId
     * @return
     */
    public long getRemainTimeToBeat(long guId) {
        UserMonster umHelp = gameUserService.getSingleItem(guId, UserMonster.class);
        long remainTimeToBeat = 0;
        if (umHelp != null) {
            remainTimeToBeat = umHelp.getNextBeatTime().getTime() - System.currentTimeMillis();
        }
        remainTimeToBeat = remainTimeToBeat < 0 ? 0 : remainTimeToBeat;
        return remainTimeToBeat / 1000;
    }

    /**
     * 给发现者发放奖励
     *
     * @param winner
     * @param monster
     */
    public void sendDiscoverAward(GameUser winner, YeGuaiEnum yeGuaiEnum, ServerMonster monster) {
        // 发现者
        GameUser discover = gameUserService.getGameUser(monster.getGuId());
        List<Award> awards = getDiscoverAwards(discover, yeGuaiEnum);
        String title = LM.I.getMsgByUid(monster.getGuId(), "mail.discover.award.title");
        String content = LM.I.getMsgByUid(monster.getGuId(), "mail.discover.buddy.award.content", winner.getRoleInfo().getNickname());
        mailService.sendAwardMail(title, content, discover.getId(), JSON.toJSONString(awards));
    }

    public void sendDiscoverAward(YeGuaiEnum yeGuaiEnum, ServerMonster monster) {
        // 发现者
        GameUser discover = gameUserService.getGameUser(monster.getGuId());
        List<Award> awards = getDiscoverAwards(discover, yeGuaiEnum);
        String title = LM.I.getMsgByUid(discover.getId(), "mail.discover.award.title");
        String content = LM.I.getMsgByUid(discover.getId(), "mail.discover.award.content");
        mailService.sendAwardMail(title, content, discover.getId(), JSON.toJSONString(awards));
    }

    private List<Award> getDiscoverAwards(GameUser discover, YeGuaiEnum yeGuaiEnum) {
        List<Award> awards = new ArrayList<>();
        CfgCardEntity awardCard = getCardAward(discover, yeGuaiEnum, discover.getLevel());
        awards.add(new Award(awardCard.getId(), AwardEnum.KP, 1));
        // 发现者加奖一个元素
        awards.add(new Award(awardCard.getType(), AwardEnum.YS, 1));
        return awards;
    }

    /**
     * 给参与者(不包括胜利者)发放奖励，胜利者不通过邮件发放奖励
     *
     * @param winner
     * @param monster
     */
    public void sendJoinerAward(GameUser winner, ServerMonster monster) {
        long winnerId = winner.getId();
        // 发现者
        GameUser discover = gameUserService.getGameUser(monster.getGuId());
        String joinerStr = monster.getJoiners().trim();
        if (joinerStr.length() > 0) {
            List<Long> joiners = ListUtil.parseStrToLongs(joinerStr);
            for (long joiner : joiners) {
                // 参与者是胜利者不做任何处理
                if (joiner == winnerId) {
                    continue;
                }
                String content = LM.I.getMsgByUid(joiner, "mail.joiner.award.content", discover.getRoleInfo().getNickname(), winner.getRoleInfo().getNickname());
                sendJoinerAward(joiner, content);
            }
        }
    }

    /**
     * 给参与者发送奖励,此方法为多人打野怪，但是打的人都没有胜利，且野怪血量已不够扣时，调用此方法结算参与奖
     *
     * @param monster
     */
    public void sendJoinerAward(ServerMonster monster) {
        // 发现者
        GameUser discover = gameUserService.getGameUser(monster.getGuId());
        String joinerStr = monster.getJoiners().trim();
        if (joinerStr.length() > 0) {
            List<Long> joiners = ListUtil.parseStrToLongs(joinerStr);
            for (long joiner : joiners) {
                String content = LM.I.getMsgByUid(joiner, "mail.joiner.award.content", discover.getRoleInfo().getNickname(), "");
                sendJoinerAward(joiner, content);
            }
        }
    }

    private void sendJoinerAward(long joiner, String mailContent) {
        String title = LM.I.getMsgByUid(joiner, "mail.joiner.award.title");
        List<Award> awards = new ArrayList<>();
        GameUser gameUser = gameUserService.getGameUser(joiner);
        // 卡牌奖励
        CfgCardEntity awardCard = getCardAward(gameUser, YeGuaiEnum.YG_FRIEND, gameUser.getLevel());
        awards.add(new Award(awardCard.getId(), AwardEnum.KP, 1));
        mailService.sendAwardMail(title, mailContent, joiner, JSON.toJSONString(awards));
    }

    /**
     * 获得玩家最近一次生成的怪物（2小时内生成的）
     *
     * @param guId
     * @return
     */
    public ServerMonster getLastMonster(long guId, int sid) {
        List<ServerMonster> monsterList = getAllMonsters(sid);
        if (monsterList.isEmpty()) {
            return null;
        }
        monsterList = monsterList.stream().filter(p -> p.getGuId() == guId).sorted(Comparator.comparing(ServerMonster::getEacapeTime).reversed()).collect(Collectors.toList());
        if (monsterList.isEmpty()) {
            return null;
        }
        return monsterList.get(0);
    }


    /**
     * 打野怪（帮好友打怪）掉落卡牌
     *
     * @param gu
     * @param yeGuaiType
     * @param openBoxLevel
     * @return
     */
    public CfgCardEntity getCardAward(GameUser gu, YeGuaiEnum yeGuaiType, int openBoxLevel) {

        int ygdlIndex = openBoxLevel / 10;
        String[] strategyKeys = null;
        // 策略
        switch (yeGuaiType) {
            case YG_FRIEND:
                // 好友普通怪
                strategyKeys = YG_FRIEND_STRATEGY_KEYS;
                break;
            case YG_ELITE_FRIEND:
                // 好友精英怪
                strategyKeys = YG_ELITE_FRIEND_STRATEGY_KEYS;
                ygdlIndex -= 2;
                break;
            default:
                break;
        }
        ygdlIndex = ygdlIndex < 0 ? 0 : ygdlIndex;
        ygdlIndex = ygdlIndex >= strategyKeys.length ? strategyKeys.length - 1 : ygdlIndex;
        // ------------刘少军 修改 为从 抽卡策略获取卡牌 2019-04-11
        // 城池卡牌
        RandomParam randomParams = new RandomParam();
        randomParams.setExtraCardsToMap(userCardService.getUserCards(gu.getId()));
        // 神仙
        int cardDropRate = this.godService.getCardDropRate(gu);

        String strategyKey = strategyKeys[ygdlIndex];
        Optional<CfgCardEntity> card = this.userCardRandomService.getRandomCard(gu.getId(), strategyKey, randomParams, cardDropRate);
        if (card.isPresent()) {
            return card.get();
        }
        return CardTool.getRandomNotSpecialCard(1);
    }
}
