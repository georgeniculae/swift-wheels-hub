package com.carrental.requestvalidator.repository;

import com.carrental.requestvalidator.model.SwaggerFolder;
import org.springframework.data.repository.CrudRepository;

public interface SwaggerRepository extends CrudRepository<SwaggerFolder, String> {
}
