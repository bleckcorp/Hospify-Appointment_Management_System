package com.hospify.appointment.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourceAlreadyExistsException extends RuntimeException {
    protected String message;

    public ResourceAlreadyExistsException(String identifier){
        super(identifier);
        this.message = "Resource with identifier: " + identifier + "already exists";
    }
}