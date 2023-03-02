package com.bbw.god.game.dfdj.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 丹药
 * 
 * @author suhq
 * @date 2019-06-18 10:29:04
 */
@Data
public class CfgDfdjMedicineEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private Integer unit;
	private Integer price;

}
