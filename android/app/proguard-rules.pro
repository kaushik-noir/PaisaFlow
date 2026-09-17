# Keep Kotlinx Serialization metadata and serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep PaisaFlow serializable model classes
-keep class com.paisaflow.app.model.** { *; }

# Keep generated Kotlinx Serialization serializers
-keepclassmembers class com.paisaflow.app.model.** {
    *** Companion;
}

-keepclasseswithmembers class com.paisaflow.app.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit API interfaces
-keep interface com.paisaflow.app.data.network.** { *; }

# Retrofit annotations / signatures
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Preserve enum members used with serialization
-keepclassmembers enum com.paisaflow.app.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
