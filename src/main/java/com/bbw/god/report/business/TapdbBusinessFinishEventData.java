package com.bbw.god.report.business;

import com.alibaba.fastjson.JSONObject;
import com.bbw.common.StrUtil;
import com.bbw.god.report.TapdbEventData;
import lombok.Data;

/**
 * TabDb上报数据
 *
 * @author: suhq
 * @date: 2021/8/18 8:53 上午
 */
@Data
public class TapdbBusinessFinishEventData extends TapdbEventData<BusinessFinishReportEvent> {

	public TapdbBusinessFinishEventData(BusinessFinishReportEvent properties) {
		super(properties);
	}

	@Override
	public String toJson() {
		JSONObject json = new JSONObject();
		json.put("index", getAppid());
		json.put("type", getType());
		json.put("user_id", getProperties().getReporter().getUid());
		json.put("name", getProperties().getEvent().getName());
		JSONObject propJson = new JSONObject();
		propJson.put("#account", getProperties().getReporter().getAccount());
		propJson.put("#sid", getProperties().getReporter().getSid());
		propJson.put("server", getProperties().getReporter().getServer());
		propJson.put("channel", getProperties().getReporter().getChannel());
		propJson.put("#businessType", getProperties().getBusinessType().getName());
		propJson.put("#businessChildType", getProperties().getBusinessChildType());

		if (StrUtil.isNotBlank(getProperties().getHandleName())) {
			propJson.put("#handledName", getProperties().getHandleName());
		}
		propJson.put("#num", getProperties().getHandleNum());
		if (StrUtil.isNotBlank(getProperties().getConsume())) {
			propJson.put("#consume", getProperties().getConsume());
			propJson.put("#consumeNumUsing", getProperties().getConsumeNum());
		}
		if (StrUtil.isNotBlank(getProperties().getAward())) {
			propJson.put("#award", getProperties().getAward());
			propJson.put("#awardNum", getProperties().getAwardNum());
		}
		json.put("properties", propJson);
		return json.toJSONString();
	}

}
