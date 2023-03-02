package com.bbw.god.city.cunz;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 坊间怪谈配置文件
 *
 * @author fzj
 * @date 2021/12/3 18:01
 */
@Data
public class CfgCunZTalk implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 怪谈id */
    private Integer talkId;
    /** 秘闻成就id */
    private Integer secretAchievementId;

    @Override
    public Serializable getId() {
        return talkId;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
