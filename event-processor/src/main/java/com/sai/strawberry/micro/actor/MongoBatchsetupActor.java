package com.sai.strawberry.micro.actor;

import akka.actor.UntypedActor;
import com.sai.strawberry.micro.model.EventProcessingContext;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by saipkri on 08/09/16.
 */
public class MongoBatchsetupActor extends UntypedActor {

    public static long timeout_in_seconds = 5 * 1000;

    private final MongoTemplate mongoTemplate;

    public MongoBatchsetupActor(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void onReceive(final Object message) throws Throwable {
        if (message instanceof EventProcessingContext) {
            EventProcessingContext context = (EventProcessingContext) message;
            if (context.getConfig().getBatchQueryConfig() != null) {
                if (!mongoTemplate.collectionExists(context.getConfig().getConfigId())) {
                    CollectionOptions options = new CollectionOptions(context.getConfig().getBatchQueryConfig().getMaxBatchSizeInBytes(), context.getConfig().getBatchQueryConfig().getMaxNumberOfDocs(), true);
                    mongoTemplate.createCollection(context.getConfig().getConfigId(), options);
                }
                mongoTemplate.save(context.getDoc(), context.getConfig().getConfigId());
            }
        }
    }
}
