package springbook.learningtest.template;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * 파일의 숫자 합을 계산하는 코드의 테스트
 */
public class Calculator {

    @Test
    public void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
//        int sum = calculator.calcSum(Calculator.class.getResource("numbers.txt").getPath());
        System.out.println(System.getProperty("user.dir"));
        System.out.println(Paths.get("").toAbsolutePath().toString());
        int sum = calculator.calcSum("C:\\Users\\Rachel\\Workspace\\study\\toby_spring\\src\\test\\java\\springbook\\learningtest\\template\\numbers.txt");
        assertThat(sum, is(10));
    }

    public Integer calcSum(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filepath));   //한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
        Integer sum = 0;
        String line = null;
        while ((line = br.readLine()) != null) {    // 마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
            sum += Integer.valueOf(line);
        }
        br.close();
        return sum;

    }
}
