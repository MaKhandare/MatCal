# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# keep the main Biweekly library classes and names intact.
-keep class net.sf.biweekly.** { *; }
-keepnames class net.sf.biweekly.** { *; }
-dontwarn net.sf.biweekly.**
# keep the "Vinnie" library (com.github.mangstadt). Biweekly relies on this
-keep class com.github.mangstadt.** { *; }

# ensure .properties files are moved/renamed to match the class packages.
# ensures that getResourceAsStream() can find the files inside the APK.
-adaptresourcefilenames **.properties

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile