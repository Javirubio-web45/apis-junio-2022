import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ejemplo_test_api {
    @Test
    public void api_covid_test(){
        RestAssured.baseURI = String.format("https://api.covidtracking.com/v2/us/daily.json");

        Response response=given()
                .log().all()
                .headers("Accept", "application/json")
                .get();

        String body_response = response.getBody().prettyPrint();
        String status = String.format(String.valueOf(response.getStatusCode()));

        System.out.println("Status code"+ status);

        //jUnit5
        //Probar el codigo de respuesta
        assertEquals(200, response.getStatusCode());
        //Validar el bodyResponse no este vacio
        assertNotNull(body_response);
        //Validar que el body contenga la cadena id
        assertTrue(body_response.contains("id"));


    }
}
