package springbook.learningtest.template;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
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
        Path path = Paths.get(System.getProperty("user.dir"), "src/test/java/springbook/learningtest/template/numbers.txt");
        int sum = calculator.calcSum(path.toString());
        // int sum = calculator.calcSum(Calculator.class.getResource("numbers.txt").getPath());
        assertThat(sum, is(10));
    }

    public Integer calcSum(String filepath) throws IOException {
        BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
            @Override
            public Integer doSomethingWithReader(BufferedReader bufferedReader) throws IOException {
                Integer sum = 0;
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {    // 마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
                    sum += Integer.valueOf(line);
                }
                bufferedReader.close();
                return sum;
            }
        };
        return fileReadTemplate(filepath, sumCallback);
    }

    public Integer calcSum2(String filepath) throws IOException {
        LineCallback sumCallback = new LineCallback() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return Integer.valueOf(line) + value;
            }
        };
        return lineReadTemplate(filepath, sumCallback, 0);
    }

    public Integer calMultiply(String filepath) throws IOException {
        BufferedReaderCallback multiplyCallback = bufferedReader -> {
            Integer multiply = 1;
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {    // 마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
                multiply *= Integer.valueOf(line);
            }
            bufferedReader.close();
            return multiply;
        };
        return fileReadTemplate(filepath, multiplyCallback);
    }

    public Integer calMultiply2(String filepath) throws IOException {
        LineCallback multiplyCallback = (line, value) -> Integer.valueOf(line) * value;
        return lineReadTemplate(filepath, multiplyCallback, 1);
    }

    /**
     * 문자열 연결 기능 콜백을 이용해 만든 concatenate() 메소드
     * @param filepath
     * @return
     * @throws IOException
     */
    public String concatenate(String filepath) throws IOException {
        LineCallbackWithTypeParam<String> concatenateCallback = new LineCallbackWithTypeParam<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }
        };
        return lineReadTemplate2(filepath, concatenateCallback, "");
    }

    /**
     * BufferedReaderCallback을 사용하는 템플릿 메소드
     *
     * @param filepath
     * @param callback
     * @return
     * @throws IOException
     */
    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));  //한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
            int ret = callback.doSomethingWithReader(br);       // 콜백 오브젝트 호출. 템플릿에서 만든 컨택스트 정보인 BufferedReader를 전달해주고 콜백의 작업 결과를 받아둔다.
            return ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * LineCallback을 사용하는 템플릿
     *
     * @param filepath
     * @param callback
     * @param initVal
     * @return
     * @throws IOException
     */
    public Integer lineReadTemplate(String filepath, LineCallback callback, int initVal) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));  //한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
            Integer res = initVal;
            String line = null;
            while ((line = br.readLine()) != null) {    // 파일의 각 라인을 루프를 돌면서 가져오는 것도 템플릿이 담당한다.
                res = callback.doSomethingWithLine(line, res); // 각 라인의 내용을 가지고 계산하는 작업만 콜백에게 맡긴다
            }
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public <T> T lineReadTemplate2(String filepath, LineCallbackWithTypeParam<T> callback, T initVal) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));  //한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
            T res = initVal;
            String line = null;
            while ((line = br.readLine()) != null) {    // 파일의 각 라인을 루프를 돌면서 가져오는 것도 템플릿이 담당한다.
                res = callback.doSomethingWithLine(line, res); // 각 라인의 내용을 가지고 계산하는 작업만 콜백에게 맡긴다
            }
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
