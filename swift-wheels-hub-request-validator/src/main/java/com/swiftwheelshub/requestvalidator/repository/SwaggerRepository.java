package com.swiftwheelshub.requestvalidator.repository;

import com.swiftwheelshub.requestvalidator.model.SwaggerFolder;
import org.springframework.data.repository.CrudRepository;

public interface SwaggerRepository extends CrudRepository<SwaggerFolder, String> {
}
