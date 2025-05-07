package com.autohub.requestvalidator.repository;

import com.autohub.requestvalidator.model.SwaggerFile;
import org.springframework.data.repository.CrudRepository;

public interface SwaggerRepository extends CrudRepository<SwaggerFile, String> {
}
