package com.refinitiv.pts.ebs_fix_server_simulator;

import javax.annotation.PostConstruct;

import com.refinitiv.pts.ebs_fix_server_simulator.fix.AcceptorApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
@ComponentScan(basePackages = "com.refinitiv.pts.ebs_fix_server_simulator")
public class EbsFixSimulatorApplication {

	@Autowired
	private AcceptorApp acceptorApp;

	public static void main(String[] args) {
		SpringApplication.run(EbsFixSimulatorApplication.class, args);
	}

	@PostConstruct
	public void StartFIXServer()
	{
		log.info("StartFIXServer");
		acceptorApp.start();
		log.info("After thread is started");
	}
}
