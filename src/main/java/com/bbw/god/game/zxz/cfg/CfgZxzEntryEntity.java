package com.bbw.god.game.zxz.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 诛仙阵词条信息
 * @author: hzf
 * @create: 2022-09-19 14:19
 **/
@Data
public class CfgZxzEntryEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 词条Id */
    private Integer entryId;
    /** 词条类型 */
    private Integer type;
    /** 档位 */
    private Integer gear;
    /** 最高等级 */
    private Integer highestLv;


    @Override
    public Serializable getId() {
        return getEntryId();
    }

    @Override
    public int getSortId() {
        return getEntryId();
    }
}
