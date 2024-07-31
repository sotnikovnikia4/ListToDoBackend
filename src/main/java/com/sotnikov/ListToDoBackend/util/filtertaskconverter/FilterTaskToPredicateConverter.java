package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.QTask;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;

@Component("filterTaskConverter")
public class FilterTaskToPredicateConverter implements Function<List<FilterTask>, Predicate> {

    private final QTask qTask = QTask.task;
    private final OneTypeConverter<?> converter;

    public FilterTaskToPredicateConverter(){
        OneTypeConverter<?> uuidConverter = new UUIDPathConverter(null);
        OneTypeConverter<?> booleanConverter = new BooleanPathConverter(uuidConverter);
        OneTypeConverter<?> dateTimeConverter = new LocalDateTimePathConverter(booleanConverter);
        OneTypeConverter<?> numberConverter = new IntegerPathConverter(dateTimeConverter);
        this.converter = new StringPathConverter(numberConverter);
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
        catch (NumberFormatException e){
            throw new TaskException("Value is not a number for field of numeric type");
        }
        catch (DateTimeParseException e){
            throw new TaskException("Error in parsing value to localDateTime format");
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
