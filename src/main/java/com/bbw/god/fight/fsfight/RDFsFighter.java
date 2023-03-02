package com.bbw.god.fight.fsfight;

import com.bbw.god.gameuser.card.UserCardGroup;
import com.bbw.god.login.RDGameUser.RDCard;
import com.bbw.god.login.RDGameUser.RDTreasure;
import com.bbw.god.rd.RDSuccess;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家登陆强联网返回的数据
 *
 * @author suhq
 * @date 2019年3月14日 上午11:08:18
 */
@Getter
@Setter
@ToString
public class RDFsFighter extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long rid = null;
    private Integer plat = 10;
    private Integer groupId;
    private String serverName = null;
    private String name = null;
    private String headName = null;
    private Integer head = null;
    private Integer level = null;
    private Long copper = null;
    private Integer gold = null;
    private Integer title = 1;
    private List<RDTreasure> prop = null;// 玩家拥有的法宝
    private List<RDCard> card = null;// 玩家拥有的卡牌
    private List<UserCardGroup> cardGroups;//卡组
}
