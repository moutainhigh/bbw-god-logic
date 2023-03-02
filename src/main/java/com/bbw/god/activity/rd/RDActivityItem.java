package com.bbw.god.activity.rd;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bbw.god.game.award.Award;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.rd.item.RDAchievableItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 活动列表
 * @date 2020/11/23 11:03
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDActivityItem extends RDAchievableItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nickname = null;
    /**系列 */
    private Integer series;
    private Integer remainTime;
    /**下标 */
    private Integer index;
    /**类型 */
    private Integer type;
    /** 关闭引用检测,重复引用对象时不会被$ref代替 */
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private List<Award> ableChooseAwards = null;
    /** 0未选,1已选 */
    private Integer extraAwardStatus = null;
    /** 活动期间可兑换的商品集合 */
    private RDMallList rdMallList = null;
    private String title;
    /** 给客户端的节日主题标识 */
    private String sign;

}
