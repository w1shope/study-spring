package com.example.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrService {

    private final int WIDTH = 200; // QR 코드 너비
    private final int HEIGHT = 200; // QR 코드 높이

    public Object createQr(String url) throws IOException, WriterException {

        // QR 코드는 byte 데이터로 표현된다.
        // MatrixToImageWriter를 통해 이미지 형태의 QR 코드를 생성하고, controller에 반환 시에는 byte 배열로 반환한다.

        // url을 QR 코드로 인코딩
        BitMatrix matrix = new MultiFormatWriter()
            .encode(url, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);

        // byte 배열로 출력 스트림을 제공
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // QR 코드를 PNG로 변환하고, 출력 스트림에 쓴다.
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);

        // QR 코드가 이미지 형태로 되어있기 때문에 byte 배열로 바꾸어 반환한다.
        return output.toByteArray();
    }
}
