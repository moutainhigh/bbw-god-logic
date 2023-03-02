package com.bbw.god.city.taiyf;

import com.bbw.coder.CoderNotify;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.ICityHandleProcessor;
import com.bbw.god.city.entity.UserTYFCell;
import com.bbw.god.city.entity.UserTYFTurn;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.special.SpecialChecker;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 太一府
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Component
public class TaiYFProcessor implements ICityArriveProcessor, ICityHandleProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.TYF);
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardRandomService userCardRandomService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private MonthLoginLogic monthLoginLogic;
    // 选卡策略
    private static final String[] STRATEGY_KEYS = {RandomKeys.TAIYF_5, RandomKeys.TAIYF_10, RandomKeys.TAIYF_15, RandomKeys.TAIYF_20, RandomKeys.TAIYF_25};

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return cityTypes;
    }

    @Override
    public Class<RDArriveTaiYF> getRDArriveClass() {
        return RDArriveTaiYF.class;
    }

    @Override
    public RDArriveTaiYF arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {

        List<Integer> specialCells = getFilledSpecialIds(gu);

        RDArriveTaiYF rdArriveTaiYF = new RDArriveTaiYF();
        rdArriveTaiYF.setSpecialCells(specialCells);
        rdArriveTaiYF.setHandleStatus("1");
        return rdArriveTaiYF;
    }

    @Override
    public RDCommon handleProcessor(GameUser gu, Object param) {
        RDCommon rd = new RDCommon();
        long uid = gu.getId();
        int specialId = (Integer) param;
        int tyfNeedFillNum = CityConfig.bean().getOcDATA().getTyfNeedFillNum();
        // 太一府只能捐献普通和高级特产
        if (specialId > tyfNeedFillNum) {
            throw new ExceptionForClientTip("city.tyf.not.topSpecial");
        }
        // 检查捐献的特产是否拥有
        UserSpecial userSpecial = userSpecialService.getOwnSpecialBySpecialId(uid, specialId);
        SpecialChecker.checkIsOwnSpecial(userSpecial);

        // 该特产是否已捐献
        UserTyfFillRecord fillRecord = getFillRecord(uid);
        List<Integer> filledSpecialIds = fillRecord.getSpecialIds();
        boolean isFilled = filledSpecialIds.contains(specialId);
        if (isFilled) {
            throw new ExceptionForClientTip("city.tyf.already.fillTheOne");
        }
        int filledCount = filledSpecialIds.size() + 1;
        // 奖励发放
        int star = getAwardStar(filledCount);
        if (star > 0) {
            int cardId = 0;
            // ------------刘少军 修改 为从 抽卡策略获取卡牌 2019-04-10

            // 指定参数
            RandomParam randomParams = new RandomParam();
            List<UserCard> ownCards = userCardService.getUserCards(uid);
            randomParams.setRoleCards(ownCards);
            randomParams.setExtraCardsToMap(ownCards);
            String strategyKey = STRATEGY_KEYS[star - 1];
            Optional<CfgCardEntity> card = userCardRandomService.getRandomCard(uid, strategyKey, randomParams);

            if (card.isPresent()) {
                cardId = card.get().getId();
            } else {
                String title = "卡牌策略[" + strategyKey + "]错误!";
                String msg = "区服sid[" + gu.getServerId() + "]玩家[" + uid + "," + gu.getRoleInfo().getNickname() + "]";
                msg += "未能从[太一府]获得卡牌！";
                CoderNotify.notifyCoderInfo(title, msg);
                // ------------ 2019-04-10 之前的原来的算法------------------------
                if (star < 5) {
                    cardId = CardTool.getRandomNotSpecialCard(star).getId();
                } else {
                    // 五星卡牌奖励
                    cardId = userCardService.getCard5ForCityDontation(uid);
                }
            }
            if (monthLoginLogic.isExistEvent(gu.getId(),MonthLoginEnum.GOOD_TY)){
                CardEventPublisher.pubCardAddEvent(uid, Arrays.asList(cardId,cardId), WayEnum.TYF, "太一府捐献特产1", rd);
            }else {
                CardEventPublisher.pubCardAddEvent(uid, cardId, WayEnum.TYF, "太一府捐献特产", rd);
            }
        }

        // 扣除特产
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.TYF, rd);
        CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
        EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(), specialId, special.getBuyPrice(userSpecial.getDiscount()));
        EPSpecialDeduct ep = EPSpecialDeduct.instance(bep, gu.getLocation().getPosition(), Arrays.asList(info));
        SpecialEventPublisher.pubSpecialDeductEvent(ep);

        // 新一轮
        if (filledCount == tyfNeedFillNum) {
            fillRecord.setIsFillAll(true);// 将本轮标注已满
            addNewTyfFillRecord(uid);
        }
        // 捐献记录
        fillRecord.addFillSpecial(specialId);
        gameUserService.updateItem(fillRecord);
        CityEventPublisher.pubTyfFillEvent(gu.getId(), rd);
        return rd;
    }

    /**
     * 获取已捐赠的特产
     *
     * @param gu
     * @return
     */
    public List<Integer> getFilledSpecialIds(GameUser gu) {
        long uid = gu.getId();
        List<UserTyfFillRecord> fillRecords = gameUserService.getMultiItems(uid, UserTyfFillRecord.class);
        // 没有任何的UserTyfFillRecord
        if (ListUtil.isEmpty(fillRecords)) {
            List<Integer> specialCells = new ArrayList<>();

            // 兼容旧数据
            UserTYFTurn turn = getCurTYFTurn(gu);
            List<UserTYFCell> userTYFCellObjs = getTYFDonates(gu, turn);
            if (ListUtil.isNotEmpty(userTYFCellObjs)) {
                specialCells = userTYFCellObjs.stream().map(UserTYFCell::getBaseId).collect(Collectors.toList());
            }

            // 创建投注记录
            UserTyfFillRecord fillRecord = UserTyfFillRecord.instance(uid, specialCells);
            gameUserService.addItem(uid, fillRecord);

            return specialCells;
        }
        UserTyfFillRecord fillRecord = fillRecords.stream().filter(tmp -> !tmp.getIsFillAll()).findFirst().get();
        return fillRecord.getSpecialIds();
    }

    /**
     * 获取未捐赠的特产
     *
     * @param gu
     * @return
     */
    public List<Integer> getUnFilledSpecialIds(GameUser gu) {
        List<Integer> filledSpecialIds = getFilledSpecialIds(gu);
        List<Integer> specialIds = SpecialTool.getSpecials().stream().map(CfgSpecialEntity::getId).collect(Collectors.toList());
        specialIds.removeAll(filledSpecialIds);
        return specialIds;
    }

    private UserTyfFillRecord getFillRecord(long uid) {
        List<UserTyfFillRecord> fillRecords = gameUserService.getMultiItems(uid, UserTyfFillRecord.class);
        // 没有任何的UserTyfFillRecord
        if (ListUtil.isEmpty(fillRecords)) {
            return addNewTyfFillRecord(uid);
        }
        Optional<UserTyfFillRecord> optional = fillRecords.stream().filter(tmp -> !tmp.getIsFillAll()).findFirst();
        // 没有捐献中的记录
        if (!optional.isPresent()) {
            return addNewTyfFillRecord(uid);
        }
        return optional.get();
    }

    /**
     * 设置新的一轮
     *
     * @param uid
     * @return
     */
    private UserTyfFillRecord addNewTyfFillRecord(long uid) {
        UserTyfFillRecord fillRecord = UserTyfFillRecord.instance(uid, new ArrayList<>());
        gameUserService.addItem(uid, fillRecord);
        return fillRecord;
    }

    /**
     * 获取当前特产捐赠轮次
     *
     * @param gameUser
     * @return
     */
    @Deprecated
    private UserTYFTurn getCurTYFTurn(GameUser gameUser) {
        List<UserTYFTurn> userTYFTurnObjs = gameUserService.getMultiItems(gameUser.getId(), UserTYFTurn.class);
        if (userTYFTurnObjs.size() == 0) {
            userTYFTurnObjs.add(addNewTYFTurn(gameUser));
        }
        return userTYFTurnObjs.get(userTYFTurnObjs.size() - 1);
    }

    /**
     * 设置新的一轮
     *
     * @param gu
     * @return
     */
    @Deprecated
    private UserTYFTurn addNewTYFTurn(GameUser gu) {
        UserTYFTurn turn = UserTYFTurn.instance(gu.getId());
        gameUserService.addItem(gu.getId(), turn);
        return turn;
    }

    /**
     * 太一府某一轮次捐献的特产
     *
     * @param gu
     * @param turn
     * @return
     */
    @Deprecated
    private List<UserTYFCell> getTYFDonates(GameUser gu, UserTYFTurn turn) {
        return gameUserService.getMultiItems(gu.getId(), UserTYFCell.class).stream().filter(userTYFCell -> userTYFCell.getTyfTurnId().longValue() == turn.getId().longValue()).collect(Collectors.toList());
    }

    /**
     * 获得奖励的星级
     *
     * @param filledCount
     * @return
     */
    private int getAwardStar(int filledCount) {
        int star = 0;
        if (filledCount % 5 == 0) {
            star = filledCount / 5;
        }
        return star;
    }

    @Override
    public String getTipCodeForAlreadyHandle() {
        return "city.tyf.already.fill";
    }

}
