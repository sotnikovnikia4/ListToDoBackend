package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public abstract class OneTypeConverter<T extends Path<?>>{
    public static final String CONTAINS = "CONTAINS";
    public static final String EQUALS = "EQUALS";
    public static final String LESS_THAN = "LESS_THAN";
    public static final String GREATER_THAN = "GREATER_THAN";
    public static final String CONTAINS_IGNORE_CASE = "CONTAINS_IGNORE_CASE";
    public static final String EQUALS_IGNORE_CASE = "EQUALS_IGNORE_CASE";
    public static final String NONE = "NONE";

    private final Class<T> pathType;
    private final Class<?> typeInPath;
    private final OneTypeConverter<?> next;

    public OneTypeConverter(T object,  OneTypeConverter<?> next){
        this.pathType = (Class<T>) object.getClass();
        this.next = next;
        this.typeInPath = object.getType();
    }

    public final BooleanExpression convert(Field field, FilterTask filter, BooleanExpression predicate){

        if(field.getType().isAssignableFrom(pathType)){
            T path = createPath(filter.getField());

            if(!path.getType().equals(typeInPath)){
                throwExceptionOperationIsNotSupported(filter);
            }

            switch (filter.getOperator()){
                case CONTAINS:{
                    predicate = doSome(predicate, () -> doContains(path, filter));
                    break;
                }
                case EQUALS:{
                    predicate = doSome(predicate, () -> doEquals(path, filter));
                    break;
                }
                case LESS_THAN:{
                    predicate = doSome(predicate, () -> doLessThan(path, filter));
                    break;
                }
                case GREATER_THAN:{
                    predicate = doSome(predicate, () -> doGreaterThan(path, filter));
                    break;
                }
                case EQUALS_IGNORE_CASE:{
                    predicate = doSome(predicate, () -> doEqualsIgnoreCase(path, filter));
                    break;
                }
                case CONTAINS_IGNORE_CASE:{
                    predicate = doSome(predicate, () -> doContainsIgnoreCase(path, filter));
                    break;
                }
                case NONE:{
                    break;
                }
                default:{
                    throw new TaskException("This operator does not exist: " + filter.getOperator());
                }
            }
        }
        else{
            if(next == null)
                throw new TaskException("The type of the field is not supported");
            predicate = next.convert(field, filter, predicate);
        }

        return predicate;
    }

    private BooleanExpression doSome(BooleanExpression predicate, Supplier<BooleanExpression> func){
        if(predicate == null){
            predicate = func.get();
        }
        else{
            predicate = predicate.and(func.get());
        }

        return predicate;
    }

    protected abstract T createPath(String field);
    protected BooleanExpression doContains(T path, FilterTask filter){
        return throwExceptionOperationIsNotSupported(filter);
    }
    protected BooleanExpression doEquals(T path, FilterTask filter){
        return throwExceptionOperationIsNotSupported(filter);
    }
    protected BooleanExpression doLessThan(T path, FilterTask filter){
        return throwExceptionOperationIsNotSupported(filter);
    }
    protected BooleanExpression doGreaterThan(T path, FilterTask filter){
        return throwExceptionOperationIsNotSupported(filter);
    }
    protected BooleanExpression doEqualsIgnoreCase(T path, FilterTask filter){
        return throwExceptionOperationIsNotSupported(filter);
    }
    protected BooleanExpression doContainsIgnoreCase(T path, FilterTask filter){
        return throwExceptionOperationIsNotSupported(filter);
    }

    private BooleanExpression throwExceptionOperationIsNotSupported(FilterTask filter){
        throw new TaskException(String.format("Operation '%s' is not compatible with field '%s'", filter.getOperator(), filter.getField()));
    }
}
