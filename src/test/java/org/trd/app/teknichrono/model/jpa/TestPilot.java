package org.trd.app.teknichrono.model.jpa;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TestPilot {

  @Test
  public void fullnameCorrectForAllCombinations() {
    Pilot p = createPilot(null, null, null);
    assertThat(p.getFullname()).isEqualTo("");
    assertThat(p.toString()).isNotBlank();
    p = createPilot(null, "First", null);
    assertThat(p.getFullname()).isEqualTo("First");
    assertThat(p.toString()).isNotBlank();
    p = createPilot(null, null, "Last");
    assertThat(p.getFullname()).isEqualTo("Last");
    assertThat(p.toString()).isNotBlank();
    p = createPilot(null, "First", "Last");
    assertThat(p.getFullname()).isEqualTo("First Last");
    assertThat(p.toString()).isNotBlank();
    p = createPilot("42", null, null);
    assertThat(p.getFullname()).isEqualTo("(42)");
    assertThat(p.toString()).isNotBlank();
    p = createPilot("42", "First", null);
    assertThat(p.getFullname()).isEqualTo("(42) First");
    assertThat(p.toString()).isNotBlank();
    p = createPilot("42", null, "Last");
    assertThat(p.getFullname()).isEqualTo("(42) Last");
    assertThat(p.toString()).isNotBlank();
    p = createPilot("42", "First", "Last");
    assertThat(p.getFullname()).isEqualTo("(42) First Last");
    assertThat(p.toString()).isNotBlank();
  }

  private Pilot createPilot(String nick, String first, String last) {
    Pilot p = new Pilot();
    p.setNickname(nick);
    p.setFirstName(first);
    p.setLastName(last);
    return p;
  }
}
