package com.bbw.god.server.flx;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 富临轩 - 开奖结果
 * 
 * @author suhq
 * @date 2018年10月30日 下午2:30:53
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFlxBetResults extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDFlxBetResult> lastBetResults = null;// 富临轩最近开奖结果

	@Getter
	@Setter
	@ToString
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDFlxBetResult implements Serializable {
		private static final long serialVersionUID = 1L;
		private String result = null;
		private String time = null;

		public RDFlxBetResult(String result, String time) {
			this.result = result;
			this.time = time;
		}
	}
}
