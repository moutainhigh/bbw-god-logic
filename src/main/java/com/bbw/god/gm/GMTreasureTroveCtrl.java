package com.bbw.god.gm;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.CfgTreasureTrove;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.TreasureTroveService;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.TroveGrandPrizeBuyTimesCacheService;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.UserTreasureTrove;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 藏宝秘境管理接口
 *
 * @author: huanghb
 * @date: 2022/7/7 16:54
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMTreasureTroveCtrl {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private TreasureTroveService treasureTroveService;
    @Autowired
    private TroveGrandPrizeBuyTimesCacheService troveGrandPrizeBuyTimesCacheService;


    /**
     * 重置宝藏值
     *
     * @param sid
     * @param nickname
     * @param addTroveValue
     * @return
     */
    @RequestMapping("zbmj!resetTroveValue")
    public Rst resetTroveValue(int sid, String nickname, int addTroveValue) {
        Long uid = this.serverUserService.getUidByNickName(sid, nickname).get();
        UserTreasureTrove userTreasureTrove = treasureTroveService.getTroveFromCache(uid);
        if (addTroveValue == 0) {
            addTroveValue = 100 - userTreasureTrove.getTroveValue();
        }
        userTreasureTrove.addTroveValue(addTroveValue);
        treasureTroveService.updateTroveToCache(uid, userTreasureTrove);
        return Rst.businessOK();
    }

    /**
     * 重置购买次数
     *
     * @param sid
     * @param nickname
     * @param buyTimes
     * @param prizePoolId
     * @return
     */
    @RequestMapping("zbmj!resetBuyTimse")
    public Rst resetBuyTimse(int sid, String nickname, int buyTimes, int prizePoolId) {
        Long uid = this.serverUserService.getUidByNickName(sid, nickname).get();
        troveGrandPrizeBuyTimesCacheService.addBuyTimes(uid, prizePoolId, buyTimes);
        return Rst.businessOK();
    }

    /**
     * 刷新宝藏
     *
     * @param sid
     * @param nickname
     * @param refreshTimes
     * @param isBigAward
     * @return
     */
    @RequestMapping("zbmj!refreshTreasureTrove")
    public Rst refreshTreasureTrove(int sid, String nickname, int refreshTimes, boolean isBigAward) {
        Long uid = this.serverUserService.getUidByNickName(sid, nickname).get();
        Rst rs = Rst.businessOK();
        Map totalMap = new LinkedHashMap();
        Map detailmap = new LinkedHashMap();
        totalMap.put("total", 0);
        for (int i = 0; i < refreshTimes; i++) {
            List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(uid, isBigAward);
            int curtimes = i + 1;
//            String jsonStr = JSON.toJSONString(troveAwards, SerializerFeature.DisableCircularReferenceDetect);
//            JSONArray res = JSON.parseArray(jsonStr);
//            List<CfgTreasureTrove.TroveAward> troveAwards1 = res.toJavaList(CfgTreasureTrove.TroveAward.class);
            List<CfgTreasureTrove.TroveAward> bigAwards = troveAwards.stream().filter(tmp -> tmp.getId() % 1000 <= 12).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(bigAwards)) {
                int total = (int) totalMap.get("total");
                total += bigAwards.size();
                totalMap.put("total", total);
                for (CfgTreasureTrove.TroveAward troveAward : bigAwards) {
                    Integer perBigAwardNum = (Integer) totalMap.get(String.valueOf(troveAward.getId()));
                    if (null == perBigAwardNum) {
                        perBigAwardNum = 0;
                    }
                    perBigAwardNum += 1;
                    totalMap.put(String.valueOf(troveAward.getId()), perBigAwardNum);
                }
            }
//            detailmap.put("第" + curtimes + "次刷新", troveAwards1);
        }
        System.out.println(totalMap.toString());
        rs.put("总计", totalMap);
//        rs.put("明细", detailmap);
        rs.put("购买次数", troveGrandPrizeBuyTimesCacheService.getAllBuyTimes(uid));
        return rs;

    }


}
