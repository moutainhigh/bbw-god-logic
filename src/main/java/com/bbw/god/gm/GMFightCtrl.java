package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 玩家数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@RestController
@RequestMapping("/gm")
public class GMFightCtrl extends AbstractController {
    @Autowired
    private ServerUserService serverUserService;

    /**
     * 设置对手词条
     *
     * @param sid
     * @param nickname
     * @param entries  词条1,数量;词条2,数量
     * @return
     */
    @GetMapping("/user!setOpponentEntries")
    public Rst setOpponentEntries(int sid, String nickname, Integer fightType, String entries) {
        String[] entryArray = entries.split(";");
        List<CombatBuff> buffs = new ArrayList<>();
        for (String entry : entryArray) {
            String[] entryInfo = entry.split(",");
            int entryId = Integer.valueOf(entryInfo[0]);
            int level = Integer.valueOf(entryInfo[1]);
            buffs.add(new CombatBuff(entryId, level));
        }
        long uid = getUid(sid, nickname);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, fightType + "entry", buffs, DateUtil.SECOND_ONE_WEEK);
        return Rst.businessOK();
    }

    private long getUid(int sId, String nickname) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            throw ExceptionForClientTip.fromMsg("无效的账号或者区服");
        }
        return guId.get();
    }

}
