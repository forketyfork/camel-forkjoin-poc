package lu.crx.prototype.callee;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ForkjoinPocCalleeApplication {

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(ForkjoinPocCalleeApplication.class);
        ApplicationContext applicationContext = app.run(args);
        CamelSpringBootApplicationController applicationController =
                applicationContext.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }

}

