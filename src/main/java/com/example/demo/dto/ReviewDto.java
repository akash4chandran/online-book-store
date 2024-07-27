package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto implements Serializable {
    private int id;
    @NotBlank(message = "Reviewer name cannot be blank")
    private String reviewerName;
    @NotBlank(message = "Content cannot be blank")
    private String content;
    private String bookIsbn;
}
