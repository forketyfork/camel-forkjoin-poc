package lu.crx.prototype.caller;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ForkjoinPocCallerApplication {

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(ForkjoinPocCallerApplication.class);
        ApplicationContext applicationContext = app.run(args);
        CamelSpringBootApplicationController applicationController =
                applicationContext.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }

}

