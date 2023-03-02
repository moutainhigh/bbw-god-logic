package com.bbw.god.gm;

import com.bbw.common.*;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficulty;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficultyLogic;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.card.CardConstant;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardGroup;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.config.GameUserExpTool;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.guide.v1.NewerGuideEnum;
import com.bbw.god.gameuser.limit.UserLimit;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.daily.service.UserDailyTaskService;
import com.bbw.god.gameuser.task.grow.UserGrowTask;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerUserService;
import com.bbw.sys.session.SingleUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 玩家数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@Service
@Slf4j
public class UserGmService {
    private static final String ALL = "所有";
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private UserDailyTaskService dailyTaskService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private SingleUserService singleUserService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserAttackDifficultyLogic attackDifficultyLogic;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;
    @Autowired
    private MallService mallService;
    @Autowired
    private UserCardService userCardService;


    /**
     * 限制登录
     *
     * @param sId
     * @param nickname
     * @param endDate  yyyy-MM-dd HH:mm:ss
     * @return
     */
    public Rst limitLogin(int sId, String nickname, String endDate) {
        if (StrUtil.isBlank(endDate)) {
            return Rst.businessFAIL("endDate不能为空");
        }
        Rst rst = manageGU(sId, nickname, (gu) -> {
            UserLimit userLimit = UserLimit.Instance(gu.getId(), gu.getRoleInfo().getUserName(), DateUtil.fromDateTimeString(endDate));
            this.gameUserService.addItem(gu.getId(), userLimit);
        });
        removeSession(sId, nickname);
        return rst;
    }

    /**
     * 限制发言
     *
     * @param sId
     * @param nickname
     * @param endDate  yyyy-MM-dd HH:mm:ss
     * @return
     */
    public Rst limitTalking(int sId, String nickname, String endDate) {
        if (StrUtil.isBlank(endDate)) {
            return Rst.businessFAIL("endDate不能为空");
        }
        Rst rst = manageGU(sId, nickname, (gu) -> {
            UserLimit userLimit = UserLimit.instanceTalkingLimit(gu.getId(), DateUtil.fromDateTimeString(endDate));
            this.gameUserService.addItem(gu.getId(), userLimit);
        });
        return rst;
    }

    private void removeSession(int sId, String nickname) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (guId.isPresent() && guId.get() > 0) {
            this.singleUserService.removeSessionId(guId.get());
        }
    }

    /**
     * 更新玩家到特定等级
     *
     * @param sId
     * @param nickname
     * @param level
     * @return
     */
    public Rst updateToLevel(int sId, String nickname, int level) {
        return manageGU(sId, nickname, (gu) -> {
            gu.incLevel(level - gu.getLevel());
            gu.addExperienceForGm(GameUserExpTool.getExpByLevel(level) - gu.getExperience());
            InsRoleInfoEntity role = new InsRoleInfoEntity();
            role.setUid(gu.getId());
            role.setLevel(level);
            updateRoleInfoAsyncHandler.setRoleInfo(role, 4);
        });
    }

    public Rst updateToAlmostLevel(int sId, String nickname, int level) {
        return manageGU(sId, nickname, (gu) -> {
            gu.incLevel(level - gu.getLevel() - 1);
            gu.addExperienceForGm(GameUserExpTool.getExpByLevel(level) - gu.getExperience() - 1);
            InsRoleInfoEntity role = new InsRoleInfoEntity();
            role.setUid(gu.getId());
            role.setLevel(level - 1);
            updateRoleInfoAsyncHandler.setRoleInfo(role, 4);
        });
    }

    /**
     * 设置新手引导的状态
     *
     * @param sId
     * @param nickname
     * @param guideStatus
     * @return
     */
    public Rst setGuideStatus(int sId, String nickname, int guideStatus) {
        return manageGU(sId, nickname, (gu) -> {
            UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(gu.getId());
            if (userNewerGuide == null) {
                boolean isPass = guideStatus == NewerGuideEnum.CARD_LEVEL_UP.getStep();
                userNewerGuide = UserNewerGuide.getInstance(gu.getId(), guideStatus, isPass);
                this.gameUserService.addItem(gu.getId(), userNewerGuide);
            } else {
                newerGuideService.updateNewerGuide(gu.getId(), NewerGuideEnum.fromValue(guideStatus), new RDCommon());
            }
        });
    }

    /**
     * 跳过新手进阶任务
     *
     * @param sId
     * @param nickname
     * @return
     */
    public Rst setPassGrowTasks(int sId, String nickname) {
        return manageGU(sId, nickname, (gu) -> {
            gu.getStatus().setGrowTaskCompleted(true);
            gu.updateStatus();
        });
    }

    /**
     * 将新手进阶任务调整为 已完成未领取 状态
     *
     * @param sId
     * @param nickname
     * @return
     */
    public Rst setGrowTaskStatus(int sId, String nickname, int index, int status) {
        CfgTaskConfig cfgTaskConfig = TaskTool.getTaskConfig(TaskGroupEnum.TASK_NEWBIE);
        Optional<CfgTaskEntity> task = cfgTaskConfig.getTasks().stream().filter(p -> p.getSeq() == index).findFirst();
        if (!task.isPresent()) {
            return Rst.businessFAIL("没有该序号的任务");
        }
        return manageGU(sId, nickname, (gu) -> {
            List<UserGrowTask> userGrowTasks = this.gameUserService.getMultiItems(gu.getId(), UserGrowTask.class);
            userGrowTasks.stream().filter(u -> u.getBaseId().equals(task.get().getId()))
                    .forEach(u -> u.setStatus(status));
            this.gameUserService.updateItems(userGrowTasks);
        });
    }


    /**
     * 将新手进阶任务调整为 已完成未领取 状态
     *
     * @param sId
     * @param nickname
     * @return
     */
    public Rst setGrowTaskStatus(int sId, String nickname) {
        return manageGU(sId, nickname, (gu) -> {
            List<UserGrowTask> userGrowTasks = this.gameUserService.getMultiItems(gu.getId(), UserGrowTask.class);
            userGrowTasks.forEach(u -> u.setStatus(TaskStatusEnum.ACCOMPLISHED.getValue()));
            this.gameUserService.updateItems(userGrowTasks);
        });
    }

    /**
     * 添加月卡的天数
     *
     * @param sId
     * @param nickname
     * @param days
     * @return
     */
    public Rst addYKEndTime(int sId, String nickname, int days) {
        return manageGU(sId, nickname, (gu) -> {
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
            Date ykEndDate = userPayInfo.getYkEndTime();
            if (ykEndDate == null) {
                ykEndDate = DateUtil.now();
            }
            ykEndDate = DateUtil.addDays(ykEndDate, days);
            userPayInfo.setYkEndTime(ykEndDate);
            gameUserService.updateItem(userPayInfo);
        });
    }

    /**
     * 添加季卡的天数
     *
     * @param sId
     * @param nickname
     * @param days
     * @return
     */
    public Rst addJKEndTime(int sId, String nickname, int days) {
        return manageGU(sId, nickname, (gu) -> {
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
            Date jkEndDate = userPayInfo.getJkEndTime();
            if (jkEndDate == null) {
                jkEndDate = DateUtil.now();
            }
            jkEndDate = DateUtil.addDays(jkEndDate, days);
            userPayInfo.setJkEndTime(jkEndDate);
            gameUserService.updateItem(userPayInfo);
        });
    }

    /**
     * 添加速战卡
     *
     * @param sId
     * @param nickname
     * @return
     */
    public Rst addSZK(int sId, String nickname) {
        return manageGU(sId, nickname, (gu) -> {
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
            userPayInfo.setEndFightBuyTime(DateUtil.now());
            gameUserService.updateItem(userPayInfo);
        });
    }

    /**
     * 设置性别
     *
     * @param sId
     * @param nickname
     * @param sex      1女2男
     * @return
     */
    public Rst setSex(int sId, String nickname, int sex) {
        return manageGU(sId, nickname, (gu) -> {
            gu.getRoleInfo().setSex(sex);
            if (sex == 1) {
                gu.getRoleInfo().setHead(51);
            } else {
                gu.getRoleInfo().setHead(1);
            }

            gu.updateRoleInfo();
        });
    }

    /**
     * 加铜钱
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    public Rst addCopper(int sId, String nickname, int num) {
        return manageGU(sId, nickname, (gu) -> {
            if (num > 0) {
                gu.addCopper(num, WayEnum.NONE);
            } else {
                gu.deductCopper(-num);
            }

        });
    }

    /**
     * 加钻石
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    public Rst addDiamond(int sId, String nickname, int num) {
        return manageGU(sId, nickname, (gu) -> {
            if (num > 0) {
                gu.addDiamond(num);
            } else {
                gu.deductDiamond(-num);
            }

        });
    }

    /**
     * 加元宝
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    public Rst addGold(int sId, String nickname, int num) {
        return manageGU(sId, nickname, (gu) -> {
            if (num > 0) {
                gu.addGold(num);
            } else {
                gu.deductGold(-num);
            }

        });
    }

    /**
     * 加体力
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    public Rst addDice(int sId, String nickname, int num) {
        return manageGU(sId, nickname, (gu) -> {
            if (num > 0) {
                gu.addDice(num);
            } else {
                gu.deductDice(-num);
            }

        });
    }

    /**
     * 加元素
     *
     * @param sId
     * @param nickname
     * @param type     金木水火土 10~50
     * @param num
     * @return
     */
    public Rst addEle(int sId, String nickname, int type, int num) {
        return manageGU(sId, nickname, (gu) -> {
            if (num > 0) {
                gu.addEle(type, num, WayEnum.NONE);
            } else {
                gu.deductEle(type, -num);
            }
        });
    }

    /**
     * 添加法宝
     *
     * @param sId
     * @param nickname
     * @param treasures 所有;七香车,定风珠
     * @param num
     * @return
     */
    public Rst addTreasures(int sId, String nickname, String treasures, int num) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgTreasureEntity> ctes = TreasureTool.getAllTreasures();
            if (!ALL.equals(treasures)) {
                String[] split = treasures.split(",");
                List<String> list = Arrays.asList(split);
                ctes = ctes.stream().filter(t -> list.contains(t.getName())).collect(Collectors.toList());
            }
            RDCommon rd = new RDCommon();
            if (num > 0) {
                List<EVTreasure> evTreasures = ctes.stream().map(t -> new EVTreasure(t.getId(), num))
                        .collect(Collectors.toList());
                TreasureEventPublisher.pubTAddEvent(gu.getId(), evTreasures, WayEnum.NONE, rd);
            } else {
                ctes.stream().forEach(t -> TreasureEventPublisher.pubTDeductEvent(gu.getId(), t.getId(), -num, WayEnum.NONE, rd));
            }

        });
    }

    public Rst addLikeTreasures(int sId, String nickname, String like, int num) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgTreasureEntity> ctes = TreasureTool.getAllTreasures();
            ctes = ctes.stream().filter(t -> t.getName().indexOf(like) > -1).collect(Collectors.toList());
            RDCommon rd = new RDCommon();
            if (num > 0) {
                List<EVTreasure> evTreasures = ctes.stream().map(t -> new EVTreasure(t.getId(), num))
                        .collect(Collectors.toList());
                TreasureEventPublisher.pubTAddEvent(gu.getId(), evTreasures, WayEnum.NONE, rd);
            } else {
                ctes.stream().forEach(t -> TreasureEventPublisher.pubTDeductEvent(gu.getId(), t.getId(), -num, WayEnum.NONE, rd));
            }

        });
    }

    public Rst addTreasuresByMail(int sId, String nickname, String treasures, int num, boolean like) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgTreasureEntity> ctes = TreasureTool.getAllTreasures();
            if (like) {
                ctes = ctes.stream().filter(t -> t.getName().indexOf(treasures) > -1).collect(Collectors.toList());
            } else if (!ALL.equals(treasures)) {
                String[] split = treasures.split(",");
                List<String> list = Arrays.asList(split);
                ctes = ctes.stream().filter(t -> list.contains(t.getName())).collect(Collectors.toList());
            }
            RDCommon rd = new RDCommon();
            if (num > 0 && !ctes.isEmpty()) {
                List<Award> awards = new ArrayList<>();
                for (CfgTreasureEntity entity : ctes) {
                    awards.add(Award.instance(entity.getId(), AwardEnum.FB, num));
                }
                String title = LM.I.getMsgByUid(gu.getId(), "mail.system.award.title");
                String content = LM.I.getMsgByUid(gu.getId(), "mail.system.award.content");
                mailService.sendAwardMail(title, content, gu.getId(), awards);
            }
        });
    }

    public Rst addSpecial(int sId, String nickname, String special, int num) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgSpecialEntity> ccs = Cfg.I.get(CfgSpecialEntity.class);
            ccs = ccs.stream().filter(c -> special.contains(c.getName())).collect(Collectors.toList());
            if (ccs.isEmpty()) {
                return;
            }
            List<Integer> specialIds = ccs.stream().map(CfgSpecialEntity::getId).collect(Collectors.toList());
            List<EVSpecialAdd> specialAdds = specialIds.stream().map(specialId -> new EVSpecialAdd(specialId % 1000, 100)).collect(Collectors.toList());
            for (int i = 0; i < num; i++) {
                SpecialEventPublisher.pubSpecialAddEvent(gu.getId(), specialAdds, WayEnum.NONE, new RDCommon());
            }
        });
    }

    /**
     * 加卡牌
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @return
     */
    public Rst addCards(int sId, String nickname, String cards) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgCardEntity> ccs = CardTool.getAllCards();
            if (!ALL.equals(cards)) {
                ccs = ccs.stream().filter(c -> cards.contains(c.getName())).collect(Collectors.toList());
            }
            List<Integer> cardIds = ccs.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
            CardEventPublisher.pubCardAddEvent(gu.getId(), cardIds, WayEnum.NONE, "", new RDCommon());
        });
    }

    /**
     * 删除卡牌
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @return
     */
    public Rst delCards(int sId, String nickname, String cards) {
        return manageGU(sId, nickname, (gu) -> {
            delCards(gu.getId(), cards);
        });
    }

    public Rst delCards(String uidsInfo, String cards) {
        List<Long> uids = ListUtil.parseStrToLongs(uidsInfo);
        uids.forEach(tmp -> {
            delCards(tmp, cards);
        });
        return Rst.businessOK();
    }

    public Rst fixUserGroup(String uidsInfo) {
        List<Long> uids = ListUtil.parseStrToLongs(uidsInfo);
        uids.forEach(tmp -> {
            fixUserGroup(tmp);
        });
        return Rst.businessOK();
    }

    private Rst fixUserGroup(long uid) {
        List<UserCardGroup> groups = gameUserService.getMultiItems(uid, UserCardGroup.class);
        if (ListUtil.isEmpty(groups)) {
            return Rst.businessOK();
        }
        List<Integer> userCardIds = gameUserService.getMultiItems(uid, UserCard.class).stream().mapToInt(UserCard::getBaseId).boxed().collect(Collectors.toList());
        for (UserCardGroup group : groups) {
            List<Integer> toRemoveIds = group.getCards().stream().filter(tmp -> !userCardIds.contains(tmp)).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(toRemoveIds)) {
                group.getCards().removeAll(toRemoveIds);
                gameUserService.updateItem(group);
            }
        }
        return Rst.businessOK();
    }

    private Rst delCards(long uid, String cards) {
        List<CfgCardEntity> ccs = CardTool.getAllCards();
        if (!ALL.equals(cards)) {
            ccs = ccs.stream().filter(c -> cards.contains(c.getName())).collect(Collectors.toList());
        }
        List<Integer> cardIds = ccs.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
        if (cardIds.contains(325)) {
            cardIds.add(10325);
        }
        List<UserCard> userCards = this.gameUserService.getCfgItems(uid, cardIds, UserCard.class);
        CardEventPublisher.pubCardDelEvent(uid, userCards);
//        userCards.stream().forEach(uc -> this.gameUserService.deleteItem(uc));
        List<UserCardGroup> groups = gameUserService.getMultiItems(uid, UserCardGroup.class);
        if (ListUtil.isEmpty(groups)) {
            return Rst.businessOK();
        }
        for (UserCardGroup group : groups) {
            boolean isMatch = group.getCards().stream().anyMatch(tmp -> cardIds.contains(tmp));
            if (isMatch) {
                group.getCards().removeAll(cardIds);
                gameUserService.updateItem(group);
            }
        }
        return Rst.businessOK();
    }

    /**
     * 添加灵石
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @param num
     * @return
     */
    public Rst addLingShi(int sId, String nickname, String cards, int num) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgCardEntity> ccs = Cfg.I.get(CfgCardEntity.class);
            if (!ALL.equals(cards)) {
                ccs = ccs.stream().filter(c -> cards.contains(c.getName())).collect(Collectors.toList());
            }
            List<Integer> cardIds = ccs.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
            List<UserCard> userCards = this.gameUserService.getCfgItems(gu.getId(), cardIds, UserCard.class);
            for (UserCard userCard : userCards) {
                if (num > 0) {
                    userCard.addLingshi(num);
                } else {
                    userCard.deductLingshi(-num);
                }
                this.gameUserService.updateItem(userCard);
            }
        });
    }

    /**
     * 调整卡牌等级
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @param minLevel 将>=minLevel的cards调为level
     * @param level
     * @return
     */
    public Rst updateCardToLevel(int sId, String nickname, String cards, int minLevel, int level) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgCardEntity> ccs = CardTool.getAllCardsIncludeDeifyCards();
            if (!ALL.equals(cards)) {
                ccs = ccs.stream().filter(c -> cards.contains(c.getName())).collect(Collectors.toList());
            }
            List<Integer> cardIds = ccs.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
            List<UserCard> userCards = this.gameUserService.getCfgItems(gu.getId(), cardIds, UserCard.class);
            for (UserCard userCard : userCards) {
                if (userCard.getLevel() < minLevel) {
                    continue;
                }
                userCard.setLevel(level);
                userCard.setExperience(CardExpTool.getExpByLevel(userCard.gainCard(), level));
                this.gameUserService.updateItem(userCard);
            }
        });
    }

    /**
     * 调整卡牌等级
     *
     * @param sId
     * @param nickname
     * @param cards    cardCfgId,lv,hie;cardCfgId,lv,hie 如果值小于0则忽略
     * @return
     */
    public Rst updateCardsToLvAndHie(int sId, String nickname, String cards) {
        return manageGU(sId, nickname, (gu) -> {
            String[] cardsInfo = cards.split(";");
            List<UserCard> userCardsToUpdate = new ArrayList<>();
            for (String cardInfo : cardsInfo) {
                String[] card = cardInfo.split(",");
                int cardId = Integer.valueOf(card[0]);
                int lv = Integer.valueOf(card[1]);
                long exp = Long.valueOf(card[2]);
                int hv = Integer.valueOf(card[3]);
                UserCard userCard = gameUserService.getCfgItem(gu.getId(), cardId, UserCard.class);
                if (null == userCard) {
                    continue;
                }
                if (lv >= 0) {
                    userCard.setLevel(lv);
                }
                if (exp >= 0) {
                    userCard.setExperience(exp);
                }
                if (hv >= 0) {
                    userCard.setHierarchy(hv);
                }
                userCardsToUpdate.add(userCard);
            }
            if (ListUtil.isNotEmpty(userCardsToUpdate)) {
                gameUserService.updateItems(userCardsToUpdate);
            }
        });
    }

    /**
     * 调整卡牌阶数
     *
     * @param sId
     * @param nickname
     * @param cards        所有;姜子牙,杨戬
     * @param minHierarchy 将>=minHierarchy的cards调为hierarchy
     * @param hierarchy
     * @return
     */
    public Rst updateCardToHierarchy(int sId, String nickname, String cards, int minHierarchy, int hierarchy) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgCardEntity> ccs = CardTool.getAllCardsIncludeDeifyCards();
            if (!ALL.equals(cards)) {
                ccs = ccs.stream().filter(c -> cards.contains(c.getName())).collect(Collectors.toList());
            }
            List<Integer> cardIds = ccs.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
            for (Integer cardId : cardIds) {
                UserCard userCard = userCardService.getUserNormalCardOrDeifyCard(gu.getId(), cardId);
                if (null == userCard) {
                    continue;
                }
                userCard.setHierarchy(hierarchy);
                this.gameUserService.updateItem(userCard);
            }
        });
    }

    /**
     * 重置礼包记录
     *
     * @param sId
     * @param nickname
     * @param mallEnum
     * @return
     */
    public Rst resetGiftRecord(int sId, String nickname, MallEnum mallEnum) {
        return manageGU(sId, nickname, (gu) -> {
            long guId = gu.getId();
            List<UserMallRecord> mallRecords = mallService.getUserMallRecord(guId, mallEnum);
            // 重置礼包记录
            if (ListUtil.isNotEmpty(mallRecords)) {
                this.gameUserService.deleteItems(guId, mallRecords);
            }
        });
    }

    /**
     * 加封地
     *
     * @param sId
     * @param nickname
     * @param cityNames
     * @return
     */
    public Rst addCities(int sId, String nickname, String cityNames, boolean useEvent) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgCityEntity> ccs = CityTool.getCities().stream().filter(tmp -> tmp.isCC()).collect(Collectors.toList());
            if (!ALL.equals(cityNames)) {
                ccs = ccs.stream().filter(c -> cityNames.contains(c.getName())).collect(Collectors.toList());
            }
            for (CfgCityEntity city : ccs) {
                UserCity uc = this.gameUserService.getCfgItem(gu.getId(), city.getId(), UserCity.class);
                if (uc != null && uc.isOwn()) {
                    continue;
                }
                if (useEvent) {
                    EPCityAdd ep = new EPCityAdd(city.getId(), false);
                    CityEventPublisher.pubUserCityAddEvent(gu.getId(), ep, new RDFightResult());
                } else {
                    if (uc == null) {
                        uc = UserCity.fromCfgCity(gu.getId(), city);
                        uc.setOwn(true);
                        gameUserService.addItem(gu.getId(), uc);
                        continue;
                    }
                    uc.setOwn(true);
                    gameUserService.updateItem(uc);
                }
            }
            if (!useEvent) {
                UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(gu.getId());
                attackDifficultyLogic.resettleAttackDifficulty(difficulty);
            }
        });
    }

    /**
     * 加梦魇封地
     *
     * @param sId
     * @param nickname
     * @param cityNames
     * @return
     */
    public Rst addNightmareCities(int sId, String nickname, String cityNames, boolean useEvent) {
        return manageGU(sId, nickname, (gu) -> {
            List<CfgCityEntity> ccs = CityTool.getCities().stream().filter(tmp -> tmp.isCC()).collect(Collectors.toList());
            if (!ALL.equals(cityNames)) {
                ccs = ccs.stream().filter(c -> cityNames.contains(c.getName())).collect(Collectors.toList());
            }
            for (CfgCityEntity city : ccs) {
                UserNightmareCity uc = this.gameUserService.getCfgItem(gu.getId(), city.getId(), UserNightmareCity.class);
                if (uc != null && uc.isOwn()) {
                    continue;
                }
                if (useEvent) {
                    EPCityAdd ep = new EPCityAdd(city.getId(), true);
                    CityEventPublisher.pubUserCityAddEvent(gu.getId(), ep, new RDFightResult());
                } else {
                    if (uc == null) {
                        //创建
                        uc = UserNightmareCity.getInstance(city, gu.getId());
                        uc.setOwn(true);
                        uc.setOwnTime(new Date());
                        gameUserService.addItem(gu.getId(), uc);
                        continue;
                    }
                    uc.setOwn(true);
                    uc.setOwnTime(new Date());
                    gameUserService.updateItem(uc);
                }
            }
            if (!useEvent) {
                UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(gu.getId());
                attackDifficultyLogic.resettleAttackDifficulty(difficulty);
            }

        });
    }


    public Rst showUserData(int sId, String nickname, String dataType) {
        Rst rst = Rst.businessOK();
        manageGU(sId, nickname, (gu) -> {
            List<UserData> uds = this.gameUserService.getMultiItems(gu.getId(), (Class<UserData>) UserDataType.fromRedisKey(dataType).getEntityClass());
            uds.forEach(tmp -> {
                rst.put(tmp.getId().toString(), tmp.toString());
            });
            rst.put("数据量：", uds.size());
        });
        return rst;
    }

    /**
     * 重置卡牌技能
     *
     * @param sid
     * @param nickname
     * @param cards
     * @return
     */
    public Rst resetCardSkill(int sid, String nickname, String cards) {
        return manageGU(sid, nickname, (gu) -> {
            String[] cardNames = cards.split(",");
            for (int i = 0; i < cardNames.length; i++) {
                CfgCardEntity cfgCardEntity = CardTool.getCardByName(cardNames[i]);
                UserCard uc = this.gameUserService.getCfgItem(gu.getId(), cfgCardEntity.getId(), UserCard.class);

                // 清除限制卷轴使用记录
                this.userTreasureRecordService.deductSkillScrollRecord(uc, 0);
                this.userTreasureRecordService.deductSkillScrollRecord(uc, 5);
                this.userTreasureRecordService.deductSkillScrollRecord(uc, 10);

                uc.resetSkill();
                this.gameUserService.updateItem(uc);
            }
        });
    }

    /**
     * 重置卡牌特定位置的技能
     *
     * @param sid
     * @param nickname
     * @param cardsWithPoes
     * @return
     */
    public Rst resetCardsSkillInPoses(int sid, String nickname, String cardsWithPoes) {
        return manageGU(sid, nickname, (gu) -> {
            CfgCard cfgCard = CardTool.getConfig();

            int addGold = 0;
            String[] cards = cardsWithPoes.split(";");
            for (int i = 0; i < cards.length; i++) {
                String[] cardWithPoses = cards[i].split("@");
                List<Integer> poses = ListUtil.parseStrToInts(cardWithPoses[1]);
                String cardName = cardWithPoses[0];
                CfgCardEntity cfgCardEntity = CardTool.getCardByName(cardName);
                UserCard uc = this.gameUserService.getCfgItem(gu.getId(), cfgCardEntity.getId(), UserCard.class);
                for (Integer pos : poses) {
                    this.userTreasureRecordService.deductSkillScrollRecord(uc, pos);
                    uc.resetSkill(pos);
                    addGold += cfgCard.getSkillChangePrice().get(cfgCardEntity.getStar() - 1);

                }
                this.gameUserService.updateItem(uc);
            }
            if (addGold > 0) {
                ResEventPublisher.pubGoldAddEvent(gu.getId(), addGold, WayEnum.GM, new RDCommon());
            }
        });
    }

    /**
     * 管理玩家数据
     *
     * @param sId
     * @param nickname
     * @param consumer
     * @return
     */
    private Rst manageGU(int sId, String nickname, Consumer<GameUser> consumer) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效的账号或者区服");
        }
        GameUser gu = this.gameUserService.getGameUser(guId.get());
        consumer.accept(gu);
        return Rst.businessOK();
    }

    /**
     * 修复玩家卡牌技能数据
     *
     * @param sId      区服
     * @param nickname 昵称
     * @return
     */
    public Rst repairUserSkillGroup(int sId, String nickname) {
        return manageGU(sId, nickname, (gu) -> {
            //获取用户拥有的全部卡信息
            List<UserCard> userCardList = userCardService.getUserCards(gu.getId());
            List<UserCard> newUserCards = new ArrayList<>();

            for (UserCard userCard : userCardList) {
                //判断该卡有没强化过技能
                if (userCard.getStrengthenInfo() == null) {
                    continue;
                }

                log.info("{}卡牌[{}]技能[{}]数据迁移。{}", gu.getId(), userCard.getBaseId(), userCard.gainSkills(), userCard.toString());
                UserCard.SkillGroup skillGroup = userCard.getStrengthenInfo().getSkillGroups().get(CardConstant.SKILL_GROUP_1);
                if (null == skillGroup) {
                    skillGroup = new UserCard.SkillGroup();
                    userCard.getStrengthenInfo().getSkillGroups().put(CardConstant.SKILL_GROUP_1, skillGroup);
                }
                //构建技能组
                if (userCard.getStrengthenInfo().getSkill0() != null) {
                    skillGroup.setS0(userCard.getStrengthenInfo().getSkill0());
                    userCard.getStrengthenInfo().setSkill0(null);
                }
                if (userCard.getStrengthenInfo().getSkill5() != null) {
                    skillGroup.setS5(userCard.getStrengthenInfo().getSkill5());
                    userCard.getStrengthenInfo().setSkill5(null);
                }
                if (userCard.getStrengthenInfo().getSkill10() != null) {
                    skillGroup.setS10(userCard.getStrengthenInfo().getSkill10());
                    userCard.getStrengthenInfo().setSkill10(null);
                }
                if (userCard.getStrengthenInfo().getLastSkillMap() != null) {
                    skillGroup.setLastSkills(userCard.getStrengthenInfo().getLastSkillMap());
                    userCard.getStrengthenInfo().setLastSkillMap(null);
                }
                if (userCard.getStrengthenInfo().getUsingSkillScrolls() != null) {
                    skillGroup.setUsingSkillScrolls(userCard.getStrengthenInfo().getUsingSkillScrolls());
                    userCard.getStrengthenInfo().setUsingSkillScrolls(null);
                }
                if (userCard.getStrengthenInfo().getUseSkillScrollTimes() != null) {
                    skillGroup.setUseSkillScrollTimes(userCard.getStrengthenInfo().getUseSkillScrollTimes());
                    userCard.getStrengthenInfo().setUseSkillScrollTimes(null);
                }
                newUserCards.add(userCard);
            }
            gameUserService.updateItems(newUserCards);
        });
    }
}
