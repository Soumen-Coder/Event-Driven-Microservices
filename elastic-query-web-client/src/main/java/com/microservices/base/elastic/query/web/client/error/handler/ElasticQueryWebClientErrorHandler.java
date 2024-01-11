package com.microservices.base.elastic.query.web.client.error.handler;

import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

//@ControllerAdvice is used to catch unhandled exceptions that could be thrown by the application
//Instead of returning unhandled exception to the client, we can return a generic message with a ControllerAdvice
//You will get to see method overloading of handle method below for the error handler class
@ControllerAdvice // enables exception handler across the whole application, used for global exception handling in spring boot, used with @ExceptionHandler
public class ElasticQueryWebClientErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticQueryWebClientErrorHandler.class);
    @ExceptionHandler(AccessDeniedException.class)
    //Adding overloaded handle method, where method overloading is done by change the parameters and keeping same name of the method
    //AccessDeniedException -> FORBIDDEN
    //put error on the model objects, to be able to show it on the template
    public String handle(AccessDeniedException e, Model model){
        LOG.error("Access Denied Exception!", e);
        model.addAttribute("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        model.addAttribute("error_description", "You are not authorized to access this resource!");
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    //IllegalArgumentException -> badRequest() -> related to client
    public String handle(IllegalArgumentException e, Model model){
        LOG.error("Illegal Argument Exception", e);
        model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        model.addAttribute("error_description", "Illegal argument exception!" + e.getMessage());
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    //RuntimeException -> badRequest() -> related to client
    public String handle(RuntimeException e, Model model){
        LOG.error("Service runtime exception!");
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("error_description", "could not get response" +e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    //Exception -> related to server -> INTERNAL_SERVER_ERROR
    public String handle(Exception e, Model model){
        LOG.error("Internal sever error!", e);
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("error_description", "A server error occurred!");
        return "home"; //redirect to the home page because we want to show the user errors on the home page instead of redirecting users to the error page
    }

    //This will collect the validation error and we can show them on the html page
    @ExceptionHandler(BindException.class)
    public String handle(BindException e, Model model) {
        LOG.error("Method argument validation exception!", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage())
        );
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        model.addAttribute("error_description", errors);
        return "home"; //redirect to the home page because we want to show the user errors on the home page instead of redirecting users to the error page
    }
}
