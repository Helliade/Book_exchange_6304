package com.example.demo.service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class OrderValidator {
    private static final Set<String> VALID_STATUSES = Set.of(
            "CART",
            "CREATED",
            "IN_TRANSIT",
            "DELIVERY_READY",
            "COMPLETED",
            "CANCELLED"
    );
    private static final Set<String> VALID_TYPES = Set.of(
            "GIVE",
            "TAKE"
    );

    //Возвращает неизменяемый набор допустимых статусов заказа
    public static Set<String> getValidStatuses() {
        return Collections.unmodifiableSet(VALID_STATUSES);
    }

    //Возвращает неизменяемый набор допустимых типов заказа
    public static Set<String> getValidTypes() {
        return Collections.unmodifiableSet(VALID_TYPES);
    }

    //Проверяет, является ли статус допустимым
    public static boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(status);
    }

    //Проверяет, является ли тип заказа допустимым
    public static boolean isValidType(String type) {
        return VALID_TYPES.contains(type);
    }

    // Мапа допустимых переходов: текущий статус -> множество допустимых следующих статусов
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "CART", Set.of("CREATED"),
            "CREATED", Set.of("IN_TRANSIT", "CANCELLED"),
            "IN_TRANSIT", Set.of("DELIVERY_READY", "CANCELLED"),
            "DELIVERY_READY", Set.of("COMPLETED", "CANCELLED"),
            "COMPLETED", Set.of(), // После COMPLETED нельзя изменить статус
            "CANCELLED", Set.of() // После COMPLETED нельзя изменить статус
    );

    public static void validateStatusTransition(String currentStatus, String newStatus) {

        if (newStatus == null) {        // 1. Проверка, что статус не null
            throw new IllegalArgumentException("Status can't be null");
        }

        if (!VALID_STATUSES.contains(newStatus)) {        // 2. Проверка, что статус существует
            throw new IllegalArgumentException("Unknown new status: " + newStatus);
        }

        Set<String> allowedTargetStatuses = ALLOWED_TRANSITIONS.get(currentStatus);        // 3. Проверка, что переход допустим
        if (allowedTargetStatuses == null || !allowedTargetStatuses.contains(newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid transition: %s -> %s. Valid transition: %s",
                            currentStatus, newStatus, allowedTargetStatuses));
        }

        if ("COMPLETED".equals(currentStatus)) {        // 4. Дополнительные бизнес-правила (пример)
            throw new IllegalStateException("A COMPLETED order can't be changed.");
        }
    }
}
