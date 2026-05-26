package br.gov.caixaverso.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@QuarkusTest
class SimulationResourceIntegrationTest {

    @Inject
    EntityManager entityManager;

    @BeforeEach
    void cleanDatabaseBeforeEach() {
        QuarkusTransaction.requiringNew().run(this::cleanDatabase);
    }

    @AfterEach
    void cleanDatabaseAfterEach() {
        QuarkusTransaction.requiringNew().run(this::cleanDatabase);
    }

    private void cleanDatabase() {
        entityManager.createQuery("delete from CalculationMemory").executeUpdate();
        entityManager.createQuery("delete from Simulation").executeUpdate();
    }

    @Test
    @DisplayName("Deve criar simulacao e permitir consulta por id")
    void shouldCreateSimulationAndAllowFindById() {
        Long simulationId = createSimulation("1000.00", "1", 3);

        given()
                .when().get("/simulacoes/{id}", simulationId)
                .then()
                .statusCode(200)
                .body("id", equalTo(simulationId.intValue()))
                .body("valorInicial", equalTo("1000.00"))
                .body("taxaJurosMensal", equalTo("1"))
                .body("prazoMeses", equalTo(3))
                .body("valorTotalFinal", equalTo("1030.30"))
                .body("valorTotalJuros", equalTo("30.30"))
                .body("calculos", hasSize(3))
                .body("calculos[0].mes", equalTo(1))
                .body("calculos[0].saldoInicial", equalTo("1000.00"))
                .body("calculos[0].juro", equalTo("10.00"))
                .body("calculos[0].saldoFinal", equalTo("1010.00"));
    }

    @Test
    @DisplayName("Deve listar simulacoes criadas")
    void shouldListCreatedSimulations() {
        createSimulation("1000.00", "1", 2);
        createSimulation("2000.00", "2", 2);

        given()
                .when().get("/simulacoes")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    @DisplayName("Deve apagar simulacao e retornar 404 ao consultar novamente")
    void shouldDeleteSimulationAndReturnNotFoundAfterwards() {
        Long simulationId = createSimulation("1500.00", "1.5", 2);

        given()
                .when().delete("/simulacoes/{id}", simulationId)
                .then()
                .statusCode(204);

        given()
                .when().get("/simulacoes/{id}", simulationId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 para id inexistente")
    void shouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .when().get("/simulacoes/{id}", 999999)
                .then()
                .statusCode(404);

        given()
                .when().delete("/simulacoes/{id}", 999999)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 400 quando body da simulacao for nulo")
    void shouldReturnBadRequestWhenBodyIsNull() {
        given()
                .contentType(ContentType.JSON)
                .body("null")
                .when().post("/simulacoes")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 400 quando prazo em meses for invalido")
    void shouldReturnBadRequestWhenTermMonthsIsInvalid() {
        String payload = """
                {
                  \"valorInicial\": \"1000.00\",
                  \"taxaJurosMensal\": \"1\",
                  \"prazoMeses\": 0
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/simulacoes")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 400 quando campo obrigatorio estiver ausente")
    void shouldReturnBadRequestWhenRequiredFieldIsMissing() {
        String payload = """
                {
                  \"valorInicial\": \"1000.00\",
                  \"prazoMeses\": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/simulacoes")
                .then()
                .statusCode(400);
    }

        @Test
        @DisplayName("Deve expor especificacao OpenAPI com campos do payload de entrada")
        void shouldExposeOpenApiWithSimulationInputFields() {
                given()
                                .when().get("/openapi")
                                .then()
                                .statusCode(200)
                                .body(containsString("/simulacoes:"))
                                .body(containsString("SimulationInputDTO"))
                                .body(containsString("valorInicial"))
                                .body(containsString("taxaJurosMensal"))
                                .body(containsString("prazoMeses"))
                                .body(containsString("\"201\""))
                                .body(containsString("\"400\""));
        }

    private Long createSimulation(String valorInicial, String taxaJurosMensal, Integer prazoMeses) {
        Map<String, Object> payload = Map.of(
                "valorInicial", valorInicial,
                "taxaJurosMensal", taxaJurosMensal,
                "prazoMeses", prazoMeses);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/simulacoes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .response();

        return response.jsonPath().getLong("id");
    }
}
