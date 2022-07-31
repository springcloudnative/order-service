package com.polarbookshop.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.domain.dto.Book;
import com.polarbookshop.orderservice.domain.dto.OrderAcceptedMessageDTO;
import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestChannelBinderConfiguration.class)
@Slf4j
class OrderServiceApplicationTestsIT {
	@Container
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withReuse(true);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private OutputDestination output;

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private BookClient bookClient;

	@DynamicPropertySource
	static void mysqlProperties(DynamicPropertyRegistry registry) {
		String jdbcUrl = mySQLContainer.getJdbcUrl();
		registry.add("spring.r2dbc.url", OrderServiceApplicationTestsIT::r2dbcUrl);
		registry.add("spring.r2dbc.username", mySQLContainer::getUsername);
		registry.add("spring.r2dbc.password", mySQLContainer::getPassword);
		registry.add("spring.flyway.url", mySQLContainer::getJdbcUrl);
	}


	private static String r2dbcUrl() {
		return String.format("r2dbc:mysql://%s://%s%s",
				mySQLContainer.getContainerIpAddress(),
				mySQLContainer.getMappedPort(MySQLContainer.MYSQL_PORT),
				mySQLContainer.getDatabaseName());
	}

	@Test
	void contextLoads() {
	}

	@Test
	void whenGetOrdersThenReturn() throws IOException {
		String bookIsbn = "1234567893";
		Book book = Book.builder()
				.isbn(bookIsbn)
				.title("Title")
				.author("Author")
				.price(9.90).build();

		given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.just(book));
		OrderRequest orderRequest = new OrderRequest(bookIsbn, 1);
		OrderEntity expectedOrder = webTestClient.post().uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(OrderEntity.class).returnResult().getResponseBody();
		assertThat(expectedOrder).isNotNull();
		assertThat(objectMapper.readValue(output.receive().getPayload(), OrderAcceptedMessageDTO.class))
				.isEqualTo(new OrderAcceptedMessageDTO(expectedOrder.getId()));

		webTestClient.get().uri("/orders")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBodyList(OrderEntity.class).value(orders -> {
					assertThat(orders.stream().filter(order -> order.getBookIsbn()
							.equals(bookIsbn)).findAny()).isNotEmpty();
				});
	}

}
