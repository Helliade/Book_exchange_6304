package com.example.demo;

import com.example.demo.Models.Book;
import com.example.demo.service.BookService;
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
import java.util.List;

@SpringBootApplication

public class DemoApplication {

	private final UsernameService usernameService;
	private final OrderService orderService;
	private final BookService bookService;


	@Autowired
	public DemoApplication(UsernameService usernameService, OrderService orderService, BookService bookService) {
		this.usernameService = usernameService;
		this.orderService = orderService;
		this.bookService = bookService;
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
	@Bean
	public CommandLineRunner demo() {
		return args -> {
// Kод для тестирования вашего приложения

//			if (usernameService.authenticateUsername("admin", "Admin123!")) {
//				Username username = usernameService.getUserByLogin("admin");
//				System.out.println("User found! ID:" + username.getId());
//				List<Order> orders = orderService.getOrdersByUser(username);
//				System.out.println(orders);
//
//
//				Order order = orderService.getOrderById(orders.get(0).getId());
//				order.setType("TAKE");
//				order = orderService.updateOrder(order);
//
//				order = orderService.getOrderById(orders.get(1).getId());
//				order.setStatus("IN_TRANSIT");
//				order = orderService.updateOrder(order);
//
//				orders = orderService.getOrdersByUser(username);
//
//				System.out.println(orders);
//			}
//			else {
//				System.out.println("User not found.");
//			}
			List<Book> book = bookService.getAllBooks();
			System.out.println(book);






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
		};
	}
}
