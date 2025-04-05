package com.example.demo;

import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication

public class DemoApplication {

	private final UsernameService usernameService;
	private final OrderService orderService;


	@Autowired
	public DemoApplication(UsernameService usernameService, OrderService orderService) {
		this.usernameService = usernameService;
		this.orderService = orderService;
	}
	public static void main(String[] args) {
		String url = "jdbc:postgresql://localhost:5432/postgres";
		String username = "postgres";
		String password = "mysecretpassword";
		System.out.println("Connecting...");

		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			System.out.println("Connection successful!");
		} catch (SQLException e) {
			System.out.println("Connection failed!");
			e.printStackTrace();
		}
		SpringApplication.run(DemoApplication.class, args);

	}
//	@Bean
//	public CommandLineRunner demo() {
//		return args -> {
// Здесь можно добавить код для тестирования вашего приложения
// Например, вызвать методы вашего сервиса для создания, чтения, обновления и удаления данных
// и выводить результаты на консоль


// Пример:
//			String[] logins = {
//					"admin",
//					"superuser",
//					"sysadmin",
//					"root",
//					"manager",
//					"ivanov",
//					"petrov",
//					"sidorova",
//					"smirnov",
//					"kuznetsov",
//					"popova",
//					"volkov",
//					"fedorov",
//					"nikolaev",
//					"orlova",
//					"test_user",
//					"demo",
//					"guest",
//					"temp_user",
//					"backup"
//			};
//
//			// Массив соответствующих паролей (нехешированных)
//			String[] passwords = {
//					"Admin123!",
//					"SuperPass456",
//					"SysAdmin789",
//					"RootPassword!",
//					"ManagerPass1",
//					"Ivanov2023",
//					"PetrovPass",
//					"Sid2023!",
//					"SmirnovPass",
//					"Kuz2023!",
//					"PopovaPass",
//					"Volkov2023",
//					"FedorPass!",
//					"Nikolaev1",
//					"Orlova2023",
//					"TestPass123",
//					"DemoPassword",
//					"GuestAccess",
//					"TempPass123",
//					"Backup2023!"
//			};
//			for (int i = 0; i<20; i++) {
//				Username username = new Username();
//				username.setLogin(logins[i]);
//				username.setPassword(passwords[i]);
//				if (usernameService.registerUsername(username) != null) {
//					Username savedUsername = usernameService.getUsernameById(username.getId());
//					System.out.println("Saved Username: " + savedUsername);
//				};
//			}



//			Order order = new Order();
//			order.setUser(username);
//			order.setType("give");
//			order.setStatus("completed");
//			orderService.createOrder(order);


//			Order savedOrder = orderService.getOrderById(order.getId());
//			System.out.println("Saved Order: " + savedOrder);
//		};
//	}
}
