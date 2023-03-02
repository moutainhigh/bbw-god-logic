package com.bbw.god.city.chengc;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 跳转世界返回
 * @date 2020/9/24 11:04
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RDChangeWorld extends RDCommon {
    private static final long serialVersionUID = -6152374194349792342L;
    /** 跳转后的坐标 */
    private Integer pos;
    /** 跳转后的方向 */
    private Integer dir;
    /** 各主城属性（轮回世界才有传） */
    private List<Integer> mainCityDefenderTypes;

    public RDChangeWorld(GameUser user) {
        this.pos = user.getLocation().getPosition();
        this.dir = user.getLocation().getDirection();
    }
}
