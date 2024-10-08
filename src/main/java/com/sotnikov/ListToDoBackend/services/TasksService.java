package com.sotnikov.ListToDoBackend.services;

import com.querydsl.core.types.Predicate;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.QTask;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.util.filtertaskconverter.FilterTaskToPredicateConverter;
import com.sotnikov.ListToDoBackend.util.filtertaskconverter.OneTypeConverter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TasksService {

    private final TasksRepository tasksRepository;
    private final FilterTaskToPredicateConverter converterToPredicate;

    @Transactional
    public Task save(Task task, User user){
        enrich(task, user);

        return tasksRepository.insert(task);
    }

    private void enrich(Task task, User user){
        task.setUserId(user.getId());
    }

    @Transactional
    public Task update(String taskToBeUpdatedId, Task updatedTask, User user){
        Task taskToBeUpdated = getOne(taskToBeUpdatedId, user);

        taskToBeUpdated.setCompleted(updatedTask.isCompleted());
        taskToBeUpdated.setPriority(updatedTask.getPriority());
        taskToBeUpdated.setSubtasks(updatedTask.getSubtasks());
        taskToBeUpdated.setDeadline(updatedTask.getDeadline());
        taskToBeUpdated.setDescription(updatedTask.getDescription());
        taskToBeUpdated.setTag(updatedTask.getTag());
        taskToBeUpdated.setName(updatedTask.getName());

        return tasksRepository.save(taskToBeUpdated);
    }

    @Transactional
    public void delete(String taskId, User user){
        Task taskWithSameId = getOne(taskId, user);

        tasksRepository.delete(taskWithSameId);
    }

    @Transactional
    public void setCompleted(String id, Boolean completed, User user) {
        Task task = getOne(id, user);

        task.setCompleted(completed);

        tasksRepository.save(task);
    }

    public List<Task> test(UUID id) {
        QTask qTask = QTask.task;

        Predicate predicate = qTask.userId.eq(id).and(qTask.completed.eq(true));

        List<Task> tasks = new ArrayList<>();

        tasksRepository.findAll(predicate).forEach(tasks::add);

        return tasks;
    }

    public Task getOne(String id, User user){
        Optional<Task> task = tasksRepository.findById(convertToObjectId(id));

        if(task.isEmpty()){
            throw new TaskException("This task does not exist!");
        }
        else if(!task.get().getUserId().equals(user.getId())){
            throw new TaskException("The task does not belong to this user!");
        }

        return task.get();
    }

    private ObjectId convertToObjectId(String id){
        try{
            return new ObjectId(id);
        }
        catch(IllegalArgumentException e){
            throw new TaskException("A task with the id does not exist");
        }
    }

    public List<Task> getTasks(UUID userId, List<FilterTask> filters) {
        Sort sort = getSort(filters);
        addUserFilter(userId, filters);

        Predicate predicate = converterToPredicate.apply(filters);

        List<Task> tasks = new ArrayList<>();
        tasksRepository.findAll(predicate, sort).forEach(tasks::add);
        return tasks;
    }

    public Page<Task> getTasks(UUID userId, List<FilterTask> filters, Integer numberOfPage, Integer itemsPerPage) {
        Sort sort = getSort(filters);
        checkUserFilter(filters);
        addUserFilter(userId, filters);

        Predicate predicate = converterToPredicate.apply(filters);

        return tasksRepository.findAll(predicate, PageRequest.of(numberOfPage, itemsPerPage, sort));
    }

    private void checkUserFilter(List<FilterTask> filters){
        for(FilterTask f : filters){
            if(f.getField().equals("userId")){
                throw new TaskException("You cannot add user filter :)");
            }
        }
    }

    private void addUserFilter(UUID userId, List<FilterTask> filters) {
        filters.add(
                FilterTask.builder()
                        .field("userId")
                        .operator(OneTypeConverter.EQUALS)
                        .value(userId.toString())
                        .build()
        );
    }

    private Sort getSort(List<FilterTask> filters){
        Sort sort = Sort.unsorted();

        for(FilterTask f : filters){
            if(f.isSorting()){
                Sort s = Sort.by(f.getField());
                if(f.isDescendingOrder()){
                    s = s.descending();
                }
                sort = sort.and(s);
            }
        }

        return sort;
    }
}
