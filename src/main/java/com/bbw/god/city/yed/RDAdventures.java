package com.bbw.god.city.yed;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 奇遇界面
 * @date 2020/6/2 14:03
 **/
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAdventures extends RDCommon implements Serializable {
	private static final long serialVersionUID = -9142026270307489553L;
	private List<RDAdventureInfo> adventureList;

	public RDAdventures(List<RDAdventureInfo> adventureList) {
		this.adventureList = adventureList;
	}

	@Data
	public static class RDAdventureInfo {
		private Long dataId;
		private Integer type;
		private Integer status;

		public static RDAdventureInfo getInstance(Long dataId, Integer type, Integer status) {
			RDAdventureInfo info = new RDAdventureInfo();
			info.setDataId(dataId);
			info.setType(type);
			info.setStatus(status);
			return info;
		}
	}
}
