package com.sotnikov.ListToDoBackend.services;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Subtask;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.util.filtertaskconverter.FilterTaskToPredicateConverter;
import com.sotnikov.ListToDoBackend.util.filtertaskconverter.OneTypeConverter;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {
    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private FilterTaskToPredicateConverter converterToPredicate;

    @InjectMocks
    private TasksService tasksService;

    private static User authenticatedUser;
    private static List<Task> tasks;

    private Task task;

    @BeforeAll
    static void setUpBeforeAll(){
        authenticatedUser = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .name("John")
                .login("123")
                .password("123")
                .build();

        Task task1 = Task.builder()
                .name("task1")
                .userId(authenticatedUser.getId())
                .build();

        Task task2 = Task.builder()
                .name("task2")
                .userId(authenticatedUser.getId())
                .build();
        Task task3 = Task.builder()
                .name("task3")
                .userId(authenticatedUser.getId())
                .completed(true)
                .priority(1)
                .build();

        tasks = List.of(task1, task2, task3);
    }

    @BeforeEach
    public void setUp(){
        String taskId = ObjectId.get().toHexString();

        task = Task.builder()
                .id(taskId)
                .description("description")
                .completed(true)
                .priority(1)
                .name("name")
                .userId(authenticatedUser.getId())
                .deadline(LocalDateTime.now())
                .subtasks(List.of(Subtask.builder().name("subtask").build()))
                .tag("tag")
                .build();
    }

    @Test
    void testGetOneWithAuthenticatedUser_ShouldReturnNotNull() {
        Task task = Task.builder().userId(authenticatedUser.getId()).build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));

        Task foundTask = tasksService.getOne(ObjectId.get().toHexString(), authenticatedUser);

        Assertions.assertThat(foundTask).isNotNull();
    }

    @Test
    void testGetOneWithNotAuthenticatedUser_ShouldThrowTaskException() {
        Task task = Task.builder().userId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4")).build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));

        TaskException e = assertThrows(TaskException.class, () -> tasksService.getOne(ObjectId.get().toHexString(), authenticatedUser));
        assertEquals("The task does not belong to this user!", e.getMessage());
    }

    @Test
    void testGetOneWhenTaskDoesNotExist_ShouldThrowTaskException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        TaskException e = assertThrows(TaskException.class, () -> tasksService.getOne(ObjectId.get().toHexString(), authenticatedUser));
        assertEquals("This task does not exist!", e.getMessage());
    }

    @Test
    void testSave_ShouldReturnNotNull() {
        when(tasksRepository.insert(Mockito.any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = Task.builder()
                .name("task")
                .build();
        task = tasksService.save(task, authenticatedUser);

        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getUserId()).isEqualTo(authenticatedUser.getId());
    }

    @Test
    void testUpdateWithAuthenticatedUser_ShouldReturnUpdatedTask() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(Task.builder().id(task.getId()).userId(task.getUserId()).build()));
        when(tasksRepository.save(Mockito.any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = tasksService.update(task.getId(), task, authenticatedUser);

        Assertions.assertThat(updatedTask).isEqualTo(task);
    }

    @Test
    void testUpdateWithNotAuthenticatedUser_ShouldThrowTaskException() {
        task.setUserId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4"));

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(Task.builder().id(task.getId()).userId(task.getUserId()).build()));

        TaskException e = assertThrows(TaskException.class, () -> tasksService.update(task.getId(), task, authenticatedUser));
        assertEquals("The task does not belong to this user!", e.getMessage());
    }

    @Test
    void testUpdateWhenTaskDoesNotExist_ShouldThrowTaskException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        TaskException e = assertThrows(TaskException.class, () -> tasksService.update(task.getId(), task, authenticatedUser));
        assertEquals("This task does not exist!", e.getMessage());
    }

    @Test
    void testDeleteWhenTaskExist_NotShouldThrowException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));
        doNothing().when(tasksRepository).delete(ArgumentMatchers.any());

        assertDoesNotThrow(() -> tasksService.delete(task.getId(), authenticatedUser));
    }

    @Test
    void testDeleteWhenTaskDoesNotExist_ShouldThrowException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        assertThrows(TaskException.class, () -> tasksService.delete(task.getId(), authenticatedUser));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSetCompleted_ReturnsNothing(boolean completed){
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));

        tasksService.setCompleted(task.getId(), completed, authenticatedUser);

        assertEquals(completed, task.isCompleted());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSetCompleted_ShouldThrowException(boolean completed){
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        assertThrows(TaskException.class, () -> tasksService.setCompleted(task.getId(), completed, authenticatedUser));
    }

    @Test
    void testGetTasksWithNoPagination_ShouldReturnList(){
        when(converterToPredicate.apply(any())).thenReturn(Expressions.stringPath("name").eq("name"));
        when(tasksRepository.findAll(any(Predicate.class), any(Sort.class))).thenReturn(tasks);

        List<Task> result = tasksService.getTasks(authenticatedUser.getId(), new ArrayList<>());
        assertEquals(tasks.size(), result.size());
    }

    @Test
    void testGetTasksWithNoPagination_ShouldReturnPage(){
        when(converterToPredicate.apply(any())).thenReturn(Expressions.stringPath("name").eq("name"));
        when(tasksRepository.findAll(any(Predicate.class), any(Pageable.class))).thenAnswer(
                e -> new PageImpl<>(tasks, PageRequest.of(0, tasks.size()), tasks.size())
        );

        Page<Task> result = tasksService.getTasks(authenticatedUser.getId(), new ArrayList<>(), 0, tasks.size());
        assertEquals(tasks.size(), result.getSize());
    }

    @Test
    void testAddUserFilter_ShouldAddFilterToList() throws Exception{
        Method method = tasksService.getClass().getDeclaredMethod("addUserFilter", UUID.class, List.class);
        method.setAccessible(true);

        List<FilterTask> filters = new ArrayList<>();
        method.invoke(tasksService, authenticatedUser.getId(), filters);

        assertEquals(1, filters.size());
        assertEquals("userId", filters.get(0).getField());
        assertEquals(OneTypeConverter.EQUALS, filters.get(0).getOperator());
        assertEquals(authenticatedUser.getId().toString(), filters.get(0).getValue());
    }

    @Test
    void testGetSortWhenIsNotSortingField_ShouldReturnUnsorted() throws Exception{
        Method method = tasksService.getClass().getDeclaredMethod("getSort", List.class);
        method.setAccessible(true);

        List<FilterTask> filters = new ArrayList<>();
        filters.add(FilterTask.builder()
                        .field("field")
                        .sorting(false)
                        .descendingOrder(false)
                        .value("value")
                        .operator(OneTypeConverter.NONE)
                .build());

        Sort sort = (Sort)method.invoke(tasksService, filters);

        assertTrue(sort.isUnsorted());
    }
    @Test
    void testGetSort_ShouldReturnSortedInAscendingOrder() throws Exception{
        Method method = tasksService.getClass().getDeclaredMethod("getSort", List.class);
        method.setAccessible(true);

        List<FilterTask> filters = new ArrayList<>();
        filters.add(FilterTask.builder()
                .field("field")
                .sorting(true)
                .descendingOrder(false)
                .value("value")
                .operator(OneTypeConverter.NONE)
                .build());

        Sort sort = (Sort)method.invoke(tasksService, filters);

        assertTrue(sort.isSorted());
        assertEquals(Sort.Order.asc("field"), sort.getOrderFor("field"));
    }

    @Test
    void testGetSort_ShouldReturnSortedInDescendingOrder() throws Exception{
        Method method = tasksService.getClass().getDeclaredMethod("getSort", List.class);
        method.setAccessible(true);

        List<FilterTask> filters = new ArrayList<>();
        filters.add(FilterTask.builder()
                .field("field")
                .sorting(true)
                .descendingOrder(true)
                .value("value")
                .operator(OneTypeConverter.NONE)
                .build());

        Sort sort = (Sort)method.invoke(tasksService, filters);

        assertTrue(sort.isSorted());
        assertEquals(Sort.Order.desc("field"), sort.getOrderFor("field"));
    }
}