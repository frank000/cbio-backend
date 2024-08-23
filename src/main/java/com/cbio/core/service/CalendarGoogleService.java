package com.cbio.core.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CalendarGoogleService {
    void executa(String id) throws IOException, GeneralSecurityException;
}
