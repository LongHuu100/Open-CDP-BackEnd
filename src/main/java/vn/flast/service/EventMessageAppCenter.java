package vn.flast.service;

import jakarta.annotation.PostConstruct;
import vn.flast.orchestration.EventDelegate;
import vn.flast.orchestration.EventTopic;
import vn.flast.orchestration.MessageInterface;
import vn.flast.orchestration.PubSubService;
import vn.flast.orchestration.Publisher;
import vn.flast.orchestration.Subscriber;
import vn.flast.utils.ActionThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("eventMessageAppCenter")
public class EventMessageAppCenter extends ActionThread implements EventDelegate {

    private PubSubService pubSubService;

    /* ============== Publisher ============= */
    @Autowired
    @Qualifier("dataService")
    private Publisher dataPublisher;

    @Autowired
    @Qualifier("orderService")
    private Publisher orderPublisher;

    /* ============== Subscriber ============= */
    @Autowired
    @Qualifier("dataService")
    private Subscriber dataSubscriber;

    @Autowired
    @Qualifier("customerPersonalService")
    private Subscriber customerSubscriber;

    private final List<Subscriber> subscriberList  = new ArrayList<>();

    @PostConstruct
    public void initEventCenter() {
        this.setName("eventCenter");
        this.pubSubService = new PubSubService();
        /* set Delegate cho các Publisher */
        orderPublisher.setDelegate(this);
        dataPublisher.setDelegate(this);
        /* Subscriber */
        this.addSubscriber();
        /* Thread Execute */
        this.execute();
    }

    private void addSubscriber() {
        customerSubscriber.addSubscriber(EventTopic.ORDER_CHANGE, pubSubService);
        customerSubscriber.addSubscriber(EventTopic.DATA_CHANGE, pubSubService);

        subscriberList.add(dataSubscriber);
        subscriberList.add(customerSubscriber);
    }

    @Override
    protected void onKilling() {
        EventTopic.allTopic().forEach(topic -> subscriberList.forEach(s -> s.unSubscribe(topic, pubSubService)));
        subscriberList.clear();
    }

    @Override
    protected void onException(Exception ex) {
        log.error("====== Error on execute: {}", ex.getMessage());
    }

    @Override
    protected long sleepTime() {
        return 2000L;
    }

    @Override
    protected void action() {
        subscriberList.forEach(Subscriber::executeMessage);
    }

    @Override
    public void addEvent(Subscriber subscriber, String topic) {
        subscriber.addSubscriber(topic ,pubSubService);
    }

    @Override
    public void sendEvent(MessageInterface message) {
        pubSubService.addMessageToQueue(message);
    }
}
