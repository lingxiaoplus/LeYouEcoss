package com.lingxiao.exception;

import com.lingxiao.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LyException extends RuntimeException{
    private ExceptionEnum exceptionEnum;
}
