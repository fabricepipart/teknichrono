package org.trd.app.teknichrono.it;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.oas.models.OpenAPI;
import static org.assertj.core.api.Assertions.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Tag("integration")
@QuarkusTest
public class TestOpenAPISpec {

  public static final int NO_CONTENT = javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode();
  public static final int OK = javax.ws.rs.core.Response.Status.OK.getStatusCode();
  public static final int NOT_FOUND = javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode();
  public static final int BAD_REQUEST = javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode();

  public static final String OPENAPI_URL = "/openapi";
  public static final String SWAGGERUI_URL = "/swagger-ui";


  public TestOpenAPISpec() {
    
  }

  @BeforeEach
  public void prepare() {
    
  }

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testOpenAPISpec() {
    OpenAPI spec = this.getOpenAPISpec();

    
    assertThat(spec.getInfo().getTitle()).isEqualTo("TekniChrono API");

    assertThat(spec.getServers().get(1).getDescription()).isEqualTo("Staging");

    assertThat(spec.getPaths().get("/rest/beacons/number/{number}").getGet()           //GET operation on /rest/beacons/number/{number}
                        .getResponses().get("200")                                     //reponse in case of 200 OK
                        .getContent().get("application/json").getSchema().get$ref())   //schema for the response
              .isEqualTo("#/components/schemas/BeaconDTO");

  }

  @Test
  public void testSwaggerUI() {
    Response r = given()
    .when().get(SWAGGERUI_URL)
    .then()
    .statusCode(OK)
    .extract().response();
    
    Document doc = Jsoup.parse(r.asString());

    assertThat(doc.title()).isEqualTo("Swagger UI");

    // get complete content of <script> tags
    StringBuffer startupScript = new StringBuffer();
    for (Element scripts : doc.getElementsByTag("script")) {
      for (DataNode dataNode : scripts.dataNodes()) {
        startupScript.append(dataNode.getWholeData());
      }
    }
    //check that it references the proper URL for OpenAPI spec
    assertThat(startupScript.toString()).contains("url: \""+OPENAPI_URL+"\"");

  }



  /**
   * ******************** Utilities
   * 
   * @return *********************
   **/

  public OpenAPI getOpenAPISpec() {
    
    Response r = given()
        .when().get(OPENAPI_URL)
        .then()
        .statusCode(OK)
        .extract().response();

    OpenAPI openAPI = new OpenAPIV3Parser().readContents(r.asString()).getOpenAPI();
    return openAPI;

  }

}
