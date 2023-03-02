package com.bbw.god.fight.fsfight;

import com.bbw.god.login.RDGameUser.RDCard;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 神仙大会匹配机器人的数据
 *
 * @author suhq
 * @date 2019年3月14日 上午11:09:05
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFsRoboter extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long roboterId = null;
    private Integer roboterType = null;
    private String name = null;
    private Integer head = null;
    private Integer level = null;
    private Integer sex = null;
    private String serverName = null;
    private Integer groupId = null;
    private List<RDCard> card = null;// 玩家拥有的卡牌

}
