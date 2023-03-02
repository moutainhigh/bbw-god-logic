package com.bbw.god.mall.processor;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 赛马商店
 * @author：lwb
 * @date: 2021/3/10 9:41
 * @version: 1.0
 */
@Service
public class HolidayHorseRacingMallProcessor extends AbstractMallProcessor {
    @Autowired
    private AwardService awardService;
    @Autowired
    private HolidayHorseRacingMallProcessor() {
        this.mallType = MallEnum.HORSE_RACING;
    }


    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        List<CfgMallEntity> list = MallTool.getMallConfig().getHorseRacingMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
        toRdMallList(guId, list, false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int num = mall.getNum() * buyNum;
        List<Award> awards=new ArrayList<>(1);
        Award award=Award.instance(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()),num);
        awards.add(award);
        awardService.fetchAward(guId,awards, WayEnum.HORSE_RACING_STORE,"在"+WayEnum.HORSE_RACING_STORE.getName(),rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        return userMallRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
    }
}
