package com.bbw.god.gameuser.shake;

import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 摇骰子服务
 * @date 2019-12-27 06:06
 **/
@Service("shake-service")
public class ShakeService {
    /**
     * 获得摇骰子的结果
     *
     * @param diceNum 骰子数
     * @return
     */
    public List<Integer> getDiceResult(GameUser gu, Integer diceNum) {
        List<Integer> randoms = new ArrayList<Integer>();
        for (int i = 0; i < diceNum; i++) {
            int rand = PowerRandom.getRandomBySeed(6);
            randoms.add(rand);
        }
        return randoms;
    }
}
