package com.dante.comment_service.api.config.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.hypersistence.tsid.TSID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TSIDJacksonConfig {

    @Bean
    public SimpleModule tsidModule(){
        SimpleModule module = new SimpleModule();
        module.addSerializer(TSID.class, new TSIDToStringSerializer());
        module.addDeserializer(TSID.class, new StringToTSIDDeserializer());
        return module;
    }
}
