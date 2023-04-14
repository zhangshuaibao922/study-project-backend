package com.example.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "db_account")
public class Account {
    int id;
    String username;
    String password;
    String email;
}
