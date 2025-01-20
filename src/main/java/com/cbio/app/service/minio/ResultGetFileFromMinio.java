package com.cbio.app.service.minio;

import java.util.Map;

public record ResultGetFileFromMinio(byte[] fileBytes, Map<String, String> metadata) {
}