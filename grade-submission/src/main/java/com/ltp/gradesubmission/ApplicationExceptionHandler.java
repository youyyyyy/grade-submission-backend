package com.ltp.gradesubmission;


import com.ltp.gradesubmission.exception.CourseNotFoundException;
import com.ltp.gradesubmission.exception.ErrorResponse;
import com.ltp.gradesubmission.exception.GradeNotFoundException;
import com.ltp.gradesubmission.exception.StudentNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMsg = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errorMsg.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(new ErrorResponse(errorMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CourseNotFoundException.class, GradeNotFoundException.class, StudentNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponse(Arrays.asList(ex.getMessage())), HttpStatus.NOT_FOUND);
    }

    // An unsuccessful deletion will throw an EmptyResultDataAccessException
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleDataAccessException(EmptyResultDataAccessException ex) {
        return new ResponseEntity<>(new ErrorResponse(Arrays.asList("Cannot delete non-existing resource")), HttpStatus.NOT_FOUND);
    }

    // Attempting to save the same grade twice throws a DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(new ErrorResponse(Arrays.asList("Data Integrity Violation: we cannot process your request")), HttpStatus.BAD_REQUEST);
    }
}
