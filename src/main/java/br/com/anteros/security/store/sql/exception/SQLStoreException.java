package br.com.anteros.security.store.sql.exception;

public class SQLStoreException extends RuntimeException {

	public SQLStoreException() {
	}

	public SQLStoreException(String message) {
		super(message);
	}

	public SQLStoreException(Throwable cause) {
		super(cause);
	}

	public SQLStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQLStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
