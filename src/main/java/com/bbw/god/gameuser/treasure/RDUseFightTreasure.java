package com.bbw.god.gameuser.treasure;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDUseFightTreasure implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer isUse = null;

	@SuppressWarnings("unused")
	private RDUseFightTreasure() {

	}

}
