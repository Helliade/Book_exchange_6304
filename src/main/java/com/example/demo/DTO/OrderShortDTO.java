package com.example.demo.DTO;

import com.example.demo.Models.Order;

import java.util.LinkedList;
import java.util.List;

public class OrderShortDTO {
    private Long id;
    private String type;
    private String status;
    private Long userId;


    //Геттеры
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Long getUserId() { return userId; }



    public OrderShortDTO(Order order) {
        this.id = order.getId();
        this.type = order.getType();
        this.status = order.getStatus();
        this.userId = order.getUser().getId();
    }

    public List<OrderShortDTO> ListToOrderShortDTO(List<Order> orders) {
        List<OrderShortDTO> result = new LinkedList<>();
        for (Order order : orders) {
            result.add(new OrderShortDTO(order));
        }
        return result;
    }

    @Override
    public String toString() {
        return "OrderShortDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", userID=" + userId +
                '}';
    }
}
