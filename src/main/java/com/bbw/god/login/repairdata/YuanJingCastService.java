package com.bbw.god.login.repairdata;

import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.login.repairdata.RepairDataConst.YUAN_JING_CAST_TIME;


/**
 * @author suchaobin
 * @description 源晶转化service
 * @date 2020/10/26 10:18
 **/
@Service
public class YuanJingCastService implements BaseRepairDataService {
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        // 源晶转化
        if (lastLoginDate.before(YUAN_JING_CAST_TIME)) {
            long uid = gu.getId();
            int myj = userTreasureService.getTreasureNum(uid, TreasureEnum.MYJ.getValue());
            int syj = userTreasureService.getTreasureNum(uid, TreasureEnum.SYJ.getValue());
            int hyj = userTreasureService.getTreasureNum(uid, TreasureEnum.HYJ.getValue());
            int tyj = userTreasureService.getTreasureNum(uid, TreasureEnum.TYJ.getValue());
            int wwyj = userTreasureService.getTreasureNum(uid, TreasureEnum.WWYJ.getValue());
            // 1:9转化
            int mzy = myj * 9;
            int szy = syj * 9;
            int hzy = hyj * 9;
            int tzy = tyj * 9;
            int xzy = wwyj * 9;
            List<Award> awards = new ArrayList<>();
            awards.add(new Award(TreasureEnum.MZY.getValue(), AwardEnum.FB, mzy));
            awards.add(new Award(TreasureEnum.SZY.getValue(), AwardEnum.FB, szy));
            awards.add(new Award(TreasureEnum.HZY.getValue(), AwardEnum.FB, hzy));
            awards.add(new Award(TreasureEnum.TZY.getValue(), AwardEnum.FB, tzy));
            awards.add(new Award(TreasureEnum.XZY.getValue(), AwardEnum.FB, xzy));
            awards = awards.stream().filter(a -> a.getNum() > 0).collect(Collectors.toList());
            // 扣除旧源晶
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.MYJ.getValue(), myj, WayEnum.LOGIN_REPAIR, new RDCommon());
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SYJ.getValue(), syj, WayEnum.LOGIN_REPAIR, new RDCommon());
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HYJ.getValue(), hyj, WayEnum.LOGIN_REPAIR, new RDCommon());
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.TYJ.getValue(), tyj, WayEnum.LOGIN_REPAIR, new RDCommon());
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.WWYJ.getValue(), wwyj, WayEnum.LOGIN_REPAIR, new RDCommon());
            // 生成邮件
            if (ListUtil.isNotEmpty(awards)) {
                String title = LM.I.getMsgByUid(uid,"mail.yuanJing.cast.title");
                String content = LM.I.getMsgByUid(uid,"mail.yuanJing.cast.content");
                UserMail userMail = UserMail.newAwardMail(title, content, uid, awards);
                gameUserService.addItem(uid, userMail);
            }
        }
    }
}
