package com.bbw.god.gameuser.task;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取卡牌精力
 *
 * @author: suhq
 * @date: 2021/8/10 9:33 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardVigorsList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDCardVigor> cardVigors = new ArrayList<>();

	public void addVigors(int cardId, int cardVigor, int maxCardVigor) {
		RDCardVigor rdCardVigor = new RDCardVigor(cardId, cardVigor, maxCardVigor);
		cardVigors.add(rdCardVigor);
	}

}
