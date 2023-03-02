package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.ListUtil;
import org.junit.Test;

public class ServerBossMaouProcessorTest {

    @Test
    public void setMaouCards() {
        String maouCards = "3,3,undefined";
        if (maouCards.contains("undefined,")) {
            maouCards = maouCards.replace("undefined,", "");
        } else if (maouCards.contains(",undefined")) {
            maouCards = maouCards.replace(",undefined", "");
        }
        System.out.println(ListUtil.parseStrToInts(maouCards));
    }
}