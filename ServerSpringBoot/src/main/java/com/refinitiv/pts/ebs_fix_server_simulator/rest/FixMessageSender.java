package com.refinitiv.pts.ebs_fix_server_simulator.rest;

import java.util.Scanner;

import com.refinitiv.pts.ebs_fix_server_simulator.fix.AcceptorApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Log4j2
@RestController
@EnableSwagger2
@RequestMapping("/message-sender")
public class FixMessageSender
{
    @Autowired
    private AcceptorApp acceptorApp;

    @PostMapping(value="send-fix-raw")
    public HttpStatus sendRawFIXMessages(@RequestBody @ApiParam
        (
            name = "Raw TCR FIX message, can send multiple lines"
            , example = "8=FIX.4.49=80835=AE34=343=Y49=MyAcceptorService52=20191017-09:29:02.24856=MyClient1122=20191017-08:47:29.58515=USD19=ExecRefID_231=1205.932=100000055=USD/KRW60=20190820-10:07:29.12963=M164=2019092375=20190820120=USD150=F167=FXNDF460=4541=20190919570=N571=FF02-10E9-0000-05-1CA0001003=FF02-10E9-0000-051056=12059000001300=OC1301=FXODM1903=FF02-10E9-0000-051904=01905=Y1KZLBMHGM1906=01907=15899=20190822552=254=237=0D2E-3A39-0001198=MySecondaryOrderID_211=MyClOrdID_2453=4448=7PC4447=D452=1802=1523=LC02803=1000448=PK1447=D452=12448=NEX_EBS447=D452=16448=INS2447=D452=10011=7PC4578=Manual1057=Y54=1198=MySecondaryOrderID_211=MyClOrdID_2453=3448=LC9N447=D452=90802=1523=LC9NLC9N803=16448=549300BZQ6CXNOPQ8E95447=N452=90448=LC9N447=D452=791=LC9N1057=N10=111"
        ) String fixMessages) 
        {        
        HttpStatus result = HttpStatus.OK;
        try{
            Scanner scanner = new Scanner(fixMessages);
            while (scanner.hasNextLine()) {
                String fixMessage = scanner.nextLine();
                log.info("REST: Send FIX message: " + fixMessage);
                acceptorApp.sendFIXMessageTCR(fixMessage);
            }
            scanner.close();
        }
        catch(Exception e)
        {
            log.error(e);
            result = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return result;
    }
    
    @PostMapping(value="send-fix-message-1")
    public HttpStatus sendFIXMessage1(@RequestBody
             //@ApiParam(name = "FIX message in format TAG=VALUE, multiple value by new line") 
             @ApiParam(example = "ABCD")
             String input) {        
        HttpStatus result = HttpStatus.OK;
        try{
            Scanner scanner = new Scanner(input);
            while (scanner.hasNextLine()) {
                String fixMessage = scanner.nextLine();
                log.info("Send FIX message: " + fixMessage);
                acceptorApp.sendFIXMessageTCR(fixMessage);
            }
            scanner.close();
        }
        catch(Exception e)
        {
            log.error(e);
            result = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return result;
    }
}