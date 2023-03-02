package com.bbw.god.server.guild;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author lwb  
* @date 2019年5月17日  
* @version 1.0  
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class GuildShop implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id=null;
	private String permit = null;
	private Integer goodId=null;
	private Integer propType=null;
	private Integer price=null;
	private Integer priceType=null;
	private Integer quantity=null;
	private Integer star=null;
	private Integer limitCount=null;
	private Integer bought=0;
	private Integer minLevel=null;
	private String memo=null;
	
	public GuildShop(int id,int bought) {
		this.id=id;
		this.bought=bought;
	}
	
	
}
