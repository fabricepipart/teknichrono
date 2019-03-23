package org.trd.app.teknichrono.model.jpa;

public enum SessionType {

  TIME_TRIAL("tt"), RACE("rc");

  private SessionType(String i) {
    setIdentifier(i);
  }

  private String identifier;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

}
