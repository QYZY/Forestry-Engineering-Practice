package com.nefu.practice.repository;

import com.nefu.practice.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public interface TaskRepository extends JpaRepository<Task,String> {

}
