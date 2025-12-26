package com.nefu.practice.repository;

import com.nefu.practice.entity.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

}
