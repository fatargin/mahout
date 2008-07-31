/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.ga.watchmaker.cd;

import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;

import junit.framework.TestCase;

/**
 * 
 */
public class CDFitnessTest extends TestCase {

  /**
   * Test method for {@link org.apache.mahout.ga.watchmaker.cd.CDFitness#get()}.
   */
  public void testGet() {
    int n = 100;
    Random rng = new MersenneTwisterRNG();
    int tp, tn, fp, fn;
    float se, sp;

    for (int nloop = 0; nloop < n; nloop++) {
      tp = rng.nextInt(1000);
      tn = rng.nextInt(1000);
      fp = rng.nextInt(1000);
      fn = rng.nextInt(1000);

      CDFitness fitness = new CDFitness(tp, fp, tn, fn);
      se = (float) tp / (tp + fn);
      sp = (float) tn / (tn + fp);

      assertEquals(se * sp, fitness.get());
    }
  }

}