package com.budwhite.studying.framework.web.model.dto;

import com.budwhite.studying.framework.web.model.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BaseResponse implements BaseDTO {
    private String message;

    public BaseResponse() {
        this.message = Message.Info.GENERIC_CONFIRMATION_MESSAGE;
    }
}
