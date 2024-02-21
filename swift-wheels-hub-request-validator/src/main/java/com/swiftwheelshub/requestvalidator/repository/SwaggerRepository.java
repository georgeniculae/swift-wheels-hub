package com.swiftwheelshub.requestvalidator.repository;

import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import org.springframework.data.repository.CrudRepository;

public interface SwaggerRepository extends CrudRepository<SwaggerFile, String> {
}
