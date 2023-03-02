package com.bbw.god.security.token;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDRefreshToken extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 后续请求客户端请求头需传递的token */
    private String token = null;
    /** token有效期 */
    private Long tokenExpiredTime = null;
}
