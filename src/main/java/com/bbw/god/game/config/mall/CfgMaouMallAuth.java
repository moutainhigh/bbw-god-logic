package com.bbw.god.game.config.mall;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 魔王商品权限
 * @date 2019/12/25 15:14
 */
@Data
public class CfgMaouMallAuth implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer mallId;
	private Integer authority;

	@Override
	public Serializable getId() {
		return this.mallId;
	}

	@Override
	public int getSortId() {
		return this.getMallId();
	}
}
