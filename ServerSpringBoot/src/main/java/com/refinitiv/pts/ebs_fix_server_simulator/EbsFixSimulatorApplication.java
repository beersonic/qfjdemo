package com.refinitiv.pts.ebs_fix_server_simulator;

import com.refinitiv.pts.ebs_fix_server_simulator.fix.AcceptorApp;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Log4j2
@SpringBootApplication
@ComponentScan(basePackages = "com.refinitiv.pts.ebs_fix_server_simulator")
public class EbsFixSimulatorApplication {
	@Autowired
	private AcceptorApp acceptorApp;

	public static void main(String[] args) {
		SpringApplication.run(EbsFixSimulatorApplication.class, args);
	}

}
