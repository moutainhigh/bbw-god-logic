package com.bbw.god.detail.disruptor;

import com.bbw.BaseTest;
import com.bbw.god.detail.kafka.KafkaTopicConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaSendTest extends BaseTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void log() {
        String json = "{\"isPlayer\":0,\"worldType\":10,\"recordingTime\":20211103125130,\"serverName\":\"99华强\",\"lv\":26,\"sid\":99,\"aiUid\":-1,\"uid\":190416009900005,\"worldTypeName\":\"普通世界\",\"cardNum\":122,\"id\":85955266339850,\"resultType\":1,\"fightTypeName\":\"封神台\",\"roleLifeMinutes\":1342799,\"resultTypeName\":\"召唤师没血了\",\"fightType\":70,\"isWin\":0,\"round\":12,\"channelName\":\"新服务端测试专用ios\",\"recordDateTime\":1635915090000,\"rechargeNum\":362,\"aiLv\":15,\"cid\":1}";
        for (int i = 0; i < 110; i++) {
            kafkaTemplate.send(KafkaTopicConfiguration.PVE_DETAIL_TOPIC, json);

        }
    }
}