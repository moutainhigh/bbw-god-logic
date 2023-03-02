package com.bbw.god.gameuser.treasure;

import com.bbw.god.rd.RDCommon;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RDUpdateSymbol extends RDCommon {
	private static final long serialVersionUID = 1L;
	private Integer useFLNum;
	private Integer useWanNFLNum;
}
