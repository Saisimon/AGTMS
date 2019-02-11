package net.saisimon.agtms.core.exception;

public class GenerateException extends Exception {

	private static final long serialVersionUID = 8255898275447003207L;
	
	public GenerateException() {
        super();
    }
	
	public GenerateException(String s) {
        super(s);
    }
	
	public GenerateException(Throwable c) {
        super(c);
    }
	
	public GenerateException(String s, Throwable c) {
        super(s, c);
    }

}
