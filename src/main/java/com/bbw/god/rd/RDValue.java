package com.bbw.god.rd;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author suhq
 * @date 2018年10月18日 上午9:27:09
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDValue<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private T value = null;
	private Integer way = null;

}
