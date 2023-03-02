package com.bbw.god.game.zxz.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户区域通关卡组
 * @author: hzf
 * @create: 2022-10-10 15:37
 **/
@Data
public class UserPassRegionCard extends ZxzCard implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 攻击符箓 */
    private Integer attackSymbol;
    /** 防御符箓 */
    private Integer defenceSymbol;
    /** 至宝 */
    private List<UserZxzCardZhiBao> zhiBaos;
    /** 仙决 */
    private List<UserZxzCardXianJue> xianJues;

}
