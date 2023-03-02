package com.bbw.god.game.config.mall;

import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.bbw.god.game.config.Cfg;

public class MallToolTest {

	@Test
	public void test() {
		List<CfgMallEntity> entities = Cfg.I.get(CfgMallEntity.class);
		entities.sort(Comparator.comparing(CfgMallEntity::getType).thenComparing(CfgMallEntity::getId));
		for (CfgMallEntity tmp : entities) {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("- {");
			sBuilder.append("id: " + tmp.getId());
			sBuilder.append(",type: " + tmp.getType());
			sBuilder.append(",name: " + tmp.getName());
			sBuilder.append(",item: " + tmp.getItem());
			sBuilder.append(",goodsId: " + tmp.getGoodsId());
			sBuilder.append(",num: " + tmp.getNum());
			sBuilder.append(",serial: " + tmp.getSerial());
			sBuilder.append(",unit: " + tmp.getUnit());
			sBuilder.append(",price: " + tmp.getPrice());
			sBuilder.append(",originalPrice: " + tmp.getOriginalPrice());
			sBuilder.append(",discount: " + tmp.getDiscount());
			sBuilder.append(",limit: " + tmp.getLimit());
			sBuilder.append(",peroid: " + tmp.getPeroid());
			sBuilder.append(",status: " + tmp.getStatus());

			sBuilder.append("}");

			String strToAppend = sBuilder.toString();
			System.out.println(strToAppend);
		}
	}

}
