package com.bbw.god.gm;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class CPMail {

	@NotEmpty
	private String title;
	@NotEmpty
	private String content;
	@NotEmpty
	private String awards;
}
