package echo.controller;

import echo.dto.request.SavedMessageRequest;
import echo.dto.response.MessageDTO;
import echo.model.Message;
import echo.service.SavedMessageService;
import echo.util.JsonUtil;
import echo.util.ResponseFactory;

public class SavedMessagesController {
    private final SavedMessageService savedMessageService;

    // constructor
    public SavedMessagesController(SavedMessageService savedMessageService) {
        this.savedMessageService = savedMessageService;
    }

    public String saveMessage(SavedMessageRequest request) {
        try {
            Message savedMessage = savedMessageService.saveMessage(request.getUserId(), request.getMessageId());
            MessageDTO messageDTO = MessageDTO.toMessageDTO(savedMessage);
            return ResponseFactory.success("Message saved to saved messages.", JsonUtil.toJson(messageDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
 
    public String deleteMessage(SavedMessageRequest request) {
        try {
            savedMessageService.deleteMessage(request.getUserId(), request.getMessageId());
            return ResponseFactory.success("Message removed from saved messages.", null);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }
}
