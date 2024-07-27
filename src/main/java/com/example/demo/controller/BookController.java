package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Book Collection")
@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Add a new book", description = "Adds a new book to the store")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json", schema = @Schema(example = """
                    {
                        "isbn": "9783161484111",
                        "title": "The Dream",
                        "authorId": 2
                    }
                    """)))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                           {
                                       "isbn": "9783161484112",
                                       "title": "The Dream",
                                       "authorId": 2,
                                       "authorName": "Author Two"
                                   }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "Author ID must be positive",
                                "ISBN cannot be null",
                                "Title cannot be null"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "409", description = "Book with ISBN already exists",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 409,
                            "message": "CONFLICT",
                            "details": [
                                "Book with ISBN already exists"
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
    public ResponseEntity<BookDto> addBook(@Validated @RequestBody BookDto bookDto) {
        log.info("Entering addBook()");
        BookDto savedBook = bookService.addNewBook(bookDto);
        log.info("Leaving addBook()");
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("/{isbn}")
    @Operation(summary = "Get book by ISBN", description = "Retrieves the details of a book using its ISBN")
    @Parameter(description = "ISBN of the book to be retrieved", example = "9783161484112", required = true,
            in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                           {
                               "isbn": "9783161484112",
                               "title": "The Dream",
                               "authorId": 2,
                               "authorName": "Author Two"
                           }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid ISBN",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "ISBN cannot be null or empty"
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
    public ResponseEntity<BookDto> getBookByIsbn(@PathVariable String isbn) {
        log.info("Entering getBookByIsbn()");
        BookDto bookDto = bookService.fetchBookByIsbn(isbn);
        log.info("Leaving getBookByIsbn()");
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Retrieve books",
            description = "Retrieves a list of books with optional search parameters and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Books retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "totalPages": 1,
                            "totalElements": 2,
                            "pageable": {
                                "pageNumber": 0,
                                "pageSize": 20,
                                "sort": {
                                    "sorted": true,
                                    "empty": false,
                                    "unsorted": false
                                },
                                "offset": 0,
                                "paged": true,
                                "unpaged": false
                            },
                            "size": 20,
                            "content": [
                                {
                                    "isbn": "9783161484111",
                                    "title": "The Dream3",
                                    "authorId": 2,
                                    "authorName": "Author Two"
                                },
                                {
                                    "isbn": "9783161484112",
                                    "title": "The Dream5",
                                    "authorId": 2,
                                    "authorName": "Author Two"
                                }
                            ],
                            "number": 0,
                            "sort": {
                                "sorted": true,
                                "empty": false,
                                "unsorted": false
                            },
                            "numberOfElements": 2,
                            "first": true,
                            "last": true,
                            "empty": false
                        }
                        """))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid sort name",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "Invalid sort 'name,ascending'. Allowed sort-orders are asc,ASC,desc,DESC"
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
    public ResponseEntity<Page<BookDto>> retrieveBooks(
            @Parameter(hidden = true, in = ParameterIn.QUERY, style = ParameterStyle.FORM)
            @SortDefault(sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> searchParameters) {
        log.info("Entering retrieveBooks()");
        Page<BookDto> bookDtos = bookService.getAllBooks(searchParameters, pageable);
        log.info("Leaving retrieveBooks()");
        return new ResponseEntity<>(bookDtos, HttpStatus.OK);
    }

    @PutMapping("/{isbn}")
    @Operation(summary = "Update a book", description = "Updates the details of an existing book using its ISBN")
    @Parameter(description = "ISBN of the book to be updated", example = "9783161484112", required = true,
            in = ParameterIn.PATH)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json", schema = @Schema(example = """
                    {
                        "isbn": "9783161484112",
                        "title": "The Dream Updated",
                        "authorId": 2
                    }
                    """)))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                           {
                               "isbn": "9783161484112",
                               "title": "The Dream Updated",
                               "authorId": 2,
                               "authorName": "Author Two"
                           }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid ISBN or input",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "ISBN cannot be null or empty",
                                "Title cannot be null"
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
    public ResponseEntity<BookDto> updateBook(@PathVariable String isbn, @Validated @RequestBody BookDto bookDto) {
        log.info("Entering updateBook()");
        BookDto updatedBook = bookService.modifyBookByIsbn(isbn, bookDto);
        log.info("Leaving updateBook()");
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    @Operation(summary = "Delete a book", description = "Deletes a book from the store using its ISBN")
    @Parameter(description = "ISBN of the book to be deleted", example = "9783161484112", required = true,
            in = ParameterIn.PATH)
    @ApiResponses(
            value = { @ApiResponse(responseCode = "204", description = "Book deleted successfully", content = @Content),
                @ApiResponse(responseCode = "400", description = "Invalid ISBN",
                        content = @Content(mediaType = "application/json", schema = @Schema(example = """
                                {
                                    "statusCode": 400,
                                    "message": "BAD_REQUEST",
                                    "details": [
                                        "ISBN cannot be null or empty"
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
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        log.info("Entering removeBookByIsbn()");
        bookService.removeBookByIsbn(isbn);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}