package com.mochousoft.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engine {

    private static final Logger logger = LoggerFactory.getLogger(Engine.class);

    public static void main(String[] args) {
        logger.info("=============================================================================================");
        logger.info("我是一个可执行的jar程序, 这是执行我时输出的日志信息");
        logger.info("I am an executable jar program, and this is the log information output when I execute it");
        logger.info("=============================================================================================");
    }
}
