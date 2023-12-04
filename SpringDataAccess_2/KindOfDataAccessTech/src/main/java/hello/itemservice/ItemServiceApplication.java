package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Import(JdbcTemplateV3Config.class)
//@Import(JdbcTemplateV2Config.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(MemoryConfig.class)
@Slf4j
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

	/**
	 * properties 파일에 datasource 설정을 하게 되면 스프링부트는 DataSource 와
	 * 사용중인 데이터 접근 기술에 맞는 TransactionManger Bean 을 자동 등록한다.
	 *
	 * 하지만, 아래와 같이 DataSource 를 직접 Bean 으로 등록하게되면, 스프링 부트 자동구성 시, DataSource Bean 이 등록되지 않는다.
	 * 따라서 애플리케이션을 실행하게 되면 properties 에 설정한 DataSource 를 사용하지 않고, 방금 등록한 DataSource 를 사용하게된다.
	 * 따라서 db 가 임베디드 (인메모리) db 를 사용하게 된다.
	 */
//	@Bean
//	@Profile("test")
//	public DataSource dataSource() {
//		log.info("메모리 데이터베이스 초기화");
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.h2.Driver");
//		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
//		dataSource.setUsername("sa");
//		dataSource.setPassword("");
//		return dataSource;
//	}

}
