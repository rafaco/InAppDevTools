/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtils {

    private ListUtils() { throw new IllegalStateException("Utility class"); }

    public static <T> List<T> cast(Class<? extends T> clazz, Collection<?> c) {
        List<T> r = new ArrayList<>(c.size());
        for(Object o: c)
            r.add(clazz.cast(o));
        return r;
    }
}
