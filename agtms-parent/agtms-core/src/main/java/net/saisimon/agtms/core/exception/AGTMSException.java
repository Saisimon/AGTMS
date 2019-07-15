package net.saisimon.agtms.core.exception;

/**
 * AGTMS 异常
 * 
 * @author saisimon
 *
 */
public class AGTMSException extends RuntimeException {

	private static final long serialVersionUID = -8855674628137353063L;

	public AGTMSException() {
		super();
	}

	public AGTMSException(String s) {
		super(s);
	}

	public AGTMSException(Throwable c) {
		super(c);
	}

	public AGTMSException(String s, Throwable c) {
		super(s, c);
	}

}
