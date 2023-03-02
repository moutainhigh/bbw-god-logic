package com.bbw.xianrenbox;

import com.bbw.BaseTest;
import com.bbw.common.JSONUtil;
import com.bbw.god.gameuser.treasure.xianrenbox.XianRenBoxLogic;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lwb
 * @date 2020/8/12 17:00
 */
public class XianRenBoxTest extends BaseTest {
    @Autowired
    private XianRenBoxLogic xianRenBoxLogic;

    @Test
    public void test(){
        for (int i=0;i<100;i++){
            List<Integer> ids=xianRenBoxLogic.initAwards();
            System.err.println(JSONUtil.toJson(ids));
            System.err.println("-------388位置"+ids.indexOf(3)+"-----588位置"+ids.indexOf(4));
        }
    }
}
