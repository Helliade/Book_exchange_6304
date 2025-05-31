package com.example.demo.DTO;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OrderDTO {
    private Long id;
    private String type;
    private String status;
    private Long userId;
    private Set<BookDTO> books;

    //Геттеры
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Long getUserId() { return userId; }
    public Set<BookDTO> getBooks() { return books; }


    public OrderDTO(Order order) {
        this.id = order.getId();
        this.type = order.getType();
        this.status = order.getStatus();
        this.userId = order.getUser().getId();
        this.books = BookDTO.convertBooksToDTO(order.getBooks());
    }

    public List<OrderDTO> ListToOrderDTO(List<Order> orders) {
        List<OrderDTO> result = new LinkedList<>();
        for (Order order : orders) {
            result.add(new OrderDTO(order));
        }
        return result;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", userID=" + userId + '\'' +
                ", books=" + books +
                '}';
    }

//    public OrderDTO convertToDTO(Order order) {
//        return new OrderDTO(order);
//    }

}
