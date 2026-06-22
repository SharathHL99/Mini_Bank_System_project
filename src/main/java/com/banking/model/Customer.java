package com.banking.model;

import java.util.List;
import com.banking.enums.CustomerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @OneToMany(mappedBy = "customer")
    private List<Account> accounts;
}