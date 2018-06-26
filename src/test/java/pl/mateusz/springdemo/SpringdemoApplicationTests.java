package pl.mateusz.springdemo;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.mateusz.springdemo.controllers.RestMainController;
import pl.mateusz.springdemo.repositories.UserLogPassRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringdemoApplicationTests {
    @Autowired
    private UserLogPassRepository userLogPassRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestMainController restMainController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(restMainController).isNotNull();
        initializeDatabase();
    }
    @Test
    public void initializeDatabase() throws Exception {

        this.mockMvc.perform(get("/rest"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Data Loaded")));
    }
    @Test
    public void testCorrectLoginPass() throws Exception {
        initializeDatabase();
        String id = "1";
        String login = "admin";
        String password = "admin";

        testLoginPass(id, login, password);
    }



    @Test
    public void testIncorrectLoginPass() throws Exception {
        initializeDatabase();

        String id = "-1";
        String login = "admin";
        String password = "zlehaslo";

        testLoginPass(id, login, password);
    }
    @Test
    public void testCorrectPayment() throws Exception {
        initializeDatabase();

        String id = "1";
        String payment = "10";
        String expectedResponse = "Success!";

        testRestPayment(id, payment, expectedResponse);
    }
    @Test
    public void testIncorrectPayment() throws Exception {
        initializeDatabase();

        String id = "-1";
        String payment = "10";
        String expectedResponse = "User doesn't exist";

        testRestPayment(id, payment, expectedResponse);
    }

    @Test
    public void testCorrectPayoff() throws Exception {
        initializeDatabase();

        String id = "1";
        String payoff = "10";
        String expectedResponse = "Success!";

        testRestPayoff(id, payoff, expectedResponse);
    }

    @Test
    public void testIncorrectPayoff() throws Exception {
        initializeDatabase();

        String id = "-1";
        String payoff = "10";
        String expectedResponse = "User doesn't exist";

        testRestPayoff(id, payoff, expectedResponse);
        id = "1";
        payoff = "10000000";
        expectedResponse = String.format("Not enough money!(User #%s)", id);

        testRestPayoff(id, payoff, expectedResponse);
    }
    @Test
    public void testCorrectTransfer() throws Exception {
        initializeDatabase();

        String idFrom = "1";
        String idTo = "2";
        String payoff = "10";
        String expectedResponse = "Success!";

        testRestTransfer(idFrom, idTo, payoff, expectedResponse);
    }

    @Test
    public void testIncorrectTransfer() throws Exception {
        initializeDatabase();

        String idFrom = "-1";
        String idTo = "2";
        String payoff = "10";
        String expectedResponse = "User that makes a transaction doesn't exist";

        testRestTransfer(idFrom, idTo, payoff, expectedResponse);

        idFrom = "1";
        idTo = "-2";
        payoff = "10";
        expectedResponse = "User that receives transaction doesn't exist";

        testRestTransfer(idFrom, idTo, payoff, expectedResponse);

        idFrom = "1";
        idTo = "2";
        payoff = "10000000";
        expectedResponse = String.format("Not enough money!(User #%s)", idFrom);

        testRestTransfer(idFrom, idTo, payoff, expectedResponse);
    }
    @Test
    public void testOneHistory() throws Exception {
        String id = "1";

        this.mockMvc.perform(get(String.format("/restOneHistory?id=%s", id)))
                .andDo(print())
                .andExpect(status().isOk());
        id = "-1";

        this.mockMvc.perform(get(String.format("/restOneHistory?id=%s", id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }
    @Test
    public void testAllHistory() throws Exception {

        this.mockMvc.perform(get("/restAllHistory"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testActualBalance() throws Exception {
        String id = "1";

        this.mockMvc.perform(get(String.format("/restActualBalance?id=%s", id)))
                .andDo(print())
                .andExpect(status().isOk());

        id = "-1";

        this.mockMvc.perform(get(String.format("/restActualBalance?id=%s", id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("-1.0")));
    }

    private void testLoginPass(String id, String login, String password) throws Exception {
        this.mockMvc.perform(get(String.format("/restUserLogin?login=%s&password=%s", login, password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(id)));
    }

    private void testRestPayment(String id, String payment, String expectedResponse) throws Exception {
        this.mockMvc.perform(get(String.format("/restPayment?id=%s&payment=%s", id, payment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedResponse)));
    }

    private void testRestPayoff(String id, String payoff, String expectedResponse) throws Exception {
        this.mockMvc.perform(get(String.format("/restPayoff?id=%s&payoff=%s", id, payoff)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedResponse)));
    }

    private void testRestTransfer(String idFrom
            , String idTo, String payoff, String expectedResponse) throws Exception {
        this.mockMvc.perform(get(String.format("/restTransfer?idFrom=%s&idTo=%s&value=%s"
                , idFrom, idTo, payoff)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedResponse)));
    }
}
