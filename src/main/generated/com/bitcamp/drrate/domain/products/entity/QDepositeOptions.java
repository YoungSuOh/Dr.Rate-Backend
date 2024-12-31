package com.bitcamp.drrate.domain.products.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDepositeOptions is a Querydsl query type for DepositeOptions
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDepositeOptions extends EntityPathBase<DepositeOptions> {

    private static final long serialVersionUID = -1425171585L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDepositeOptions depositeOptions = new QDepositeOptions("depositeOptions");

    public final NumberPath<java.math.BigDecimal> basicRate = createNumber("basicRate", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProducts products;

    public final StringPath rateType = createString("rateType");

    public final StringPath rateTypeKo = createString("rateTypeKo");

    public final NumberPath<Integer> saveTime = createNumber("saveTime", Integer.class);

    public final NumberPath<java.math.BigDecimal> spclRate = createNumber("spclRate", java.math.BigDecimal.class);

    public QDepositeOptions(String variable) {
        this(DepositeOptions.class, forVariable(variable), INITS);
    }

    public QDepositeOptions(Path<? extends DepositeOptions> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDepositeOptions(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDepositeOptions(PathMetadata metadata, PathInits inits) {
        this(DepositeOptions.class, metadata, inits);
    }

    public QDepositeOptions(Class<? extends DepositeOptions> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.products = inits.isInitialized("products") ? new QProducts(forProperty("products")) : null;
    }

}

