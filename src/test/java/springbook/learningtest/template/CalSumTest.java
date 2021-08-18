package springbook.learningtest.template;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;


public class CalSumTest {
    Calculator calculator;
    String numFilepath;

    @Before
    public void setUp() {
        this.calculator = new Calculator();
        Path path = Paths.get(System.getProperty("user.dir"), "src/test/java/springbook/learningtest/template/numbers.txt");
        this.numFilepath = path.toString();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum2(this.numFilepath), is(10));
    }

    @Test
    public void multiplyOfNumber() throws IOException {
        assertThat(calculator.calMultiply2(this.numFilepath), is(24));
    }

    @Test
    public void concatenateStrings() throws IOException {
        assertThat(calculator.concatenate(this.numFilepath), is("1234"));
    }
}
