package com.bbw.god.random.config;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 选择结果限制
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:46
 */
@Data
public class ResultLimt implements Serializable {
	private static final long serialVersionUID = 297019076577195972L;
	private int maxSize = 1;//最多返回多少条记录
	private int minSize = 1;//最少返回多少条记录
	private List<ResultLimtRule> rules;
}
