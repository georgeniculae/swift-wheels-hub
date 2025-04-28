package com.autohub.requestvalidator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("SwaggerFile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerFile implements Serializable {

    @Id
    private String identifier;
    private String swaggerContent;

}
