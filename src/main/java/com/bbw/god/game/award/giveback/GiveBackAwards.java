package com.bbw.god.game.award.giveback;

import com.bbw.god.game.award.Award;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 待返还奖励
 *
 * @author: suhq
 * @date: 2022/5/26 3:20 下午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiveBackAwards implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 待返还的玩家ID **/
    private Long uid;
    /** 返还奖励对应的原始唯一标示符 **/
    private Long srcDataId;
    /** 待返还的奖励 */
    private List<Award> awards = null;
    /** 邮件标题 */
    private String title;
    /** 邮件内容 */
    private String content;

    public static GiveBackAwards instance(long uid, long srcDataId, List<Award> giveBackAwards, String title, String content) {
        GiveBackAwards instance = new GiveBackAwards();
        instance.setUid(uid);
        instance.setSrcDataId(srcDataId);
        instance.setAwards(giveBackAwards);
        instance.setTitle(title);
        instance.setContent(content);
        return instance;
    }

    /**
     * 获取该数据在Redis中的fieldKey
     *
     * @return
     */
    public String gainFieldKey() {
        return getUid() + "@" + getSrcDataId();
    }
}
