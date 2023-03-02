package com.bbw.god.server.flx;

import org.hibernate.validator.constraints.Range;

import com.bbw.common.StrUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 猜数字数馆投注参数
 * 
 * @author suhq
 * @date 2018年11月20日 下午5:29:14
 */
@Getter
@Setter
@ToString
public class CPCaiShuZiBet {
	private String betNum; // 投注数值[1,36]
	@Range(min = 1, max = 2, message = "无效的投注类型!")
	private int betKind;// 投注类型 1-元宝；2-铜钱
	@Range(min = 1, max = 50000, message = "无效的投注数量!")
	private int betCount;// 投注额度 元宝[1,2000] 铜钱[1,50000]

	public int getBetNum() {
		if (StrUtil.isNull(betNum)) {
			return -1;
		}
		return Integer.parseInt(betNum);
	}
}
