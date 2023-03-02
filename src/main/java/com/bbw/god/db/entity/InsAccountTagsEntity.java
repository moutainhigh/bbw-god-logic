package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @description: ins_account_tags实体类
 * @author: suchaobin
 * @createTime: 2019-11-04 10:14
 **/
@Data
@TableName("ins_account_tags")
public class InsAccountTagsEntity {
    @TableId(type = IdType.INPUT)
    private String id;
    @TableField("account")
    private String account;
    @TableField("tag")
    private String tag;
    @TableField("opdate")
    private Long opDate;
    @TableField("overtime")
    private Long overTime;

    public static InsAccountTagsEntity getInstance(String account, String tag, Long overTime) {
        InsAccountTagsEntity entity = new InsAccountTagsEntity();
        entity.setId(account + "#" + tag);
        entity.setAccount(account);
        entity.setTag(tag);
        entity.setOpDate(DateUtil.toDateTimeLong());
        entity.setOverTime(overTime);
        return entity;
    }
}
