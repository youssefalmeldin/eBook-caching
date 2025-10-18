package com.joo.eBook.services;

import com.joo.eBook.DTO.BookDTO;
import com.joo.eBook.mapper.BookMapper;
import com.joo.eBook.model.Book;
import com.joo.eBook.repo.BookRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
public class BookService implements BookServiceI {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @CachePut(value = "book", key = "#result.id")
    public BookDTO createBook(BookDTO dto) {
        Book book = bookMapper.toEntity(dto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Cacheable(value = "books")
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "book", key = "#id")
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(" Book "));
        return bookMapper.toDto(book);

    }

    @CachePut(value = "book", key = "#id")
    public BookDTO updateBook(BookDTO bookDTO, Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPrice(bookDTO.getPrice());
        return bookMapper.toDto(bookRepository.save(book));
    }


    @CacheEvict(value = "book", key = "#id")
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

}
