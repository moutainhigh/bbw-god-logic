package com.bbw.god.server.maou;

import com.bbw.BaseTest;
import com.bbw.god.rd.RDSuccess;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerMaouCtrlTest extends BaseTest {
    @Autowired
    private MaouProcessorFactory maouProcessorFactory;

    @Test
    public void getMaou() {
        IServerMaouProcessor maouProcessor = this.maouProcessorFactory.getMaouProcessor(SERVER,
                ServerMaouKind.ALONE_MAOU.getValue());
        RDSuccess rd = maouProcessor.getMaou(UID, SERVER);
        System.out.println(rd.toString());
    }

    @Test
    public void refreshMaou() {
    }

    @Test
    public void getAttackingInfo() {
    }

    @Test
    public void setMaouCards() {
    }

    @Test
    public void getRankers() {
    }

    @Test
    public void getRankerAwards() {
    }

    @Test
    public void resetMaouLevel() {
    }

    @Test
    public void attack() {
    }
}