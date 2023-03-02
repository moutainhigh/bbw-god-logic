package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @description: ins_user_tags实体类
 * @author: suchaobin
 * @createTime: 2019-10-30 15:15
 **/
@Data
@TableName("ins_user_tags")
public class InsUserTagsEntity {
    @TableId(type = IdType.INPUT)
    private String id;
    @TableField("uid")
    private Long uid;
    @TableField("tag")
    private String tag;
    @TableField("opdate")
    private Long opDate;
    @TableField("overtime")
    private Long overTime;
    @TableField(exist = false)
    private boolean isFirstLogin = true;

    public static InsUserTagsEntity getInstance(Long uid, String tag) {
        InsUserTagsEntity entity = new InsUserTagsEntity();
        entity.setId(uid + "#" + tag);
        entity.setUid(uid);
        entity.setTag(tag);
        entity.setOpDate(DateUtil.toDateTimeLong());
        Date overDate = DateUtil.addYears(DateUtil.now(), 100);
        entity.setOverTime(DateUtil.toDateTimeLong(overDate));
        return entity;
    }

    public static InsUserTagsEntity getInstance(Long uid, String tag, Long overTime) {
        InsUserTagsEntity entity = new InsUserTagsEntity();
        entity.setId(uid + "#" + tag);
        entity.setUid(uid);
        entity.setTag(tag);
        entity.setOpDate(DateUtil.toDateTimeLong());
        entity.setOverTime(overTime);
        return entity;
    }
}
