package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Review Collection")
@Slf4j
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @PostMapping(path = "/{isbn}")
    @Operation(summary = "Submit a review", description = "Submits a review for a specific book using its ISBN")
    @Parameter(description = "Review for the book with ISBN", example = "9783161484112", required = true,
            in = ParameterIn.PATH)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json", schema = @Schema(example = """
                    {
                        "reviewerName": "John Doe",
                        "content": "Great book!"
                    }
                    """)))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review submitted successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "id": 1,
                            "reviewerName": "John Doe",
                            "content": "Great book!",
                            "bookIsbn": "9783161484112"
                        }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "Invalid ISBN format",
                                "Reviewer name cannot be null",
                                "Content cannot be null"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 404,
                            "message": "NOT_FOUND",
                            "details": [
                                "Book not found for the given ISBN"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized request",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                            "statusCode": 401,
                            "message": "UNAUTHORIZED",
                            "details": [
                                "Full authentication is required to access this resource"
                            ]
                        }
                        """))) })
    public ResponseEntity<ReviewDto> submitReview(@PathVariable String isbn, @RequestBody ReviewDto reviewDto) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{isbn}")
    @Operation(summary = "Get reviews by ISBN",
            description = "Retrieves a list of reviews for a specific book using its ISBN")
    @Parameter(description = "ISBN of the book to get reviews for", example = "9783161484112", required = true,
            in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        [
                            {
                                "id": 1,
                                "reviewerName": "John Doe",
                                "content": "Nice Book",
                                "bookIsbn": "9783161484112"
                            },
                            {
                                "id": 4,
                                "reviewerName": "Mr.X",
                                "content": "Nice Book",
                                "bookIsbn": "9783161484112"
                            }
                        ]
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid ISBN",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "Invalid ISBN format"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 404,
                            "message": "NOT_FOUND",
                            "details": [
                                "Book not found for the given ISBN"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized request",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                            "statusCode": 401,
                            "message": "UNAUTHORIZED",
                            "details": [
                                "Full authentication is required to access this resource"
                            ]
                        }
                        """))) })
    public ResponseEntity<List<ReviewDto>> getReviewsByIsbn(@PathVariable String isbn) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PutMapping(path = "/{isbn}/{review-id}")
    @Operation(summary = "Update a review", description = "Updates an existing review using its ID and book's ISBN")
    @Parameter(description = "ISBN of the book to update the review for", name = "isbn", example = "9783161484112",
            required = true, in = ParameterIn.PATH)
    @Parameter(description = "ID of the review to be updated", name = "review-id", example = "1", required = true,
            in = ParameterIn.PATH)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json", schema = @Schema(example = """
                    {
                        "reviewerName": "John Doe",
                        "content": "Great book!"
                    }
                    """)))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        [
                            {
                                "id": 1,
                                "reviewerName": "John Doe",
                                "content": "Great book!",
                                "bookIsbn": "9783161484112"
                            }
                        ]
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid input or ISBN",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "Invalid ISBN format",
                                "Reviewer name cannot be null",
                                "Content cannot be null"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "404", description = "Review or Book not found",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 404,
                            "message": "NOT_FOUND",
                            "details": [
                                "Review not found for ID: 1"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "409", description = "Review does not belong to the specified book",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 409,
                            "message": "CONFLICT",
                            "details": [
                                "Review does not belong to the specified book"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized request",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                            "statusCode": 401,
                            "message": "UNAUTHORIZED",
                            "details": [
                                "Full authentication is required to access this resource"
                            ]
                        }
                        """))) })
    public ResponseEntity<ReviewDto> updateReview(@PathVariable String isbn,
            @PathVariable(name = "review-id") long reviewId, @Validated @RequestBody ReviewDto reviewDto) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
