package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.sotnikov.ListToDoBackend.dto.FilterTask;

public class IntegerPathConverter extends OneTypeConverter<NumberPath<Integer>>{
    IntegerPathConverter(OneTypeConverter<?> next) {
        super(Expressions.numberPath(Integer.class, "?"), next);
    }

    @Override
    protected NumberPath<Integer> createPath(String field) {
        return Expressions.numberPath(Integer.class, field);
    }

    @Override
    protected BooleanExpression doEquals(NumberPath<Integer> path, FilterTask filter) {
        return path.eq(Integer.parseInt(filter.getValue()));
    }

    @Override
    protected BooleanExpression doLessThan(NumberPath<Integer> path, FilterTask filter) {
        return path.lt(Integer.parseInt(filter.getValue()));
    }

    @Override
    protected BooleanExpression doGreaterThan(NumberPath<Integer> path, FilterTask filter) {
        return path.gt(Integer.parseInt(filter.getValue()));
    }
}
