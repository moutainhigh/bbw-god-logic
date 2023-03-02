package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgXianJia;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.treasure.xianjiabox.UserXianJiaBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 仙家宝袋  ID范围 11690~11699
 * @author lwb
 */
@Service
public class XianJiaBoxProcessor extends TreasureUseProcessor{
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;
    @Override
    public boolean isMatch(int treasureId) {
        if (treasureId>=11690 && treasureId<=11699){
            return true;
        }
        return false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        int treasureId=param.getProId();
        long uid=gu.getId();
        UserTreasure ut = userTreasureService.getUserTreasure(uid, treasureId);
        if (ut == null) {
            throw new ExceptionForClientTip("treasure.not.exist", treasureId);
        }
        UserXianJiaBox box = getXianRenBox(uid, ut.getId());
        if (box.getLastOpenDate() == DateUtil.getTodayInt() && box.getOpenTimes() >= ut.getOwnNum()) {
            //今日已开启过了，不允许再开
            throw new ExceptionForClientTip("xianrenbox.today.opened");
        }
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
        UserXianJiaBox box = getXianRenBox(uid, ut.getId());
        CfgXianJia xianJia = Cfg.I.get(treasureId, CfgXianJia.class);
        if (treasureId > 11690) {
            //还没开完 追加下一个宝袋
            TreasureEventPublisher.pubTAddEvent(uid,treasureId-1,1, WayEnum.OPEN_XIANJIA_BOX,rd);
            UserTreasure newUt = userTreasureService.getUserTreasure(uid, treasureId-1);
            box.setTreasureDataId(newUt.getId());
            box.setLastOpenDate(DateUtil.getTodayInt());
            box.setOpenTimes(box.getOpenTimes()+1);
            gameUserService.updateItem(box);
        }else {
            //全部开启完 删除记录
            gameUserService.deleteItem(box);
        }
        awardService.fetchAward(uid,xianJia.getAwards(),WayEnum.OPEN_XIANJIA_BOX,WayEnum.OPEN_XIANJIA_BOX.getName(),rd);
    }

    /**
     * 获取宝袋记录
     * @param uid
     * @param treasureDataId
     * @return
     */
    public UserXianJiaBox getXianRenBox(long uid, long treasureDataId) {
        List<UserXianJiaBox> boxes = gameUserService.getMultiItems(uid, UserXianJiaBox.class);
        List<UserXianJiaBox> list = boxes.stream().filter(p -> p.getTreasureDataId() == treasureDataId).collect(Collectors.toList());
        if (list.size() == 1) {
            //有一个 则返回当前的
            return list.get(0);
        } else if (list.size() > 1) {
            //有多个默认优先返回未开启的那个,都开启则返回第一个
            int today = DateUtil.getTodayInt();
            Optional<UserXianJiaBox> first = list.stream().filter(p -> p.getLastOpenDate() != today).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
            return list.get(0);
        }
        //没有对应的记录 需要重新初始化一个补充
        UserXianJiaBox userXianJiaBox = UserXianJiaBox.instance(uid, treasureDataId);
        userXianJiaBox.setOpenTimes(0);
        gameUserService.addItem(uid, userXianJiaBox);
        return userXianJiaBox;
    }
}
