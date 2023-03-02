package com.bbw.god.gameuser;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFightInfo extends RDSuccess {
    private Integer maouStatus;
    private Long maouRemainTime;

}
