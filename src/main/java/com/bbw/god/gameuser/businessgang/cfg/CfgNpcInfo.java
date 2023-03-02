package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * npc信息
 *
 * @author fzj
 * @date 2022/1/17 10:24
 */
@Data
public class CfgNpcInfo implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** id */
    private Integer id;
    /** 名称 */
    private String name;
    /** 类型 */
    private Integer type;
    /** 所在商帮 */
    private Integer gangId;
    /** 喜爱礼物 */
    private List<Integer> hobbyGifts;
    /** 厌恶礼物 */
    private List<Integer> hateGifts;
}
