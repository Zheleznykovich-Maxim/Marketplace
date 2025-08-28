//package com.app.marketplace;
//
//import com.app.marketplace.domain.CustomerOrder;
//import com.app.marketplace.domain.Product;
//import com.app.marketplace.domain.dto.CreatedOrderRequest;
//import com.app.marketplace.repository.OrderRepository;
//import com.app.marketplace.repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
//class OrderIntegrationTest {
//
//    @Container
//    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"))
//            .withDatabaseName("marketplace")
//            .withUsername("app")
//            .withPassword("password");
//
//    @Container
//    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
//            .withEmbeddedZookeeper();
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//        registry.add("kafka.bootstrap.servers", kafka::getBootstrapServers);
//    }
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    private String baseUrl;
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port + "/api/orders";
//        productRepository.deleteAll();
//        orderRepository.deleteAll();
//
//        Product product = new Product();
//        product.setName("Test Product");
//        product.setStock(10);
//        product.setPrice(new BigDecimal("10.99"));
//        productRepository.save(product);
//    }
//
//    @Test
//    void testCreateOrder_success() {
//        Product product = productRepository.findAll().get(0);
//
//        CreatedOrderRequest req = new CreatedOrderRequest(
//                product.getId(),
//                2
//        );
//
//        String orderPublicId = this.restTemplate.postForObject(baseUrl, req, String.class);
//
//        assertThat(orderPublicId).isNotNull();
//
//        Optional<CustomerOrder> orderOpt = orderRepository.findAll().stream().findFirst();
//        assertThat(orderOpt).isPresent();
//        CustomerOrder order = orderOpt.get();
//
//        assertEquals(product.getId(), order.getProductId());
//        assertEquals(2, order.getQty());
//        assertEquals("CREATED", order.getStatus());
//    }
//}