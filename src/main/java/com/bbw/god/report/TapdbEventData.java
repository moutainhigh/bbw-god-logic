package com.bbw.god.report;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TabDb上报数据
 *
 * @author: suhq
 * @date: 2021/8/18 8:53 上午
 */
@Data
@NoArgsConstructor
public abstract class TapdbEventData<T extends ReportEvent> {
	/** 应用ID */
	private String appid = "ru1b5is4yn9ky9iq";
	/** 对应事件的业务类型 */
	private String type = "track";
	/** 上传事件自定义属性 */
	private T properties;

	public TapdbEventData(T properties) {
		this.properties = properties;
	}

	public abstract String toJson();

}
