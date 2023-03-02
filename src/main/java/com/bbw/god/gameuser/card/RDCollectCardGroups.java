package com.bbw.god.gameuser.card;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 收藏的卡组信息
 * @date 2020/5/8 15:17
 **/
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class RDCollectCardGroups extends RDSuccess  implements Serializable {
	private static final long serialVersionUID = -2271137655501229497L;
	private List<RDShareCardGroup> cardGroups;

	public static RDCollectCardGroups getInstance(List<RDShareCardGroup> cardGroups) {
		RDCollectCardGroups rd = new RDCollectCardGroups();
		rd.setCardGroups(cardGroups);
		return rd;
	}
}
