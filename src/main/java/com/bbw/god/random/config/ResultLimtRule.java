package com.bbw.god.random.config;

import java.io.Serializable;

import lombok.Data;

/**
 * 选择结果过滤规则
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:46
 */
@Data
public class ResultLimtRule implements Serializable {
	private static final long serialVersionUID = 297019076571195962L;
	private int star;//星级
	private int maxSize;//最多返回多少条记录
}
