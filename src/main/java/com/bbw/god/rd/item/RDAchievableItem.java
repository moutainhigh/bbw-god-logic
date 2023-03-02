package com.bbw.god.rd.item;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bbw.god.game.award.Award;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 奖励列表返回通用父类
 * @date 2020/10/12 15:28
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAchievableItem extends RDItem implements Serializable {
    private static final long serialVersionUID = 1L;
    //列表项描述格式化值
    private String[] titleFormats = null;
    // 状态
    private Integer status = null;
    // 进度
    private Integer progress = null;
    // 达成所需要的值
    private Integer totalProgress = null;
    // 类型
    private Integer type = null;
    // 动态奖励
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private List<Award> awards = null;
    // 特殊描述
    private String memo;

    public void addAward(Award award) {
        if (this.awards == null) {
            this.awards = new ArrayList<>();
        }
        this.awards.add(award);
    }
}
