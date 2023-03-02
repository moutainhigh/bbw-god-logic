package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import com.bbw.god.rechargeactivities.wartoken.WarTokenLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 战令商店
 *
 */
@Service
public class WarTokenMallProcessor extends AbstractMallProcessor {
    @Autowired
    private MallService mallService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private WarTokenLogic warTokenLogic;
    @Autowired
    private ActivityService activityService;
    /**
     * 需要进阶战令
     */
    private static final List<Integer> needSupWarToken = Arrays.asList(400006, 400007, 400008, 400009, 400010, 400011);

    WarTokenMallProcessor() {
        this.mallType = MallEnum.WAR_TOKEN;
    }

    @Override
    public RDMallList getGoods(long guId) {
        UserWarToken userWarToken = warTokenLogic.getOrCreateUserWarToken(guId);
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getWarTokenMalls();
        RDMallList rd = new RDMallList();
        toRdMallList(guId, fMalls, false, rd);
        if (userWarToken.getSupToken()==0){
            for (RDMallInfo mallGood : rd.getMallGoods()) {
                if (needSupWarToken.contains(mallGood.getMallId())){
                    mallGood.setLock(1);
                }
            }
        }
        return rd;
    }

    @Override
    public void checkAuth(long uid, CfgMallEntity mall) {
        if (needSupWarToken.contains(mall.getId())){
            UserWarToken userWarToken = warTokenLogic.getOrCreateUserWarToken(uid);
            if (userWarToken.getSupToken()==0){
                throw new ExceptionForClientTip("wartoken.need.sup");
            }
        }
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        Award award=Award.instance(mall.getGoodsId(),AwardEnum.fromValue(mall.getItem()),mall.getNum()*buyNum);
        awardService.fetchAward(guId, Arrays.asList(award),WayEnum.WAR_TOKEN_EXCHANGE,WayEnum.WAR_TOKEN_EXCHANGE.getName(),rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        IActivity activity = activityService.getGameActivity(gameUserService.getActiveSid(guId), ActivityEnum.WAR_TOKEN);
        if (null == activity) {
            return new ArrayList<>();
        }
        List<UserMallRecord> favorableRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        List<UserMallRecord> validRecords = favorableRecords.stream()
                .filter(umr -> umr.ifValid() && activity.gainBegin().before(umr.getDateTime()))
                .collect(Collectors.toList());
        return validRecords;
    }
}
