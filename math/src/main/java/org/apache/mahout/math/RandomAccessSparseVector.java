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

package org.apache.mahout.math;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.mahout.math.function.IntDoubleProcedure;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.map.OpenIntDoubleHashMap;


/** Implements vector that only stores non-zero doubles */
public class RandomAccessSparseVector extends AbstractVector {

  private static final int INITIAL_SIZE = 11;

  private OpenIntDoubleHashMap values;

  /** For serialization purposes only. */
  public RandomAccessSparseVector() {
    super(0);
  }

  public RandomAccessSparseVector(int cardinality) {
    this(cardinality, Math.min(cardinality, INITIAL_SIZE)); // arbitrary estimate of 'sparseness'
  }

  public RandomAccessSparseVector(int cardinality, int size) {
    super(cardinality);
    values = new OpenIntDoubleHashMap(size);
  }

  public RandomAccessSparseVector(Vector other) {
    this(other.size(), other.getNumNondefaultElements());
    Iterator<Element> it = other.iterateNonZero();
    Element e;
    while(it.hasNext() && (e = it.next()) != null) {
      values.put(e.index(), e.get());
    }
  }

  private RandomAccessSparseVector(int cardinality, OpenIntDoubleHashMap values) {
    super(cardinality);
    this.values = values;
  }

  public RandomAccessSparseVector(RandomAccessSparseVector other, boolean shallowCopy) {
    super(other.size());
    values = shallowCopy ? other.values : (OpenIntDoubleHashMap)other.values.clone() ;
  }

  @Override
  protected Matrix matrixLike(int rows, int columns) {
    int[] cardinality = {rows, columns};
    return new SparseRowMatrix(cardinality);
  }

  @Override
  public RandomAccessSparseVector clone() {
    return new RandomAccessSparseVector(size(), (OpenIntDoubleHashMap) values.clone());
  }

  @Override
  public Vector assign(Vector other) {
    if (size() != other.size()) {
      throw new CardinalityException(size(), other.size());
    }
    values.clear();
    Iterator<Element> it = other.iterateNonZero();
    Element e;
    while(it.hasNext() && (e = it.next()) != null) {
      setQuick(e.index(), e.get());
    }
    return this;
  }

  public boolean isDense() {
    return false;
  }

  public boolean isSequentialAccess() {
    return false;
  }

  public double getQuick(int index) {
    return values.get(index);
  }

  public void setQuick(int index, double value) {
    lengthSquared = -1.0;
    if (value == 0.0) {
      values.removeKey(index);
    } else {
      values.put(index, value);
    }
  }

  public int getNumNondefaultElements() {
    return values.size();
  }

  public RandomAccessSparseVector like() {
    return new RandomAccessSparseVector(size(), values.size());
  }

  /**
   * NOTE: this implementation reuses the Vector.Element instance for each call of next(). If you need to preserve the
   * instance, you need to make a copy of it
   *
   * @return an {@link Iterator} over the Elements.
   * @see #getElement(int)
   */
  public Iterator<Element> iterateNonZero() {
    return new NonDefaultIterator();
  }
  
  public Iterator<Element> iterator() {
    return new AllIterator();
  }

  @Override
  public void addTo(Vector v) {
    if (v.size() != size()) {
      throw new CardinalityException(size(), v.size());
    }
    values.forEachPair(new AddToVector(v));
  }

  @Override
  public double dot(Vector x) {
    if (size() != x.size()) {
      throw new CardinalityException(size(), x.size());
    }
    if (this == x) {
      return dotSelf();
    }
    
    double result = 0;
    if (x instanceof SequentialAccessSparseVector) {
      Iterator<Element> iter = x.iterateNonZero();
      while (iter.hasNext()) {
        Element element = iter.next();
        result += element.get() * getQuick(element.index());
      }
      return result;
    } else { 
      Iterator<Element> iter = iterateNonZero();
      while (iter.hasNext()) {
        Element element = iter.next();
        result += element.get() * x.getQuick(element.index());
      }
      return result;
    }
  }


  private static class AddToVector implements IntDoubleProcedure {
    private final Vector v;
    private AddToVector(Vector v) {
      this.v = v;
    }
    public boolean apply(int key, double value) {
      v.set(key, value + v.get(key));
      return true;
    }
  }

  private final class NonDefaultIterator implements Iterator<Element> {

    private final RandomAccessElement element = new RandomAccessElement();
    private final IntArrayList indices = new IntArrayList();
    private int offset;

    private NonDefaultIterator() {
      values.keys(indices);
    }

    public boolean hasNext() {
      return offset < indices.size();
    }

    public Element next() {
      if (offset >= indices.size()) {
        throw new NoSuchElementException();
      } else {
        element.index = indices.get(offset);
        offset++;
        return element;
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private final class AllIterator implements Iterator<Element> {

    private final RandomAccessElement element = new RandomAccessElement();

    private AllIterator() {
      element.index = -1;
    }

    public boolean hasNext() {
      return element.index+1 < size();
    }

    public Element next() {
      if (element.index+1 >= size()) {
        throw new NoSuchElementException();
      } else {
        element.index++;
        return element;
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private final class RandomAccessElement implements Element {

    int index;

    public double get() {
      return values.get(index);
    }

    public int index() {
      return index;
    }

    public void set(double value) {
      lengthSquared = -1;
      if (value == 0.0) {
        values.removeKey(index);
      } else {
        values.put(index, value);
      }
    }
  }
  
}
