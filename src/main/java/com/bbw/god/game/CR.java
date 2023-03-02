package com.bbw.god.game;

/**
 * 客户端请求接口
 *
 * @author suhq
 * @date 2018年11月5日 下午3:27:26
 */
public class CR {

    /**
     * 活动福利相关接口
     *
     * @author suhq
     * @date 2019年3月2日 下午4:16:20
     */
    public static class Activity {
        public static final String GET_ACTIVITIES = "activity!listActivities";
        public static final String GET_ACTIVITIES_V2 = "activity!listActivities!v2";
        public static final String RECEIVE_AWARD = "activity!joinActivity";
        public static final String RECEIVE_TASK_AWARD = "activity!receiveTaskAward";
        public static final String GET_RANK_ACTIVITIES = "activity!listRankActivities";
        public static final String GET_RANK_AWARDS = "activity!listRankersAwards";
        public static final String EXCHANGE_GOOD = "activity!exchangeGood";
        public static final String REPLENISH = "activity!replenish";
        public static final String SET_AWARD_ITEM = "activity!setAwardItem";
        public static final String ENTER_LOTTERY = "activity!enterLottery";// 进入活动抽奖
        public static final String PREVIEW_LOTTERY_AWARD = "activity!previewLotteryAward";// 预览抽奖奖励
        public static final String DRAW_LOTTERY = "activity!drawLottery";// 活动抽奖
        public static final String REFRESH_LOTTERY_AWARD = "activity!refreshLotteryAward";// 刷新抽奖奖励
        public static final String HORSE_RACING_BET = "activity!horseRacingBet";// 赛马下注
        /** 活动开宝箱 */
        public static final String ACTIVITY_OPEN_BOX = "activity!OpenBox";
        /** 获得感恩节（感恩之举）活动信息 */
        public static final String THANKSGIVING_INFOS = "activity!getThanksGivingInfo";
        /** 获得折扣变化商店折扣 */
        public static final String MALL_DISCOUNT = "activity!getDiscount";
        /** 获得合服折扣特惠商店折扣 */
        public static final String MALL_COMBINED_SERVICE_DISCOUNT = "activity!getCombinedServiceDiscount";
        /** 赠送食物 */
        public static final String GET_NPC_GRATITUDE = "activity!getNpcGratitude";
        /** 烹饪食物 */
        public static final String COOKING_FOODS = "activity!cookingFoods";
        /** 使用好感度兑换食物 */
        public static final String USE_GRATITUDE = "activity!useGratitude";
        /*获得全服庆典奖励*/
        public static final String RECEIVE_CELEBRATION_AWARD = "activity!receiveCelebrationAward";
        /** 建造祭坛捐献 */
        public static final String DONATE = "activity!donate";
        /** 村庄疑云消耗指定道具 */
        public static final String CONSUME_TREASURES = "activity!consumeTreasures";
        /** 村庄疑云战斗编组 */
        public static final String CUNZ_YIYUN_CARD_GROUPING = "activity!editCunzYiyunCardGrouping";
        /** 村庄疑云刷新牌库 */
        public static final String CUNZ_YIYUN_REFRESH_CARD_LIBRARY = "activity!cunzYiyunRefreshCardLibrary";
        /** 赠送鲜花 */
        public static final String SEND_FLOWERS = "activity!sendFlowers";
        /** 搜索玩家信息 */
        public static final String SEARCH_USER_INFO = "activity!searchUserInfo";
        /** 放飞天灯 */
        public static final String PUT_SKY_LANTERN = "activity!putSkyLantern";
        /** 投注 */
        public static final String LANTERN_BET = "activity!lanternBet";
        /** 刷新翻牌挑战 */
        public static final String REFRESH_FLOP_CHALLENGE = "activity!refreshFlopChallenge";
        /** 翻牌 */
        public static final String FLOP = "activity!flop";
        /** 领取连线奖励 */
        public static final String RECEIVE_CONNECTION_AWARDS = "activity!receiveConnectionAwards";
        /** 领取翻牌目标奖励 */
        public static final String RECEIVE_FLOP_TARGET_AWARDS = "activity!receiveFlopTargetAwards";
        /** 获得藏宝图奖励用于显示 */
        public static final String GET_TREASURE_TROVE_MAP_TO_SHOW = "activity!getTreasureTroveMapToShow";
        /** 获得藏宝图奖励用于显示 */
        public static final String RECEIVE_TREASURE_TROVE_MAP_AWARDS = "activity!receiveTreasureTroveMapAwards";
        /** 萌虎集市售卖糕点 */
        public static final String CUTE_TIGER_MARKET_PASTRY_SOLD = "activity!sellPastries";
        /** 进入小虎商店 */
        public static final String ENTER_LITTLE_TIGER_STORE = "activity!enterLittleTigerStore";
        /** 小虎商店刷新奖励 */
        public static final String LITTLE_TIGER_STORE_REFRESH = "activity!LittleTigerStoreRefresh";
        /** 替换奖励 */
        public static final String LITTLE_TIGER_STORE_REPLACE = "activity!LittleTigerStoreReplace";
        /** 发送累计刷新次数奖励 */
        public static final String SEND_TOTAL_REFRESH_AWARDS = "activity!sendTotalRefreshAwards";
        /** 领取小虎商店奖励 */
        public static final String RECEIVE_LITTLE_TIGER_STORE_AWARDS = "activity!receiveLittleTigerStoreAwards";
        /** 锦鲤祈愿 */
        public static final String KOI_PRAY = "activity!koiPray";
        /** 万圣餐厅合成 */
        public static final String HALLOWEEN_RESTAURANT_COMPOUND = "activity!halloweenRestaurantCompound";
        /** 万圣餐厅解锁盘子 */
        public static final String HALLOWEEN_RESTAURANT_UNLOCK_PLATE = "activity!halloweenRestaurantUnlockPlate";
        /** 获得万圣餐厅订单信息 */
        public static final String GET_HALLOWEEN_RESTAURANT_ORDER_INFO = "activity!getHalloweenRestaurantOrderInfo";
        /** 万圣餐厅接受订单 */
        public static final String HALLOWEEN_RESTAURANT_ACCEPT_ORDER = "activity!halloweenRestaurantAcceptOrder";
        /** 万圣餐厅完成订单 */
        public static final String HALLOWEEN_RESTAURANT_COMPLETE_ORDER = "activity!halloweenRestaurantCompleteOrder";
        /** 领取万圣餐厅离线收益 */
        public static final String RECEIVE_HALLOWEEN_RESTAURANT_OFFLINE_REVENUE = "activity!receiveHalloweenRestaurantOfflineRevenue";
        /** 万圣餐厅卖出食物 */
        public static final String HALLOWEEN_RESTAURANT_SELL_FOOD = "activity!halloweenRestaurantSellFood";
        /** 万圣餐厅购买食物 */
        public static final String HALLOWEEN_RESTAURANT_BUY_FOOD = "activity!halloweenRestaurantBuyFood";
        /** 万圣餐厅购买食物 */
        public static final String HALLOWEEN_RESTAURANT_SWAP_FOOD_POS = "activity!halloweenRestaurantSwapFoodPos";
        /** 世界杯超级16强的投注情况 */
        public static final String WORLD_CUP_SUPER16_BET = "activity!worldCupSuper16Bet";
        /** 世界杯-超级16强分组情况 */
        public static final String WORLD_CUP_SUPER16_DIVIDE_GROUP = "activity!worldCupSuper16DivideGroup";
        /** 世界杯-决战8强投注 */
        public static final String WORLD_CUP_DROIYAN8P_BET = "activity!worldCupDroiyan8PBet";
        /** 世界杯-决战8强分组情况 */
        public static final String WORLD_CUP_DROIYAN8P_BET_DIVIDE_GROUP = "activity!worldCupDroiyan8PBetDivideGroup";
        /** 世界杯我是预言家投注 */
        public static final String WORLD_CUP_PROPHET_BET = "activity!worldCupProphetBet";
        /** 世界杯我是竞猜王投注 */
        public static final String WORLD_CUP_QUIZ_KING_BET = "activity!worldCupQuizKingBet";
        /** 世界杯我是竞猜王根据日期获取信息 */
        public static final String world_Cup_Quiz_King_Day_Date = "activity!worldCupQuizKingDayDate";
        /** 感恩花语种植 */
        public static final String THANK_FLOWER_LANGUAGE_PLANT = "activity!thankFlowerLanguagePlant";
        /** 感恩花语一键种植 */
        public static final String THANK_FLOWER_LANGUAGE_ONE_CLICK_PLANT = "activity!thankFlowerLanguageOneClickPlant";
        /** 感恩花语施肥 */
        public static final String THANK_FLOWER_LANGUAGE_APPLY_FERTILIZER = "activity!thankFlowerLanguageApplyFertilizer";
        /** 感恩花语一键施肥 */
        public static final String THANK_FLOWER_LANGUAGE_ONE_CLICK_APPLY_FERTILIZER = "activity!thankFlowerLanguageOneClickApplyFertilizer";
        /** 感恩花语采摘 */
        public static final String THANK_FLOWER_LANGUAGE_PICK = "activity!thankFlowerLanguagePick";
        /** 感恩花语一键采摘 */
        public static final String THANK_FLOWER_LANGUAGE_ONE_CLICK_PICK = "activity!thankFlowerLanguageOneClickPick";
        /** 感恩花语制作花束 */
        public static final String THANK_FLOWER_LANGUAGE_MAKE_BOUQUET = "activity!thankFlowerLanguageMakeABouquet";
        /** 杂货小铺 选择大奖 */
        public static final String CHOICE_GRAND_PRIX = "activity!choiceGrandPrix";
        /** 杂货小铺 够买盲盒 */
        public static final String BUT_BLIND_BOX = "activity!butBlindBox";
        /** 杂货小铺 查看大奖 */
        public static final String GET_GRAND_PRIX_AWARD = "activity!getGrandPrixAward";
        /** 杂货小铺 重选大奖 */
        public static final String RESE_LECT_GRAND_PRIX = "activity!reselectGrandPrix";
        /** 魔法女巫 炼制信息 */
        public static final String GET_HOLIDAY_REFIN_INFO = "activity!getHolidayRefinInfo";
        /** 魔法女巫 炼制 */
        public static final String REFIN = "activity!refin";
        /** 圣诞心愿完成心愿 */
        public static final String COMPLETE_WISH = "activity!completeWish";
        /** 刷新生肖地图 */
        public static final String REFRESH_CHINESE_ZODIAC_MAP = "activity!refreshChineseZodiacMap";
        /** 生肖翻牌 */
        public static final String CHINESE_ZODIAC_FLIP = "activity!chineseZodiacFlip";


    }

    /**
     * 卡牌相关卡组
     *
     * @author suhq
     * @date 2018年11月6日 下午2:20:47
     */
    public static class Card {
        public static final String SET_FIGHT_CARDS = "card!setFightCards";
        public static final String UPDATE = "card!updateCard";
        public static final String ADVANCED = "card!updateHierarchy";
        public static final String SET_DEFAULT_DECK = "card!setDefaultDeck";
        public static final String GAIN_JL_CARDS = "card!gainJLCards";
        public static final String JU_LING = "card!juLing";
        public static final String USE_SKILL_SCROLL = "card!useSkillScroll";// 炼技
        public static final String CLEAR_SKILL_SCROLL = "card!clearSkillScroll";// 重置
        public static final String TAKE_OUT_SKILL_SCROLL = "card!takeOutSkillScroll";// 取回技能
        public static final String PUT_ON_SYMBOL = "card!putOnSymbol";// 修体-穿
        public static final String UNLOAD_SYMBOL = "card!unloadSymbol";// 修体-卸
        public static final String SET_GROUP_NAME = "card!setGroupName";//修改卡组昵称
        public static final String SHARE_CARD_GROUP = "card!shareCardGroup";//分享卡组
        public static final String GET_SHARE_CARD_GROUP = "card!getShareCardGroup";//获取分享的卡组信息
        public static final String SET_SHOW_CARDS = "card!setShowCards";//设置展示卡
        public static final String SET_FIERCE_FIGHTING_CARDS = "card!setFierceFightingCards";//设置封神台和诛仙阵的攻防卡组
        public static final String GET_FIERCE_FIGHTING_CARDS = "card!getFierceFightingCards";//设置封神台和诛仙阵的攻防卡组
        public static final String COLLECT_CARD_GROUP = "card!collectCardGroup";// 收藏卡组
        public static final String GET_COLLECT_CARD_GROUP = "card!getCollectCardGroups";// 获取收藏卡组信息
        public static final String DEL_COLLECT_CARD_GROUP = "card!delCollectCardGroup";// 取消收藏卡组
        public static final String SET_BOOST = "card!setBoost";// 设置为助力卡牌
        public static final String SYNC_BOOST_CARDS = "card!syncBoostCards";// 同步助力卡牌
        public static final String SET_FUCE = "card!setFuCe";// 同步助力卡牌
        public static final String SYNC_CARD_GROUP = "card!syncCardGroup";// 同步卡组
        public static final String ADD_SKILL_GROUP = "card!addSkillGroup";// 添加技能组二
        public static final String CHANGE_SKILL_GROUP = "card!changeSkillGroup";//切换技能组
        public static final String GET_SKILL_GROUP = "card!getSkillGroup";//获取技能组

    }

    /**
     * 卡牌装备
     *
     * @author: huanghb
     * @date: 2022/9/17 11:26
     */
    public static class CardEquipment {
        public static final String ACTIVE_CARD_XIAN_JUE = "cardEquipment!activeXianJue";//激活仙诀
        public static final String GET_XIAN_JUE_INFO = "cardEquipment!getXianJueInfo";//获得仙诀信息
        public static final String STRENGH_XIAN_JUE = "cardEquipment!strengthXianJue";//仙诀强化
        public static final String UPDATE_STAR_MAP = "cardEquipment!updateStarMap";//仙诀淬星
        public static final String COMPREHEND_XIAN_JUE = "cardEquipment!comprehendXianJue";//仙诀参悟
        public static final String TAKE = "cardEquipment!take";//灵宝穿戴
        public static final String TAKE_OFF = "cardEquipment!takeOff";//灵宝取下

    }

    /**
     * 卡牌屋
     *
     * @author suhq
     * @date 2019-05-08 11:03:25
     */
    public static class CardShop {
        public static final String GET_CARD_SHOP_INFO = "cardShop!getCardShopInfo";
        public static final String GET_CARD_POOL_INFO = "cardShop!getCardPoolInfo";
        public static final String GET_WISH_POOL_INFO = "cardShop!getWishPoolInfo";
        public static final String ACTIVE_CARD_POOL = "cardShop!activeCardPool";
        public static final String ADD_TO_POOL = "cardShop!addToPool";
        public static final String DRAW = "cardShop!draw";
    }

    /**
     * 城池相关接口
     *
     * @author suhq
     * @date 2018年11月5日 上午11:24:42
     */
    public static class ChengC {
        public static final String SUBMIT_ATTACK_CITY = "city!submitAttackCity";
        public static final String LIST_CITY_TRADE_INFO = "city!listCityTradeInfo";
        public static final String REFRESH_TRADE_SPECIALS = "city!refreshTradeSpecials";
        public static final String BUY_SPECIAL = "special!buy";
        public static final String SELL_SPECIAL = "special!sell";
        public static final String GET_PROMOTE_INFO = "city!promoteInfo";
        public static final String INVESTIGATE_CITY = "city!investigate";
        public static final String GET_CITY_INFO = "city!getCityInfo";
        public static final String ARRIVE_CITY_INFO = "city!arriveCityInfo";
        public static final String CHANGE_WORD = "city!changeWorld";
        public static final String GAIN_LEVEL_AWARD = "city!gainLevelAward";
        public static final String GET_ATTACK_CARD_GROUP = "city!getCardGroup";
        public static final String SET_ATTACK_CARD_GROUP = "city!setCardGroup";
        public static final String AUTO_BUY_SPECIAL = "special!autoBuy";
        public static final String AUTO_SELL_SPECIAL = "special!autoSell";
        public static final String SYN_ATTACK_CARD_GROUP = "city!synCardGroup";
    }

    /**
     * 城池内建筑相关接口
     *
     * @author suhq
     * @date 2018年11月5日 上午11:24:42
     */
    public static class ChengCIn {
        public static final String TRAINING = "city!attackCity";//练兵
        public static final String GET_ALL_OUTPUT = "cityIn!gainAllOutput";
        public static final String INTO_CITY = "cityIn!intoCity";
        public static final String KC_GET = "cityIn!kcGetEle";
        public static final String SET_DEFAULT_ELES = "cityIn!setDefaultEles";
        public static final String QZ_GET = "cityIn!qzGet";
        public static final String LBL_GET = "cityIn!lblGet";
        public static final String UPDATE = "cityIn!update";
        public static final String JXZ_GET_CARD = "cityIn!jxzGetCard";
        public static final String JXZ_GET = "cityIn!jxzGet";
        public static final String LDF_GET = "cityIn!promoteCard";
        public static final String SET_LDF_CARD = "cityIn!setLdfCard";
        /** 解锁法坛 */
        public static final String UNLOCK_FA_TAN = "cityIn!unlockFaTan";
    }

    /**
     * 城市相关接口
     *
     * @author suhq
     * @date 2018年11月5日 上午11:36:33
     */
    public static class City {
        public static final String FLX_DRAW_LOTS = "city!drawLots";
        public static final String FLX_LIST_LAST_FLX_RESULTS = "city!listLastFlxResults";
        public static final String FLX_BET_SG = "city!betSG";
        public static final String FLX_BET_YSG = "city!betYSG";
        public static final String HS_BUY = "city!buyHS";
        public static final String NWM_DRAW = "city!donateNWM";
        public static final String TYF_FILL = "city!fillTYF";
        public static final String MYTYF_CONVERT = "city!convertMYTYF";
        public static final String KZ_RECRUIT = "city!recruitCard";
        public static final String YSG_BUY = "city!buySpecial";
        public static final String LT_TRIBUTE = "city!tribute";
        public static final String MXD_OUT = "city!outMXD";
        /** 特殊建筑体验 */
        public static final String EXP = "city!exp";
    }

    /**
     * 开发维护使用相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午12:18:54
     */
    public static class Coder {
        public static final String GAIN_SERVER_INFO = "coder!serverInfo";
    }

    /**
     * 玩家竞技接口
     *
     * @author suhq
     * @date 2019年3月14日 上午9:21:48
     */
    public static class FsFight {
        public static final String GET_GU_INFO = "fsfight!listGuInfoForFsFight";
        public static final String GET_ROBOT_INFO = "fsfight!gainRobotInfoForFsFight";
        public static final String GET_ROBOTS_INFO = "fsfight!gainRobotsInfoForFsFight";
        public static final String SYNC_BUY = "fsfight!syncBuy";
        public static final String SYNC_SXDH_CARD_REFRESH = "fsfight!syncSxdhCardRefresh";
        public static final String SYNC_TREASURES = "fsfight!syncTreasuresForFight";
        public static final String SYNC_TICKET = "fsfight!syncTicket";
        public static final String TO_MATCH = "fsfight!toMatch";
        public static final String SUBMIT_SXDH_FIGHT_RESULT = "fsfight!submitSxdhFightResult";
        public static final String SUBMIT_CDJF2_FIGHT_RESULT = "fsfight!submitCjdf2FightResult";
        public static final String SUBMIT_DFDJ_FIGHT_RESULT = "fsfight!submitDfdjFightResult";
        public static final String CHANJIE_CHECK_ELIGIBILITY = "fsfight!chanjieCheckEligibility";// 阐截战斗资格校验
        public static final String CHANJIE_FIGHT_RESULT = "fsfight!chanjieFightResult";// 阐截战斗结果
        public static final String GET_CARD_INFO = "fsfight!getCardInfo";// 获取卡牌信息
        public static final String GET_ALL_CARDS = "fsfight!getAllCards";// 获取卡牌信息
    }

    /**
     * 玩家相关接口
     *
     * @author suhq
     * @date 2018年11月5日 上午11:56:59
     */
    public static class GameUser {
        public static final String CREATE_ROLE = "gu!createRole";
        public static final String SHAKE_DICE = "gu!shakeDice";
        public static final String CHOOSE_DIRECTION = "gu!chooseDirection";
        public static final String CHANGE_STATUS_FOR_MBX = "gu!changeStatusForMBX";
        public static final String GAIN_USER_INFO = "gu!gainUserInfo";
        public static final String GAIN_USER_STATISTIC_INFO = "gu!gainUserStatisticInfo";
        public static final String GAIN_UNFILLED_SPECIALS = "gu!gainUnfilledSpecials";
        public static final String GAIN_NEW_INFO = "gu!gainNewInfo";
        public static final String GAIN_COPPER = "gu!gainCopper";
        public static final String GAIN_SHARE_AWARD = "gu!gainShareAward";
        public static final String SET_HEAD = "gu!setHead";
        public static final String HIDE_NAME = "gu!hideName";
        public static final String RENAME = "gu!rename";
        public static final String GAIN_STATUS = "gu!gainFightInfo";
        public static final String BUY_DICE = "gu!buyDice";
        public static final String INC_DICE = "gu!gainDice";
        public static final String SET_HEAD_ICON = "gu!setHeadIcon";
        public static final String SET_EMOTICON = "gu!setEmoticon";//表情包设置
        public static final String LIST_HEAD_ICON = "gu!listHeadIcon";
        public static final String GAIN_TIANLING_BAG_STATUS = "gu!getTianlingBagStatus";
        public static final String GAIN_TIANLING_BAG = "gu!gainTianlingBag";
        public static final String LIST_HEAD = "gu!listHead";
        public static final String GAIN_USER_SHOW_INFO = "gu!gainUserShowInfo";
        public static final String MENU_OPEN = "gu!listOpenMenu";
        public static final String USER_DICE_CAPACITY = "gu!diceCapacity";
        public static final String BUY_USER_DICE_BY_CAPACITY = "gu!buyDiceByCapacity";

    }

    /**
     * 商城相关接口
     *
     * @author suhq
     * @date 2018年11月29日 下午3:37:25
     */
    public static class Mall {
        public static final String LIST_MALLS = "mall!listProducts";
        public static final String BUY = "mall!buy";
        /** 购物车结账 */
        public static final String CART_BUY = "mall!cartBuy";
        public static final String REFRESH_MYSTERIOUS = "mall!refreshMysteriousTreasures";
        public static final String GET_MALL_INFO = "mall!getMallInfo";
        public static final String GET_GOODS_INFO = "mall!getGoodsInfo";
        /** 刷新藏宝秘境奖池 */
        public static final String REFRESH_MY_TREASURE_TROVE = "mall!refreshMyTreasureTrove";
        /** 藏宝秘境购买 */
        public static final String BUY_MY_TREASURE_TROVE = "mall!buyMyTreasureTrove";
    }

    /**
     * 积分商城相关接口
     */
    public static class Store {
        public static final String LIST_MALLS = "store!listProducts";
        public static final String BUY = "store!buy";
    }

    /**
     * 魔王相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午12:05:52
     */
    public static class Maou {
        public static final String GET_MAOU = "maou!gainMaou";
        public static final String REFRESH_MAOU = "maou!refreshMaou";
        public static final String SET_CARDS = "gu!setMaouCards";
        public static final String GET_ATTACKING_INFO = "maou!getAttackingInfo";
        public static final String GET_RANKERS = "maou!listRankings";
        public static final String GET_RANKER_AWARDS = "maou!getRankAwards";
        public static final String RESET_MAOU_LEVEL = "maou!resetMaouLevel";
        public static final String ATTACK = "maou!attack";
        public static final String GET_MAOU_WITH_RANERS = "maou!gainInfoIncludeRankings";
        public static final String GET_REMAIN_BLOOD = "maou!gainRemainBlood";
        public static final String CONFIRM_MAOU = "maou!confirmMaou";
        public static final String GET_ALONE_MAOU_AWARD = "maou!getAloneMaouAward";
        //跨服魔王接口
        /** 获取魔王信息 */
        public static final String GET_GAME_MAOU = "maou!gainGameMaou";
        /** 定时刷新魔王信息 */
        public static final String REFRESH_GAME_MAOU = "maou!refreshGameMaou";
        /** 设置攻打魔王的卡牌 */
        public static final String SET_GAME_MAOU_CARDS = "maou!setGameMaouCards";
        /** 攻打魔王 */
        public static final String ATTACK_GAME_MAOU = "maou!attackGameMaou";
        /** 获取魔王目标奖励 */
        public static final String GET_GAME_MAOU_AWARD = "maou!getGameMaouAward";

    }

    /**
     * 招财兽相关接口
     *
     * @author: huanghb
     * @date: 2022/1/30 17:24
     */
    public static class LuckyBeast {
        /** 获得招财兽本次奖励信息 */
        public static final String LUCKY_BEAST_GET_AWARDS_INFO = "luckyBeast!getAwardsInfo";
        /** 购买攻打次数 */
        public static final String LUCKY_BEAST_BUY_ATTACK_TIMES = "luckyBeast!buyAttackTimes";
        /** 设置攻打招财兽的卡牌 */
        public static final String SET_LUCKY_BEAST_CARD = "luckyBeast!setAttackCard";
        /** 攻击招财兽 */
        public static final String ATTACK_LUCKY_BEAST = "luckyBeast!attack";
        /** 刷新招财兽 */
        public static final String REFRESH_MY_LUCKY_BEAST = "luckyBeast!refresh";

    }

    /**
     * 招财兽相关接口
     *
     * @author: huanghb
     * @date: 2022/1/30 17:24
     */
    public static class DigTreasure {
        /** 挖宝 */
        public static final String DIG_TREASURE_DIG = "digTreasure!dig";
        /** 获得所有挖宝信息 */
        public static final String GET_ALL_DIG_TREASURE_INFO = "digTreasure!getAlldigTreasureIfo";
    }

    /**
     * 兑换礼包相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午12:07:16
     */
    public static class Pack {
        public static final String EXCHANGE = "gu!gainPacks";
    }

    /**
     * 产品相关接口
     *
     * @author suhq
     * @date 2019年3月6日 下午2:38:01
     */
    public static class Product {
        public static final String LIST_PRODUCTS = "product!listProducts";
        public static final String WECHAT_PRODUCTS = "webProduct!listProducts";// 微信公众号产品列表
        public static final String NOTIFY = "product!notify";
        public static final String CAN_BUG = "product!canbuy";
    }

    /**
     * 封神台相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午12:08:49
     */
    @Deprecated
    public static class FST_DEPRECATED_API {
        @Deprecated
        public static final String LIST_RANKERS = "gu!listPvpRank";
        @Deprecated
        public static final String CHALLENGE = "pvp!challenge";
        @Deprecated
        public static final String SUBMIT_RESULT = "pvp!submitChallengeResult";
        @Deprecated
        public static final String LIST_CONVERTIBLE_GOODS = "pvp!listConvertibleGoods";
        @Deprecated
        public static final String EXCHANGE = "pvp!exchange";
        @Deprecated
        public static final String GAIN_INCREMENT_POINT = "pvp!gainIncrementPoint";
        //上面为所有的已弃用接口
    }

    public static class FST{
        /**
         * 进入主页
         */
        public static final String INTO="fst!into";
        /**
         * 领取积分
         */
        public static final String GAIN_INCREMENT_POINT="fst!gainIncrementPoint";
        /**
         * 神位(榜单)
         */
        public static final String RANK="fst!ranking";
        /**
         * 编组
         */
//        public static final String INTO="";
        /**
         * 挑战
         */
//        public static final String FIGHT="fst!fight";
        /**
         * 查看日志
         */
        public static final String FIGHT_LOG="fst!fightLog";
        /**
         * 查看卡组
         */
        public static final String GET_CARD_GROUP="fst!getCardGroup";
    }

    /**
     * 特产相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午3:49:38
     */
    public static class Special {
        public static final String DISCARD = "special!discardSpecial";
        public static final String LIST_SPECIAL_CITIES = "special!listSpecialCities";
        public static final String SPECIAL_LOCK = "special!lockSpecial";// 口袋操作
        public static final String SPECIAL_UNLOCK = "special!unlockSpecial";// 口袋操作
        public static final String SPECIAL_SYNTHESIS = "special!synthesisSpecial";// 合成特产
        public static final String ENTER_SPECIAL_SYNTHESIS = "special!enterSynthesisSpecial";// 进入特产合成界面
        public static final String GET_SPECIAL_SETTINGS = "special!getSpecialSettings";// 获得特产设置
        public static final String UPDATE_SPECIAL_SETTINGS = "special!updateSpecialSettings";// 更新特产设置
    }

    /**
     * 神仙大会接口
     *
     * @author suhq
     * @date 2019-06-21 09:42:40
     */
    public static class Sxdh {
        public static final String GET_FIGHTER_INFO = "sxdh!getFighterInfo";
        public static final String GET_FIGHTER_RANK = "sxdh!getFighterRank";
        public static final String GET_BEAN_INFO = "sxdh!getBeanInfo";
        public static final String BUY_BEAN = "sxdh!buyBean";
        @Deprecated
        public static final String GET_SHOP_PRODUCTS = "sxdh!getShopProducts";
        @Deprecated
        public static final String BUY_SHOP_PRODUCT = "sxdh!buyShopProduct";
        public static final String BUY_MEDICINE = "sxdh!buyMedicine";
        @Deprecated
        public static final String CLEAR_MINUS_SCORE = "sxdh!clearMinusScore";
        public static final String EXCHANGE_TICKET = "sxdh!exchangeTicket";
        public static final String GET_RANK_AWARD = "sxdh!getRankAward";
        public static final String GET_LAST_SEASON_RANK_AWARD = "sxdh!getLastSeasonRankers";
        public static final String ENABLE_MECHINE = "sxdh!enableMechine";
        public static final String GET_SPRINT_AWARD = "sxdh!getSprintAward";
    }

    /**
     * 任务相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午12:09:23
     */
    public static class Task {
        public static final String GAIN_TASKS = "gu!gainTasks";
        public static final String GAIN_TASKS_V2 = "gu!gainTasks!v2";
        public static final String SET_TASK_AWARD_INDEX = "gu!setTaskAwardIndex";
        public static final String GAIN_AWARD = "gu!gainTaskAward";
        /** 批量领取奖励 */
        public static final String GAIN_BATCH_AWARD = "gu!gainBatchTaskAward";
        public static final String GAIN_TASK_NOTICE = "task!gainTaskNotices";
        /** 放弃任务 */
        public static final String ABANDOM_TASK = "task!abandom";
        /** 派遣信息 */
        public static final String GET_TASK_DISPATCHED_INFO = "task!getDispatchInfo";
        /** 获取任务信息*/
        public static final String GET_TASK_INFO = "task!getTaskInfo";
        /** 获取派遣卡牌列表 */
        public static final String LIST_TASK_DISPATCH_CARDS = "task!listDispatchCards";
        /** 派遣 */
        public static final String TASK_DISPATCH = "task!dispatch";
        /** 派遣加速 */
        public static final String TASK_DISPATCH_SPEEDUP = "task!dispatchSpeedup";
        /** 获取可重复任务奖励 */
        public static final String GAIN_REPEATABLE_TASK_AWARD = "task!gainRepeatableTaskAward";
        /** 重新开始任务 */
        public static final String RESTART = "task!restart";
        /** 恢复卡牌精力 */
        public static  final String RECOVER_VIGOR = "task!recoverCardVigor";
        /** 商帮任务领取奖励 */
        public static  final String BUSINESS_GANG_TASK_AWARD = "task!businessGangTaskAward";
        //以下4个接口已废弃
        public static final String GAIN_DAILY_TASKS = "gu!gainDailyTasks";
        public static final String GAIN__DAILY_AWARD = "gu!gainDailyTaskAward";
        public static final String REFRESH_DAILY_TASKS = "gu!refreshDailyTasks";
        public static final String GAIN_MAIN_TASK_AWARD = "gu!gainMainTaskAward";
    }

    /**
     * 法宝相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午3:49:38
     */
    public static class Treasure {
        public static final String USE_TREASURE = "treasure!useTreasure";
        public static final String USE_FIGHT_TREASURE = "treasure!useFightTreasure";
        public static final String UPDATE_SYMBOL = "treasure!updateSymbol";// 升级符箓
        public static final String SEE_AWARD = "treasure!seeAward";// 看奖励信息
        public static final String SYNTHESIS_TREASURE = "treasure!synthesis";//合成
    }

    /**
     * 野地相关接口
     *
     * @author suchaobin
     * @date 2020/6/1 14:11
     **/
    public static class YeD {
        public static final String EXTRA_OPERATION = "yeD!extraOperation";
        public static final String LIST_ADVENTURES = "yeD!listAdventures";
        public static final String GET_ADVENTURE_INFO = "yeD!getAdventureInfo";
        public static final String GAIN_CARD_EXP = "yeD!gainCardExp";
    }

    /**
     * 野怪相关接口
     *
     * @author suhq
     * @date 2018年11月5日 下午12:17:14
     */
    public static class YG {
        public static final String SUBMIT_ATTACK = "city!submitAttackYG";
        public static final String OPEN_BOX = "city!openBox";
    }



    /**
     * 帮好友打怪接口
     *
     * @author suhq
     * @date 2019年1月7日 下午2:16:26
     */
    public static class Monster {
        public static final String LIST_MONSTERS = "monster!listMonsters";
        public static final String ATTACK_MONSTER = "monster!attack";
        public static final String SUBMIT_FIGHT_RESULT = "monster!submitHelpAttackYG";
    }

    /**
     * 成就
     *
     * @author suhq
     * @date 2019年2月21日 下午2:41:57
     */
    public static class Achievement {
        public static final String LIST_ACHIEVEMENT = "task!listTasks";
        public static final String LIST_ACHIEVEMENT_V2 = "task!listAchievement!v2";
        public static final String GAIN_AWARD = "task!gainAward";
        public static final String ACHIEVEMENT_INFO = "task!achievementInfo";
        public static final String ACHIEVEMENT_GAME_RANK = "task!achievementGameRank";
        public static final String ACHIEVEMENT_SERVER_RANK = "task!achievementServerRank";
    }

    /**
     * 用户 阅读帮助奖励
     *
     * @author lwb
     * @version 1.0
     * @date 2019年4月11日
     */
    public static class HelpAbout {
        public static final String LIST_HELPABOUT = "helpabout!listHelpAbout"; // 获取所有列表
        public static final String GAIN_AWARD = "helpabout!gainAward";// 领取奖励
    }

    /**
     * 商会
     *
     * @author lwb
     * @version 1.0
     * @date 2019年4月12日
     */
    public static class ChamberOfCommerce {
        public static final String LIST_TASK = "cofc!task";// 任务列表
        public static final String OPTION_TASK = "cofc!taskOption";// 任务操作
        public static final String REFRESH_TASK = "cofc!taskRefresh";// 刷新任务
        public static final String ADD_TASK = "cofc!addTask";// 增加可接受任务数量
        public static final String LIST_EXPERIENCE = "cofc!experience";// 跑商历练
        public static final String EXPERIENCE_GET_REWARD = "cofc!experienceGetReward";// 领取历练奖励
        public static final String LIST_SHOP = "cofc!shopList";// 商店列表
        public static final String BUY_SHOP = "cofc!buy";// 购买
        public static final String LIST_HONOR = "cofc!honorList";// 头衔列表
        public static final String GET_LV = "cofc!honorLv";// 商会等级
        public static final String ACCEPTED_TASK_INFO = "cofc!acceptedTaskInfo";
    }

    /**
     * 行会入口
     *
     * @author lwb
     * @version 1.0
     * @date 2019年5月15日
     */
    public static class Guild {
        public static final String GUILD_HAVE_GUILD = "guild!havejoinGuild";// 是否有加入行会
        public static final String GUILD_LIST = "guild!listGuild";
        public static final String GUILD_CREATE = "guild!createGuild";
        public static final String GUILD_JOIN = "guild!joinGuild";
        public static final String GUILD_INFO = "guild!infoOverview";
        public static final String GUILD_RENAME = "guild!renameGuild";
        /** 弹劾会长 */
        public static final String GUILD_IMPEACH_BOS = "guild!impeachBoss";
        public static final String GUILD_LIST_EXAMIE = "guild!listExamie";
        public static final String GUILD_MEMBER_OPTION = "guild!optionMember";// 成员操作
        public static final String GUILD_WRITE_WORDS = "guild!writeWord";
        public static final String GUILD_EXIT = "guild!exitGuild";
        public static final String GUILD_READ_WORDS = "guild!readWord";
        public static final String GUILD_ED_INFO_TASK = "guild!infoEightDiagramTask";// 八卦任务获取
        public static final String GUILD_ED_OPTION_TASK = "guild!optionEightDiagramTask";// 八卦任务操作
        public static final String GUILD_ED_REFREASH_TASK = "guild!refreshEightDiagramTask";// 八卦任务刷新
        public static final String GUILD_ED_HELP_TASK = "guild!helpEightDiagramTask";// 八卦任务求助
        public static final String GUILD_LIST_MEMBER = "guild!listMember";
        public static final String GUILD_LIST_SHOP = "guild!listShop";
        public static final String GUILD_BUY_SHOP = "guild!buyGoods";
        public static final String GUILD_ACCEPTED_TASK_INFO = "guild!acceptedTaskInfo";
    }

    /**
     * 消息记录入口
     *
     * @author: huanghb
     * @date: 2021/10/25 15:17
     */
    public static class HelperMessage {
        public static final String MESSAGE_WRITE = "helperMessage!write";
        public static final String MESSAGE_READ = "helperMessage!read";
    }

    /**
     * 阐截斗法 接口
     *
     * @author lwb
     * @version 1.0
     * @date 2019年6月18日
     */
    public static class Chanjie {
        public static final String CHECK_JOIN = "chanjie!checkJoin";// 检查是否加入教派
        public static final String JOIN_RELIGIOUS = "chanjie!join";
        public static final String MAIN_INFO = "chanjie!mainInfo";// 主页信息
        public static final String RANKING_LIST = "chanjie!rankingList";// 排行榜
        public static final String HONOR_LIST = "chanjie!honorList";// 荣誉榜
        public static final String SPECAIL_LIST = "chanjie!specailList";// 教派奇人
        public static final String THUMBS_UP = "chanjie!thumbsup";// 点赞
        public static final String BUY_BLOOD = "chanjie!buyblood";// 购买血量
        public static final String FIGHT_DEAL = "chanjie!fightDeal";// 购买血量
        public static final String WAR_SITUATION = "chanjie!warSituation";// 实时战况
    }

    /**
     * 碧游宫相关接口
     *
     * @author suhq
     * @date 2019-09-09 09:53:38
     */
    public static class BYPalace {
        /** 进入碧游宫 */
        public static final String ENTER_BYPALACE = "byPalace!enterBYPalace";
        /** 获得奖励 */
        public static final String GET_AWARDS = "byPalace!getAwards";
        /** 刷新奖励 */
        public static final String REFRESH_AWARDS = "byPalace!refreshAwards";
        /** 重置 */
        public static final String RESET = "byPalace!reset";
        /** 领悟 */
        public static final String REALIZATION = "byPalace!realization";
        /** 获得要排除的技能信息 */
        public static final String GET_EXCLUDE_INFO = "byPalace!getExcludeInfo";
        /** 筛选技能 */
        public static final String CHOOSE_EXCLUDE_SKILLS = "byPalace!chooseExcludeSkills";
    }

    /**
     * 玉虚宫相关接口
     *
     * @author suhq
     * @date 2019-09-09 09:53:38
     */
    public static class YuXG {
        /** 进入玉虚宫 */
        public static final String ENTER = "yuxg!enter";
        /** 祈符 */
        public static final String PRAY = "yuxg!pray";
        /** 设置许愿清单 */
        public static final String SET_WISHING_DETAILED = "yuxg!setWishingDetailed";
        /** 获取许愿清单 */
        public static final String GET_WISHING_DETAILED = "yuxg!getWishingDetailed";

        /** 熔炼 */
        public static final String MELT = "yuxg!melt";
        /** 使用符首改变符坛等级 */
        public static final String CHANGE_FUTAN = "yuxg!changeFuTan";
        /** 玉虚宫升级护符 */
        public static final String UPDATE_RUNE = "yuxg!updateRune";
        /** 设置(升级设置or符册设置) */
        public static final String SETTING = "yuxg!setting";
        /** 获取设置(升级设置or符册设置) */
        public static final String GET_SETTING = "yuxg!getSetting";
        /** 保护符图 */
        public static final String PROTECT_RUNE = "yuxg!protectRune";
        /** 取消符图保护 */
        public static final String UNPROTECT_RUNE = "yuxg!unprotectRune";
        /** 获取符册 */
        public static final String GET_RUNE_BOOKS = "yuxg!getRuneBooks";
        /** 解锁符册 */
        public static final String UNCLOCK_RUNE_BOOK = "yuxg!unlockRuneBook";
        /** 替换符图 */
        public static final String REPLACE_RUNE = "yuxg!replaceRune";
        /** 批量替换符图 */
        public static final String BATCH_REPLACE_RUNES = "yuxg!batchReplaceRunes";
        /** 编辑符册名 */
        public static final String EDIT_RUNE_BOOK_NAME = "yuxg!editRuneBookName";
        /** 熔炼值获取晶石 */
        public static final String USER_MELTVALUE_GET_SPAR = "yuxg!meltValueToSpar";
        /** 更新祈福设置 */
        public static final String UPDATE_PRAY_SETTINGS = "yuxg!updatePraySettings";

    }

    /**
     * 昆仑山相关接口
     *
     * @author: huanghb
     * @date: 2022/9/15 11:32
     */
    public static class KunLS {
        /** 炼制 */
        public static final String MAKING = "kunLS!making";
        /** 进入注灵室 */
        public static final String ENTER_INFUSION = "kunLS!enterInfusion";
        /** 注灵 */
        public static final String INFUSION = "kunLS!infusion";
        /** 灵宝出世 */
        public static final String BORN = "kunLS!born";
        /** 提炼 */
        public static final String REFINE = "kunLS!refine";
    }

    /**
     * 诛仙阵相关接口
     *
     * @author: hzf
     * @date: 2022/9/27 20:25
     */
    public static class Zxz {
        /** 进入诛仙阵 */
        public static final String ENTER = "zxz!enter";
        /** 进入难度 */
        public static final String ENTER_LEVEL = "zxz!enterLevel";
        /** 进入区域 */
        public static final String ENTER_REGION = "zxz!enterRegion";
        /** 编辑用户卡组 */
        public static final String EDIT_CARD_GROUP = "zxz!editCardGroup";
        /** 设置符册 */
        public static final String SET_FU_CE = "zxz!setFuCe";
        /**编辑词条 */
        public static final String EDIT_ENTRY = "zxz!editEntry";

        /** 扫荡 */
        public static final String MOP_UP = "zxz!mopUp";
        /** 敌方配置 */
        public static final String ENEMY_CONFIG = "zxz!enemyConfig";
        /** 查看词条 */
        public static final String GET_ENTRY = "zxz!getEntry";
        /** 查看用户卡组 */
        public static final String GET_USER_CARD_GROUP = "zxz!getUserCardGroup";
        /** 查看玩家榜单卡组 */
        public static final String GET_USER_RANK_CARD_GROUP = "zxz!getUserRankCardGroup";
        /** 开宝箱 */
        public static final String OPEN_BOX = "zxz!openBox";
        /** 开全通宝箱 */
        public static final String OPEN_DIFFICULTY_PASSBOX = "zxz!openDifficultyPassBox";
        /** 获取区域榜单 */
        public static final String GET_ZXZ_RANK = "zxz!getZxzRank";
        /** 获取诅咒效果 */
        public static final String get_Zu_Zhou = "zxz!getZuZhou";
        /** 进入四圣挑战 */
        public static final String ENTER_FOUR_SAINTS = "zxz!enterFourSaints";
        /**进入四圣区域 */
        public static final String ENTER_FOUR_SAINTS_REGION = "zxz!enterFourSaintsRegion";
        /** 进入四圣挑战战斗前 */
        public static final String ENTER_FOUR_SAINTS_CHALLENGE = "zxz!enterFourSaintsChallenge";
        /** 四圣挑战：编辑卡组 */
        public static final String EDIT_FOUR_SAINTS_CARD_GROUP = "zxz!editFourSaintsCardGroup";
        /** 四圣挑战编辑符册 */
        public static final String SET_FOUR_SAINTS_FU_CE = "zxz!setFourSaintsFuCe";
        /** 四圣挑战查看卡组 */
        public static final String GET_FOUR_SAINTS_CARD_GROUP = "zxz!getFourSaintsCardGroup";
        /** 四圣挑战 查看词条 */
        public static final String GET_FOUR_SAINTS_ENTRY = "zxz!getFourSaintsEntry";
        /** 四圣挑战 查看敌方配置 */
        public static final String GET_FOUR_SAINTS_ENEMY_REGION = "zxz!getFourSaintsEnemyRegion";
        /** 四圣挑战 开宝箱 */
        public static final String OPEN_FOUR_SAINTS_BOX = "zxz!openFourSaintsBox";
    }



    /**
     * 推送功能相关接口
     *
     * @author suchaobin
     * @date 2019-12-20 17:52
     */
    public static class Push {
        public static final String GET_PUSH = "push!getPush";
        public static final String UPDATE_PUSH = "push!updatePush";
    }

    /**
     * 战斗录像相关接口
     */
    public static class CombatVideo {
        public static final String SAVE_VIDEO = "combatVideo!save";
        public static final String COLLECT_VIDEO = "combatVideo!collect";
        public static final String DEL_VIDEO = "combatVideo!del";
        public static final String SHARE_VIDEO = "combatVideo!share";
        public static final String LIST_VIDEO = "combatVideo!list";
        public static final String SHARE_VICTORY = "combatVideo!shareVictory";
        public static final String STRATEGY_LIST_VIDEO = "combatVideo!strategyList";//攻略视频
    }

    /**
     * 封神任务助手
     */
    public static class FsHelper {
        public static final String ADD_TASK = "fsHepler!addTask";
        public static final String DEL_TASK = "fsHepler!delTask";
        public static final String LIST_TASK = "fsHepler!listTask";
    }

    /**
     * 玩家资源获取：背包、法宝
     */
    public static class UserAsset {
        public static final String LIST_PACKAGE = "userAsset!listPackage";
        public static final String LIST_FAST_TREASURE = "userAsset!listFastTreasure";
    }

    public static class CardComment {
        public static final String GET_COMMENTS = "comment!getCardComments";
        public static final String GET_RECENT_COMMENTS = "comment!getRecentCardComments";
        public static final String ADD_FAVORITE = "comment!addCardFavorite";
        public static final String COMMENT = "comment!cardComment";
        public static final String UPDATE_COMMENT = "comment!updateCardComment";
    }

    public static class CombatPVE {
        public static final String ATTACK = "combat!attackCity";
        public static final String AGAIN = "combat!attackAgain";
        public static final String RAPID_STRIKE = "combat!autoEndCombat";
        public static final String USE_WEAPON = "combat!useWeapon";
        public static final String NEXT_ROUND = "combat!nextRound";
        public static final String RECOVER_DATA = "combat!recoverAttack";
        public static final String SURRENDER = "combat!surrender";
        public static final String ESCAPE = "combat!escape";
        public static final String ACCOMPLISH_ACHIEVEMENT = "combat!accomplishAchievement";
        public static final String FST_ATTACK = "combat!attackFst";
    }

    public static class WanXian {
        public static final String GET_WANXIAN_TYPE = "wanxian!getWanxianType";//获取当前万仙阵类型
        public static final String MAIN_PAGE = "wanxian!mainPage";//主页
        public static final String LIST_QUALIFYING_RANK = "wanxian!listQualifyingRank";//资格赛榜单翻页
        public static final String LIST_ELIMINATION_SERIES_GROUP = "wanxian!listEliminationSeriesGroup";//资格赛榜单翻页
        public static final String HISTORY_SEASON = "wanxian!historySeason";//历史荣誉殿堂
        public static final String CHAMPION_PREDICTION_PAGE = "wanxian!championPredictionPage";//冠军预测界面
        public static final String CHAMPION_PREDICTION = "wanxian!championPrediction";//冠军预测
        public static final String SIGN_UP = "wanxian!signUp";//报名
        public static final String SAVE_CARDGROUP = "wanxian!saveCardGroup";
        public static final String LIST_FIGHT_LOGS = "wanxian!listFightLogs";//战报记录
        public static final String LIST_AWARDS = "wanxian!listAwards";//战报记录
        public static final String GET_CARD_GROUP = "wanxian!getCardGroup";
        public static final String PLAY_VIDEO = "wanxian!playVideo";
        public static final String LOGS_BY_VIDKEY = "wanxian!logsByVidKey";
    }

    public static class SnatchTreasure {
        /**
         * 进入夺宝界面
         */
        public static final String ENTER_SNATCH_TREASURE = "snatchTreasure!enterSnatchTreasure";
        /**
         * 抽奖
         */
        public static final String DRAW = "snatchTreasure!draw";
        /**
         * 开启周累计宝箱
         */
        public static final String OPEN_WEEK_BOX = "snatchTreasure!openWeekBox";
        /**
         * 周累计宝箱奖励预览
         */
        public static final String GET_WEEK_BOX_AWARD = "snatchTreasure!getWeekBoxAward";
    }

    public static class Lottery {
        /**
         * 进入奖券界面
         */
        public static final String ENTER_LOTTERY = "lottery!enterLottery";
        /**
         * 下注
         */
        public static final String BET = "lottery!bet";
    }

    /**
     * 奇珍接口
     */
    public static class RechargeActivities {
        public static final String LIST = "rechargeActivities!list";
        public static final String GAIN_AWARD = "rechargeActivities!gainAward";//领取奖励
        public static final String BUY_AWARD = "rechargeActivities!buyAward";//元宝或钻石购买
        public static final String PICK_AWARD = "rechargeActivities!pickAward";//选择奖励
        public static final String GAIN_ALL_AVAILABLE_AWARDS = "rechargeActivities!gainAllAvailableAwards";//一键领取季卡、月卡奖励
        public static final String REFRESH_ITEM = "rechargeActivities!refreshItem";//刷新项

    }

    /**
     * 魔王拍卖
     */
    public static class MaouAuction {
        /**
         * 获取拍卖信息
         */
        public static final String GET_AUCTION_INFO = "maouAuction!getAuctionInfo";
        /**
         * 出价
         */
        public static final String AUCTION_BID = "maouAuction!bid";
    }

    /**
     * 问卷调查
     */
    public static class Questionnaire {
        /**
         * 保存问卷调查
         */
        public static final String QUESTIONNAIRE_JOIN = "questionnaire!join";

        /**
         * 不显示问卷调查的小图标
         */
        public static final String QUESTIONNAIRE_HIDE_ICON = "questionnaire!hideIcon";
    }

    /**
     * 背包格子
     */
    public static class Bag {
        /**
         * 购买背包格子
         */
        public static final String BUY_BAG = "bag!buyBag";
    }

    /**
     * 卡牌
     */
    public static class CardSkillRecommend {
        public static final String RecommendList = "cardSkillRecommend!list";//获取推荐列表
        public static final String RecommendComment = "cardSkillRecommend!comment";//点赞、踩
        public static final String CARD_SKILLS_RECOMMEND = "cardSkillsRecommend!list";//获取推荐列表
    }

    /**
     * 巅峰对决接口
     *
     * @author suhq
     * @date 2019-06-21 09:42:40
     */
    public static class Dfdj {
        public static final String GET_FIGHTER_INFO = "dfdj!getFighterInfo";
        public static final String GET_FIGHTER_RANK = "dfdj!getFighterRank";
        public static final String GET_BEAN_INFO = "dfdj!getBeanInfo";
        public static final String BUY_BEAN = "dfdj!buyBean";
        public static final String BUY_MEDICINE = "dfdj!buyMedicine";
        public static final String GET_RANK_AWARD = "dfdj!getRankAward";
        public static final String GET_LAST_SEASON_RANK_AWARD = "dfdj!getLastSeasonRankers";
        public static final String ENABLE_MEDICINE = "dfdj!enableMedicine";
        public static final String GET_SPRINT_AWARD = "dfdj!getSprintAward";
        public static final String GET_CARD_GROUP = "dfdj!getCardGroup";
        public static final String SET_CARD_GROUP = "dfdj!setCardGroup";
    }

    /**
     * 技能卷轴
     */
    public static class SkillScroll {
        // 合成技能卷轴
        public static final String SYNTHESIS_SKILL_SCROLL = "skillScroll!synthesis";
        // 获取可指定的技能卷轴列表
        public static final String LIST_ABLE_SYNTHESIS_SKILL_SCROLLS = "skillScroll!listAbleSynthesis";
    }

    /**
     * 法外分身相关接口
     */
    public static class LeaderCard {
        //合成卡
        public static final String SYNTHESIS = "leaderCard!synthesis";
        /**
         * 主角卡主要信息
         */
        public static final String MAIN_INFO = "leaderCard!getInfo";
        public static final String GET_RANDOM_SKILL = "leaderCard!getRandomSkill";
        public static final String SET_RANDOM_SKILL = "leaderCard!setRandomSkill";
        /** 加强：升级、进阶、升星 */
        public static final String GET_UP_LV_INFO = "leaderCard!getUpLvInfo";
        public static final String ADD_POINT_RESET = "leaderCard!addPointReset";
        public static final String HV_INFO = "leaderCard!hvInfo";
        public static final String UP_HV = "leaderCard!upHv";
        public static final String UP_STAR = "leaderCard!upStar";
        /** 属性加点 */
        public static final String ADD_POINT = "leaderCard!addPoint";
        /** 穿戴装备、神兽、时装 */
        public static final String TAKE = "leaderCard!take";
        /** 卸下神兽 */
        public static final String TAKE_OFF = "leaderCard!takeOff";
        /** 神兽禁用技能 */
        public static final String ACTIVE_SKILL = "leaderCard!activeSkill";
        /** 获取神兽信息 */
        public static final String GET_BEAST_INFO = "leaderCard!getBeastInfo";
        /** 强化装备 */
        public static final String STRENGTHEN_EQUIPMENT = "leaderCard!strengthenEquipment";
        /** 获取星图信息 */
        public static final String GET_EQUIPMENTS_INFO = "leaderCard!getEquipmentsInfo";
        /** 装备星图升星 */
        public static final String UPDATE_EQUIPMENT_STAR_MAP = "leaderCard!updateEquipmentStarMap";
        /** 获取时装列表 */
        public static final String GET_FASHIONS = "leaderCard!listFashions";
        /** 升级时装 */
        public static final String UPDATE_FASHION = "leaderCard!updateFashion";

        /**
         * 技海主页
         */
        public static final String SKILL_TREE = "leaderCard!skillTree";

        /**
         * 替换技能
         */
        public static final String REPLACE_SKILL = "leaderCard!replaceSkill";
        /**
         * 技海技能列表
         */
        public static final String SKILL_TREE_LIST = "leaderCard!listSkillTree";

        /**
         * 技海树状图页
         */
        public static final String SKILL_TREE_INFO = "leaderCard!infoSkillTree";

        /**
         * 翻页技海
         */
        public static final String TURN_PAGE_SKILL_TREE = "leaderCard!turnPageSkillTree";


        /**
         * 技海技能激活
         */
        public static final String SKILL_TREE_ACTIVE = "leaderCard!activeSkillTree";
        /**
         * 修改主角卡属性
         */
        public static final String CHANGE_PROPERTY = "leaderCard!changeProperty";
        /**
         * 激活主角卡属性
         */
        public static final String ACTIVE_PROPERTY = "leaderCard!activeProperty";
        /**
         * 获取主角卡所有属性
         */
        public static final String LIST_PROPERTY = "leaderCard!listProperty";
        /** 切换技能组按钮 */
        public static final String GET_SKILLS_GROUP= "leaderCard!getSkillsGroup";
        /** 切换技能组 */
        public static final String CHANGE_SKILLS_GROUP = "leaderCard!changeSkillsGroup";
        /** 激活技能组 */
        public static final String ACTIVATION_SKILLS_GROUP = "leaderCard!activationSkillsGroup";
    }

    /**
     * 六十四卦象
     */
    public static class Hexagram {
        /**
         * 获取当前BUFF信息
         */
        public static final String GET_BUFF_INFO = "hexagram!info";
        /**
         * 抽卦
         */
        public static final String GET_HEXAGRAM = "hexagram!draw";
    }

    /**
     * 迷仙洞
     */
    public static class MiXianDong {
        /**
         * 进入迷仙洞
         */
        public static final String INTO = "mxd!into";
        /**
         * 进入宝库
         */
        public static final String INTO_TREASURE_HOUSE = "mxd!intoTreasureHouse";
        /**
         * 到达事件所在位置
         */
        public static final String TOUCH_POS = "mxd!touchPos";
        /**
         * 进入下一层迷仙洞
         */
        public static final String NEXT = "mxd!next";
        /**
         * 退出迷仙洞
         */
        public static final String CLOSE = "mxd!close";
        /**
         * 熔炼
         */
        public static final String SMELT = "mxd!smelt";
        /**
         * 保存卡组
         */
        public static final String SAVE_CARDS = "mxd!saveCards";
        /**
         * 重置
         */
        public static final String RESET = "mxd!reset";
    }

    /**
     * 妖族相关接口
     */
    public static class YaoZu {
        /** 生成妖族 */
        public static final String GENERATE = "yaozu!generate";
        /** 获得妖族信息 */
        public static final String GAIN_YAO_ZU_INFO = "yaozu!gainInfo";
        /** 获取斩妖卡组 */
        public static final String GAIN_YAO_ZU_CARD_GROUP = "yaozu!gainYaoZuCardGroup";
        /** 触发妖族的信息 */
        public static final String ARRIVE_YAO_ZU_INFO = "yaozu!arriveYaoZuInfo";
        /** 撤退 */
        public static final String RETREAT = "yaozu!retreat";
        /** 设置攻击卡组 */
        public static final String SET_ATTACK_CARDS = "yaozu!setAttackCards";
        /** 同步卡组 */
        public static final String SYNCHRONIZE_ATTACK_CARDS = "yaozu!synchronizeAttackCards";
    }

    /**
     * 轮回世界接口定义
     *
     * @author: suhq
     * @date: 2021/9/14 5:10 下午
     */
    public static class Transmigration {
        /** 获取轮回世界主页信息 */
        public static final String GET_INFO = "transmigration!getInfo"; // ==>> RDTransmigrationInfo
        /** 获取所有城池的挑战信息 */
        public static final String LIST_CHALLENGE_RECORDS = "transmigration!listChallengeRecords";// ==>> RDTransmigrationRecords
        /** 获取当前所在城池的挑战信息 */
        public static final String GET_CHALLENGE_INFO = "transmigration!getChallengeInfo";//?cityId=***   ==>> RDTransmigrationChallengeInfo
        /** 获取高光时刻 */
        public static final String LIST_HIGH_LIGHTS = "transmigration!listHighLights";//?isPersonal=*** 1 个人 0 全服 ==>> RDTransmigrationHighLights
//        /** 获取排行信息 */
//        public static final String LIST_RANKERS = "transmigration!listRankers";//page***&limit=*** ==>> RDTransmigrationRankers
//        /** 获取排行奖励 */
//        public static final String LIST_RANKER_AWARDS = "transmigration!listRankerAwards"; // ==>> RDRankerAwards
        /** 获取卡组 */
        public static final String GET_CARD_GROUP = "transmigration!getCardGroup";// ==>> RDCardGroup
        /** 设置卡组 */
        public static final String SET_CARD_GROUP = "transmigration!setCardGroup";//?cardIds=***
        /** 同步卡组 */
        public static final String SYNC_CARD_GROUP = "transmigration!synCardGroup";
        /** 获取目标奖励 */
        public static final String GET_TARGET_AWARDS = "transmigration!gainTargetAwards";//targetId=***
        /** 获取战斗评分奖励 */
        public static final String GET_FIGTHT_AWARDS = "transmigration!gainFightAwards";//cityId=***&index=***
    }

    /**
     * 村庄接口定义
     */
    public static class CunZ {
        /** 验证村庄怪谈 */
        public static final String VERIFY_TALK = "cunz!verifyTalk";
    }

    /**
     * 商帮接口定义
     */
    public static class BusinessGang {
        /** 进入商帮 */
        public static final String ENTER_BUSINESS_GANG = "businessGang!enter";
        /** 加入商帮 */
        public static final String JOIN_BUSINESS_GANG = "businessGang!join";
        /** 退出商帮 */
        public static final String QUIT_BUSINESS_GANG = "businessGang!quit";
        /** 送礼 */
        public static final String SEND_GIFTS = "businessGang!sengGifts";
        /** 获取npc好感度 */
        public static final String VISIT_BUSINESS_GANG = "businessGang!visitBusinessGang";
        /** 刷新任务 */
        public static final String REFRESH_TASK = "businessGang!refreshTask";
        /** 获取各商帮掌舵人信息 */
        public static final String GAIN_BUSINESS_NPC_INFO = "businessGang!gainBusinessGangNpcInfo";
        /** 令牌兑换奖励领取次数 */
        public static final String EXCHANGE_AVAILABLE_TIMES = "businessGang!exchangeAvailableTimes";
    }

    /**
     * 梦魇女娲庙定义接口
     */
    public static class NightmareNvWM {
        /** 开启梦魇女娲庙 */
        public static final String IS_ACTIVE_NIGHTMARE_NWM = "nightMareNWM!isActiveNightmareNWM";
        /** 进入捏人总界面 */
        public static final String ENTER_KNEAD_SOIL = "nightMareNWM!enterKneadSoil";
        /** 捏人 */
        public static final String PINCH_PEOPLE = "nightMareNWM!pinchPeople";
        /** 获得累计分数奖励 */
        public static final String SEND_TOTAL_SCORE_AWARD = "nightMareNWM!sendTotalScoreAward";
        /** 进入女娲集市 */
        public static final String ENTER_NVW_MARKET = "nightMareNWM!enterNvWMarket";
        /** 获得对应的卡牌道具 */
        public static final String CARD_RELATE_TREASURE = "nightMareNWM!cardRelateTreasure";
        /** 进入神格仓库 */
        public static final String ENTER_GODS_ALTAR = "nightMareNWM!enterGodsAltar";
        /** 消耗令牌 */
        public static final String CONSUME_BRAND = "nightMareNWM!consumeBrand";
        /** 摊位详情 */
        public static final String BOOTH_DETAILS = "nightMareNWM!boothDetails";
        /** 发送消息 */
        public static final String SEND_MESSAGE = "nightMareNWM!sendMessage";
        /** 讨价还价 */
        public static final String BARGAIN = "nightMareNWM!bargain";
        /** 同意还价 */
        public static final String AGREE_PRICE = "nightMareNWM!agreePrice";
        /** 拒绝还价 */
        public static final String REFUSE_PRICE = "nightMareNWM!refusePrice";
        /** 撤销还价 */
        public static final String REVOKE_PRICE = "nightMareNWM!revokePrice";
        /** 我的摊位 */
        public static final String MY_BOOTH = "nightMareNWM!myBooth";
        /** 租赁摊位 */
        public static final String RENTAL_BOOTH = "nightMareNWM!rentalBooth";
        /** 续租摊位 */
        public static final String LEASE_RENEWAL = "nightMareNWM!leaseRenewal";
        /** 更改摊位状态 */
        public static final String UPDATE_BOOTH_STATUS = "nightMareNWM!updateBoothStatus";
        /** 上架商品 */
        public static final String LISTINGS = "nightMareNWM!listings";
        /** 下架商品 */
        public static final String TAKE_DOWN = "nightMareNWM!takeDown";
        /** 更改商品 */
        public static final String MODIFY_PRODUCT = "nightMareNWM!modifyProduct";
        /** 更改要价 */
        public static final String MODIFY_BARGAIN = "nightMareNWM!modifyBargain";
        /** 设置摊位标语 */
        public static final String SET_BOOTH_SLOGAN = "nightMareNWM!setBoothSlogan";
        /** 交易 */
        public static final String TRADE = "nightMareNWM!trade";
        /** 交易记录 */
        public static final String TRANSACTION_RECORD = "nightMareNWM!transactionRecord";
        /** 获得模板 */
        public static final String GET_PRICE_MODEL = "nightMareNWM!getPriceModel";
        /** 设置模板 */
        public static final String SET_PRICE_MODEL = "nightMareNWM!setPriceModel";
        /** 还价列表 */
        public static final String BARGAIN_LIST = "nightMareNWM!getBargainList";
        /** 搜索摊位 */
        public static final String SEARCH_BOOTH = "nightMareNWM!searchBooth";
    }
}