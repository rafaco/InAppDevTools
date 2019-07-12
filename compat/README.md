
# Module overview

This module allow to produce two library flavors from the same source code: one using AndroidX, another using Android Support Libraries.

# Usage
On your root build.gradle, add the preprocessor dependency:
```gradle
buildscript {
    dependencies {
        ...
        classpath 'wang.dannyhe.tools:plugin:0.0.7'
    }
}
```


On your module build.gradle: apply compat.gradle script, define a processor symbols per flavor and add this module as dependency
```gradle
apply from: '../compat/compat.gradle'

android{
    flavorDimensions "compatibility"
    productFlavors {
        androidx {
            dimension "compatibility"
            processor.symbols "ANDROIDX"
        }
        support {
            dimension "compatibility"
            processor.symbols "SUPPORT"
        }
    }
}

dependencies {
    api project(path: ':compat')
}
```

### 1. Imports in Java classes

Instead of duplicate your Java source, just to have different imports, you can use conditional imports base on previous preprocessor symbols. We will preprocess your sources for every build and comment/uncomment the right one.

```java
package com.namespace.yours;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

public class YourClass {
``` 

### 2. Conditional imports in XML layouts

Instead of duplicate your XML layouts
You can replace the namespace of standard view components to es.rafaco.compat. This will automatically apply the correct view component (support or androidx)

```xml
<!-- Replace: 
    <androidx.appcompat.widget.AppCompatButton  /> and
    <android.support.v7.widget.AppCompatButton  /> -->
<!-- By: -->
<es.rafaco.compat.AppCompatButton
    android:id="@+id/button"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
``` 

Only the needed wrappers by my library has been created. Wrappers use conditional sources and are preprocessed as well.