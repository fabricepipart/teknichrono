package org.trd.app.teknichrono.rest;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
@Transactional
public class RestApplication extends Application {

  public RestApplication() {
    overrideToLocalDB();
  }

  private void overrideToLocalDB() {
    Map<String, String> env = System.getenv();
    Map<String, Object> configOverrides = new HashMap<String, Object>();
    boolean remoteDB = false;
    for (String envName : env.keySet()) {
      if (envName.contains("DB_USERNAME")) {
        remoteDB = true;
      }
      // You can put more code in here to populate configOverrides...
    }
    if (remoteDB) {
      configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      EntityManagerFactory emf = Persistence.createEntityManagerFactory("teknichrono-persistence-unit",
          configOverrides);
    }
  }
}