package com.sword.signature.model.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Configuration
class MongoConvertersConfig {
    @Bean
    fun mongoCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf(
                offsetDateTimeReadConverter(),
                offsetDateTimeWriteConverter()
            )
        )
    }

    fun offsetDateTimeWriteConverter() = Converter<OffsetDateTime?, String?> {
        it.toInstant().atZone(ZoneOffset.UTC).toString()
    }


    fun offsetDateTimeReadConverter() = Converter<String?, OffsetDateTime?> {
        OffsetDateTime.parse(it)
    }
}
