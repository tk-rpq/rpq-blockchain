package com.example.baas.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author John
 * @create 2019/7/11 17:37
 */
@Data
@AllArgsConstructor
public class Block {
    private String txId;
    private Long blockNumber;
}
