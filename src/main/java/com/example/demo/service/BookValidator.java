package com.example.demo.service;

import com.example.demo.Models.Book;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Set;

public class BookValidator {
    private static final Set<String> VALID_STATUSES = Set.of(
            "FREE", "BOOKED", "OUT"
    );
    private static final Set<String> VALID_CONDIT = Set.of(
            "PERFECT", "HAS_FLAWS", "DAMAGED"
    );



    public static Specification<Book> hasYearOfPubl(Short yearOfPubl) {
        return (root, query, cb) ->
                yearOfPubl == null ? null : cb.equal(root.get("yearOfPubl"), yearOfPubl);
    }

    public static Specification<Book> hasPublHouse(String publHouse) {
        return (root, query, cb) ->
                publHouse == null ? null : cb.equal(root.get("publHouse"), publHouse);
    }

    public static Specification<Book> hasLanguage(String language) {
        return (root, query, cb) ->
                language == null ? null : cb.equal(root.get("language"), language);
    }

    public static Specification<Book> hasCondit(String condit) {
        return (root, query, cb) ->
                condit == null ? null : cb.equal(root.get("condit"), condit);
    }

    public static Specification<Book> hasStatus(String status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }




    //Возвращает неизменяемый набор допустимых
    public static Set<String> getValidStatuses() {
        return Collections.unmodifiableSet(VALID_STATUSES);
    }

    public static Set<String> getValidCondit() {
        return Collections.unmodifiableSet(VALID_CONDIT);
    }

    //Проверяет, является ли допустимым
    public static boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(status);
    }

    public static boolean isValidCondit(String condit) {
        return VALID_CONDIT.contains(condit);
    }

    public static void validateStatusTransition(String currentStatus, String newStatus) {

        if (newStatus == null) {        // 1. Проверка, что статус не null
            throw new IllegalArgumentException("Status can't be null");
        }

        if (!VALID_STATUSES.contains(newStatus)) {        // 2. Проверка, что статус существует
            throw new IllegalArgumentException("Unknown new status: " + newStatus);
        }
    }
}
