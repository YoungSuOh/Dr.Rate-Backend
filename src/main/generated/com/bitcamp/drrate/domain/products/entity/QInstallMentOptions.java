package com.bitcamp.drrate.domain.products.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstallMentOptions is a Querydsl query type for InstallMentOptions
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstallMentOptions extends EntityPathBase<InstallMentOptions> {

    private static final long serialVersionUID = -424575651L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstallMentOptions installMentOptions = new QInstallMentOptions("installMentOptions");

    public final NumberPath<java.math.BigDecimal> basicRate = createNumber("basicRate", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProducts products;

    public final StringPath rateType = createString("rateType");

    public final StringPath rateTypeKo = createString("rateTypeKo");

    public final StringPath rsrvType = createString("rsrvType");

    public final StringPath rsrvTypeName = createString("rsrvTypeName");

    public final NumberPath<Integer> saveTime = createNumber("saveTime", Integer.class);

    public final NumberPath<java.math.BigDecimal> spclRate = createNumber("spclRate", java.math.BigDecimal.class);

    public QInstallMentOptions(String variable) {
        this(InstallMentOptions.class, forVariable(variable), INITS);
    }

    public QInstallMentOptions(Path<? extends InstallMentOptions> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstallMentOptions(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstallMentOptions(PathMetadata metadata, PathInits inits) {
        this(InstallMentOptions.class, metadata, inits);
    }

    public QInstallMentOptions(Class<? extends InstallMentOptions> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.products = inits.isInitialized("products") ? new QProducts(forProperty("products")) : null;
    }

}

