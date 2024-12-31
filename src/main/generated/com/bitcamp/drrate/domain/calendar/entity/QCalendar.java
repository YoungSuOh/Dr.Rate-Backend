package com.bitcamp.drrate.domain.calendar.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCalendar is a Querydsl query type for Calendar
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCalendar extends EntityPathBase<Calendar> {

    private static final long serialVersionUID = 1419060656L;

    public static final QCalendar calendar = new QCalendar("calendar");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath bank_name = createString("bank_name");

    public final NumberPath<Long> cal_user_id = createNumber("cal_user_id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> created_at = createDateTime("created_at", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> end_date = createDate("end_date", java.time.LocalDate.class);

    public final StringPath groupId = createString("groupId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath installment_name = createString("installment_name");

    public final DatePath<java.time.LocalDate> start_date = createDate("start_date", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> updated_at = createDateTime("updated_at", java.time.LocalDateTime.class);

    public QCalendar(String variable) {
        super(Calendar.class, forVariable(variable));
    }

    public QCalendar(Path<? extends Calendar> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCalendar(PathMetadata metadata) {
        super(Calendar.class, metadata);
    }

}

