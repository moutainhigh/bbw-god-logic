package com.bbw.god.detail.async;

import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsMallDetailEntity;
import com.bbw.god.db.service.InsMallDetailService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.pay.UserReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商城购买明细
 *
 * @author: suhq
 * @date: 2021/12/16 11:57 上午
 */
@Slf4j
@Async
@Component
public class MallDetailAsyncHandler {
    private static MallDetailAsyncHandler hander = new MallDetailAsyncHandler();
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private InsMallDetailService insMallDetailService;

    /**
     * 记录明细
     *
     * @param ep
     */
    public void log(MallDetailEventParam ep) {
        try {
            addMallDetail(ep);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void addMallDetail(MallDetailEventParam ep) {
        long guId = ep.getUid();
        InsMallDetailEntity detailEntity = new InsMallDetailEntity();
        //区服信息
        CfgServerEntity server = gameUserService.getOriServer(guId);
        detailEntity.setMallId(ep.getMallId());
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
        detailEntity.setRechargeAmount(rechargeAmount);
        //购买信息
        detailEntity.setItem(ep.getItem());
        detailEntity.setGoodId(ep.getGoodId());
        detailEntity.setGoodName(ep.getGoodName());
        detailEntity.setPrice(ep.getPrice());
        detailEntity.setBuyNum(ep.getBuyNum());
        detailEntity.setPay(ep.getPay());
        detailEntity.setOwnMoney(ep.getOwnMoney());
        detailEntity.setUnit(ep.getUnit().getValue());
        detailEntity.setUnitName(ep.getUnit().getName());
        //自角色创建时间
        setTimeSinceRoleCreate(guId, detailEntity);

        insMallDetailService.insert(detailEntity);
    }

    /**
     * 自角色创建时间
     *
     * @param uid
     * @param detailEntity
     * @return
     */
    private String setTimeSinceRoleCreate(long uid, InsMallDetailEntity detailEntity) {
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
