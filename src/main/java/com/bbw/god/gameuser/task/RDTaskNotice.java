package com.bbw.god.gameuser.task;

import com.bbw.common.ListUtil;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 任务通知接口
 * @date 2019-11-20 10:02
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTaskNotice extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
	protected List<String> redNotices;

	public void addRedNotice(String redNotice){
	    if (ListUtil.isEmpty(redNotices)){
	        redNotices = new ArrayList();
        }
	    redNotices.add(redNotice);
    }

	public void addRedNotice(List<String> notices){
		if (ListUtil.isEmpty(redNotices)){
			redNotices = new ArrayList();
		}
		redNotices.addAll(notices);
	}
}
