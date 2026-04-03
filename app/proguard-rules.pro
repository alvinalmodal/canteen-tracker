# Apache POI - keep reflection-dependent classes
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn javax.xml.**

# ML Kit
-keep class com.google.mlkit.** { *; }
