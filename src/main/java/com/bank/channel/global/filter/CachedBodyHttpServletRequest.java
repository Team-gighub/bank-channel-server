package com.bank.channel.global.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Request Body를 여러 번 읽을 수 있도록 캐싱하는 Wrapper
 * 
 * 문제:
 * - HttpServletRequest의 InputStream은 한 번만 읽을 수 있음
 * - Filter에서 merchantId 추출 시 Body를 읽으면, Controller에서 다시 읽을 수 없음
 * 
 * 해결:
 * - Request Body를 byte[]로 캐싱
 * - getInputStream()이 호출될 때마다 캐시된 데이터 반환
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;
    private final String characterEncoding;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.characterEncoding = request.getCharacterEncoding(); // 요청의 인코딩 저장
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream()); // 바디 캐싱
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);

        Charset charset = (this.characterEncoding != null)
                ? Charset.forName(this.characterEncoding)
                : StandardCharsets.UTF_8;

        return new BufferedReader(new InputStreamReader(byteArrayInputStream, charset));
    }

    /**
     * 캐싱된 Body를 문자열로 반환 (요청의 charset 적용)
     */
    public String getCachedBody() {
        Charset charset = (this.characterEncoding != null)
                ? Charset.forName(this.characterEncoding)
                : StandardCharsets.UTF_8;

        return new String(this.cachedBody, charset);
    }

    /**
     * 캐싱된 Body를 다시 읽을 수 있는 InputStream 구현
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
    }
}
