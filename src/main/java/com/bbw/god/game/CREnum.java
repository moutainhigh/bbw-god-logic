package com.bbw.god.game;

import lombok.Getter;

/**
 * 客户端请求接口
 *
 * @author suhq
 * @date 2018年11月5日 下午3:27:26
 */
@Getter
public enum CREnum {
    NONE("NONE", "未知"),
    ACTIVITY_LIST("activity!listActivities", "获取活动列表"),///
    ACTIVITY_LIST_V2("activity!listActivities!v2", "获取活动列表2"),///
    ACTIVITY_RECEIVE_AWARD("activity!joinActivity", "领取活动奖励"),
    ACTIVITY_RECEIVE_TASK_AWARD("activity!receiveTaskAward", "领取活动任务奖励"),
    ACTIVITY_EXCHANGE_GOOD("activity!exchangeGood", "活动兑换商品"),
    ACTIVITY_REPLENISH("activity!replenish", "活动补签"),
    ACTIVITY_SET_AWARD_ITEM("activity!setAwardItem", "活动指定奖励"),
    ACTIVITY_ENTER_LOTTERY("activity!enterLottery", "进入活动抽奖"),
    ACTIVITY_PREVIEW_LOTTERY_AWARD("activity!previewLotteryAward", "预览抽奖奖励"),
    ACTIVITY_DRAW_LOTTERY("activity!drawLottery", "活动抽奖"),
    ACTIVITY_REFRESH_LOTTERY_AWARD("activity!refreshLotteryAward", "刷新抽奖奖励"),
    RANK_ACTIVITY_LIST("activity!listRankActivities", "获取冲榜活动"),
    RANK_ACTIVITY_LIST_AWARDS("activity!listRankersAwards", "获取冲榜排行奖励"),

    CARD_SET_FIGHT_CARDS("card!setFightCards", "编组"),
    CARD_UPDATE("card!updateCard", "卡牌升级"),
    CARD_ADVANCED("card!updateHierarchy", "卡牌进阶"),
    CARD_SET_DEFAULT_DECK("card!setDefaultDeck", "设置默认卡组"),
    CARD_USE_SKILL_SCROLL("card!useSkillScroll", "炼技"),
    CARD_CLEAR_SKILL_SCROLL("card!clearSkillScroll", "重置"),
    CARD_TAKE_OUT_SKILL_SCROLL("card!takeOutSkillScroll", "取回技能"),
    CARD_PUT_ON_SYMBOL("card!putOnSymbol", "修体-穿"),
    CARD_UNLOAD_SYMBOL("card!unloadSymbol", "修体-卸"),
    CARD_SET_GROUP_NAME("card!setGroupName", "修改卡组昵称"),
    CARD_SHARE_CARD_GROUP("card!shareCardGroup", "分享卡组"),
    CARD_GET_SHARE_CARD_GROUP("card!getShareCardGroup", "获取分享的卡组信息"),
    CARD_SET_SHOW_CARDS("card!setShowCards", "设置展示卡"),
    CARD_SET_FIERCE_FIGHTING_CARDS("card!setFierceFightingCards", "设置封神台和诛仙阵的攻防卡组"),
    CARD_GET_FIERCE_FIGHTING_CARDS("card!getFierceFightingCards", "设置封神台和诛仙阵的攻防卡组"),
    CARD_COLLECT_CARD_GROUP("card!collectCardGroup", "收藏卡组"),
    CARD_GET_COLLECT_CARD_GROUP("card!getCollectCardGroups", "获取收藏卡组信息"),
    CARD_DEL_COLLECT_CARD_GROUP("card!delCollectCardGroup", "取消收藏卡组"),
    JuL_LIST("card!gainJLCards", "获得聚灵卡牌"),////
    JuL_JU_LING("card!juLing", "聚灵"),

    CARD_SHOP_GET_INFO("cardShop!getCardShopInfo", "获得卡牌商店信息"),
    CARD_POOL_GET__INFO("cardShop!getCardPoolInfo", "获得卡池信息"),
    CARD_POOL_GET_WISH_POOL_INFO("cardShop!getWishPoolInfo", "获得许愿卡池列表"),
    CARD_POOL_ACTIVE("cardShop!activeCardPool", "激活卡池"),
    CARD_POOL_ADD_WISH_CARD("cardShop!addToPool", "将许愿卡加入卡池"),
    CARD_POOL_DRAW("cardShop!draw", "抽卡"),////

    CC_SUBMIT_ATTACK_CITY("city!submitAttackCity", "提交攻城结果"),
    CC_LIST_CITY_TRADE_INFO("city!listCityTradeInfo", "获取城内交易信息"),
    CC_REFRESH_TRADE_SPECIALS("city!refreshTradeSpecials", "刷新城内可购特产"),
    CC_BUY_SPECIAL("special!buy", "购买特产"),
    CC_SELL_SPECIAL("special!sell", "出售特产"),
    CC_GET_PROMOTE_INFO("city!promoteInfo", "振兴信息"),
    CC_INVESTIGATE_CITY("city!investigate", "侦查"),
    CC_GET_INFO("city!getCityInfo", "点击城池查看信息"),
    CC_ARRIVE_INFO("city!arriveCityInfo", "获取当前城池的城池访问信息"),
    CHANGE_WORD("city!changeWorld", "世界跳转"),
    CC_GAIN_LEVEL_AWARD("city!gainLevelAward", "领取城池关卡奖励"),
    CC_GET_ATTACK_CARD_GROUP("city!getCardGroup", "梦魇@获取战斗卡组"),
    CC_SET_ATTACK_CARD_GROUP("city!setCardGroup", "@梦魇@设置战斗卡组"),
    CC_AUTO_BUY_SPECIAL("special!autoBuy", "城池交易@一键购买"),
    CC_AUTO_SELL_SPECIAL("special!autoSell", "城池交易@一键出售"),
    CC_SYN_ATTACK_CARD_GROUP("city!synCardGroup", "梦魇@同步卡组"),
    CC_TRAINING("city!attackCity", "练兵"),

    CC_IN_GET_ALL_OUTPUT("cityIn!gainAllOutput", "城内一键领取"),
    CC_IN_INTO("cityIn!intoCity", "进入城内"),
    CC_IN_KC_GET("cityIn!kcGetEle", "矿场领元素"),
    CC_IN_SET_DEFAULT_ELES("cityIn!setDefaultEles", "矿场设置元素优先"),
    CC_IN_QZ_GET("cityIn!qzGet", "钱庄领取"),
    CC_IN_LBL_GET("cityIn!lblGet", "炼宝炉领取"),
    CC_IN_UPDATE("cityIn!update", "建筑升级"),
    CC_IN_JXZ_GET_CARD("cityIn!jxzGetCard", "聚贤庄领卡"),
    CC_IN_JXZ_GET("cityIn!jxzGet", "聚贤庄领取"),
    CC_IN_LDF_GET("cityIn!promoteCard", "炼丹房升级卡牌"),
    CC_IN_SET_LDF_CARD("cityIn!setLdfCard", "炼丹房设置默认卡牌"),

    MiaoY_DRAW_LOTS("city!drawLots", "庙宇抽签"),
    FuLX_LIST_LAST_FLX_RESULTS("city!listLastFlxResults", "富临轩获取近期开奖"),
    FuLX_BET_SG("city!betSG", "数馆投注"),
    FuLX_BET_YSG("city!betYSG", "元素馆投足"),
    HeiS_BUY("city!buyHS", "黑市购买"),
    NvWM_DRAW("city!donateNWM", "女娲庙捐赠"),
    TaiYF_FILL("city!fillTYF", "太一府捐献"),
    KeZ_RECRUIT("city!recruitCard", "客栈购买卡牌"),
    YouSG_BUY("city!buySpecial", "游商馆购买特产"),
    LuT_TRIBUTE("city!tribute", "鹿台捐献"),
    MiXD_OUT("city!outMXD", "出迷仙洞"),

    MALL_LIST_GOODS("mall!listProducts", "获取商品列表"),////
    MALL_BUY("mall!buy", "商店购买"),
    MALL_REFRESH_MYSTERIOUS("mall!refreshMysteriousTreasures", "刷新神秘商店"),
    MALL_GET_INFO("mall!getMallInfo", "获取物品信息，当前只支持助力礼包"),
    MALL_GET_GOODS_INFO("mall!getGoodsInfo", "获取某个商品的信息和购买次数"),

    STORE_LIST_MALLS("store!listProducts", "获取商品列表2"),////
    STORE_BUY("store!buy", "商店购买2"),

    MAOU_GET("maou!gainMaou", "获取魔王信息"),
    MAOU_REFRESH("maou!refreshMaou", "客户端定时刷新魔王信息"),
    MAOU_SET_CARDS("gu!setMaouCards", "设置攻打魔王的卡牌"),
    MAOU_GET_ATTACKING_INFO("maou!getAttackingInfo", "进入攻击界面信息"),
    MAOU_GET_RANKERS("maou!listRankings", "获取魔王排行"),
    MAOU_GET_RANKER_AWARDS("maou!getRankAwards", "获取魔王排行奖励"),
    ALONE_MAOU_RESET_LEVEL("maou!resetMaouLevel", "重置独战魔王级别"),
    MAOU_ATTACK("maou!attack", "攻打魔王。userGold:0常规攻击；1元宝1倍攻击；2元宝多倍攻击。maouKind:10独战魔王；20魔王降临"),////
    MAOU_GET_MAOU_WITH_RANERS("maou!gainInfoIncludeRankings", "获取魔王信息(包含排名)"),
    MAOU_GET_REMAIN_BLOOD("maou!gainRemainBlood", "获取魔王剩余血量"),
    MAOU_CONFIRM_MAOU("maou!confirmMaou", "选择魔王"),
    ALONE_MAOU_GET_AWARD("maou!getAloneMaouAward", "获得独战魔王奖励"),

    EXCHANGE("gu!gainPacks", "兑换码领取"),

    LIST_PRODUCTS("product!listProducts", "获得充值产品"),
    WECHAT_PRODUCTS("webProduct!listProducts", "微信公众号产品列表"),
    NOTIFY("product!notify", "支付通知"),
    CAN_BUG("product!canbuy", "支付校验"),

    FST_LIST_RANKERS("gu!listPvpRank", "获取封神台排行"),
    FST_CHALLENGE("pvp!challenge", "封神台挑战"),
    FST_SUBMIT_RESULT("pvp!submitChallengeResult", "提交封神台挑战结果"),
    FST_LIST_CONVERTIBLE_GOODS("pvp!listConvertibleGoods", "获取可兑换的道具"),
    FST_EXCHANGE("pvp!exchange", "兑换"),
    FST_GAIN_INCREMENT_POINT("pvp!gainIncrementPoint", "封神台领取积分"),

    // CombatVideo
    SAVE_VIDEO("combatVideo!save", "保存战斗录像"),
    COLLECT_VIDEO("combatVideo!collect", "收藏录像"),
    DEL_VIDEO("combatVideo!del", "删除录像"),
    SHARE_VIDEO("combatVideo!share", "分享录像"),
    LIST_VIDEO("combatVideo!list", "获取保存的视频列表"),
    SHARE_VICTORY("combatVideo!shareVictory", "胜利分享"),
    STRATEGY_LIST_VIDEO("combatVideo!strategyList", "获取攻略视频"),

    // FsHelper
    ADD_TASK("fsHepler!addTask", "添加任务到封神助手"),
    DEL_TASK("fsHepler!delTask", "从封神助手上删除任务"),
    LIST_TASK("fsHepler!listTask", "获取封神助手任务列表"),

    // UserAsset
    LIST_PACKAGE("userAsset!listPackage", "获取玩家的道具资源"),
    LIST_FAST_TREASURE("userAsset!listFastTreasure", "获取快捷地图法宝以及对应的状态"),

    // CardComment
    GET_COMMENTS("comment!getCardComments", "获取卡牌评论"),
    GET_RECENT_COMMENTS("comment!getRecentCardComments", "获取卡牌最近评论"),
    ADD_FAVORITE("comment!addCardFavorite", "给卡牌评论点赞"),
    COMMENT("comment!cardComment", "发表卡牌评论"),
    UPDATE_COMMENT("comment!updateCardComment", "修改评论"),

    // CombatPVE
    ATTACK("combat!attackCity", "pve战斗"),
    AGAIN("combat!attackAgain", "再战"),
    RAPID_STRIKE("combat!autoEndCombat", "速战"),
    USE_WEAPON("combat!useWeapon", "使用法宝"),
    NEXT_ROUND("combat!nextRound", "下一回合"),
    RECOVER_DATA("combat!recoverAttack", "恢复战斗数据"),
    SURRENDER("combat!surrender", "投降"),
    ESCAPE("combat!escape", "逃跑 目前逃跑接口 只用于打野怪 其他类型的战斗未实现对应的方法"),
    ACCOMPLISH_ACHIEVEMENT("combat!accomplishAchievement", "完成特殊的战斗成就"),

    // WanXian
    GET_WANXIAN_TYPE("wanxian!getWanxianType", "获取当前万仙阵类型"),
    MAIN_PAGE("wanxian!mainPage", "万仙主页"),
    LIST_QUALIFYING_RANK("wanxian!listQualifyingRank", "万仙阵资格赛榜单翻页"),
    LIST_ELIMINATION_SERIES_GROUP("wanxian!listEliminationSeriesGroup", "万仙阵资格赛榜单翻页"),
    HISTORY_SEASON("wanxian!historySeason", "万仙阵历史荣誉殿堂"),
    CHAMPION_PREDICTION_PAGE("wanxian!championPredictionPage", "万仙阵冠军预测界面"),
    CHAMPION_PREDICTION("wanxian!championPrediction", "万仙阵冠军预测"),
    SIGN_UP("wanxian!signUp", "万仙阵报名"),
    SAVE_CARDGROUP("wanxian!saveCardGroup", "万仙阵保存卡组"),
    LIST_FIGHT_LOGS("wanxian!listFightLogs", "万仙阵战报记录"),
    LIST_AWARDS("wanxian!listAwards", "万仙阵获取奖励预览"),
    GET_CARD_GROUP("wanxian!getCardGroup", "获取万仙阵卡组"),
    PLAY_VIDEO("wanxian!playVideo", "万仙阵记录点击预测界面中的选手战绩 查看视频的玩家流程"),
    LOGS_BY_VIDKEY("wanxian!logsByVidKey", "万仙阵获取战斗日志"),

    // SnatchTreasure
    ENTER_SNATCH_TREASURE("snatchTreasure!enterSnatchTreasure", "进入夺宝界面"),
    DRAW("snatchTreasure!draw", "夺宝抽奖"),
    OPEN_WEEK_BOX("snatchTreasure!openWeekBox", "开启夺宝周累计宝箱"),
    GET_WEEK_BOX_AWARD("snatchTreasure!getWeekBoxAward", "查看夺宝周累计宝箱奖励"),

    // Lottery
    ENTER_LOTTERY("lottery!enterLottery", "进入奖券界面"),
    BET("lottery!bet", "幸运奖券@下注"),

    // RechargeActivities
    LIST("rechargeActivities!list", "获取奇珍列表"),////
    GAIN_AWARD("rechargeActivities!gainAward", "奇珍礼包购买后领取奖励"),
    BUY_AWARD("rechargeActivities!buyAward", "奇珍礼包元宝购买"),
    PICK_AWARD("rechargeActivities!pickAward", "奇珍礼包选择奖励"),

    // MaouAuction
    GET_AUCTION_INFO("maouAuction!getAuctionInfo", "获取魔王拍卖信息"),
    AUCTION_BID("maouAuction!bid", "魔王拍卖出价"),

    // Questionnaire
    QUESTIONNAIRE_JOIN("questionnaire!join", "参与问卷调查"),
    QUESTIONNAIRE_HIDE_ICON("questionnaire!hideIcon", "隐藏问卷调查图标"),

    // Bag
    BUY_BAG("bag!buyBag", "购买背包格子"),

    // CardSkillRecommend
    RecommendList("cardSkillRecommend!list", "获取技能推荐列表"),
    RecommendComment("cardSkillRecommend!comment", "技能推荐点赞、踩"),

    // Dfdj
    GET_FIGHTER_INFO("dfdj!getFighterInfo", "获得玩家巅峰对决信息"),
    GET_FIGHTER_RANK("dfdj!getFighterRank", "获得巅峰对决排行"),
    GET_BEAN_INFO("dfdj!getBeanInfo", "获取巅峰对决金豆信息"),
    BUY_BEAN("dfdj!buyBean", "巅峰对决买金豆"),
    BUY_MEDICINE("dfdj!buyMedicine", "巅峰对决购买丹药"),
    GET_RANK_AWARD("dfdj!getRankAward", "巅峰对决获取排行榜奖励"),
    GET_LAST_SEASON_RANK_AWARD("dfdj!getLastSeasonRankers", "巅峰对决获取上个阶段的排行"),
    ENABLE_MEDICINE("dfdj!enableMedicine", "巅峰对决使用丹药"),
    GET_SPRINT_AWARD("dfdj!getSprintAward", "巅峰对决每日冲刺福利"),
    DFDJ_GET_CARD_GROUP("dfdj!getCardGroup", "巅峰对决获得卡组"),
    DFDJ_SET_CARD_GROUP("dfdj!setCardGroup", "巅峰对决设置卡组"),

    // SkillScroll
    SYNTHESIS_SKILL_SCROLL("skillScroll!synthesis", "合成技能卷轴"),
    LIST_ABLE_SYNTHESIS_SKILL_SCROLLS("skillScroll!listAbleSynthesis", "获取可指定的技能卷轴列表"),

    // FsFight
    GET_GU_INFO("fsfight!listGuInfoForFsFight", "玩家登陆玩家竞技调用"),
    GET_ROBOT_INFO("fsfight!gainRobotInfoForFsFight", "神仙大会匹配到机器人调用"),
    SYNC_BUY("fsfight!syncBuy", "同步购买灵石"),
    SYNC_SXDH_CARD_REFRESH("fsfight!syncSxdhCardRefresh", "神仙大会同步刷新卡牌"),
    SYNC_TREASURES("fsfight!syncTreasuresForFight", "神仙大会同步法宝，扣除战斗中使用的法宝"),
    SYNC_TICKET("fsfight!syncTicket", "神仙大会成功匹配后同步门票"),
    TO_MATCH("fsfight!toMatch", "加入匹配队列前调用"), ////
    SUBMIT_SXDH_FIGHT_RESULT("fsfight!submitSxdhFightResult", "提交神仙大会战斗结果"),
    SUBMIT_DFDJ_FIGHT_RESULT("fsfight!submitDfdjFightResult", "提交巅峰对决战斗结果"),
    CHANJIE_CHECK_ELIGIBILITY("fsfight!chanjieCheckEligibility", "阐截战斗资格校验"),
    CHANJIE_FIGHT_RESULT("fsfight!chanjieFightResult", "提交阐截战斗结果"),
    GET_CARD_INFO("fsfight!getCardInfo", "获取卡牌信息"),
    GET_ALL_CARDS("fsfight!getAllCards", "强联网获得所有卡牌"),

    // GameUser
    CREATE_ROLE("gu!createRole", "创建角色"),
    SHAKE_DICE("gu!shakeDice", "丢骰子"),
    CHOOSE_DIRECTION("gu!chooseDirection", "选择方向"),
    CHANGE_STATUS_FOR_MBX("gu!changeStatusForMBX", "开启/关闭漫步靴"),
    GAIN_USER_INFO("gu!gainUserInfo", "跨0点|客户端从后台唤醒，请求玩家信息"),
    GAIN_USER_STATISTIC_INFO("gu!gainUserStatisticInfo", "点击角色头像获取玩家的统计信息"),
    GAIN_UNFILLED_SPECIALS("gu!gainUnfilledSpecials", "获取玩家未捐赠的特产"),
    GAIN_NEW_INFO("gu!gainNewInfo", "客户端定时来获得数据 未读邮件、添加好友通知"),
    GAIN_COPPER("gu!gainCopper", "领取俸禄"),
    GAIN_SHARE_AWARD("gu!gainShareAward", "获得分享奖励，三级城以上及五星卡牌"),
    SET_HEAD("gu!setHead", "设置头像"),
    RENAME("gu!rename", "修改昵称"),
    GAIN_STATUS("gu!gainFightInfo", "获取激战信息"),
    BUY_DICE("gu!buyDice", "购买体力"),
    INC_DICE("gu!gainDice", "客户端定时获取体力"),
    SET_HEAD_ICON("gu!setHeadIcon", "设置头像框"),
    LIST_HEAD_ICON("gu!listHeadIcon", "获取头像框列表"),
    GAIN_TIANLING_BAG_STATUS("gu!getTianlingBagStatus", "获得天灵印的状态"),
    GAIN_TIANLING_BAG("gu!gainTianlingBag", "领取天灵印礼包"),
    LIST_HEAD("gu!listHead", "获取头像列表"),
    GAIN_USER_SHOW_INFO("gu!gainUserShowInfo", "获取玩家空间信息"),
    MENU_OPEN("gu!listOpenMenu", "获取要显示的菜单"),
    USER_DICE_CAPACITY("gu!diceCapacity", "获取体力罐信息"),
    BUY_USER_DICE_BY_CAPACITY("gu!buyDiceByCapacity", "购买体力罐的体力"),

    DISCARD("special!discardSpecial", "丢弃特产"),
    LIST_SPECIAL_CITIES("special!listSpecialCities", "获得特产在各个城市的买入价和卖出价"),
    SPECIAL_LOCK("special!lockSpecial", "锁定特产"),
    SPECIAL_UNLOCK("special!unlockSpecial", "解锁特产"),
    SPECIAL_SYNTHESIS("special!synthesisSpecial", "合唱特产"),
    ENTER_SPECIAL_SYNTHESIS("special!enterSynthesisSpecial", "进入特产合成界面"),
    GET_SPECIAL_SETTINGS("special!getSpecialSettings", "获得特产设置"),
    UPDATE_SPECIAL_SETTINGS("special!updateSpecialSettings", "更新特产设置"),

    SXDH_GET_FIGHTER_INFO("sxdh!getFighterInfo", "获得玩家神仙大会信息"),
    SXDH_GET_FIGHTER_RANK("sxdh!getFighterRank", "神仙大会获取排行"),
    SXDH_GET_BEAN_INFO("sxdh!getBeanInfo", "获取仙豆信息"),
    SXDH_BUY_BEAN("sxdh!buyBean", "神仙大会购买仙豆"),
    SXDH_BUY_MEDICINE("sxdh!buyMedicine", "神仙大会购买丹药"),
    SXDH_GET_RANK_AWARD("sxdh!getRankAward", "神仙大会获取排行奖励"),
    SXDH_EXCHANGE_TIKCET("sxdh!exchangeTicket", "兑换门票"),
    SXDH_GET_LAST_SEASON_RANK_AWARD("sxdh!getLastSeasonRankers", "神仙大会获取上赛季排行"),
    SXDH_ENABLE_MECHINE("sxdh!enableMechine", "激活丹药"),
    SXDH_GET_SPRINT_AWARD("sxdh!getSprintAward", "神仙大会每日冲刺福利"),

    TASK_LIST("gu!gainTasks", "获得任务列表"),////
    TASK_LIST_V2("gu!gainTasks!v2", "获得任务列表2"),////
    TASK_SET_AWARD_INDEX("gu!setTaskAwardIndex", "指定任务奖励"),
    TASK_GAIN_AWARD("gu!gainTaskAward", "领取任务奖励"),
    TASK_GAIN_TASK_NOTICE("task!gainTaskNotices", "获取任务红点"),

    USE_TREASURE("treasure!useTreasure", "使用地图法宝"),
    USE_FIGHT_TREASURE("treasure!useFightTreasure", "使用战斗法宝"),
    UPDATE_SYMBOL("treasure!updateSymbol", "升级符箓"),
    SEE_AWARD("treasure!seeAward", "查看可选奖励列表"),

    EXTRA_OPERATION("yeD!extraOperation", "野地玩家可选操作"),
    LIST_ADVENTURES("yeD!listAdventures", "获取奇遇列表"),
    GET_ADVENTURE_INFO("yeD!getAdventureInfo", "获取单个奇遇信息"),
    GAIN_CARD_EXP("yeD!gainCardExp", "野地领取经验"),

    SUBMIT_ATTACK("city!submitAttackYG", "提交野怪战斗数据"),
    OPEN_BOX("city!openBox", "野怪开宝箱"),

    ENTER_ZXZ("zxz!enterZXZ", "进入诛仙阵"),
    GAIN_NEW_ZXZ("zxz!gainNewZXZ", "刷新诛仙阵"),
    CHALLENGE("zxz!challenge", "诛仙阵挑战"),
    SUBMIT_FIGHT_RESULT("zxz!submitChallengeResult", "提交诛仙阵战斗数据"),

    MONSTER_LIST("monster!listMonsters", "获取友怪列表"),
    MONSTER_ATTACK("monster!attack", "攻击友怪"),
    MONSTER_SUBMIT_FIGHT_RESULT("monster!submitHelpAttackYG", "提交友怪战斗数据"),

    ACHIEVEMENT_LIST("task!listTasks", "获取成就列表"),
    ACHIEVEMENT_LIST_V2("task!listAchievement!v2", "获取成就列表2"),
    ACHIEVEMENT_GAIN_AWARD("task!gainAward", "领取成就奖励"),
    ACHIEVEMENT_INFO("task!achievementInfo", "获取成就主页面信息"),
    ACHIEVEMENT_GAME_RANK("task!achievementGameRank", "获取全服成就榜单"),
    ACHIEVEMENT_SERVER_RANK("task!achievementServerRank", "获取区服成就榜单"),

    HELPABOUT_LIST_("helpabout!listHelpAbout", "获取帮助列表"),
    HELPABOUT_GAIN_AWARD("helpabout!gainAward", "领取帮助奖励"),

    COFC_LIST_TASK("cofc!task", "获取商会任务列表"),
    COFC_OPTION_TASK("cofc!taskOption", "商会任务操作，0接任务，1领取，2取消任务"),////
    COFC_REFRESH_TASK("cofc!taskRefresh", "刷新商会任务操作"),
    COFC_ADD_TASK("cofc!addTask", "商会增加可接受任务数量"),
    COFC_LIST_EXPERIENCE("cofc!experience", "跑商历练"),
    COFC_EXPERIENCE_GET_REWARD("cofc!experienceGetReward", "商会领取历练奖励"),
    COFC_LIST_SHOP("cofc!shopList", "商会商店列表"),//
    COFC_BUY_SHOP("cofc!buy", "商会商店购买"),// 购买
    COFC_LIST_HONOR("cofc!honorList", "商会头衔列表"),//
    COFC_GET_LV("cofc!honorLv", "商会等级"),//
    COFC_ACCEPTED_TASK_INFO("cofc!acceptedTaskInfo", "商会已接受任务信息"),

    GUILD_HAVE_GUILD("guild!havejoinGuild", "是否有加入行会"),
    GUILD_LIST("guild!listGuild", "获取行会列表"),
    GUILD_CREATE("guild!createGuild", "行会创建"),
    GUILD_JOIN("guild!joinGuild", "申请加入行会"),
    GUILD_INFO("guild!infoOverview", "行会信息"),
    GUILD_RENAME("guild!renameGuild", "行会重命名"),
    GUILD_IMPEACH_BOS("guild!impeachBoss", "行会弹劾队长"),
    GUILD_LIST_EXAMIE("guild!listExamie", "行会申请列表"),
    GUILD_MEMBER_OPTION("guild!optionMember", "成员操作，-2踢出成员；-1拒绝申请；1接受审核；2授予副队长；3转让队长；4降级为会员"),
    GUILD_WRITE_WORDS("guild!writeWord", "行会留言"),
    GUILD_EXIT("guild!exitGuild", "退出行会"),
    GUILD_READ_WORDS("guild!readWord", "查看留言"),
    GUILD_ED_INFO_TASK("guild!infoEightDiagramTask", "行会八卦任务获取"),
    GUILD_ED_OPTION_TASK("guild!optionEightDiagramTask", "行会八卦任务操作。0接受任务；1领奖；2取消"),////
    GUILD_ED_REFREASH_TASK("guild!refreshEightDiagramTask", "行会八卦任务刷新"),
    GUILD_ED_HELP_TASK("guild!helpEightDiagramTask", "行会八卦任务求助"),
    GUILD_LIST_MEMBER("guild!listMember", "行会成员列表"),
    GUILD_LIST_SHOP("guild!listShop", "商会商店"),
    GUILD_BUY_SHOP("guild!buyGoods", "行会商店购买"),
    GUILD_ACCEPTED_TASK_INFO("guild!acceptedTaskInfo", "已接收的行会任务的信息"),

    CHECK_JOIN("chanjie!checkJoin", "阐截斗法检查是否加入教派"),
    JOIN_RELIGIOUS("chanjie!join", "阐截斗法选择加入教派"),
    MAIN_INFO("chanjie!mainInfo", "阐截斗法主页信息"),
    RANKING_LIST("chanjie!rankingList", "阐截斗法排行榜"),
    HONOR_LIST("chanjie!honorList", "阐截斗法荣誉榜"),
    SPECAIL_LIST("chanjie!specailList", "阐截斗法教派奇人"),
    THUMBS_UP("chanjie!thumbsup", "阐截斗法点赞"),
    BUY_BLOOD("chanjie!buyblood", "阐截斗法购买血量"),
    WAR_SITUATION("chanjie!warSituation", "阐截斗法实时战况"),

    BYPALACE_ENTER("byPalace!enterBYPalace", "进入碧游宫"),
    BYPALACE_GET_AWARDS("byPalace!getAwards", "碧游宫获得奖励"),
    BYPALACE_REFRESH_AWARDS("byPalace!refreshAwards", "碧游宫刷新奖励"),
    BYPALACE_RESET("byPalace!reset", "碧游宫重置"),
    BYPALACE_REALIZATION("byPalace!realization", "碧游宫领悟"),

    GET_PUSH("push!getPush", "获取推送设置信息"),
    UPDATE_PUSH("push!updatePush", "更新推送设置"),

    MAIL_SEND("mail!send", "发送邮件"),
    MAIL_LIST("mail!listPage", "邮件列表"),
    MAIL_READ("mail!readMail", "打开邮件"),
    MAIL_DELETE("mail!delete", "删除邮件"),
    MAIL_ACCEPT_AWARD("mail!acceptAward", "领取邮件奖励"),
    MAIL_GAIN_INFO("mail!gainMailsInfo", "获取未读和未领取奖励读邮件数量提示信息"),

    BUDDY_LIST("buddy!listBuddies", "显示好友列表"),
    BUDDY_APPLY("buddy!apply", "申请添加好友"),
    BUDDY_DELETE("buddy!delete", "删除好友"),
    BUDDY_APPROVE("buddy!approve", "审批"),
    BUDDY_SEARCH("buddy!searchToAdd", "查找添加好友"),

    PVP_COMBAT_INIT("combatPVP!pvpInit", "PVP初始化"),
    PVP_COMBAT_ROUND("combatPVP!pvpNextRound", "PVP回合"),
    PVP_COMBAT_USE_WEAPON("combat!pvpUseWeapon", "PVP使用道具"),
    PVP_COMBAT_USE_WEAPON2("combatPVP!useWeapon", "PVP使用道具2"),
    PVP_COMBAT_SURRENDER("combatPVP!surrender", "PVP投降"),
    PVP_COMBAT_RECOVER("combatPVP!recoverAttack", "PVP掉线重连"),
    ;
    private String url;
    private String memo;

    CREnum(String url, String memo) {
        this.url = url;
        this.memo = memo;
    }

    public static CREnum fromUrl(String url) {
        for (CREnum model : values()) {
            if (model.getUrl().equals(url)) {
                return model;
            }
        }
        return NONE;
    }
}