package com.bbw.god.server.maou.alonemaou;

import com.bbw.god.gameuser.GameUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author suhq
 * @description: 攻击前处理结构
 * @date 2020-01-03 10:52
 **/
@Data
@AllArgsConstructor
public class BeforeAloneAttack {
    private GameUser gu;
    private int needGold;
}
