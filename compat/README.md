
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


On your module/s build.gradle:
- apply compat.gradle script(//TODO!). 
- Define a processor symbols per flavor if you already have then, otherwise copy and add this module as follows. To finish add a dynamic dependency to the compat library
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

// Dynamic dependency base on selected productFlavors
def compatVersion = "0.0.50"
android.applicationVariants.all { variant ->
    if (variant.flavorName == 'androidx'){
        dependencies.androidxApi "org.inappdevtools:comnpat_androidx:$compatVersion"
    }
    else{
        dependencies.supportApi "org.inappdevtools:comnpat_support:$compatVersion"
    }
}

dependencies {
    (YOUR_DEPENDENCIES)
}
```

### 1. Conditional java sources

Instead of duplicate your Java classes for each library, you can use conditional source base on selected variant. Our pre-processor will comment/uncomment the right one at build time.

```java
package com.namespace.yours;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

public class YourClass {
``` 
This is specially useful on imports as shown on the previous example. It can also be used anywhere on your java source files: package, class signature, method name, implementations, comments...

### 2. Conditional namespace in XML layouts

Instead of duplicate your XML layouts, you can replace the namespace of standard view components to org.inappdevtools.compat. This will automatically apply the correct view component base on the build variant (support or androidx)

```xml
<!-- Replace: 
    <androidx.appcompat.widget.AppCompatButton
        .../> and
    <android.support.v7.widget.AppCompatButton  /> -->
<!-- By: -->
<org.inappdevtools.compat.AppCompatButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
``` 

Only the needed wrappers by my library has been created. Wrappers use conditional sources and are preprocessed as well.