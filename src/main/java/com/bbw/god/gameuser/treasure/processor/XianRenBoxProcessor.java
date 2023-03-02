package com.bbw.god.gameuser.treasure.processor;

import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.treasure.xianrenbox.UserXianRenBox;
import com.bbw.god.gameuser.treasure.xianrenbox.XianRenBoxLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仙人遗落的袋子  ID范围11301  ~11330
 * @author lwb
 * @date 2020/8/12 15:45
 */
@Service
public class XianRenBoxProcessor extends TreasureUseProcessor{
    @Autowired
    private XianRenBoxLogic xianRenBoxLogic;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private App app;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;

    @Override
    public boolean isMatch(int treasureId) {
        if (treasureId >= 11301 && treasureId <= 11330) {
            return true;
        }
        return false;
    }

    /**
     * 是否宝箱类
     *
     * @return
     */
    @Override
    public boolean isChestType() {
        return true;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        int treasureId = param.getProId();
        long uid = gu.getId();
        UserTreasure ut = userTreasureService.getUserTreasure(uid, treasureId);
        UserXianRenBox box = xianRenBoxLogic.getXianRenBox(uid, ut.getId());
        if (box.getLastOpenDate() == DateUtil.getTodayInt()) {
            //今日已开启过了，不允许再开
            throw new ExceptionForClientTip("xianrenbox.today.opened");
        }
        int index=30-(treasureId%100);//法宝ID最后一位代表 第几次开启
        Integer boxId=box.getAwardsList().get(index);
        List<Award> awards=xianRenBoxLogic.getAward(boxId);
        if (treasureId>11301){
            TreasureEventPublisher.pubTAddEvent(uid,treasureId-1,1, WayEnum.OPEN_XIANREN_BOX,rd);
            UserTreasure newUt = userTreasureService.getUserTreasure(uid, treasureId-1);
            box.setTreasureDataId(newUt.getId());
            box.setLastOpenDate(DateUtil.getTodayInt());
            gameUserService.updateItem(box);
        }else {
            LogUtil.logDeletedUserData("仙人遗失的袋子已开启30次",box);
            gameUserService.deleteItem(box);
        }
        awardService.fetchAward(uid,awards,WayEnum.OPEN_XIANREN_BOX,"开启仙人遗落的袋子",rd);
    }
}
