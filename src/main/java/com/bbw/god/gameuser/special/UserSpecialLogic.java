package com.bbw.god.gameuser.special;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.city.chengc.ChengChiLogic;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.special.RDSpecialBuinessInfo.RDSpecialPrice;
import com.bbw.god.gameuser.special.event.EPPocketSpecial;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.special.GameSpecialPrice;
import com.bbw.god.server.special.GameSpecialService;
import com.bbw.god.server.special.ServerSpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserSpecialLogic {
    @Autowired
    private ServerSpecialService serverSpecialService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private ChengChiLogic chengChiLogic;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private GameSpecialService gameSpecialService;

    /**
     * 获得特产的城市信息
     *
     * @param specialId
     * @return
     */
    public RDSpecialBuinessInfo getSpecialCities(long guId, int sid, int specialId) {
        RDSpecialBuinessInfo rd = new RDSpecialBuinessInfo();
        CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
        Integer specialCountry = special.getCountry();
        TypeEnum countryEnum = TypeEnum.fromValue(specialCountry);
        if (countryEnum == null) {
            throw new ExceptionForClientTip("special.country.not.exist");
        }
        // 系统特产出售城池的价格
        List<RDSpecialPrice> rdSpecialCities = CityTool.getChengCs().stream()
                                                       .filter(tmp -> isShowCity(countryEnum, tmp.getId()))
                                                       .map(tmp -> {
                                                           int cityId = tmp.getId();
                                                           CfgCityEntity city = CityTool.getCityById(cityId);
                                                           Integer price = serverSpecialService.getSellingPrice(special, city);
                                                           int premiumRate = chengChiLogic.getPremiumRate(guId, cityId);
                                                           price = price * (100 + premiumRate) / 100;
                                                           return new RDSpecialPrice(cityId, price);
                                                       }).collect(Collectors.toList());
        rd.setSpecialCities(rdSpecialCities);
        // 玩家特产卖出的城池价格
        List<Integer> sellingCities = ListUtil.parseStrToStrs(special.getSellingCities()).stream()
                                              .filter(StrUtil::isNotEmpty)
                                              .map(Integer::parseInt)
                                              .collect(Collectors.toList());
        List<RDSpecialPrice> rdSellingCities = sellingCities.stream().map(sc -> {
            Integer price = special.getPrice();
            int discount = chengChiLogic.getDiscount(guId, sc);
            price = price * (100 - discount) / 100;
            return new RDSpecialPrice(sc, price);
        }).collect(Collectors.toList());
        // 合成特产特殊处理
        IActivity a = activityService.getActivity(sid, ActivityEnum.TCHC);
        if (null != a && special.isSyntheticSpecialty()) {
            List<ChengC> chengCs = CityTool.getChengCs();
            List<RDSpecialPrice> list = chengCs.stream().map(tmp -> {
                CfgServerEntity server = ServerTool.getServer(sid);
                int price = gameSpecialService.getSellPrice(specialId, server.getGroupId(), CityTool.getCityById(tmp.getId()));
                return new RDSpecialPrice(tmp.getId(), price);
            }).collect(Collectors.toList());
            rdSpecialCities.addAll(list);
        }
        rd.setSellingCities(rdSellingCities);
        return rd;
    }

    /**
     * 筛选出商情页面需要展示的城池价格信息
     *
     * @param countryEnum 选择的特产的产出国家属性枚举
     * @param cityId      城池id
     * @return
     */
    private boolean isShowCity(TypeEnum countryEnum, int cityId) {
        CfgCityEntity city = CityTool.getCityById(cityId);
        switch (countryEnum) {
            case Gold:
                if (city.getCountry() == TypeEnum.Wood.getValue()) {
                    return true;
                }
                break;
            case Wood:
                if (city.getCountry() == TypeEnum.Gold.getValue()) {
                    return true;
                }
                break;
            case Water:
                if (city.getCountry() == TypeEnum.Fire.getValue()) {
                    return true;
                }
                break;
            case Fire:
                if (city.getCountry() == TypeEnum.Water.getValue()) {
                    return true;
                }
                break;
            case Earth:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * 丢弃特产
     *
     * @param guId
     * @param dataId
     * @return
     */
    public RDSuccess discardSpecial(long guId, long dataId) {
        Optional<UserSpecial> optional = userSpecialService.getOwnSpecialByDataId(guId, dataId);
        if (!optional.isPresent()) {
            //不存在
            throw new ExceptionForClientTip("special.not.exist");
        }
        BaseEventParam bep = new BaseEventParam(guId, WayEnum.SPECIAL_DISCARD);
        UserSpecial userSpecial = optional.get();
        CfgSpecialEntity special = SpecialTool.getSpecialById(userSpecial.getBaseId());
        EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(),
                special.getId(), special.getBuyPrice(userSpecial.getDiscount()));
        EPSpecialDeduct ep = EPSpecialDeduct.instance(bep, Arrays.asList(info));
        SpecialEventPublisher.pubSpecialDeductEvent(ep);
        return new RDSuccess();
    }

    /**
     * 放入口袋特产操作
     *
     * @param guId
     * @param dataId
     * @return
     */
    public RDSuccess lockSpecial(long guId, long dataId) {
        int size = userSpecialService.getPocketEmptySize(guId);
        if (size < 1) {
            //空间不足
            throw new ExceptionForClientTip("specail.pocket.full");
        }
        Optional<UserSpecial> optional = userSpecialService.getOwnSpecialByDataId(guId, dataId);
        if (!optional.isPresent()) {
            //不存在
            throw new ExceptionForClientTip("special.not.exist");
        }
        Optional<UserSpecial> pocketOptional =
                userSpecialService.getLockSpecials(guId).stream().filter(p -> p.getBaseId() == dataId).findFirst();
        if (pocketOptional.isPresent()) {
            throw new ExceptionForClientTip("specail.pocket.exists");
        }
        EPPocketSpecial ep = new EPPocketSpecial(new BaseEventParam(guId), dataId);
        SpecialEventPublisher.pubSpecialLockEvent(ep);
        return new RDSuccess();
    }

    /**
     * 取出口袋操作
     *
     * @param guId
     * @param dataId
     * @return
     */
    public RDSuccess unlockSpecial(long guId, long dataId) {
        EPPocketSpecial ep = new EPPocketSpecial(new BaseEventParam(guId), dataId);
        SpecialEventPublisher.pubSpecialUnLockEvent(ep);
        return new RDSuccess();
    }

    /**
     * 合成特产
     *
     * @param uid         玩家id
     * @param materialId1 材料id1
     * @param materialId2 材料id2
     * @param targetId    合成特产的id
     * @return
     */
    public RDCommon synthesisSpecial(long uid, int materialId1, int materialId2, int targetId) {
        RDCommon rd = new RDCommon();
        // 是否在活动期间内
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        IActivity a = activityService.getActivity(sid, ActivityEnum.TCHC);
        if (null == a) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        // 获取特产
        List<UserSpecial> ownSpecials = userSpecialService.getOwnSpecials(uid);
        Optional<UserSpecial> optional1 = ownSpecials.stream().filter(tmp -> tmp.getBaseId().equals(materialId1)).findFirst();
        Optional<UserSpecial> optional2 = ownSpecials.stream().filter(tmp -> tmp.getBaseId().equals(materialId2)).findFirst();
        // 检查资源
        if (!optional1.isPresent() || !optional2.isPresent()) {
            throw new ExceptionForClientTip("synthesis.special.material.not.enough");
        }
        List<Integer> needSpecialIds = Arrays.asList(optional1.get().getBaseId(), optional2.get().getBaseId());
        if (!SpecialTool.getSpecialById(targetId).getMaterialIds().containsAll(needSpecialIds)) {
            throw new ExceptionForClientTip("synthesis.special.material.not.enough");
        }
        // 扣除特产
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = new ArrayList<>();
        List<Integer> materialIds = Arrays.asList(materialId1, materialId2);
        for (int i = 0; i < materialIds.size(); i++) {
            UserSpecial userSpecial = i == 0 ? optional1.get() : optional2.get();
            Long id = userSpecial.getId();
            CfgSpecialEntity special = SpecialTool.getSpecialById(userSpecial.getBaseId());
            int buyPrice = special.getBuyPrice(userSpecial.getDiscount());
            EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(id, special.getId(), buyPrice);
            specialInfoList.add(info);
        }
        GameUser gu = gameUserService.getGameUser(uid);
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.SYNTHESIS, rd);
        EPSpecialDeduct ep = EPSpecialDeduct.instance(bep, gu.getLocation().getPosition(), specialInfoList);
        SpecialEventPublisher.pubSpecialDeductEvent(ep);
        // 发放新特产
        List<EVSpecialAdd> specialAdds = Arrays.asList(new EVSpecialAdd(targetId % 100));
        SpecialEventPublisher.pubSpecialAddEvent(uid, specialAdds, WayEnum.SYNTHESIS, rd);
        // 发布特产合成事件
        SpecialEventPublisher.pubSpecialSynthesisEvent(bep);
        return rd;
    }

    /**
     * 进入特产合成界面
     *
     * @param uid 玩家id
     * @return
     */
    public RDEnterSynthesisSpecial enterSynthesisSpecial(long uid) {
        // 是否在活动期间内
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        IActivity a = activityService.getActivity(sid, ActivityEnum.TCHC);
        if (null == a) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        long remainTime = getRefreshRemainTime();
        List<GameSpecialPrice> gameDatas = gameDataService.getGameDatas(GameSpecialPrice.class);
        List<RDEnterSynthesisSpecial.SynthesisSpecialInfo> infos = new ArrayList<>();
        List<CfgSpecialEntity> cfgSpecialEntityList = SpecialTool.getSpecials(SpecialTypeEnum.SYNTHETIC);
        for (CfgSpecialEntity cfgSpecialEntity : cfgSpecialEntityList) {
            int specialId = cfgSpecialEntity.getId();
            List<Integer> materialIds = cfgSpecialEntity.getMaterialIds();
            GameSpecialPrice data = gameDatas.stream().filter(tmp -> tmp.getSpecialId() == specialId).findFirst().orElse(null);
            if (null == data) {
                int serverGroup = ServerTool.getServerGroup(sid);
                data = gameSpecialService.initGameSynthesisSpecialData(specialId, serverGroup);
            }
            RDEnterSynthesisSpecial.SynthesisSpecialInfo info = new RDEnterSynthesisSpecial.SynthesisSpecialInfo(specialId, materialIds, data.getHighPriceCountry());
            infos.add(info);
        }
        return new RDEnterSynthesisSpecial(infos, remainTime);
    }

    private long getRefreshRemainTime() {
        Date hour0 = DateUtil.getDateBegin(DateUtil.now());
        Date hour8 = DateUtil.addHours(hour0, 8);
        Date hour16 = DateUtil.addHours(hour0, 16);
        Date hour24 = DateUtil.addHours(hour0, 24);
        Date now = DateUtil.now();
        // 0-8点期间
        if (now.after(hour0) && now.before(hour8)) {
            return hour8.getTime() - System.currentTimeMillis();
        }
        // 8-16点期间
        if (now.after(hour8) && now.before(hour16)) {
            return hour16.getTime() - System.currentTimeMillis();
        }
        // 16-24点期间
        return hour24.getTime() - System.currentTimeMillis();
    }

    /**
     * 获取特产设置
     *
     * @param uid
     * @return
     */
    public RDSpecialSetting getSpecialSetting(long uid) {
        UserSpecialSetting setting = userSpecialService.getCurUserSpecialSetting(uid);
        return RDSpecialSetting.getInstance(setting);
    }

    /**
     * 更新特产设置
     *
     * @param uid 玩家id
     * @return
     */
    public Rst updateSpecialSetting(long uid, CPUserSpecialSeting cpUserSpecialSeting) {
        //一键购买特产状态参数检测
        List<Integer> specialIds = ListUtil.parseStrToInts(cpUserSpecialSeting.getSpecials());
        //特产排序
        if (ListUtil.isNotEmpty(specialIds)) {
            specialIds = specialIds.stream().distinct().collect(Collectors.toList());
        }
        //特产一键状态设置
        UserSpecialSetting setting = userSpecialService.getCurUserSpecialSetting(uid);
        setting.updateUserSpecialSetting(specialIds, cpUserSpecialSeting);
        //更新特产设置
        gameUserService.updateItem(setting);
        return Rst.businessOK();
    }
}
