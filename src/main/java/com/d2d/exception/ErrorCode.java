package com.d2d.exception;

public enum ErrorCode implements ErrorHandle {
	D2D_1(404, "list is empty"),
	D2D_2(400, "Given input type is wrong or null"),
	D2D_3(401, "UNAUTHORIZED"),

	D2D_4( 400,"AutoIncrement should not be null "),
	D2D_5( 400,"tableReference should not be null "),
	D2D_6( 400,"Null values present in spring initializer details"),
	D2D_7( 400,"Column Name should not be null"),
	D2D_8( 400,"Table Name already used"),
	D2D_9( 400,"Column Name Must be unique and not multiple time Used"),
	D2D_10( 400,"Primary key Must be unique and not multiple time Used"),
	D2D_11( 400,"While generating database Script Null Values Present"),
	D2D_12( 500,"INTERNAL SERVER ERROR"),
	D2D_13( 400,"Column Name could not found from the table"),
	D2D_14( 400,"dtd file format is only supported"),
	D2D_15( 400,"TableName is a mandatory field and cannot be null or Empty"),
	D2D_16( 400,"ColumnName is a mandatory field and cannot be null or Empty"),
	D2D_17( 400,"DataType is a mandatory field and cannot be null or Empty"),
	D2D_18( 400,"Please provide at least one table Information"),
	D2D_19( 400,"Header value of Table Name not properly defined in Excel"),
	D2D_20( 401,"UNAUTHORIZED Unable to get JWT Token no username present"),
	D2D_21  ( 401,"UNAUTHORIZED token expired "),
	D2D_22 ( 401,"UNAUTHORIZED JWT Token does not begin with Bearer String"),
	D2D_23  ( 500,"INTERNAL SERVER ERROR"),
	D2D_24  (1001,"USER ALREADY EXIST"),
	D2D_25  (1002,"Email id not exist"),
	D2D_26  (1003,"Password not exist"),
	D2D_27  (500,"INVALID_CREDENTIALS"),
	D2D_28  (500,"BAD_REQUEST"),
	D2D_29  (500,"USER_DISABLED"),
	D2D_30  (500,"INVALID_CREDENTIALS"),
	D2D_31 (500,"USER_DISABLED");













	private final int code;
	private final String message;

	ErrorCode(int errorCode, String message) {
		this.code = errorCode;
		this.message = message;
	}

	@Override
	public int getErrorCode() {

		return this.code;
	}

	@Override
	public String getMessage() {

		return this.message;
	}
}
