package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.Expressions;
import com.sotnikov.ListToDoBackend.dto.FilterTask;

import java.util.UUID;

public class UUIDPathConverter extends OneTypeConverter<ComparablePath<UUID>>{
    UUIDPathConverter(OneTypeConverter<?> next) {
        super(Expressions.comparablePath(UUID.class, "?"), next);
    }

    @Override
    protected ComparablePath<UUID> createPath(String field) {
        return Expressions.comparablePath(UUID.class, field);
    }

    @Override
    protected BooleanExpression doEquals(ComparablePath<UUID> path, FilterTask filter) {
        return path.eq(UUID.fromString(filter.getValue()));
    }
}
