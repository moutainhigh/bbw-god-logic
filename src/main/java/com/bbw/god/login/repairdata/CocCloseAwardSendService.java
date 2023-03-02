package com.bbw.god.login.repairdata;

import com.bbw.common.LM;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.CocHonorEnum;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
import com.bbw.god.gameuser.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.COC_CLOSE_AWARD_SEND;

/**
 * 发放商会关闭补偿奖励
 *
 * @author fzj
 * @date 2022/1/24 14:02
 */
@Service
public class CocCloseAwardSendService implements BaseRepairDataService {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    MailService mailService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (!lastLoginDate.before(COC_CLOSE_AWARD_SEND)) {
           return;
        }
        Long uid = gu.getId();
        UserCocInfo cocInfo = this.gameUserService.getSingleItem(uid, UserCocInfo.class);
        if (null == cocInfo){
            return;
        }
        Integer honorLevel = cocInfo.getHonorLevel();
        CocHonorEnum honorEnum = CocHonorEnum.fromLevel(honorLevel);
        int awardId ;
        int num ;
        switch (honorEnum){
            case FSSF:
            case FKDG:
                awardId = TreasureEnum.RANDOM_SECRET_SCROLL.getValue();
                num = 1;
                break;
            case SZJF:
                awardId = TreasureEnum.RANDOM_ADVANCED_SCROLL.getValue();
                num = 2;
                break;
            case YFCZ:
                awardId = TreasureEnum.RANDOM_ADVANCED_SCROLL.getValue();
                num = 1;
                break;
            case LSFS:
                awardId = TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getValue();
                num = 2;
                break;
            case WPLB:
                awardId = TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getValue();
                num = 1;
                break;
            case XDZG:
                awardId = TreasureEnum.TongTCJ.getValue();
                num = 100;
                break;
            case JDXF:
                awardId = TreasureEnum.TongTCJ.getValue();
                num = 60;
                break;
            default:
                awardId = TreasureEnum.TongTCJ.getValue();
                num = 30;
                break;
        }
        List<Award> awards = new ArrayList<>();
        awards.add(Award.instance(awardId, AwardEnum.FB, num));
        String title = LM.I.getMsgByUid(uid, "coc.close.compensation.title");
        String content = LM.I.getMsgByUid(uid, "coc.close.compensation.content");
        mailService.sendAwardMail(title, content, uid, awards);
    }
}
