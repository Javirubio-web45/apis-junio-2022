import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


public class eComerce {
    //Variables
    static private String url_base = "webapi.segundamano.mx";
    static private String token_basic1 = "dGVzdDQ1X3FhQG1haWxpbmF0b3IuY29tOjEyMzQ1Ng==";
    static private String email = "test45_qa@mailinator.com";
    static private String pass = "123456";
    static private String access_token;
    static private String account_id = "/private/accounts/11687822";
    static private String uuid;
    static private String username;
    static private int phoneNumber;
    static private String token2;
    static private String addressID;
    static private String ad_id;
    static private String alert_id;

    @BeforeAll
    public static void configurarVariables() {
        Allure.addAttachment("Environment", "QA");
    }

    //@BeforeEach
    private String getToken() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic(email, pass)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body Response: " + body_response);

        JsonPath jsonResponse = response.jsonPath();

        System.out.println("AccessToken: " + jsonResponse.get("access_token"));
        access_token = jsonResponse.get("access_token");
        System.out.println("AccessToken: " + access_token);

        System.out.println("Account id: " + jsonResponse.get("account.account_id"));
        System.out.println("uuid: " + jsonResponse.get("account.uuid"));

        account_id = jsonResponse.get("account.account_id");
        uuid = jsonResponse.get("account.uuid");

        String datos = uuid + ":" + access_token;
        String encodedToken2UP = Base64.getEncoder().encodeToString(datos.getBytes());

        return encodedToken2UP;
    }

    @Test
    @Order(1)
    @DisplayName("Test case: Validar que se desplieguen todas las categorias")
    @Severity(SeverityLevel.CRITICAL)
    public void obtener_categorias() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/public/categories/filter", url_base);
        //Con el queryparam podemos quitar del link el '?lang=es'

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();

        System.out.println("Body Response: " + body_response);
        System.out.println("Headers: " + headers_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("all_categories"));

    }

    @Test
    @Order(2)
    public void obtener_token_usando_header_authorization() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "Basic " + token_basic1)
                .post();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();

        System.out.println("Body Response: " + body_response);
        System.out.println("Headers: " + headers_response);

        assertEquals(200, response.getStatusCode());

        assertNotNull(body_response);

        assertTrue(body_response.contains("access_token"));

    }

    @Test
    @Order(3)
    public void obtener_token_usando_auth_email_pass() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic(email, pass)
                .post();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();

        System.out.println("Body Response: " + body_response);
        System.out.println("Headers: " + headers_response);
        System.out.println("Status response: " + response.getStatusCode());
        System.out.println("Time response: " + response.getTime());

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));

        //Obtener valores de variables

        JsonPath jsonResponse = response.jsonPath();

        System.out.println("AccessToken: " + jsonResponse.get("access_token"));
        access_token = jsonResponse.get("access_token");
        System.out.println("AccessToken: " + access_token);

        System.out.println("Account id: " + jsonResponse.get("account.account_id"));
        System.out.println("uuid: " + jsonResponse.get("account.uuid"));

        account_id = jsonResponse.get("account.account_id");
        uuid = jsonResponse.get("account.uuid");
    }

    @Test
    @Order(4)
    public void obtener_mi_usuario() {

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Content-type", "application/json")
                .header("Authorization", "Basic " + token_basic1)
                .header("Origin", "https://www.segundamano.mx")
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("test45_qa@mailinator.com"));
    }

    @Test
    @Order(5)
    public void editar_usuario() {
        String body_request = "{\"account\":{\"name\":\"Laureano Gomez Montes\",\"phone\":\"3319497849\",\"locations\":[{\"code\":\"8\",\"key\":\"region\",\"label\":\"Colima\",\"locations\":[{\"code\":\"104\",\"key\":\"municipality\",\"label\":\"Comala\"}]}],\"professional\":false,\"phone_hidden\":false}}";
        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s", url_base, account_id);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Content-type", "application/json")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .body(body_request)
                .patch();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("phone"));
    }

    @Test
    @Order(6)
    public void obtener_informacion_del_usuario() {

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s", url_base, account_id);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "tag:scmcoord.com,2013:api  " + access_token)
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(uuid));
    }

    @Test
    @Order(7)
    public void crear_usuario() {
        username = "agente_ventas" + (Math.floor(Math.random() * 435) + 4321) + "@mailinator.com";

        double password = (Math.floor(Math.random() * 488) + 54321);

        String datos = username + ":" + password;
        String encode = Base64.getEncoder().encodeToString(datos.getBytes());
        String bodyRequest = "{\"account\":{\"email\":\"" + username + "\"}}";

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts?lang=es", url_base);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "Basic " + encode)
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        //Prueba negativa, requiere verificacion
        assertEquals(401, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ACCOUNT_VERIFICATION_REQUIRED"));
    }

    @Test
    @Order(8)
    public void actualizar_telefono() {
        phoneNumber = (int) (Math.random() * 999999999 + 987654321);
        String bodyRequest = "{\"account\":{\"name\":\"" + username + "\",\"phone\":\"" + phoneNumber + "\",\"professional\":false}}";

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s", url_base, account_id);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .contentType("application/json")
                .body(bodyRequest)
                .patch();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));

        JsonPath jsonResponse = response.jsonPath();

        System.out.println("Telefono: " + jsonResponse.get("account.phone"));
        String phoneResponse = jsonResponse.get("account.phone");
        assertEquals(phoneResponse, "" + phoneNumber);
    }

    @Test
    @Order(9)
    public void crear_direccion() {

        String token2UP = uuid + ":" + access_token;
        token2 = Base64.getEncoder().encodeToString(token2UP.getBytes());

        RestAssured.baseURI = String.format("https://%s/addresses/v1/create", url_base);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + token2)
                .formParam("contact", "Martha Luz")
                .formParam("phone", phoneNumber)
                .formParam("rfc", "CPFJ911130ZC0")
                .formParam("zipCode", "11011")
                .formParam("exteriorInfo", "Miguel Hidalgo300")
                .formParam("region", "11")
                .formParam("municipality", "292")
                .formParam("area", "7489")
                .formParam("alias", "Casa de agente")
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(201, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addressID"));

        //Ambas formas funcionan
        //JsonPath jsonResponse = response.jsonPath();
        //addressID = jsonResponse.get("addressID");
        addressID = response.jsonPath().get("addressID");

        System.out.println("Address: " + addressID);
    }

    @Test
    @Order(10)
    public void editar_direccion() {
        String tokenUp2 = getToken();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/modify/%s", url_base, addressID);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Contact", "Contacto Modificado")
                .header("Authorization", "Basic " + tokenUp2)
                .put();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("modified correctly"));
    }

    @Test
    @Order(11)
    public void borrar_direccion() {

        RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s", url_base, addressID);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid, access_token)
                .delete();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"message\":\"" + addressID + " deleted correctly\"}"));
    }

    @Test
    @Order(12)
    public void crear_anuncio() {
        String tokenUp2 = getToken();
        System.out.println("Token 2: " + tokenUp2);

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up", url_base, uuid);

        System.out.println("Endpoint: " + RestAssured.baseURI);

        String body_request = "{\"category\":\"8121\",\"subject\":\"Mudanzas y fletes economicos\",\"body\":\"Mudanzas y feltes varatos a tu ciudad (prueba no real)\",\"region\":\"4\",\"municipality\":\"86179\",\"area\":\"142027\",\"price\":\"1\",\"phone_hidden\":\"false\",\"show_phone\":\"true\",\"contact_phone\":\"9944556633\"}";

        Response response = given()
                .log().all()
                .header("Authorization", "Basic " + tokenUp2)
                .header("Content-type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://www.segundamano.mx")
                .header("X-source", "PHOENIX_DESKTOP")
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);

        //Setear variables de ambiente

        JsonPath jsonResponse = response.jsonPath();
        System.out.println("ad_id: "+jsonResponse.get("data.ad.ad_id"));

        ad_id = jsonResponse.get("data.ad.ad_id");
        System.out.println("ad_id: "+ad_id);
    }

    @Test
    @Order(13)
    public void editar_anuncio() {
        String tokenUp2 = getToken();
        String body_request = "{\"category\":\"8121\",\"subject\":\"Modificado\",\"body\":\"Mudanzas y feltes a toda la republica(prueba no real)\",\"region\":\"4\",\"municipality\":\"86179\",\"area\":\"142027\",\"price\":\"1\",\"phone_hidden\":\"false\",\"show_phone\":\"true\",\"contact_phone\":\"9966554433\"}";

        RestAssured.baseURI = String.format("https://%s/accounts/%s/up/%s", url_base, uuid, ad_id);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Content-type", "application/json")
                .header("Origin", "https://www.segundamano.mx")
                .header("X-source", "PHOENIX_DESKTOP")
                .header("Authorization", "Basic " + tokenUp2)
                .body(body_request)
                .filter(new AllureRestAssured())
                .put();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("Modificado"));


    }

    @Test
    @Order(14)
    public void borrar_anuncio() {
        String body_request = "{\"delete_reason\":{\"code\":\"5\"}}";
        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst/%s", url_base, account_id, ad_id);

        Response response = given()
                .log().all()
                .header("Content-type", "application/json")
                .header("Authorization", "tag:scmcoord.com,2013:api "+access_token)
                .body(body_request)
                .delete();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        //Prueba negativa ya que no se puede borrar un anuncio que aun no se ha aprobado por la pagina
        assertEquals(403, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ERROR_AD_ALREADY_DELETED"));
    }

    @Test
    @Order(15)
    public void anuncios_pendientes() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst", url_base, account_id);

        Response response = given()
                .log().all()
                .queryParam("status", "pending")
                .queryParam("lim", "20")
                .queryParam("o", "0")
                .queryParam("query", "")
                .queryParam("lang", "es")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .header("Content-type", "application/json")
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
    }

    @Test
    @Order(16)
    public void obtener_balance_del_usuario() {
        RestAssured.baseURI = String.format("https://%s/credits/v1%s", url_base, account_id);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"balance\":{\"balance\":0},\"Transactions\":[]}"));


    }

    @Test
    @Order(17)
    public void obtener_detalle_del_balance() {
        String tokenUp2 = getToken();
        RestAssured.baseURI = String.format("https://%s/tokens/v1/public/balance/detail/%s", url_base, uuid);

        Response response = given()
                .log().all()
                .header("Authorization", "Basic " + tokenUp2)
                .header("Content-type", "application/json")
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("token_if_re_free"));
    }

    @Test
    @Order(18)
    public void editar_datos_del_usuario() {

        String bodyRequest = "{\"account\":{\"name\":\"Laureano Gomez Montes\",\"phone\":\"3319497849\",\"locations\":[{\"code\":\"8\",\"key\":\"region\",\"label\":\"Colima\",\"locations\":[{\"code\":\"104\",\"key\":\"municipality\",\"label\":\"Comala\"}]}],\"professional\":false,\"phone_hidden\":false}}";

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s", url_base, account_id);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .contentType("application/json")
                .body(bodyRequest)
                .patch();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(email));


    }

    @Test
    @Order(19)
    public void obtener_resumen() {

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1%s/stats?lang=es", url_base, account_id);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .header("Content-type", "application/json")
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ad_events"));
    }

    @Test
    @Order(20)
    public void obtener_tiendas() {

        RestAssured.baseURI = String.format("https://%s/shops/api/v1/public/shops", url_base);

        Response response = given()
                .log().all()
                .queryParam("limit", "15")
                .queryParam("search", "piezas")
                .queryParam("cat", "2000")
                .queryParam("req", "16")
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("total_count"));
    }

    @Test
    @Order(21)
    public void crear_alerta() {
        String tokenUp2 = getToken();

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert", url_base, uuid);

        String body_request = "{\"ad_listing_service_filters\":{\"region\":\"16\",\"municipality\":\"589\",\"category_lv0\":\"5000\",\"category_lv1\":\"5040\",\"label\":\"1\",\"sort\":\"price\"}}";

        Response response = given()
                .log().all()
                .header("Authorization", "Basic " + tokenUp2)
                .header("Content-type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://www.segundamano.mx")
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);

        //Setear variables de ambiente

        JsonPath jsonResponse = response.jsonPath();

        alert_id = jsonResponse.get("data.alert.id");
        System.out.println("alert_id: "+alert_id);
    }

    @Test
    @Order(22)
    public void borrar_alerta() {
        String tokenUp2 = getToken();

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert/%s", url_base, uuid, alert_id);

        Response response = given()
                .log().all()
                .header("Content-type", "application/json")
                .header("Authorization", "Basic "+tokenUp2)
                .filter(new AllureRestAssured())
                .delete();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ok"));
    }




}