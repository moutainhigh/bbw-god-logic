package com.bbw.god.gameuser.mail;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 16:38
 */
@Data
public class MailReadResult extends RDSuccess {
	private String content;
	private int isAccept = 1;
	/** 关闭引用检测,重复引用对象时不会被$ref代替 */
	@JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
	private List<Award> awards = new ArrayList<Award>();// 奖励列表
}
