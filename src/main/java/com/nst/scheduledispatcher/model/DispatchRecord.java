package com.nst.scheduledispatcher.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "scheduled-dispatch")
@Data
@RequiredArgsConstructor
public class DispatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    final String message;

    final Date timestamp;

    final String threadPrefix;

    final String destination;

}
