package com.bitcamp.drrate.domain.visitor.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDailyVisitor is a Querydsl query type for DailyVisitor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyVisitor extends EntityPathBase<DailyVisitor> {

    private static final long serialVersionUID = 1247562793L;

    public static final QDailyVisitor dailyVisitor = new QDailyVisitor("dailyVisitor");

    public final NumberPath<Integer> guestVisitorsCount = createNumber("guestVisitorsCount", Integer.class);

    public final NumberPath<Integer> memberVisitorsCount = createNumber("memberVisitorsCount", Integer.class);

    public final NumberPath<Integer> newMembersCount = createNumber("newMembersCount", Integer.class);

    public final NumberPath<Integer> totalVisitorsCount = createNumber("totalVisitorsCount", Integer.class);

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QDailyVisitor(String variable) {
        super(DailyVisitor.class, forVariable(variable));
    }

    public QDailyVisitor(Path<? extends DailyVisitor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDailyVisitor(PathMetadata metadata) {
        super(DailyVisitor.class, metadata);
    }

}

