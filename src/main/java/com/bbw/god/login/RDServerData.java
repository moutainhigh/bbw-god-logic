package com.bbw.god.login;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 玩家登录的区服信息
 * @author: suchaobin
 * @createTime: 2019-11-14 15:47
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper=false)
public class RDServerData extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<ServerData> serverDataList = null;

    @Data
    static class ServerData  implements Serializable{
		private static final long serialVersionUID = 1L;
		private String sname = null;
        private Integer sid = null;
        private Integer level = null;
    }
}
