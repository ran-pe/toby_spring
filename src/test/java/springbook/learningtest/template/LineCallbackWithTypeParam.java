package springbook.learningtest.template;

public interface LineCallbackWithTypeParam<T> {
    T doSomethingWithLine(String line, T value);
}
