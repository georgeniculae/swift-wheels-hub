package com.swiftwheelshub.emailnotification.model;

import com.swiftwheelshub.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "customer_details", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CustomerDetails extends BaseEntity {

    @NonNull
    private String username;

    @NonNull
    private String email;

    public CustomerDetails(@NotNull String email) {
        this.email = email;
    }

}
