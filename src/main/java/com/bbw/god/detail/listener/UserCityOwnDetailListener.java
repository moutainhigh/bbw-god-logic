package com.bbw.god.detail.listener;

import com.bbw.common.ListUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsCityOwnDetailEntity;
import com.bbw.god.db.service.InsCityOwnDetailService;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.pay.UserReceipt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 城池占有明细监听
 *
 * @author suhq
 * @date 2020-04-15 10:59
 **/
//@Component
public class UserCityOwnDetailListener {
    @Autowired
    private InsCityOwnDetailService insCityOwnDetailService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;

    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        long guId = ep.getGuId();
        InsCityOwnDetailEntity detailEntity = new InsCityOwnDetailEntity();
        //区服信息
        CfgServerEntity server = gameUserService.getOriServer(guId);
        detailEntity.setServerGroup(server.getGroupId());
        detailEntity.setSid(server.getId());
        //玩家信息
        GameUser gameUser = gameUserService.getGameUser(guId);
        detailEntity.setUid(guId);
        detailEntity.setGuLv(gameUser.getLevel());
        //充值金额
        int rechargeAmount = 0;
        List<UserReceipt> userReceipts = gameUserService.getMultiItems(guId, UserReceipt.class);
        if (ListUtil.isNotEmpty(userReceipts)) {
            rechargeAmount = userReceipts.stream().mapToInt(UserReceipt::getPrice).sum();
        }
        detailEntity.setPay(rechargeAmount);
        //城池信息
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        detailEntity.setCityId(city.getId());
        detailEntity.setCityLv(city.getLevel());
        detailEntity.setCityCountry(city.getCountry());
        detailEntity.setCityName(city.getName());
        detailEntity.setCityLvNum(userCityService.getOwnCityNumAsLevel(guId, city.getLevel()));
        detailEntity.setCityNum(userCityService.getUserCities(guId).size());
        //自角色创建时间
        setTimeSinceRoleCreate(guId, detailEntity);

        insCityOwnDetailService.insert(detailEntity);
    }

    /**
     * 自角色创建时间
     *
     * @param uid
     * @param detailEntity
     * @return
     */
    private String setTimeSinceRoleCreate(long uid, InsCityOwnDetailEntity detailEntity) {
        UserLoginInfo loginInfo = gameUserService.getSingleItem(uid, UserLoginInfo.class);
        Long seconds = (System.currentTimeMillis() - loginInfo.getEnrollTime().getTime()) / 1000;
        int minutes = seconds.intValue() / 60;
        if (seconds % 60 > 0) {
            minutes++;
        }
        String timeStr = "";
        int date = minutes / (24 * 60);
        int hour = (minutes % (24 * 60)) / 60;
        int minute = minutes % 60;
        timeStr = date + "天" + hour + "时" + minute + "分";
        detailEntity.setRoleLifeMinutes(minutes);
        detailEntity.setRoleLife(timeStr);
        return timeStr;
    }
}
