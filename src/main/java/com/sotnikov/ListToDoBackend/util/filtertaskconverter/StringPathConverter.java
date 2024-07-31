package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.sotnikov.ListToDoBackend.dto.FilterTask;

public class StringPathConverter extends OneTypeConverter<StringPath>{
    StringPathConverter(OneTypeConverter<?> next) {
        super(Expressions.stringPath( "?"), next);
    }

    @Override
    protected StringPath createPath(String field) {
        return Expressions.stringPath(field);
    }

    @Override
    protected BooleanExpression doEquals(StringPath path, FilterTask filter) {
        return path.eq(filter.getValue());
    }

    @Override
    protected BooleanExpression doContains(StringPath path, FilterTask filter) {
        return path.contains(filter.getValue());
    }

    @Override
    protected BooleanExpression doContainsIgnoreCase(StringPath path, FilterTask filter) {
        return path.containsIgnoreCase(filter.getValue());
    }

    @Override
    protected BooleanExpression doEqualsIgnoreCase(StringPath path, FilterTask filter) {
        return path.equalsIgnoreCase(filter.getValue());
    }
}
