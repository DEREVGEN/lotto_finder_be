package com.ydg.project.be.lottofinder.batch.exception;

public class LottoResultNotUpdatedException extends RuntimeException{

    public LottoResultNotUpdatedException() {
    }

    public LottoResultNotUpdatedException(String message) {
        super(message);
    }
}
