package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class GitlabEventDTO {

    private Map<String, Object> event;

    public Map<String, Object> getEvent(String key) {
        return (Map<String, Object>) event.get(key);
    }


    public static class Chaves{
        public static String OBJECTATTRIBUTES ="object_attributes";
        public static String SOURCEBRANCH = "source_branch";
        public static String TARGETBRANCH = "target_branch";
        public static String ACTION = "action";
        public static String USER = "user";
        public static String PROJETO = "project";
        public static String NAME = "name";

    }
}
