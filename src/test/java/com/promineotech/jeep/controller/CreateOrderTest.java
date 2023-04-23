package com.promineotech.jeep.controller;

//ASSIGNMENT 
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.controller.support.CreateOrderTestSupport;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;
import lombok.Getter;

//We are saying we want the tests run in a web environment. To make sure tests are not
//run on top of each other, we specify the .RANDOM_PORT for each class
//Local host means it will stay local to the machine

@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles ("test") //signifies this as a test
//Calls the sql schemas from flyway 
@Sql(scripts = {
    "classpath:flyway/migrations/V1.0__Jeep_Schema.sql", 
    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
    config = @SqlConfig(encoding = "utf-8")
)


class CreateOrderTest extends CreateOrderTestSupport {

  //This holds the port that Tomcat is listening in on the test. 
  @LocalServerPort
  private int serverPort;
  
  //TestRestTemplate sends the HTTP requests
  @Autowired
  @Getter
  private TestRestTemplate restTemplate;
    
  @Test
  void testCreateOrderReturnsSuccess201() {
    
    //Given: body as an order in JSON
    String body = createOrderBody();
    String uri = String.format("http://localhost:%d/orders", serverPort); 
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    //sets the request body and headers 
    HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);
    
    //When: an order is sent
    //Sends the request body and headers to the server. Imports Order from Jeep.entity
    ResponseEntity<Order> response = restTemplate.exchange(uri,
        HttpMethod.POST, bodyEntity, Order.class);

    //Then: a 201 status is returned and it is not null
      //First sends a code to say it was created, then checks to ensure it is not null
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    //And: Asserts that the order response is correct
    Order order = response.getBody();
    assertThat(order.getCustomer().getCustomerId()).isEqualTo("MORISON_LINA");
    assertThat(order.getModel().getModelId()).isEqualTo(JeepModel.WRANGLER);
    assertThat(order.getModel().getTrimLevel()).isEqualTo("Sport Altitude");
    assertThat(order.getModel().getNumDoors()).isEqualTo(4);
    assertThat(order.getColor().getColorId()).isEqualTo("EXT_NACHO");
    assertThat(order.getEngine().getEngineId()).isEqualTo("2_0_TURBO");
    assertThat(order.getTire().getTireId()).isEqualTo("35_TOYO");
    assertThat(order.getOptions()).hasSize(6);
      
  }
  

}