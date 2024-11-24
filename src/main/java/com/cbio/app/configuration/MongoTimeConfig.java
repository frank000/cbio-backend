package com.cbio.app.configuration;

import com.cbio.app.base.utils.CbioDateUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoTimeConfig {


    @Bean
    public MongoTransactionManager transactionManager(MongoTemplate mongoTemplate) {
        return new MongoTransactionManager(mongoTemplate.getMongoDatabaseFactory());
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(LocalDateTimeReferenceReadingConverter.INSTANCE);
        converters.add(LocalDateTimeReferenceWriterConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    @ReadingConverter
    enum LocalDateTimeReferenceReadingConverter implements Converter<Date, LocalDateTime> {

        INSTANCE;

        @Override
        public LocalDateTime convert(final Date date) {
            return CbioDateUtils.fromDate(date);
        }
    }

    @WritingConverter
    enum LocalDateTimeReferenceWriterConverter implements Converter<LocalDateTime, Date> {

        INSTANCE;

        @Override
        public Date convert(LocalDateTime localDateTime) {
            return CbioDateUtils.fromLocalDateTime(localDateTime);
        }
    }
}
