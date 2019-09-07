package org.trd.app.teknichrono.business.client;

import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.jpa.Session;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public class TestSessionManagement {

  @Test
  public void whenNoIntersectionBefore() {
    Instant now = Instant.now();
    Session s = new Session();
    s.setStart(now.plusSeconds(100));
    s.setEnd(now.plusSeconds(200));
    Session other = new Session();
    other.setStart(now.plusSeconds(10));
    other.setEnd(now.plusSeconds(20));
    assertThat(s.intersects(other)).isFalse();
  }

  @Test
  public void whenIntersectionBefore() {
    Instant now = Instant.now();
    Session s = new Session();
    s.setStart(now.plusSeconds(100));
    s.setEnd(now.plusSeconds(200));
    Session other = new Session();
    other.setStart(now.plusSeconds(10));
    other.setEnd(now.plusSeconds(150));
    assertThat(s.intersects(other)).isTrue();
  }

  @Test
  public void whenNoIntersectionAfter() {
    Instant now = Instant.now();
    Session s = new Session();
    s.setStart(now.plusSeconds(100));
    s.setEnd(now.plusSeconds(200));
    Session other = new Session();
    other.setStart(now.plusSeconds(300));
    other.setEnd(now.plusSeconds(400));
    assertThat(s.intersects(other)).isFalse();

  }

  @Test
  public void whenIntersectionAfter() {
    Instant now = Instant.now();
    Session s = new Session();
    s.setStart(now.plusSeconds(100));
    s.setEnd(now.plusSeconds(200));
    Session other = new Session();
    other.setStart(now.plusSeconds(150));
    other.setEnd(now.plusSeconds(300));
    assertThat(s.intersects(other)).isTrue();

  }

  @Test
  public void whenIntersectionOverlap() {
    Instant now = Instant.now();
    Session s = new Session();
    s.setStart(now.plusSeconds(100));
    s.setEnd(now.plusSeconds(200));
    Session other = new Session();
    other.setStart(now.plusSeconds(10));
    other.setEnd(now.plusSeconds(300));
    assertThat(s.intersects(other)).isTrue();
  }

  @Test
  public void whenIntersectionContains() {
    Instant now = Instant.now();
    Session s = new Session();
    s.setStart(now.plusSeconds(100));
    s.setEnd(now.plusSeconds(200));
    Session other = new Session();
    other.setStart(now.plusSeconds(110));
    other.setEnd(now.plusSeconds(120));
    assertThat(s.intersects(other)).isTrue();

  }

}
