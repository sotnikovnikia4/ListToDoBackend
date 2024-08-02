package com.sotnikov.ListToDoBackend.util.filtertaskconverter;

import com.querydsl.core.types.Predicate;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.QTask;
import com.sotnikov.ListToDoBackend.models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilterTaskToPredicateConverterTest {

    private static FilterTaskToPredicateConverter converter;
    private FilterTask filter;

    @BeforeAll
    static void setUpBeforeAll(){
        converter = new FilterTaskToPredicateConverter();
    }

    @BeforeEach
    void setUp(){
        filter = FilterTask.builder()
                .field("name")
                .value("value")
                .operator(OneTypeConverter.EQUALS)
                .build();
    }

    @Test
    void testApply_ShouldReturnNull() {
        Predicate predicate = converter.apply(Collections.emptyList());

        assertNull(predicate);
    }

    @Test
    void testApply_ShouldReturnNotNull() {
        Predicate predicate = converter.apply(List.of(filter));

        assertNotNull(predicate);
    }

    @Test
    void testCheckFilter_ShouldThrowException() throws Exception{
        Method method = converter.getClass().getDeclaredMethod("checkFilter", FilterTask.class);
        method.setAccessible(true);
        filter.setField(null);

        assertThrows(Exception.class, () -> method.invoke(converter, filter));
    }

    @Test
    void testCheckFilter_ShouldNotThrowException() throws Exception{
        Method method = converter.getClass().getDeclaredMethod("checkFilter", FilterTask.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(converter, filter));
    }
}