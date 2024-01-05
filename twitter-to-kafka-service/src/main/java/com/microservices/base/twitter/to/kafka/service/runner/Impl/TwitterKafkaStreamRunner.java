package com.microservices.base.twitter.to.kafka.service.runner.Impl;

import com.microservices.base.config.TwitterToKafkaServiceConfigData;
import com.microservices.base.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.base.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.*;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Component
//@ConditionalOnProperty(name = "${twitter-to-kafka-service.enable-mock-tweets}", havingValue = "false", matchIfMissing = true) -> allows to load a spring bean at runtime with the configuration value, if true, then only this will load, but now its false
@ConditionalOnExpression("${twitter-to-kafka-service.enable-mock-tweets} && not ${twitter-to-kafka-service.enable-v2-tweets}")
public class TwitterKafkaStreamRunner implements StreamRunner {
    private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);
    private final TwitterToKafkaServiceConfigData configData;
    private final TwitterKafkaStatusListener statusListener;
    TwitterStream twitterStream;

    public TwitterKafkaStreamRunner(TwitterToKafkaServiceConfigData configData, TwitterKafkaStatusListener statusListener){
        this.configData = configData;
        this.statusListener = statusListener;
    }
    @Override
    public void start() throws TwitterException {
        twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.addListener(statusListener);
        addFilter();
    }

    private void addFilter() {
        String[] keywords = configData.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Filtering Twitter Streams for keywords {}", Arrays.toString(keywords));
    }

    @PreDestroy //called before the bean destroys, before the application shutdown, so that stream connection gets closed prior to application close
    //@PreDestroy is not called with @Prototype, remember this. As of now, by default, Spring beans are singleton, we haven't declared them prototype.
    public void shutDown(){
        if(twitterStream!=null){
            LOG.info("Closing Twitter Stream, shutting down before the application stops");
            twitterStream.shutdown();
        }
    }
}
