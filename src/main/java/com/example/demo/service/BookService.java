package com.example.demo.service;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.Models.Work;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final WorkRepository workRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public BookService(BookRepository bookRepository, WorkRepository workRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.workRepository = workRepository;
        this.orderRepository = orderRepository;
    }

    //Получение списка всех книг
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new EntityNotFoundException("No books found.");
        }
        return books;
    }

    //Получение заказа по ID
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
    }

    //Создание заказа
    public Book createBook(Short yearOfPubl, String publHouse, String language, String condit) {
        if (condit != null && !BookValidator.isValidCondit(condit)) {
            throw new IllegalArgumentException(
                    String.format("Invalid condit: %s. Valid condit: %s", condit, BookValidator.getValidCondit()));
        }
        Book book = new Book(yearOfPubl, publHouse, language, condit, "FREE");
        return bookRepository.save(book);
    }

    //Обновление заказа
//    public Book updateBook(Book book) {
//        return bookRepository.save(book);
//    }

    //Удаление заказа
    public void deleteBook(Long bookId) {
        bookRepository.findById(bookId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        bookRepository.deleteById(bookId);
    }

    //Удаление связанных с заказом данных
    public void deleteBookLinkedData(Long bookId) {
        Book book = bookRepository.findById(bookId)                           //находим книгу
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        // Удаляем книгу из заказов
        for (Order order : book.getOrders()) {
            order.getBooks().remove(book);
            orderRepository.save(order);
        }
        // Удаляем произведения из книги
        for (Work work : book.getWorks()) {
            work.getBooks().remove(book);
            workRepository.save(work);
        }
    }

    public Book updateBookArguments(Long bookId, String newCondit, String newStatus) {

        // Проверка статуса и типа (если переданы)
        if (newCondit != null && !BookValidator.isValidCondit(newCondit)) {
            throw new IllegalArgumentException(
                    String.format("Invalid condit: %s. Valid condit: %s", newCondit, BookValidator.getValidCondit()));
        }
        if (newStatus != null && !BookValidator.isValidStatus(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid status: %s. Valid statuses: %s", newStatus, BookValidator.getValidStatuses()));
        }

        // Проверка, что хотя бы один параметр задан
        if (newCondit == null && newStatus == null) {
            throw new IllegalArgumentException("At least one parameter must be provided");
        }

        Book book = bookRepository.findById(bookId)         // Проверка, что книга существует
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (newCondit != null) {
            book.setCondit(newCondit);
        }
        if (newStatus != null) {
            book.setStatus(newStatus);
        }
        return bookRepository.save(book);
    }


    public List<Book> getBooksByArguments(Short yearOfPubl, String publHouse, String language, String condit, String status) {

        // Проверка статуса и типа (если переданы)
        if (condit != null && !BookValidator.isValidCondit(condit)) {
            throw new IllegalArgumentException(
                    String.format("Invalid condit: %s. Valid condit: %s", condit, BookValidator.getValidCondit()));
        }
        if (status != null && !BookValidator.isValidStatus(status)) {
            throw new IllegalArgumentException(
                    String.format("Invalid status: %s. Valid statuses: %s", status, BookValidator.getValidStatuses()));
        }

        // Проверка, что хотя бы один параметр задан
        if (yearOfPubl == null && publHouse == null && language == null
                && condit == null && status == null) {
            throw new IllegalArgumentException("At least one parameter must be provided");
        }

        // Динамическое построение запроса
        Specification<Book> spec = Specification.where(null);

        if (yearOfPubl != null) {
            spec = spec.and(BookValidator.hasYearOfPubl(yearOfPubl));
        }
        if (publHouse != null) {
            spec = spec.and(BookValidator.hasPublHouse(publHouse));
        }
        if (language != null) {
            spec = spec.and(BookValidator.hasLanguage(language));
        }
        if (condit != null) {
            spec = spec.and(BookValidator.hasCondit(condit));
        }
        if (status != null) {
            spec = spec.and(BookValidator.hasStatus(status));
        }

        List<Book> books = bookRepository.findAll(spec);

        // Проверка результатов
        if (books.isEmpty()) {
            StringBuilder message = new StringBuilder("No books found.");
            if (yearOfPubl != null) message.append(" Searched for yearOfPubl: ").append(yearOfPubl).append(".");
            if (publHouse != null) message.append(" Searched for publHouse: ").append(publHouse).append(".");
            if (language != null) message.append(" Searched for language: ").append(language).append(".");
            if (condit != null) message.append(" Searched for condit: ").append(condit).append(".");
            if (status != null) message.append(" Searched for status: ").append(status).append(".");
            throw new EntityNotFoundException(message.toString());
        }
        return books;
    }

    public List<Work> getWorksByBookId(Long bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        List<Work> works = workRepository.findByBookId(bookId);
        if (works.isEmpty()) {
            throw new EntityNotFoundException("No works found.");
        }
        return works;
    }

    @Transactional
    public Book addWorkToBook(Long bookId, Long workId) {
        Book book = bookRepository.findById(bookId)                           //находим книгу
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        Work work = workRepository.findById(workId)                               //находим произведение
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        if (bookRepository.findCreationIdsByBookId(bookId).contains(workId)) {   // Добавлено ли произв уже в книгу
            throw new IllegalStateException("Work already exists in the book");
        }

        // Добавляем и сохраняем (обратная связь обновляется автоматически)
        book.getWorks().add(work);

        return bookRepository.save(book);
    }

    @Transactional
    public Book deleteWorkFromBook(Long bookId, Long workId) {
        Book book = bookRepository.findById(bookId)                           //находим книгу
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        Work work = workRepository.findById(workId)                               //находим произведение
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        if (!bookRepository.findCreationIdsByBookId(bookId).contains(workId)) {   // Есть ли произв в книге
            throw new IllegalStateException("Work not exists in the book");
        }

        // Удаляем и сохраняем (обратная связь обновляется автоматически)
        book.getWorks().remove(work);

        return bookRepository.save(book);
    }
}
