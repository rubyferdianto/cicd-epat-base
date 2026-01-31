package sg.edu.nus.iss.d13revision.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(DataController.class)
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataController dataController;

    @Test
    void testHealthCheck_ShouldReturnHealthCheckOK() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("HEALTH CHECK OK!"));
    }

    @Test
    void testHealthCheck_ShouldReturnContentTypeText() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void testVersion_ShouldReturnVersion() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().string("The actual version is 1.0.0"));
    }

    @Test
    void testVersion_ShouldReturnContentTypeText() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void testGetRandomNations_ShouldReturnJsonArray() throws Exception {
        mockMvc.perform(get("/nations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", isA(java.util.ArrayList.class)));
    }

    @Test
    void testGetRandomNations_ShouldReturn10Nations() throws Exception {
        mockMvc.perform(get("/nations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    void testGetRandomNations_ShouldContainRequiredFields() throws Exception {
        mockMvc.perform(get("/nations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nationality", notNullValue()))
                .andExpect(jsonPath("$[0].capitalCity", notNullValue()))
                .andExpect(jsonPath("$[0].flag", notNullValue()))
                .andExpect(jsonPath("$[0].language", notNullValue()));
    }

    @Test
    void testGetRandomNations_ShouldReturnValidNationData() throws Exception {
        mockMvc.perform(get("/nations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nationality", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].capitalCity", everyItem(notNullValue())));
    }

    @Test
    void testGetRandomCurrencies_ShouldReturnJsonArray() throws Exception {
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", isA(java.util.ArrayList.class)));
    }

    @Test
    void testGetRandomCurrencies_ShouldReturn20Currencies() throws Exception {
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)));
    }

    @Test
    void testGetRandomCurrencies_ShouldContainRequiredFields() throws Exception {
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].code", notNullValue()));
    }

    @Test
    void testGetRandomCurrencies_ShouldReturnValidCurrencyData() throws Exception {
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].code", everyItem(notNullValue())));
    }

    @Test
    void testGetRandomCurrencies_CodeShouldBeString() throws Exception {
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code", isA(String.class)));
    }

    @Test
    void testHealthCheck_DirectCall_ShouldReturnCorrectString() {
        String result = dataController.healthCheck();
        assert result.equals("HEALTH CHECK OK!");
    }

    @Test
    void testVersion_DirectCall_ShouldReturnCorrectVersion() {
        String result = dataController.version();
        assert result.equals("The actual version is 1.0.0");
    }

    @Test
    void testGetRandomNations_DirectCall_ShouldReturnValidJsonNode() {
        JsonNode result = dataController.getRandomNations();
        assert result.isArray();
        assert result.size() == 10;
    }

    @Test
    void testGetRandomCurrencies_DirectCall_ShouldReturnValidJsonNode() {
        JsonNode result = dataController.getRandomCurrencies();
        assert result.isArray();
        assert result.size() == 20;
    }
}
