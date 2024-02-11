package org.trd.app.teknichrono.rest;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/rest")
@Transactional
public class RestApplication extends Application {

  public RestApplication() {
    super();
  }

}