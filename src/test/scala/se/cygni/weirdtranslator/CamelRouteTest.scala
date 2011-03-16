package se.cygni.weirdtranslator

import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.scala.dsl.builder.{RouteBuilder => ScalaRouteBuilder}
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.{EndpointInject, Produce, ProducerTemplate}
import org.junit.{Test, After, Before}
import org.scalatest.junit.{JUnitSuite}

class CamelRouteTest extends CamelTestSupport with JUnitSuite with CamelRouteBuilder {

  setUseRouteBuilder(false)

  @EndpointInject(uri = "mock:result")  var resultEndpoint: MockEndpoint = null
  @Produce(uri = "direct:start")  var producerTemplate: ProducerTemplate = null

  @Before
  def beforeTest: Unit = {
    setUp
    context.addRoutes(createBuilder)   //Workaround since the super.createRouteBuilder returns a RouteBuilderFactory class
   context.addRoutes(commonRouteBuilder)
    startCamelContext
  }

  @After
  def afterTest: Unit = {
     resetMocks
  }

  @Test
  def testSendText: Unit = {
    var expectedBody = "Welcome"
    resultEndpoint.expectedBodiesReceived(expectedBody)
    producerTemplate.sendBody(expectedBody)
    resultEndpoint.assertIsSatisfied
  }


  def createBuilder = new ScalaRouteBuilder  {
    "direct:start" --> "seda:in"
    "seda:xmpp" --> "mock:result"
  }

}