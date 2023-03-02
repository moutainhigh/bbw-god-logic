package com.bbw.god.gm;

import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.UserCitySetting;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.CfgChannelService;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.achievement.*;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.config.GameUserExpTool;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.main.UserMainTaskService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.mall.cardshop.CardShopService;
import com.bbw.god.server.RoleVO;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountService;
import com.bbw.god.validator.GodValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 区服相关服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:30
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMRoleCreateCtrl extends AbstractController {
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private GameUserRedisUtil userRedis;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserMainTaskService userMainTaskService;
    @Autowired
    private CardShopService cardShopService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    private UserAchievementLogic userAchievementLogic;
    @Autowired
    private StatisticServiceFactory statisticServiceFactory;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private CfgChannelService cfgChannelService;

    @GetMapping("user!createRoles")
    public Rst createRole(String accounts) {
        int sid = 99;
        String[] usernames = accounts.split(",");
        for (String username : usernames) {
            Optional<InsRoleInfoEntity> role = roleInfo.getUidAtLoginServer(sid, username);
            // 账号已存在，则不再创建
            if (role.isPresent()) {
                continue;
            }
            // 初始化一个角色
            RoleVO roleVO = new RoleVO();
            roleVO.setServerId(sid);
            roleVO.setUserName(username);
            roleVO.setNickname(serverUserService.getRandomNickName());
            roleVO.setChannelCode("10");
            roleVO.setProperty("10");
            roleVO.setIp("127.0.0.1");
            GameUser gameUser = serverUserService.newGameUser(roleVO, 1000, DateUtil.getTodayInt());
            gameUser.incLevel(20);
            gameUser.addExperienceForGm(GameUserExpTool.getExpByLevel(gameUser.getLevel()));
            gameUser.getStatus().setGuideStatus(10);
            gameUser.updateStatus();
            System.out.println(gameUser.getId());
        }
        return Rst.businessOK();
    }

    @GetMapping("user!createRole")
    public Rst createRole(GMRoleVO param) {
        log.debug("server!createRole " + param);
        if (StrUtil.isBlank(param.getNickname())) {
            throw new ExceptionForClientTip("createrole.nickname.empty");
        }
        //除去昵称中的所有空格
        param.setNickname(param.getNickname().replaceAll(" ", ""));
        // 校验角色创建参数
        GodValidator.validateEntity(param);
        // 账号检查
        AccountEntity account = this.accountService.findByAccount(param.getUserName());
        if (account == null) {
            throw new ExceptionForClientTip("login.account.invalid");
        }
        // 验证敏感词汇
        CfgChannelEntity cfgChannel = cfgChannelService.getByPlatCode(param.getChannelCode()).get();
        if (SensitiveWordUtil.isNotPass(param.getNickname().trim(), cfgChannel.getId(), account.getOpenId())) {
            throw new ExceptionForClientTip("createrole.not.sensitive.words");
        }

        CfgServerEntity loginServer = Cfg.I.get(param.getServerId(), CfgServerEntity.class);
        // 合服后仍然保持原始区服入口，可以在原始区服创建角色，这里使用原始区服Id
        // 验证在原始区服是否存在账号
        Optional<InsRoleInfoEntity> role = this.roleInfo.getUidAtLoginServer(loginServer.getId(), param.getUserName());
        if (role.isPresent()) {
            throw new ExceptionForClientTip("createrole.has.role");
        }

        // 昵称必须在合服区里保持唯一，这里必须使用合服ID
        Optional<Long> uid = this.serverUserService.getUidByNickName(loginServer.getMergeSid(), param.getNickname());
        if (uid.isPresent()) {
            throw new ExceptionForClientTip("createrole.nickname.is.exist");
        }
        param.setMyInviCode(account.getInvitationCode());
        param.setIp(IpUtil.getIpAddr(this.request));
        // 创建角色信息
        GameUser user = this.serverUserService.newGameUser(param, cfgChannel.getId(), DateUtil.getTodayInt());
        this.gameUserService.setActiveSid(user.getId(), user.getServerId());
        handleAfterCreateRole(user.getId(), param);
        return Rst.businessOK("角色创建成功");
    }

    @GetMapping("user!fixRoleData")
    public Rst fixRoleData(GMRoleVO param) {

        CfgServerEntity loginServer = Cfg.I.get(param.getServerId(), CfgServerEntity.class);
        // 合服后仍然保持原始区服入口，可以在原始区服创建角色，这里使用原始区服Id
        // 验证在原始区服是否存在账号
        Optional<InsRoleInfoEntity> role = this.roleInfo.getUidAtLoginServer(loginServer.getId(), param.getUserName());
        if (!role.isPresent()) {
            return Rst.businessOK("角色不存在");
        }
        handleAfterCreateRole(role.get().getUid(), param);
        return Rst.businessOK("角色数据修复成功");
    }

    private void handleAfterCreateRole(long uid, GMRoleVO param) {
        GameUser user = gameUserService.getGameUser(uid);
        user.setLevel(param.getLevel());
        user.setExperience(param.getExp());
        user.setCopper(param.getCopper());
        user.setGold(param.getGold());
        String[] eles = param.getEles().split(",");
        user.setGoldEle(Integer.valueOf(eles[0]));
        user.setWoodEle(Integer.valueOf(eles[1]));
        user.setWaterEle(Integer.valueOf(eles[2]));
        user.setFireEle(Integer.valueOf(eles[3]));
        user.setEarthEle(Integer.valueOf(eles[4]));
        userRedis.toRedis(user);
        //添加卡牌
        addCards(user.getId(), param.getCardInfo());
        //添加法宝
        addTreasures(user.getId(), param.getTreasureInfo());
        //添加城池
        addCities(user.getId(), param.getCityInfo());
        //跳过新手引导
        this.userGmService.setGuideStatus(param.getServerId(), param.getNickname(), 10);
        //跳过新手任务
        this.userGmService.setPassGrowTasks(param.getServerId(), param.getNickname());
        // 初始化城池设置
        UserCitySetting ucSetting = this.gameUserService.getSingleItem(uid, UserCitySetting.class);
        if (ucSetting == null) {
            ucSetting = UserCitySetting.instance(uid);
            this.gameUserService.addItem(uid, ucSetting);
        }
        // 初始化卡池
        this.cardShopService.initUserCardPool(uid);
        // 添加userLogin对象
        UserLoginInfo userLoginInfo = this.gameUserService.getSingleItem(uid, UserLoginInfo.class);
        if (null == userLoginInfo) {
            userLoginInfo = UserLoginInfo.instance(uid);
            userLoginInfo.setLastLoginTime(DateUtil.addDays(DateUtil.now(), -1));
            gameUserService.addItem(uid, userLoginInfo);
        }
        // 初始化主线任务
        List<UserCard> userCards = gameUserService.getMultiItems(uid, UserCard.class);
        List<UserCity> userCities = gameUserService.getMultiItems(uid, UserCity.class);
        List<UserMainTask> umts = this.userMainTaskService.getUserMainTasks(uid);
        if (ListUtil.isEmpty(umts)) {
            List<CfgTaskEntity> mainTasks = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.TASK_MAIN);
            mainTasks.forEach(tmp -> {
                UserMainTask umt = UserMainTask.fromTask(uid, tmp);
                this.userMainTaskService.addUserMainTask(uid, umt);
            });
            umts = this.userMainTaskService.getUserMainTasks(uid);
        }
        umts.forEach(umt -> {
            if (umt.getBaseId() == 1100) {//城池数
                umt.setAwardedIndex(userCities.size());
                umt.setEnableAwardIndex(userCities.size());
            } else if (umt.getBaseId() == 1200) {// 升满5级
                int num5 = (int) userCities.stream().filter(tmp -> tmp.ifUpdate5()).count();
                umt.setAwardedIndex(num5);
                umt.setEnableAwardIndex(num5);
            } else if (umt.getBaseId() == 1300) {//卡牌数
                umt.setAwardedIndex(userCards.size());
                umt.setEnableAwardIndex(userCards.size());
            }
        });
        gameUserService.updateItems(umts);
        // 重新初始化统计数据
        statisticServiceFactory.init(uid);
        // 修复成就
        List<Integer> levelAchievementIds = Arrays.asList(10, 20, 30, 40, 50, 55, 60);
        List<Integer> cardAchievementIds = AchievementTool.getAchievements(AchievementTypeEnum.CARD)
                .stream().map(CfgAchievementEntity::getId).collect(Collectors.toList());
        List<Integer> cityAchievementIds = Arrays.asList(510, 520, 525, 530, 535, 536, 541, 542, 543, 544, 545, 550);
        // 获取对象
        UserAchievementInfo achievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == achievementInfo) {
            achievementInfo = userAchievementLogic.initUserAchievementInfo(uid);
            gameUserService.addItem(uid, achievementInfo);
        }
        // 等级成就
        for (Integer levelAchievementId : levelAchievementIds) {
            Integer needValue = AchievementTool.getAchievement(levelAchievementId).getValue();
            if (user.getLevel() >= needValue) {
                achievementInfo.awardedAchievement(levelAchievementId);
            }
        }
        // 卡牌成就
        for (Integer cardAchievementId : cardAchievementIds) {
            setCardOrCityAchievementAwarded(uid, achievementInfo, cardAchievementId);
        }
        // 城池成就
        for (Integer cityAchievementId : cityAchievementIds) {
            setCardOrCityAchievementAwarded(uid, achievementInfo, cityAchievementId);
        }
        // 保存数据
        gameUserService.updateItem(achievementInfo);
    }

    /**
     * 将卡牌或者城池成就设置为已领取状态
     *
     * @param uid
     * @param achievementInfo
     * @param cityAchievementId
     */
    private void setCardOrCityAchievementAwarded(long uid, UserAchievementInfo achievementInfo, Integer cityAchievementId) {
        BaseAchievementService service = achievementServiceFactory.getById(cityAchievementId);
        int value = service.getMyValueForAchieve(uid, achievementInfo);
        Integer needValue = AchievementTool.getAchievement(cityAchievementId).getValue();
        if (value >= needValue) {
            achievementInfo.awardedAchievement(cityAchievementId);
        }
    }

    //    @GetMapping("user!makeCreateRoleParam")
    public String makeCreateRoleParam(String db, String username, String nickname, int sex) {
        StringBuilder url = new StringBuilder();
        url.append("gm/user!createRole");
        url.append("?sex=" + sex);
        url.append("&head=1");
        url.append("&userName=" + username);
        url.append("&nickname=" + nickname);
        url.append("&serverId=2069&sid=2069");
        url.append("&deviceId=nodevice&channelCode=78000&invitationCode=&versionCode=380");
        Map<String, Object> gu = jdbcTemplate.queryForMap("select id,property,level,experience,copper,gold,gold_ele,wood_ele,warter_ele,fire_ele,earth_ele from " + db + ".game_user where user_name = '" + username + "'");
        int guId = (int) gu.get("id");
        url.append("&property=" + gu.get("property"));
        url.append("&level=" + gu.get("level"));
        url.append("&exp=" + gu.get("experience"));
        url.append("&copper=" + gu.get("copper"));
        url.append("&gold=" + gu.get("gold"));
        String eles = gu.get("gold_ele") + "," + gu.get("wood_ele") + "," + gu.get("warter_ele") + "," + gu.get("fire_ele") + "," + gu.get("earth_ele");
        url.append("&eles=" + eles);
        List<Map<String, Object>> cards = jdbcTemplate.queryForList("select card_id,level,experience,hierarchy,lingshi from " + db + ".card where game_user_id = " + guId);
        List<Map<String, Object>> treasures = jdbcTemplate.queryForList("select treasure_id,quantity from " + db + ".user_treasure where game_user_id = " + guId);
        List<Map<String, Object>> cities = jdbcTemplate.queryForList("select city_id,hierarchy,fy,kc,qz,tcp,jxz,lbl,dc,ldf from " + db + ".manor where game_user_id = " + guId);
        String cardsInfo = "";
        for (Map<String, Object> card : cards) {
            if (cardsInfo.length() > 0) {
                cardsInfo += ";";
            }
            cardsInfo += card.get("card_id") + "_" + card.get("experience") + "_" + card.get("level") + "_" + card.get("hierarchy") + "_" + card.get("lingshi");
        }
        url.append("&cardInfo=" + cardsInfo);
        String treasuresInfo = "";
        for (Map<String, Object> treasure : treasures) {
            if (treasuresInfo.length() > 0) {
                treasuresInfo += ";";
            }
            treasuresInfo += treasure.get("treasure_id") + "_" + treasure.get("quantity");
        }
        url.append("&treasureInfo=" + treasuresInfo);
        String citiesInfo = "";
        for (Map<String, Object> city : cities) {
            if (citiesInfo.length() > 0) {
                citiesInfo += ";";
            }
            citiesInfo += city.get("city_id") + "_" + city.get("hierarchy") + "_" + city.get("fy") + "_" + city.get("kc") + "_" + city.get("qz") + "_" + city.get("tcp") + "_" + city.get("jxz") + "_" + city.get("lbl") + "_" + city.get("dc") + "_" + city.get("ldf");
        }
        url.append("&cityInfo=" + citiesInfo);
        return url.toString();
    }

    /**
     * @param uid
     * @param cardsInfo 卡牌ID_经验_等级_阶数_灵石数
     */
    private void addCards(long uid, String cardsInfo) {
        if (StrUtil.isEmpty(cardsInfo)) {
            return;
        }
        List<UserCard> updates = new ArrayList<>();
        List<UserCard> adds = new ArrayList<>();
        String[] cards = cardsInfo.split(";");
        for (String card : cards) {
            String[] cardData = card.split("_");
            int cardId = Integer.valueOf(cardData[0]);
            long exp = Long.valueOf(cardData[1]);
            int lv = Integer.valueOf(cardData[2]);
            int hie = Integer.valueOf(cardData[3]);
            int lingshi = Integer.valueOf(cardData[4]);
            UserCard userCard = gameUserService.getCfgItem(uid, cardId, UserCard.class);
            boolean isOwn = userCard != null;
            if (!isOwn) {
                CfgCardEntity cardEntity = CardTool.getCardById(cardId);
                userCard = UserCard.fromCfgCard(uid, cardEntity, WayEnum.GM);
            }
            userCard.setExperience(exp);
            userCard.setLevel(lv);
            userCard.setHierarchy(hie);
            userCard.setLingshi(lingshi);
            if (isOwn) {
                updates.add(userCard);
            } else {
                adds.add(userCard);
            }
        }
        if (ListUtil.isNotEmpty(updates)) {
            gameUserService.updateItems(updates);
        }
        if (ListUtil.isNotEmpty(adds)) {
            gameUserService.addItems(adds);
        }
    }

    /**
     * @param uid
     * @param treasuresInfo 法宝ID_法宝数量
     */
    private void addTreasures(long uid, String treasuresInfo) {
        if (StrUtil.isEmpty(treasuresInfo)) {
            return;
        }
        List<UserTreasure> updates = new ArrayList<>();
        List<UserTreasure> adds = new ArrayList<>();
        String[] treasures = treasuresInfo.split(";");
        for (String treasure : treasures) {
            String[] treasureData = treasure.split("_");
            int treasureId = Integer.valueOf(treasureData[0]);
            int num = Integer.valueOf(treasureData[1]);
            UserTreasure userTreasure = userTreasureService.getUserTreasure(uid, treasureId);
            if (userTreasure != null) {
                userTreasure.setOwnNum(num);
                updates.add(userTreasure);
                continue;
            }
            CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(treasureId);
            if (treasureEntity == null) {
                continue;
            }
            userTreasure = UserTreasure.instance(uid, treasureEntity, num);
            adds.add(userTreasure);
        }
        if (ListUtil.isNotEmpty(updates)) {
            gameUserService.updateItems(updates);
        }
        if (ListUtil.isNotEmpty(adds)) {
            gameUserService.addItems(adds);
        }
    }


    /**
     * @param uid
     * @param citiesInfo 城池ID_阶数_府衙_矿场_钱庄_特产铺_聚贤庄_炼宝炉_道场_炼丹房
     */
    private void addCities(long uid, String citiesInfo) {
        if (StrUtil.isEmpty(citiesInfo)) {
            return;
        }
        List<UserCity> updates = new ArrayList<>();
        List<UserCity> adds = new ArrayList<>();
        String[] cities = citiesInfo.split(";");
        for (String city : cities) {
            String[] cityData = city.split("_");
            System.err.println("city = " + city);
            int cityId = Integer.parseInt(cityData[0]);
            int hie = Integer.parseInt(cityData[1]);
            int fy = Integer.parseInt(cityData[2]);
            int kc = Integer.parseInt(cityData[3]);
            int qz = Integer.parseInt(cityData[4]);
            int tcp = Integer.parseInt(cityData[5]);
            int jxz = Integer.parseInt(cityData[6]);
            int lbl = Integer.parseInt(cityData[7]);
            int dc = Integer.parseInt(cityData[8]);
            int ldf = Integer.parseInt(cityData[9]);
            UserCity userCity = gameUserService.getCfgItem(uid, cityId, UserCity.class);
            boolean isOwn = userCity != null;
            if (!isOwn) {
                CfgCityEntity cityEntity = CityTool.getCityById(cityId);
                userCity = UserCity.fromCfgCity(uid, cityEntity);
            }
            userCity.setHierarchy(hie);
            userCity.setFy(fy);
            userCity.setKc(kc);
            userCity.setQz(qz);
            userCity.setTcp(tcp);
            userCity.setJxz(jxz);
            userCity.setLbl(lbl);
            userCity.setDc(dc);
            userCity.setLdf(ldf);
            userCity.setOwn(true);
            if (isOwn) {
                updates.add(userCity);
            } else {
                adds.add(userCity);
            }
        }
        if (ListUtil.isNotEmpty(updates)) {
            gameUserService.updateItems(updates);
        }
        if (ListUtil.isNotEmpty(adds)) {
            gameUserService.addItems(adds);
        }
    }
}
