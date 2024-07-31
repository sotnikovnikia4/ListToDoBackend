package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.sotnikov.ListToDoBackend.dto.FilterTask;

import java.time.LocalDateTime;

public class LocalDateTimePathConverter extends OneTypeConverter<DateTimePath<LocalDateTime>>{
    LocalDateTimePathConverter(OneTypeConverter<?> next) {
        super(Expressions.dateTimePath(LocalDateTime.class, "?"), next);
    }

    @Override
    protected DateTimePath<LocalDateTime> createPath(String field) {
        return Expressions.dateTimePath(LocalDateTime.class, field);
    }

    @Override
    protected BooleanExpression doEquals(DateTimePath<LocalDateTime> path, FilterTask filter) {
        return path.eq(LocalDateTime.parse(filter.getValue()));
    }

    @Override
    protected BooleanExpression doGreaterThan(DateTimePath<LocalDateTime> path, FilterTask filter) {
        return path.gt(LocalDateTime.parse(filter.getValue()));
    }

    @Override
    protected BooleanExpression doLessThan(DateTimePath<LocalDateTime> path, FilterTask filter) {
        return path.lt(LocalDateTime.parse(filter.getValue()));
    }
}
