package com.garethahealy.gatling.amq

import javax.jms._

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.jms.Predef._

import org.apache.activemq.jndi.ActiveMQInitialContextFactory

class TestJmsDsl extends Simulation {

    val jmsConfig = jms
        .connectionFactoryName("ConnectionFactory")
        .url("tcp://localhost:61616")
        .credentials("admin", "admin")
        .contextFactory(classOf[ActiveMQInitialContextFactory].getName)
        .listenerCount(1)
        .usePersistentDeliveryMode

    val scn = scenario("JMS DSL test").repeat(1) {
        exec(jms("req reply testing").reqreply
            .queue("inbound")
            .replyQueue("outbound")
            .textMessage("hello from gatling jms dsl")
            .check(simpleCheck(checkBodyTextCorrect))
        )
    }

    setUp(scn.inject(rampUsersPerSec(1) to 1 during (1 second)))
        .protocols(jmsConfig)

    def checkBodyTextCorrect(m: Message) = {
        m match {
            case `m` => true
            case _ => false
        }
    }
}
