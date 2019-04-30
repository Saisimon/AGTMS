package net.saisimon.agtms.remote.exception;

/**
 * 远程调用运行时异常
 * 
 * @author saisimon
 *
 */
public class RemoteException extends RuntimeException {
	
	public RemoteException() {
		super();
	}

	public RemoteException(String s) {
		super(s);
	}

	public RemoteException(Throwable c) {
		super(c);
	}

	public RemoteException(String s, Throwable c) {
		super(s, c);
	}
	
}
