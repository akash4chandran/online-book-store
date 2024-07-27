package com.example.demo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBook is a Querydsl query type for Book
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBook extends EntityPathBase<Book> {

    private static final long serialVersionUID = 133226106L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBook book = new QBook("book");

    public final QAuthor author;

    public final StringPath isbn = createString("isbn");

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public QBook(String variable) {
        this(Book.class, forVariable(variable), INITS);
    }

    public QBook(Path<? extends Book> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBook(PathMetadata metadata, PathInits inits) {
        this(Book.class, metadata, inits);
    }

    public QBook(Class<? extends Book> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QAuthor(forProperty("author")) : null;
    }

}

