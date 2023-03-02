package com.bbw.god.activity.processor.cardboost;

import com.bbw.god.login.RDGameUser;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 助力返回数据
 *
 * @author: suhq
 * @date: 2021/8/3 3:19 下午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDSyncBoostCards extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RDGameUser.RDCard> cards = null;// 玩家拥有的卡牌

}
