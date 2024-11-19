package vn.flast.orchestration;

import jakarta.annotation.PostConstruct;
import vn.flast.utils.ActionThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("eventCenter")
public class EventCenter extends ActionThread implements EventDelegate {

    private PubSubService pubSubService;

    @Autowired
    @Qualifier("dataChanelService")
    private Subscriber dataSubscriber;

    @Autowired
    @Qualifier("customerService")
    private Subscriber customerSubscriber;

    @Autowired
    @Qualifier("packageService")
    private Subscriber packageService;

    @Autowired
    @Qualifier("dataChanelService")
    private Publisher dataPublisher;

    @Autowired
    @Qualifier("orderService")
    private Publisher orderPublisher;

    private final List<Subscriber> subscriberList  = new ArrayList<>();

    @PostConstruct
    public void initEventCenter() {
        this.setName("eventCenter");
        this.pubSubService = new PubSubService();
        /* set Delegate cho các Publisher */
        orderPublisher.setDelegate(this);
        dataPublisher.setDelegate(this);
        /* otherSubscriber */
        addSubscriber();
        /* Thread Execute */
        this.execute();
    }

    private void addSubscriber() {
        customerSubscriber.addSubscriber(EventTopic.ORDER_CHANGE, pubSubService);
        customerSubscriber.addSubscriber(EventTopic.DATA_CHANGE, pubSubService);
        packageService.addSubscriber(EventTopic.ORDER_CHANGE, pubSubService);

        subscriberList.add(dataSubscriber);
        subscriberList.add(customerSubscriber);
        subscriberList.add(packageService);
    }

    @Override
    protected void onKilling() {
        EventTopic.allTopic().forEach( topic -> subscriberList.forEach(s -> s.unSubscribe(topic, pubSubService)));
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