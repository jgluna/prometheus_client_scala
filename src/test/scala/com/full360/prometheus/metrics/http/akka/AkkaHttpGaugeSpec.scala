/*
 * Copyright © 2017 Full 360 Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.full360.prometheus.metrics.http.akka

import com.full360.prometheus.{ BaseSpec, Metric }

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is

class AkkaHttpGaugeSpec extends BaseSpec with ScalatestRouteTest with AkkaHttpGauge {

  val route =
    gauge {
      pathSingleSlash {
        get {
          assertThat(Metric.getRegistry, is(
            s"""# HELP ${namespace}_$name $help
               |# TYPE ${namespace}_$name gauge
               |${namespace}_$name{method="get",path="/",} 1.0
               |""".stripMargin
          ))

          complete("foo")
        }
      }
    }

  "Gauge metric" should provide {
    "a gauge DSL for Akka Http" which {
      "increase and decrease by 1" in {
        assertThat(Metric.getRegistry, is(""))

        Get() ~> route ~> check {
          assertThat(responseAs[String], is("foo"))
          assertThat(Metric.getRegistry, is(
            s"""# HELP ${namespace}_$name $help
               |# TYPE ${namespace}_$name gauge
               |${namespace}_$name{method="get",path="/",} 0.0
               |""".stripMargin
          ))
        }
      }
    }
  }
}