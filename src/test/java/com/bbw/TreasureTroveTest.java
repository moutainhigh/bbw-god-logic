package com.bbw;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayWZJZProcessor;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.CfgTreasureTrove;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.TreasureTroveService;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.TroveGrandPrizeBuyTimesCacheService;
import com.bbw.god.city.chengc.ChengChiLogic;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author：lwb
 * @date: 2020/11/30 16:21
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LogicServerApplication.class}) // 指定启动类
public class TreasureTroveTest {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private TreasureTroveService treasureTroveService;
    @Autowired
    private TroveGrandPrizeBuyTimesCacheService troveGrandPrizeBuyTimesCacheService;
    @Autowired
    private ChengChiLogic chengChiLogic;
    @Autowired
    private HolidayWZJZProcessor holidayWZJZProcessor;
    @Autowired
    private UserSpecialService userSpecialService;

    @Test

    public void test() {
        Long uid = this.serverUserService.getUidByNickName(91, "贡明").get();
        Rst rs = Rst.businessOK();
        Map totalMap = new LinkedHashMap();
        Map detailmap = new LinkedHashMap();
        totalMap.put("total", 0);
        for (int i = 0; i < 2000000; i++) {
            System.out.println(i);
            List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(uid, false);
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
        for (Object key : totalMap.keySet()) {
            System.out.println(key + ":" + totalMap.get(key));
        }
        System.out.println(totalMap.toString());
//        rs.put("总计", totalMap);
////        rs.put("明细", detailmap);
//        rs.put("购买次数", troveGrandPrizeBuyTimesCacheService.getAllBuyTimes(uid));

    }

    @Test
    public void test1() {
        for (int i = 0; i < 199; i++) {
            List<UserSpecial> userSpecials = userSpecialService.getOwnSpecials(Long.parseLong("211110009100001")).stream().filter(tmp -> tmp.getBaseId() == 47).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(userSpecials)) {
                System.out.println(userSpecials.size());
                if (userSpecials.size() >= 199) {
                    continue;
                }
            }
            List<EVSpecialAdd> specialAdds = Arrays.asList(47, 47, 47, 47, 47, 47, 47, 47, 47).stream()
                    .map(specialId -> new EVSpecialAdd(specialId % 100, 100))
                    .collect(Collectors.toList());
            SpecialEventPublisher.pubSpecialAddEvent(Long.parseLong("211110009100001"), specialAdds, WayEnum.FIND_TREASURE_MAP, new RDCommon());
        }
    }

    @Test
    public void test2() {
        RDAdvance rd = new RDAdvance();
        for (int i = 0; i < 10000; i++) {
            System.out.println(i);
            holidayWZJZProcessor.cunZTriggerEvent(Long.parseLong("220516009100001"), rd);
        }

    }
}
