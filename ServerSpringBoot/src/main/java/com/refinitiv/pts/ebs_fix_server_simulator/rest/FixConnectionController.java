package com.refinitiv.pts.ebs_fix_server_simulator.rest;

import com.refinitiv.pts.ebs_fix_server_simulator.fix.AcceptorApp;
import com.refinitiv.pts.ebs_fix_server_simulator.fix.FixGeneratorTcr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Log4j2
@RestController
@EnableSwagger2
public class FixConnectionController {
    @Autowired
    private AcceptorApp acceptorApp;

    @PostMapping("/fix-acceptor/toggle-start-stop")
    public HttpStatus toggleStartStop()
    {
        HttpStatus result = HttpStatus.OK;
        try{
            acceptorApp.ToggleStartStopServer();
        }
        catch(Exception e)
        {
            log.error(e);
            result = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    @PostMapping("/fix-acceptor/msgrate")
    public HttpStatus setOutputMessageRate(double nMsgPerSecond)
    {
        HttpStatus ret = HttpStatus.OK;
        try{
            acceptorApp.setMessageRate(nMsgPerSecond);
        }
        catch(Exception e)
        {
            log.error(e);
            ret = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ret;
    }

    @PostMapping("/fix-acceptor/start-tcr-from-file")
    public HttpStatus startSendTCRFromFile()
    {
        HttpStatus ret = HttpStatus.OK;
        try{
            acceptorApp.StartGenerateTCRFromFile();
        }
        catch(Exception e)
        {
            log.error(e);
            ret = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ret;
    }

    @PostMapping("/fix-acceptor/stop-tcr-from-file")
    public HttpStatus stopSendTCRFromFile()
    {
        HttpStatus ret = HttpStatus.OK;
        try{
            acceptorApp.StopGenerateTCRFromFile();
        }
        catch(Exception e)
        {
            log.error(e);
            ret = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ret;
    }
}
