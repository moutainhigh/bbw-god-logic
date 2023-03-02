package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HexagramFactory {
    @Autowired
    private List<AbstractHexagram> hexagrams;
    @Autowired
    private Hexagram39Processor hexagram39Processor;
    @Autowired
    private Hexagram1Processor hexagram1Processor;

    /**
     * 生成随机卦象
     *
     * @param gu
     * @param rd
     */
    public void buildRandomHexagram(GameUser gu, RDHexagram rd) {
        int maxTimes = 30;
        AbstractHexagram hexagramService = PowerRandom.getRandomFromList(hexagrams);
        while (!hexagramService.canEffect(gu.getId()) && maxTimes > 0) {
            hexagramService = PowerRandom.getRandomFromList(hexagrams);
            maxTimes--;
        }
        if (maxTimes == 0 && !hexagramService.canEffect(gu.getId())) {
            hexagramService = hexagram39Processor;
        }
        hexagramService.effect(gu.getId(), rd);
        rd.setHexagramId(hexagramService.getHexagramId());
    }

    /**
     * 生成卦象
     * @param uid
     * @param rd
     */
    public void buildHexagram(long uid, int hexagramId,RDHexagram rd) {
        AbstractHexagram hexagramService = hexagram39Processor;
        for (AbstractHexagram hmService : hexagrams) {
            if (hmService.getHexagramId() == hexagramId) {
                hexagramService = hmService;
                break;
            }
        }
        hexagramService.effect(uid, rd);
        rd.setHexagramId(hexagramService.getHexagramId());
    }

    /**
     * 体验卦象
     *
     * @param gu
     * @param rd
     */
    public void buildHexagramAsExp(GameUser gu, RDHexagram rd) {
        hexagram1Processor.effect(gu.getId(), rd);
        rd.setHexagramId(hexagram1Processor.getHexagramId());
    }
}
