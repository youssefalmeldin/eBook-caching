package com.joo.eBook.DTO;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private double price;
}
