package com.d2d.exception;

import com.d2d.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String ERROR = "Oops!";

	@Autowired
    EmailUtil emailUtil;

	@ExceptionHandler(value = CustomValidationException.class)
	public ResponseEntity<ErrorMessage> handleBaseException(Exception ex) {
		log.error(ERROR, ex);
		CustomValidationException validationException = (CustomValidationException) ex;
		ErrorHandle errorHandle = validationException.getErrorCode();
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setErrorCode(errorHandle.getErrorCode());
		errorMessage.setMessage(errorHandle.getMessage());
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ModelNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleModelNotFoundException(Exception ex) {
		log.error(ERROR, ex);
		ModelNotFoundException validationException = (ModelNotFoundException) ex;
		ErrorHandle errorHandle = validationException.getErrorCode();
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setErrorCode(errorHandle.getErrorCode());
		errorMessage.setMessage(errorHandle.getMessage());
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorMessage> handleRuntimeException(Exception ex) {
		log.error(ERROR, ex);
		ErrorHandle errorHandle = ErrorCode.D2D_12;
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setErrorCode(errorHandle.getErrorCode());
		errorMessage.setMessage(ex.getMessage());
		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
