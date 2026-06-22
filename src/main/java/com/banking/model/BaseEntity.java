package com.banking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;


@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
