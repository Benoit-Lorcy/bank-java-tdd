package com.example.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class Check {
    private String checkId;
    private Float amount;
    private String accountId;
    private Boolean isValidated;
}
