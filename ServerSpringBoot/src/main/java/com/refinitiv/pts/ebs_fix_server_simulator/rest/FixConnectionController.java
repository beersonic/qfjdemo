package com.refinitiv.pts.ebs_fix_server_simulator.rest;

import com.refinitiv.pts.ebs_fix_server_simulator.fix.AcceptorApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FixConnectionController {
    @Autowired
    private AcceptorApp acceptorApp;

    @GetMapping("/fix-acceptor/start")
    public HttpStatus startFixAcceptor() {
        new Thread(() -> {
            this.acceptorApp.start();
        }).start();

        return HttpStatus.OK;
    }

}
