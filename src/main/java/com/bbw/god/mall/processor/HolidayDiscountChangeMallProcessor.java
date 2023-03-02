package com.bbw.god.mall.processor;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日折扣变化商店
 *
 * @author fzj
 * @date 2021/11/18 14:09
 */
@Service
public class HolidayDiscountChangeMallProcessor extends AbstractMallProcessor {
    @Autowired
    MallService mallService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    ActivityService activityService;
    /** 特惠商品 */
    private static final List<Integer> SPECIAL_MALLS = MallTool.getGoods().stream().filter(g -> g.getType() == 610)
            .map(CfgMallEntity::getId).collect(Collectors.toList());
    @Autowired
    private HolidayDiscountChangeMallProcessor() {
        this.mallType = MallEnum.DISCOUNT_CHANGER;
    }

    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        //获取活动开始时间
        int sid = gameUserService.getActiveSid(guId);
        IActivity a = activityService.getActivity(sid, ActivityEnum.BF_DISCOUNT);
        Date begin = a.gainBegin();
        for (Integer mallId : SPECIAL_MALLS){
            UserMallRecord userMallRecord = mallService.getUserMallRecord(guId, mallId);
            if (null == userMallRecord){
                continue;
            }
            Date recordDate = userMallRecord.getDateTime();
            if (recordDate.after(begin)){
                continue;
            }
            gameUserService.deleteItem(userMallRecord);
        }
        List<CfgMallEntity> list = MallTool.getMallConfig().getDiscountChangeMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
        toRdMallList(guId, list, false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int num = mall.getNum() * buyNum;
        TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.DISCOUNT_CHANGE_MALL, rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        return userMallRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
    }
}
