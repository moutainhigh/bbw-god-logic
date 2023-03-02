package com.bbw.god.mall;

import com.bbw.god.game.config.mall.CfgMallEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 魔王商品信息
 * @date 2019/12/31 16:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
public class RDMaouMallInfo extends RDMallInfo implements Serializable {
	private static final long serialVersionUID = 3748186250891146428L;
	private Integer maouSoul = null;// 魔王魂
	private Integer maouType = null;// 魔王类型
	private Integer maouLevel = null;// 魔王等级
	private Integer authority = null;// 所需权限，null表示不需要权限

	public static RDMaouMallInfo fromMall(CfgMallEntity mall) {
		RDMaouMallInfo rdMallInfo = new RDMaouMallInfo();
		rdMallInfo.setMallId(mall.getId());
		rdMallInfo.setItem(mall.getItem());
		rdMallInfo.setQuantity(mall.getNum());
		rdMallInfo.setRealId(mall.getGoodsId());
		rdMallInfo.setMaouSoul(mall.getPrice());
		return rdMallInfo;
	}

	public void setMaouTypeFromAuth() {
		int type = this.authority / 100 * 10;
		this.maouType = type;
	}

	public void setMaouLevelFromAuth() {
		int level = this.authority % 10;
		if (level == 0) {
			level = 10;
		}
		this.maouLevel = level;
	}
}
