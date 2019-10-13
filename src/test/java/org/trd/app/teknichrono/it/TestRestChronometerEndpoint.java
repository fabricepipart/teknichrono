package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
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
    ChronometerDTO dto = getById(id);
    assertThat(dto.getSelectionStrategy()).isEqualTo("HIGH");
    assertThat(dto.getSendStrategy()).isEqualTo("ASYNC");
    assertThat(dto.getInactivityWindow()).isEqualTo(Duration.ofSeconds(5));
    assertThat(dto.isBluetoothDebug()).isEqualTo(false);
    assertThat(dto.isDebug()).isEqualTo(false);
    assertThat(dto.getOrderToExecute()).isNull();

    ChronometerDTO modifiedChronometer = new ChronometerDTO();
    modifiedChronometer.setName("C2");
    modifiedChronometer.setId(id);

    modifiedChronometer.setSelectionStrategy(Chronometer.PingSelectionStrategy.LAST.toString());
    modifiedChronometer.setSendStrategy(Chronometer.PingSendStrategy.NONE.toString());
    modifiedChronometer.setInactivityWindow(Duration.ofSeconds(10));
    modifiedChronometer.setBluetoothDebug(true);
    modifiedChronometer.setDebug(true);
    modifiedChronometer.setOrderToExecute(Chronometer.ChronometerOrder.UPDATE.toString());


    update(id, modifiedChronometer);

    List<ChronometerDTO> chronos = getAll();
    assertThat(chronos.size()).isEqualTo(1);
    getByName("C1", NOT_FOUND);
    ChronometerDTO newReturnedChrono = getByName("C2");
    assertThat(newReturnedChrono.getId()).isEqualTo(id);

    assertThat(newReturnedChrono.getSelectionStrategy()).isEqualTo("LAST");
    assertThat(newReturnedChrono.getSendStrategy()).isEqualTo("NONE");
    assertThat(newReturnedChrono.getInactivityWindow()).isEqualTo(Duration.ofSeconds(10));
    assertThat(newReturnedChrono.isBluetoothDebug()).isEqualTo(true);
    assertThat(newReturnedChrono.isDebug()).isEqualTo(true);
    assertThat(newReturnedChrono.getOrderToExecute()).isEqualTo("UPDATE");

    delete(id);
    assertTestCleanedEverything();
  }

  @Test
  public void ackReturns404ForChronoThatDoesNotExist() {
    List<ChronometerDTO> chronometers = getAll();
    assertThat(chronometers.size()).isEqualTo(0);
    failedAck(123);
    assertTestCleanedEverything();
  }

  @Test
  public void ackClearsOrders() {
    create("C1");
    ChronometerDTO b = getByName("C1");
    long id = b.getId();
    ChronometerDTO dto = getById(id);
    assertThat(dto.getOrderToExecute()).isNull();

    dto.setOrderToExecute(Chronometer.ChronometerOrder.UPDATE.toString());
    update(id, dto);

    dto = getById(id);
    assertThat(dto.getOrderToExecute()).isEqualTo(Chronometer.ChronometerOrder.UPDATE.toString());

    dto = ack(id);
    assertThat(dto.getOrderToExecute()).isNull();
    dto = getById(id);
    assertThat(dto.getOrderToExecute()).isNull();
    dto = ack(id);
    assertThat(dto.getOrderToExecute()).isNull();

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

  private void failedAck(long id) {
    given().pathParam("id", id)
        .when().post("/rest/" + restEntrypointName + "/{id}/ack")
        .then()
        .statusCode(NOT_FOUND);
  }

  private ChronometerDTO ack(long id) {
    Response r = given().pathParam("id", id)
        .when().post("/rest/" + restEntrypointName + "/{id}/ack")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public void assertTestCleanedEverything() {
    assertThat(getAll()).isNullOrEmpty();
  }
}
