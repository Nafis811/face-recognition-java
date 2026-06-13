package com.facerecog.impl;

import com.facerecog.model.RecognitionResult;
import com.facerecog.service.RecognitionClient;
import java.awt.image.BufferedImage;

public class RecognitionClientImpl implements RecognitionClient {

    private final boolean useMock;

    public RecognitionClientImpl(boolean useMock) {
        this.useMock = useMock;
    }

    @Override
    public RecognitionResult recognize(BufferedImage frame) {
        if (useMock) {
            return new RecognitionResult("recognized", "Scan Test", 0.95);
        }
        return callPythonAPI(frame);
    }

    private RecognitionResult callPythonAPI(BufferedImage frame) {
        // TODO: diisi saat Python API Anggota 1 sudah siap
        throw new UnsupportedOperationException("Python API belum siap");
    }
}