/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * This is a modified source from WCViewPager by rnevet, which is available
 * Apache License, Version 2.0 at https://github.com/rnevet/WCViewPager
 *
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Copyright (c) 2015 Raanan Nevet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * Changelog:
 *     - Added previous attribution notice and this changelog
 *     - Conditional imports for AndroidX builds
 */
package nevet.me.wcviewpager;

import android.util.SparseArray;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.viewpager.widget.PagerAdapter;
//#else
import android.support.v4.view.PagerAdapter;
//#endif

/**
 * Simple implementation of @see ObjectAtPositionInterface can be extended by any custom adapter
 */
public abstract class ObjectAtPositionPagerAdapter extends PagerAdapter implements ObjectAtPositionInterface {
    protected SparseArray<Object> objects = new SparseArray<>();

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        Object object = instantiateItemObject(container, position);
        objects.put(position, object);
        return object;
    }

    /**
     * Replaces @see PagerAdapter#instantiateItem and handles objects tracking for getObjectAtPosition
     */
    public abstract Object instantiateItemObject(ViewGroup container, int position);

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        objects.remove(position);
        destroyItemObject(container, position, object);
    }

    /**
     * Replaces @see PagerAdapter#destroyItem and handles objects tracking for getObjectAtPosition
     */
    public abstract void destroyItemObject(ViewGroup container, int position, Object object);


    @Override
    public Object getObjectAtPosition(int position) {
        return objects.get(position);
    }
}
