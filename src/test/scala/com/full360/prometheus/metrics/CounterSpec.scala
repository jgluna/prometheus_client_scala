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

package com.full360.prometheus.metrics

import com.full360.prometheus.{ BaseSpec, Metric }

import org.hamcrest.MatcherAssert._
import org.hamcrest.Matchers._

class CounterSpec extends BaseSpec {

  val name = "name"
  val help = "help"

  @Counter(Metric(name, help, Map("method" -> "foo")))
  def foo() = {}

  @Counter(Metric(name, help, Map("method" -> "bar")))
  def bar() = {}

  @Counter(Metric(name, help, Map("method" -> "baz")))
  def baz(a: Int, b: Int): Int = a + b

  "Counter metric" should provide {
    "a counter annotation" which {
      "increase the counter by 1" in {
        assertThat(Metric.getRegistry, is(""))
        foo()
        assertThat(Metric.getRegistry, is(s"""# HELP $name $help\n# TYPE $name counter\n$name{method="foo",} 1.0\n"""))
        foo()
        assertThat(Metric.getRegistry, is(s"""# HELP $name $help\n# TYPE $name counter\n$name{method="foo",} 2.0\n"""))
      }
      "increase the counter by 1 with different labels" in {
        assertThat(Metric.getRegistry, is(""))
        foo()
        bar()
        assertThat(Metric.getRegistry, is(s"""# HELP $name $help\n# TYPE $name counter\n$name{method="bar",} 1.0\n$name{method="foo",} 1.0\n"""))
      }
      "does not affect parameters and result of the method" in {
        assertThat(baz(4, 6), is(10))
      }
    }
  }
}