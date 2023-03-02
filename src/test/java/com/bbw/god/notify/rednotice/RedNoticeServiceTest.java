package com.bbw.god.notify.rednotice;

import com.bbw.BaseTest;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RedNoticeServiceTest extends BaseTest {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedNoticeService redNoticeService;

    @Test
    public void getAllNotice() {
        GameUser gu = gameUserService.getGameUser(UID);
        System.out.println(this.redNoticeService.getAllNotice(gu));
    }
}