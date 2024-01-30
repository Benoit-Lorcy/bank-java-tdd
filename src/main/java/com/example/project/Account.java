package com.example.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class Account {
    private String accountId;
    private Float balance;
    private String ownerId;
}
