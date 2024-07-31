package com.sotnikov.ListToDoBackend.util;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.QTask;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Component("filterTaskConverter")
public class FilterTaskToPredicateConverter implements Function<List<FilterTask>, Predicate> {

    public final String CONTAINS = "CONTAINS";
    public final String EQUALS = "EQUALS";
    public final String LESS_THAN = "LESS_THAN";
    public final String GREATER_THAN = "GREATER_THAN";

    private final QTask qTask = QTask.task;
    private final OneTypeConverter<StringPath> converter;

    public FilterTaskToPredicateConverter(){
        OneTypeConverter<Object> failConverter = new OneTypeConverter<>(Object.class, null) {
            @Override
            protected Object createPath(String field) {
                throw new TaskException("The type of the field is not supported");
            }
        };
        OneTypeConverter<ComparablePath> uuidConverter = new OneTypeConverter<>(ComparablePath.class, failConverter) {
            @Override
            protected ComparablePath createPath(String field) {
                return Expressions.comparablePath(UUID.class, field);
            }

            @Override
            protected BooleanExpression doEquals(ComparablePath path, FilterTask filter) {
                return path.eq(UUID.fromString(filter.getValue()));
            }
        };
        OneTypeConverter<BooleanPath> booleanConverter = new OneTypeConverter<>(BooleanPath.class, uuidConverter) {
            @Override
            protected BooleanPath createPath(String field) {
                return Expressions.booleanPath(field);
            }

            @Override
            protected BooleanExpression doEquals(BooleanPath path, FilterTask filter) {
                return path.eq(Boolean.parseBoolean(filter.getValue()));
            }
        };
        OneTypeConverter<DateTimePath> dateTimeConverter = new OneTypeConverter<>(DateTimePath.class, booleanConverter) {
            @Override
            protected DateTimePath createPath(String field) {
                return Expressions.dateTimePath(LocalDateTime.class, field);
            }

            @Override
            protected BooleanExpression doEquals(DateTimePath path, FilterTask filter) {
                return path.eq(LocalDateTime.parse(filter.getValue()));
            }
        };
        OneTypeConverter<NumberPath> numberConverter = new OneTypeConverter<>(NumberPath.class, dateTimeConverter) {
            @Override
            protected NumberPath createPath(String field) {
                return Expressions.numberPath(Integer.class, field);
            }

            @Override
            protected BooleanExpression doEquals(NumberPath path, FilterTask filter) {
                return path.eq(Integer.parseInt(filter.getValue()));
            }
        };
        this.converter = new OneTypeConverter<>(StringPath.class, numberConverter) {
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
        };
    }

    @Override
    public Predicate apply(List<FilterTask> filters) {
        BooleanExpression predicate = null;

        for(FilterTask filter : filters){
            checkFilter(filter);
            predicate = tryConvertOneFilter(filter, predicate);
        }

        return predicate;
    }

    private void checkFilter(FilterTask filter){
        if(filter.getField() == null || filter.getOperator() == null || filter.getValue() == null){
            throw new TaskException("field, operator, value are not supposed to be null");
        }
    }

    private BooleanExpression tryConvertOneFilter(FilterTask filter, BooleanExpression predicate) throws TaskException {
        try {
            Field field = qTask.getClass().getField(filter.getField());

            return converter.convert(field, filter, predicate);
        }
        catch (TaskException e){
            throw e;
        }
        catch (NoSuchFieldException e){
            throw new TaskException("The field does not exist: " + e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

//    private BooleanExpression convertOneFilter(FilterTask filter, BooleanExpression predicate) throws Exception{
//        Field field = qTask.getClass().getField(filter.getField());
//
//        switch (filter.getOperator()){
//            case CONTAINS:{
//                if(!field.getType().isAssignableFrom(StringPath.class)){
//                    throw new TaskException("This field is not compatible with this operation, type of field should be \"String\"");
//                }
//
//                StringPath path = Expressions.stringPath(filter.getField());
//                if(predicate == null)
//                    predicate = path.containsIgnoreCase(filter.getValue());
//                else
//                    predicate = predicate.and(path.containsIgnoreCase(filter.getValue()));
//                break;
//            }
//            case EQUALS:{
//                if(field.getType().isAssignableFrom(StringPath.class)){
//                    StringPath path = Expressions.stringPath(filter.getField());
//                    if(predicate == null)
//                        predicate = path.equalsIgnoreCase(filter.getValue().toLowerCase());
//                    else
//                        predicate = predicate.and(path.equalsIgnoreCase(filter.getValue().toLowerCase()));
//                }
//                else if(field.getType().isAssignableFrom(NumberPath.class)){
//                    NumberPath<Integer> path = Expressions.numberPath(Integer.class, filter.getField());
//                    if(predicate == null)
//                        predicate = path.eq(Integer.parseInt(filter.getValue()));
//                    else
//                        predicate = predicate.and(path.eq(Integer.parseInt(filter.getValue())));
//                }
//                else if(field.getType().isAssignableFrom((DateTimePath.class))){
//                    DateTimePath<LocalDateTime> path = Expressions.dateTimePath(LocalDateTime.class, filter.getField());
//                    if(predicate == null)
//                        predicate = path.eq(LocalDateTime.parse(filter.getValue()));
//                    else
//                        predicate = predicate.and(path.eq(LocalDateTime.parse(filter.getValue())));
//                }
//                else if(field.getType().isAssignableFrom(BooleanPath.class)){
//                    BooleanPath path = Expressions.booleanPath(filter.getField());
//                    if(predicate == null)
//                        predicate = path.eq(Boolean.parseBoolean(filter.getValue()));
//                    else
//                        predicate = predicate.and(path.eq(Boolean.parseBoolean(filter.getValue())));
//                }
//                else if(field.getType().isAssignableFrom(ComparablePath.class)){
//                    ComparablePath<UUID> path = Expressions.comparablePath(UUID.class, qTask, filter.getField());
//                    if(predicate == null)
//                        predicate = path.eq(UUID.fromString(filter.getValue()));
//                    else
//                        predicate = predicate.and(path.eq(UUID.fromString(filter.getValue())));
//                }
//                else{
//                    throw new TaskException("This operation is not compatible with this type of the field");
//                }
//
//                break;
//            }
//            default:{
//                throw new TaskException("Bad operationType");
//            }
//        }
//
//        return predicate;
//    }

    private abstract class OneTypeConverter<T>{
        private final Class<T> cls;
        private final OneTypeConverter<?> next;

        public OneTypeConverter(Class<T> cls, OneTypeConverter<?> next){
            this.cls = cls;
            this.next = next;
        }

        public final BooleanExpression convert(Field field, FilterTask filter, BooleanExpression predicate){

            if(field.getType().isAssignableFrom(cls)){
                T path = createPath(filter.getField());

                switch (filter.getOperator()){
                    case CONTAINS:{
                        predicate = doSome(predicate, () -> doContains(path, filter));
                        break;
                    }
                    case EQUALS:{
                        predicate = doSome(predicate, () -> doEquals(path, filter));
                        break;
                    }
                    default:{
                        throw new TaskException("This operation does not exist: " + filter.getOperator());
                    }
                }
            }
            else{
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
            return throwException(filter);
        }
        protected BooleanExpression doEquals(T path, FilterTask filter){
            return throwException(filter);
        }

        private BooleanExpression throwException(FilterTask filter){
            throw new TaskException(String.format("Operation \"%s\" is not compatible with field \"%s\"", filter.getOperator(), filter.getField()));
        }
    }
}
