package com.bitcamp.drrate.domain.products.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProducts is a Querydsl query type for Products
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProducts extends EntityPathBase<Products> {

    private static final long serialVersionUID = -755382788L;

    public static final QProducts products = new QProducts("products");

    public final NumberPath<Long> bankCo = createNumber("bankCo", Long.class);

    public final StringPath bankLogo = createString("bankLogo");

    public final StringPath bankName = createString("bankName");

    public final StringPath ctg = createString("ctg");

    public final ListPath<DepositeOptions, QDepositeOptions> depOptions = this.<DepositeOptions, QDepositeOptions>createList("depOptions", DepositeOptions.class, QDepositeOptions.class, PathInits.DIRECT2);

    public final NumberPath<Integer> etc = createNumber("etc", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<InstallMentOptions, QInstallMentOptions> insOptions = this.<InstallMentOptions, QInstallMentOptions>createList("insOptions", InstallMentOptions.class, QInstallMentOptions.class, PathInits.DIRECT2);

    public final NumberPath<Integer> joinMemberAge = createNumber("joinMemberAge", Integer.class);

    public final StringPath joinWay = createString("joinWay");

    public final NumberPath<Long> max = createNumber("max", Long.class);

    public final StringPath mtrtInt = createString("mtrtInt");

    public final StringPath prdCo = createString("prdCo");

    public final StringPath prdName = createString("prdName");

    public final StringPath spclCnd = createString("spclCnd");

    public final StringPath url = createString("url");

    public QProducts(String variable) {
        super(Products.class, forVariable(variable));
    }

    public QProducts(Path<? extends Products> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProducts(PathMetadata metadata) {
        super(Products.class, metadata);
    }

}

