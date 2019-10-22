package com.refinitiv.pts.ebs_fix_server_simulator.rest.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

@ApiModel
public class ServerConfig
{
    @ApiParam(value = "enable/disable server to genearate TCR message from file")
    @Getter
    @Setter
    private boolean enableGenerateMessageFromFile;

    @ApiModelProperty(notes = "set server publishing message rate as <numberOfMessage / second>", example = "1.0")
    //@ApiParam(value = "set server publishing message rate as <numberOfMessage / second>")
    @Getter
    @Setter
    private double messageRatePerSecond;
}