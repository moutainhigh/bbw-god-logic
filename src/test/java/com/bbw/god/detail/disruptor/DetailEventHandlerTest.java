package com.bbw.god.detail.disruptor;

import com.bbw.BaseTest;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.PowerRandom;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.InsUserDetailEntity;
import com.bbw.god.db.pool.DetailDataDAO;
import com.bbw.god.detail.AwardDetail;
import com.bbw.god.detail.DetailData;
import com.bbw.god.game.config.WayEnum;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailEventHandlerTest extends BaseTest {
    List<Long> uids = Arrays.asList(190415009900001L, 190415009900002L, 190415009900004L, 190415009900005L, 190415009900006L, 190415009900010L);

    @Test
    public void log() {
        DetailDataDAO pdd = SpringContextUtil.getBean(DetailDataDAO.class, 99);
        doSerial(10, pdd);
        doBatch(10, pdd);
//        doSerial(100, pdd);
//        doBatch(100, pdd);
//        doSerial(1000, pdd);
//        doBatch(1000, pdd);
//        doBatch(10000, pdd);
    }

    private void doSerial(int num, DetailDataDAO pdd) {
        Long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            long uid = PowerRandom.getRandomFromList(uids);
            InsUserDetailEntity insEntity = InsUserDetailEntity.fromDetailData(getInstance(uid));
            pdd.dbInsertInsUserDetailEntity(insEntity);
        }
        System.out.println("插入条数：" + num + ",执行时间：" + (System.currentTimeMillis() - start));
    }

    private void doBatch(int num, DetailDataDAO pdd) {
        Long start = System.currentTimeMillis();
        List<InsUserDetailEntity> insentities = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            long uid = PowerRandom.getRandomFromList(uids);
            InsUserDetailEntity insEntity = InsUserDetailEntity.fromDetailData(getInstance(uid));
            insentities.add(insEntity);
        }
        pdd.dbBatchInsertInsUserDetailEntity(insentities);
        System.out.println("批量插入条数：" + num + ",执行时间：" + (System.currentTimeMillis() - start));
    }

    private DetailData getInstance(long uid) {
        DetailData data = new DetailData();
        data.setId(ID.INSTANCE.nextId());
        data.setSid(99);
        data.setUid(uid);
        data.setUserLevel(10);
        data.setOpdate(DateUtil.getTodayInt());
        data.setOptime(DateUtil.toHMSInt(DateUtil.now()));
        data.setAwardDetail(AwardDetail.fromCopper(10000));
        data.setWay(WayEnum.Mail);
        data.setAfterValue(0L);
        return data;
    }
}