package com.bbw.god.city.yed;

import java.io.Serializable;

import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

@Data
public class CfgYeDiEventEntity implements CfgEntityInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id; //
	private String name; // 事件名称
	private Integer type = 10; // 正面10、0中性、-10负面
	private Integer probability; // 野地事件概率
	private String memo; // 备注

	@Override
	public int getSortId() {
		return id;
	}
}
