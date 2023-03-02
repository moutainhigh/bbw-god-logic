package com.bbw.god.city.yeg;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 野怪宝箱奖励内容
 * @date 2020/5/9 10:40
 **/
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDYeGuaiEliteBox extends RDCommon implements Serializable {
	private static final long serialVersionUID = 6688758293647111798L;
	private List<Award> boxAwards = new ArrayList<>();

	public void addAwards(List<Award> awards) {
		this.boxAwards.addAll(awards);
	}
}
