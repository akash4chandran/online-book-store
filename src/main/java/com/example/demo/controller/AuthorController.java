package com.example.demo.controller;

import com.example.demo.dto.AuthorDto;
import com.example.demo.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Author Collection")
@Slf4j
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/{author-id}")
    @Operation(summary = "Get author by ID", description = "Retrieves an author by their ID")
    @Parameter(description = "ID of the author to retrieve", example = "1", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Author retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "id": 2,
                            "name": "Author Two"
                        }
                        """))),
        @ApiResponse(responseCode = "404", description = "Author not found",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 404,
                            "message": "NOT_FOUND",
                            "details": [
                                "Author not found for the given ID"
                            ]
                        }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid ID format",
                content = @Content(mediaType = "application/json", schema = @Schema(example = """
                        {
                            "statusCode": 400,
                            "message": "BAD_REQUEST",
                            "details": [
                                "Invalid ID format"
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
                        """)))

    })
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable(name = "author-id") Long authorId) {
        log.info("Entering getAuthorById()");
        AuthorDto authorDto = authorService.findAuthorById(authorId);
        log.info("Leaving getAuthorById()");
        return new ResponseEntity<>(authorDto, HttpStatus.OK);
    }
}
