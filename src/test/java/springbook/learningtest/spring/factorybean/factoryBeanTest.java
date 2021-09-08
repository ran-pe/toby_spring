package springbook.learningtest.spring.factorybean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/FactoryBeanTest-context.xml")
//@DirtiesContext
public class factoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        assertThat(message, is(Message.class));
        assertThat(((Message) message).getText(), is("Factory Bean"));
    }

    @Test
    public void getFactoryBean() throws Exception {
        Object factory = context.getBean("&message");
        assertThat(factory, is(MessageFactoryBean.class));
    }
}
