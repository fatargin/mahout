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

package org.apache.mahout.fpm.pfpgrowth.convertors.integer;

import java.util.Iterator;
import java.util.List;

import org.apache.mahout.common.IntegerTuple;

public final class IntegerTupleIterator implements Iterator<List<Integer>> {

  private Iterator<IntegerTuple> iterator = null;

  public IntegerTupleIterator(Iterator<IntegerTuple> iterator) {
    this.iterator = iterator;
  }

  @Override
  public final boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public final List<Integer> next() {
    IntegerTuple transaction = iterator.next();
    return transaction.getEntries();
  }

  @Override
  public final void remove() {
    iterator.remove();
  }

}