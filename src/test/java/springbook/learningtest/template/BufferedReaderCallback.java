package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * BufferedReader를 전달받는 콜백 인터페이스
 */
public interface BufferedReaderCallback {
    Integer doSomethingWithReader(BufferedReader bufferedReader) throws IOException;
}
