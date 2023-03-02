package com.bbw.god.game.sxdh.config;

import java.io.Serializable;

import lombok.Data;

/**
 * 丹药
 * 
 * @author suhq
 * @date 2019-06-18 10:29:04
 */
@Data
public class CfgSxdhMedicineEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private Integer unit;
	private Integer price;

}
