package org.opengroup.osdu.file.provider.azure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;

@Configuration
public class EventGridConfig {

    private boolean eventGridEnabled;

    private String eventGridCustomTopic;

    public boolean isEventGridEnabled() {
        return eventGridEnabled;
    }

    public String getCustomTopicName() {
        return eventGridCustomTopic;
    }

    public EventGridConfig(@Value("#{new Boolean('${azure.eventGrid.enabled:true}')}") boolean publish,
            @Value("#{new String('${azure.eventGrid.topicName:statuschangedtopic}')}") String topicName) {
        if (publish && StringUtils.isEmpty(topicName)) {
                throw new RuntimeException("Missing EventGrid Configuration");
        }
        
        this.eventGridEnabled = publish;
        this.eventGridCustomTopic = topicName;
    }

}
