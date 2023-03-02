package com.bbw.god.game.combat.attackstrategy;

import com.bbw.god.gameuser.GameUser;
import lombok.Data;

/**
 * @author：lwb
 * @date: 2020/11/27 16:54
 * @version: 1.0
 */
@Data
public class PlayerInfoVO {
    private Integer lv;
    private Integer head;
    private Integer icon;
    private String nickname;
    private Long uid;

    public static PlayerInfoVO instance(String nickname, int lv, int head, int icon, long uid) {
        PlayerInfoVO p = new PlayerInfoVO();
        p.setNickname(nickname);
        p.setLv(lv);
        p.setHead(head);
        p.setIcon(icon);
        p.setUid(uid);
        return p;
    }

    public static PlayerInfoVO instance(GameUser gu) {
        PlayerInfoVO p = new PlayerInfoVO();
        p.setNickname(gu.getRoleInfo().getNickname());
        p.setLv(gu.getLevel());
        p.setHead(gu.getRoleInfo().getHead());
        p.setIcon(gu.getRoleInfo().getHeadIcon());
        p.setUid(gu.getId());
        return p;
    }
}
