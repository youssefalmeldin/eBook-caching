package com.joo.eBook.services;

import com.joo.eBook.DTO.BookDTO;

import java.util.List;

public interface BookServiceI {
    BookDTO createBook(BookDTO dto);

    List<BookDTO> getAllBooks();

    BookDTO getBookById(Long id);

    BookDTO updateBook(BookDTO bookDTO, Long id);

    void deleteBook(Long id);
}
