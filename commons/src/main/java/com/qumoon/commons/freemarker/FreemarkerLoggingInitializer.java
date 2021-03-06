package com.qumoon.commons.freemarker;

import freemarker.log.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author kevin
 */
public class FreemarkerLoggingInitializer implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      // SLF4J logging must be selected manually in Freemarker 2.3.x
      Logger.selectLoggerLibrary(Logger.LIBRARY_SLF4J);

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
