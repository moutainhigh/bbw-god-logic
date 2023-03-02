package com.bbw.god.random.box;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbw.BaseTest;
import com.bbw.common.CloneUtil;
import com.bbw.god.game.config.Cfg;

public class BoxServiceTest extends BaseTest {
	@Autowired
	private BoxService boxService;

	@Test
	public void test() {
		String boxId = "510";
		BoxGoods srcBox = Cfg.I.get(boxId, BoxGoods.class);
		if (srcBox == null) {
			System.out.println("箱子配置" + boxId + "不存在");
			return;
		}
		// 对元数据进行克隆，避免修改元数据
		BoxGoods box = CloneUtil.clone(srcBox);
		System.out.println(box.toString());
	}

	@Test
	public void getBoxGoods() {
		List<BoxGood> goods = boxService.getBoxGoods(1040);
		goods.forEach(tmp -> System.out.println(tmp));
	}

}
