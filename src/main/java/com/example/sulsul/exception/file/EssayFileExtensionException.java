package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class EssayFileExtensionException extends BadInputException {
    public EssayFileExtensionException(String fileExtension) {
        super("FILE_03", "첨삭파일은 pdf 파일만 업로드 가능합니다.", Map.of("fileExtension", fileExtension));
    }
}