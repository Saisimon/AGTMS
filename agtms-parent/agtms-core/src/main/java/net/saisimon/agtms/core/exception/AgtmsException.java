package net.saisimon.agtms.core.exception;

/**
 * AGTMS 异常
 * 
 * @author saisimon
 *
 */
public class AgtmsException extends RuntimeException {

	private static final long serialVersionUID = -8855674628137353063L;

	public AgtmsException() {
		super();
	}

	public AgtmsException(String s) {
		super(s);
	}

	public AgtmsException(Throwable c) {
		super(c);
	}

	public AgtmsException(String s, Throwable c) {
		super(s, c);
	}

}
