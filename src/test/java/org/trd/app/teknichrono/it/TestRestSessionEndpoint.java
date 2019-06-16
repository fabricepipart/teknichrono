package org.trd.app.teknichrono.it;

import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.SessionType;

import java.time.Instant;

public class TestRestSessionEndpoint extends TestRestEndpoint<SessionDTO> {

  public TestRestSessionEndpoint() {
    super("sessions", SessionDTO.class);
  }
  /**
   * ******************** Tests *********************
   **/

  /**
   * ******************** Reusable *********************
   **/

  public void create(String name) {
    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName(name);
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(120 * 60L));
    create(sessionDTO);
  }
}
