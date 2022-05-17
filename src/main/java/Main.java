import com.alibaba.nacos.api.annotation.NacosProperties;
import consumer.TccConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.dtm.client.DtmClient;
import pub.dtm.client.model.feign.ServiceMessage;
import pub.dtm.client.properties.DtmProperties;
import pub.dtm.client.saga.Saga;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String svc = "http://127.0.0.1:8888";

    private static final String serviceName = "dtmcli-spring-sample";

    public static void main(String[] args) throws Exception {
        testTccMicroService();

//        testTccHttp();

//        testSagaMicroService();

//        testSagaHttp();
    }


    private static void testTccHttp() throws Exception {
        DtmClient dtmClient = new DtmClient();

        String s = dtmClient.tccGlobalTransaction(TccConsumer::tccTransForHttp);
        log.info("returned gid is {}", s);

        s = dtmClient.tccGlobalTransaction(TccConsumer::tccBarrierTrans);
        log.info("returned gid is {}", s);

        // test branch barrier error, will throw Exception
//        String s = dtmClient.tccGlobalTransaction(TccConsumer::tccBarrierTransError);
//        log.info("returned gid is {}", s);
    }

    public static void testTccMicroService() throws Exception {
        DtmClient dtmClient = new DtmClient();

        String s = dtmClient.tccGlobalTransaction(TccConsumer::tccTransForMicroService);
        log.info("returned gid is {}", s);

        s = dtmClient.tccGlobalTransaction(TccConsumer::tccTransForHttp);
        log.info("returned gid is {}", s);

        s = dtmClient.tccGlobalTransaction(TccConsumer::tccBarrierTrans);
        log.info("returned gid is {}", s);

        s = dtmClient.tccGlobalTransaction(TccConsumer::tccBarrierTransMs);
        log.info("returned gid is {}", s);

        // test branch barrier error, will throw Exception
//        String s = dtmClient.tccGlobalTransaction(TccConsumer::tccBarrierTransErrorMs);
//        log.info("returned gid is {}", s);

        // test branch barrier error, will throw Exception
//        String s = dtmClient.tccGlobalTransaction(TccConsumer::tccBarrierTransError);
//        log.info("returned gid is {}", s);
    }

    public static void testSagaMicroService() {
        DtmClient dtmClient = new DtmClient();
        try {


            Saga saga = dtmClient
                    .newSaga()
                    .add(new ServiceMessage(serviceName, "/TransOut"), new ServiceMessage(serviceName, "/TransOutCompensate"), "")
                    .add(new ServiceMessage(serviceName, "/TransIn"), new ServiceMessage(serviceName, "/TransInCompensate"), "")
                    .enableWaitResult();
            saga.submit();
        } catch (Exception e) {
            log.error("saga error.");
        }
    }

    public static void testSagaHttp() {
        DtmClient dtmClient = new DtmClient();
        try {
            // create saga transaction
            Saga saga = dtmClient
                    .newSaga()
                    .add(svc + "/TransOut", svc + "/TransOutCompensate", "")
                    .add(svc + "/TransIn", svc + "/TransInCompensate", "")
                    .enableWaitResult();

            saga.submit();
        } catch (Exception e) {
            log.error("saga submit error", e);
        }
    }
}
