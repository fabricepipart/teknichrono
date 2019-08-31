package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestChronometerEndpoint extends TestRestEndpoint<ChronometerDTO> {


  public TestRestChronometerEndpoint() {
    super("chronometers", ChronometerDTO.class, new ArrayList<ChronometerDTO>() {
      private static final long serialVersionUID = 2400271710550958571L;
    }.getClass().getGenericSuperclass());
  }

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<ChronometerDTO> chronometers = getAll();
    assertThat(chronometers.size()).isEqualTo(0);
    create("C1");
    chronometers = getAll();
    assertThat(chronometers.size()).isEqualTo(1);
    create("C2");
    chronometers = getAll();
    assertThat(chronometers.size()).isEqualTo(2);

    List<ChronometerDTO> someChronos = getAllInWindow(1, 1);
    assertThat(someChronos.size()).isEqualTo(1);

    chronometers = getAll();
    for (ChronometerDTO beacon : chronometers) {
      delete(beacon.getId());
    }
    chronometers = getAll();
    assertThat(chronometers.size()).isEqualTo(0);
    assertTestCleanedEverything();
  }


  @Test
  public void testCreateModifyDelete() {
    create("C1");
    ChronometerDTO b = getByName("C1");
    long id = b.getId();
    getById(id);

    ChronometerDTO modifiedChronometer = new ChronometerDTO();
    modifiedChronometer.setName("C2");
    modifiedChronometer.setId(id);
    update(id, modifiedChronometer);
    List<ChronometerDTO> chronos = getAll();
    Assertions.assertThat(chronos.size()).isEqualTo(1);
    getByName("C1", NOT_FOUND);
    ChronometerDTO newReturnedChrono = getByName("C2");
    Assertions.assertThat(newReturnedChrono.getId()).isEqualTo(id);

    delete(id);
    assertTestCleanedEverything();
  }

  /**
   * ******************** Utilities *********************
   **/

  public void create(String name) {
    ChronometerDTO c = new ChronometerDTO();
    c.setName(name);
    create(c);
  }

  public void assertTestCleanedEverything() {
    Assertions.assertThat(getAll()).isNullOrEmpty();
  }
}
