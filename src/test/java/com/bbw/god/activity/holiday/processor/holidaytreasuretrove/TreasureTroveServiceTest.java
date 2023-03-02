package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class TreasureTroveServiceTest extends BaseTest {
    @Autowired
    private TreasureTroveService treasureTroveService;

    @Test
    public void buildTroveAwards() {
        System.out.println("================1");
        for (int i = 0; i < 10000; i++) {
            List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(UID, false);
            if (troveAwards.stream().anyMatch(tmp -> tmp.getId() == 620001)) {
                System.out.println(troveAwards.stream().map(CfgTreasureTrove.TroveAward::getId).collect(Collectors.toList()));
            }
        }
        System.out.println("================2");
        for (int i = 0; i < 10000; i++) {
            List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(UID, false);
            if (troveAwards.stream().anyMatch(tmp -> tmp.getId() == 620001)) {
                System.out.println(troveAwards.stream().map(CfgTreasureTrove.TroveAward::getId).collect(Collectors.toList()));
            }
        }
        System.out.println("================3");
        for (int i = 0; i < 10000; i++) {
            List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(UID, false);
            if (troveAwards.stream().anyMatch(tmp -> tmp.getId() == 620001)) {
                System.out.println(troveAwards.stream().map(CfgTreasureTrove.TroveAward::getId).collect(Collectors.toList()));
            }
        }
        System.out.println("================4");
        for (int i = 0; i < 10000; i++) {
            List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(UID, false);
            if (troveAwards.stream().anyMatch(tmp -> tmp.getId() == 620001)) {
                System.out.println(troveAwards.stream().map(CfgTreasureTrove.TroveAward::getId).collect(Collectors.toList()));
            }
        }
    }
}