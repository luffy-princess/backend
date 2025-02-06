package com.phishme.backend.dto;

import java.util.List;

import com.phishme.backend.entities.Terms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TermResponse {
    private final List<Terms> terms;
}
