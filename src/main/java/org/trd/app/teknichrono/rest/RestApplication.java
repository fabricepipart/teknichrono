package org.trd.app.teknichrono.rest;

import javax.transaction.Transactional;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;


@OpenAPIDefinition(
  info = @Info(
    title="TekniChrono API",
    version = "1.0.0",
    contact = @Contact(
        name = "TBD",
        url = "https://github.com/fabricepipart/teknichrono"    
    ),
    license = @License(
        name = "GNU General Public License v3.0",
        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"    
    )  
  ),
  servers = {
    @Server(
      url= "http://localhost:8080",     //TODO should be taken dynamically
      description= "Dev"
    ),    
    @Server(
      url= "https://staging.teknichrono.fr",  
      description= "Staging"
    ),
  }

)

@ApplicationPath("/rest")
@Transactional
public class RestApplication extends Application {

  public RestApplication() {
    super();
  }

}