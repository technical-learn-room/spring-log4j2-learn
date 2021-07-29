package com.j.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    private static final Logger logger = LogManager.getLogger(LogController.class);

    @PostMapping("/logs/{logCount}")
    public void createLogs(@PathVariable("logCount") int logCount) {
        for (int i = 0 ; i < logCount ; i++) {
            logger.info("[" + i + "] 로그 테스트");
        }
    }

    @PostMapping("/logs")
    public void createLogs() {
        logger.trace("trace 로그 테스트");
        logger.debug("debug 로그 테스트");
        logger.info("info 로그 테스트");
        logger.warn("warn 로그 테스트");
        logger.error("error 로그 테스트");
    }

    @PostMapping("/logs/trace")
    public void createTraceLogs() {
        logger.trace("trace 로그 테스트1");
        logger.trace("trace 로그 테스트2");
        logger.trace("trace 로그 테스트3");
    }

    @PostMapping("/logs/debug")
    public void createDebugLogs() {
        logger.debug("debug 로그 테스트1");
        logger.debug("debug 로그 테스트2");
        logger.debug("debug 로그 테스트3");
    }

    @PostMapping("/logs/info")
    public void createInfoLogs() {
        logger.info("info 로그 테스트1");
        logger.info("info 로그 테스트2");
        logger.info("info 로그 테스트3");
    }

    @PostMapping("/logs/warn")
    public void createWarnLogs() {
        logger.warn("warn 로그 테스트1");
        logger.warn("warn 로그 테스트2");
        logger.warn("warn 로그 테스트3");
    }

    @PostMapping("/logs/error")
    public void createErrorLogs() {
        logger.error("error 로그 테스트1");
        logger.error("error 로그 테스트2");
        logger.error("error 로그 테스트3");
    }

    @PostMapping("/logs/fatal")
    public void createFatalLogs() {
        logger.fatal("fatal 로그 테스트1");
        logger.fatal("fatal 로그 테스트2");
        logger.fatal("fatal 로그 테스트3");
    }

    @PostMapping("/exception")
    public void throwException() {
        throw new RuntimeException("에러입니다요");
    }
}
