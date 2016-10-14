package com.shifumix;

/**
 * Created by u016272 on 29/10/2015.
 */
public class SearchException extends Exception {
    public SearchException() {
    }

    public SearchException(String s) {
        super(s);
    }

    public SearchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SearchException(Throwable throwable) {
        super(throwable);
    }

    public SearchException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
