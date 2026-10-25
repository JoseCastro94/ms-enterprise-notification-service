package com.enterprise.app.util;

import com.enterprise.app.exception.BusinessException;
import java.util.Base64;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AttachmentDecoder {
    public static byte[] decode(String contentBase64, int maxSizeBytes) {
        final byte[] content;
        try {
            content = Base64.getDecoder().decode(contentBase64);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("El contenido del adjunto debe ser Base64 válido.");
        }

        if (content.length > maxSizeBytes) {
            throw new BusinessException(
                    "El tamaño de cada adjunto no debe superar " + maxSizeBytes + " bytes.");
        }
        return content;
    }
}
