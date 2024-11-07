package com.cbio.core.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.notification.NotificationJobDTO;

import java.util.Map;

public interface EventService {

    void alterNotify(String id);

    void notify(String id) throws CbioException;

}
