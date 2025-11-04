package com.joo.eBook.services;

import com.joo.eBook.DTO.BookDTO;
import com.joo.eBook.mapper.BookMapper;
import com.joo.eBook.model.Book;
import com.joo.eBook.repo.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "books")
public class BookService implements BookServiceI {


    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @CachePut(value = "book", key = "#result.id")
    @CacheEvict(value = "books", allEntries = true)
    public BookDTO createBook(BookDTO dto) {
        Book book = bookMapper.toEntity(dto);
        Book saved = bookRepository.save(book);
        log.info("CREATE: Book saved and cached with ID: {}", saved.getId());
        return bookMapper.toDto(saved);
    }

    @Cacheable(value = "books")
    public List<BookDTO> getAllBooks() {
        log.info("📘 DB CALL: Fetching all books from database...");
        return bookRepository.findAll().stream().map(bookMapper::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "book", key = "#id")
    public BookDTO getBookById(Long id) {
        log.info("DB CALL: Fetching book with ID {} from database...", id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return bookMapper.toDto(book);
    }

    @CachePut(value = "book", key = "#id")
    @CacheEvict(value = "books", allEntries = true)
    public BookDTO updateBook(BookDTO bookDTO, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPrice(bookDTO.getPrice());
        Book updated = bookRepository.save(book);
        log.info("UPDATE: Book with ID {} updated and cache refreshed", id);
        return bookMapper.toDto(updated);
    }

    // ✅ Delete book + Evict from cache
    @Caching(evict = {@CacheEvict(value = "book", key = "#id"), @CacheEvict(value = "books", allEntries = true)})
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        log.info("DELETE: Book with ID {} deleted and cache evicted", id);
    }

    // ✅ Scheduler: Every 10 seconds → Clear + Preload cache
    @Scheduled(fixedRate = 10000)
    @CacheEvict(value = {"book", "books"}, allEntries = true)
    public void refreshCache() {
        log.info("SCHEDULER: Clearing all cache...");
        preloadCache();
    }

    // ✅ Preload the cache automatically after clearing
    public void preloadCache() {
        List<BookDTO> books = getAllBooks();
        log.info("PRELOAD: Reloaded {} books into cache.", books.size());
    }
}