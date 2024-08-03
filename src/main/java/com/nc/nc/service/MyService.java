package com.nc.nc.service;

import com.nc.nc.entities.TaskEntity;
import com.nc.nc.entities.UserEntity;
import com.nc.nc.model.TaskModel;
import com.nc.nc.model.UserModel;
import com.nc.nc.repository.LoginRepository;
import com.nc.nc.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class MyService {
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired(required = true)
    ModelMapper modelMapper;
    public static Logger logger = LoggerFactory.getLogger(MyService.class);

    public ResponseEntity signIn(String name, String pass) {
        UserEntity userEntity = loginRepository.findByUsernameAndPassword(name, pass);
        if (userEntity != null) {
            HashMap<String, String> map = new HashMap();
            map.put("userId", userEntity.getUser_id().toString());
            map.put("msg", "Login Successful");
            logger.debug("this is for testing purpose");
            return ResponseEntity.ok(map);
        } else {
            logger.debug("not found");
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity singUp(UserModel userModel) {
        UserEntity userEntity = modelMapper.map(userModel, UserEntity.class);
        logger.info("usr model is " + userModel.toString());
        UserEntity user = loginRepository.save(userEntity);
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", user.getUser_id().toString());
        map.put("msg", "Sign up successful");
        return ResponseEntity.ok(map);
    }

    public ResponseEntity createTask(TaskModel taskModel) {
        TaskEntity taskEntity = modelMapper.map(taskModel, TaskEntity.class);
        taskRepository.save(taskEntity);
        return ResponseEntity.ok("Created");
    }

    public List<TaskModel> getTasks(Long userId) {
        List<TaskEntity> tasksList = taskRepository.findAllByUser(userId);
        logger.info("list of task entity is " + tasksList);
        return modelMapper.map(tasksList, List.class);
    }

    public List<TaskModel> getFilteredData(String status, Long userId) {
        List<TaskEntity> taskEntityList=  taskRepository.findAllByUserAndStatus(userId, status);
        logger.info(status+"  list is :=>"+taskEntityList);
        return modelMapper.map(taskEntityList,List.class);
    }

    public ResponseEntity updateStatus(Long taskId, String status) throws ChangeSetPersister.NotFoundException {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        taskEntity.setStatus(status);
        taskRepository.save(taskEntity);
        return ResponseEntity.ok("Task Updated successfully");
    }

    public ResponseEntity deleteStatus(Long taskId) throws ChangeSetPersister.NotFoundException {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        taskRepository.delete(taskEntity);
        return ResponseEntity.ok("Task delete successfully");

    }
}
