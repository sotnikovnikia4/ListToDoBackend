package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.Expressions;
import com.sotnikov.ListToDoBackend.dto.FilterTask;

public class BooleanPathConverter extends OneTypeConverter<BooleanPath> {
    BooleanPathConverter(OneTypeConverter<?> next) {
        super(Expressions.booleanPath("?"), next);
    }

    @Override
    protected BooleanPath createPath(String field) {
        return Expressions.booleanPath(field);
    }

    @Override
    protected BooleanExpression doEquals(BooleanPath path, FilterTask filter) {
        return path.eq(Boolean.parseBoolean(filter.getValue()));
    }
}
