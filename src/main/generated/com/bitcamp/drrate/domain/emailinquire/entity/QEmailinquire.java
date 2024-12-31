package com.bitcamp.drrate.domain.emailinquire.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmailinquire is a Querydsl query type for Emailinquire
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailinquire extends EntityPathBase<Emailinquire> {

    private static final long serialVersionUID = 1184835938L;

    public static final QEmailinquire emailinquire = new QEmailinquire("emailinquire");

    public final com.bitcamp.drrate.global.entity.QBaseEntity _super = new com.bitcamp.drrate.global.entity.QBaseEntity(this);

    public final StringPath answerContent = createString("answerContent");

    public final StringPath answerFile = createString("answerFile");

    public final StringPath answerTitle = createString("answerTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath fileUuid = createString("fileUuid");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inquireContent = createString("inquireContent");

    public final StringPath inquireCtg = createString("inquireCtg");

    public final StringPath inquireEmail = createString("inquireEmail");

    public final NumberPath<Long> inquireId = createNumber("inquireId", Long.class);

    public final StringPath inquireTitle = createString("inquireTitle");

    public final StringPath inquireUser = createString("inquireUser");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QEmailinquire(String variable) {
        super(Emailinquire.class, forVariable(variable));
    }

    public QEmailinquire(Path<? extends Emailinquire> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmailinquire(PathMetadata metadata) {
        super(Emailinquire.class, metadata);
    }

}

