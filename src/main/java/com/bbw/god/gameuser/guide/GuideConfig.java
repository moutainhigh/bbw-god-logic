package com.bbw.god.gameuser.guide;

import com.bbw.common.SpringContextUtil;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 新手引导相关数据配置
 *
 * @author suhq
 * @date 2018年11月14日 下午3:08:39
 */
@Getter
@Configuration
@Component
public class GuideConfig {
	// 大福神掉落的二星卡
	private final List<Integer> dropCardsAsDfs = Arrays.asList(118, 220, 318, 420, 520);

	// 聚贤卡池抽到的三星卡
	private final List<Integer> drawCardsAsJxPool = Arrays.asList(111, 214, 313, 410, 514);

	public static GuideConfig bean() {
		return SpringContextUtil.getBean(GuideConfig.class);
	}

}
