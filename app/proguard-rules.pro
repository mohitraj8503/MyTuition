# ============================================================
#  MyTuition — ProGuard / R8 Rules
#  Purpose: Maximum obfuscation + anti-reverse-engineering
# ============================================================

# ---------- R8 Full Mode (most aggressive) ----------
-allowaccessmodification
-repackageclasses 'a'          # Moves all classes into package 'a' (flat, opaque)
-overloadaggressively           # Reuses same names for different methods/fields
-mergeinterfacesaggressively    # Collapses interfaces into single class

# ---------- Keep only the app entry point ----------
-keep public class com.example.mytuition.MainActivity { *; }
-keep public class com.aistudio.mytuition.abxycd.** extends android.app.Application { *; }

# ---------- Keep Android framework hooks ----------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

# ---------- Jetpack Compose — keep only what Compose runtime needs ----------
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ---------- Firebase — keep model classes used by Firestore ----------
# Firestore needs no-arg constructors and field names intact for deserialization
-keep class com.example.mytuition.core.data.model.** { *; }
-keepclassmembers class com.example.mytuition.core.data.model.** {
    <init>();
    <fields>;
}
# Firebase internals
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ---------- Kotlin metadata (needed for reflection) ----------
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$Companion { *; }
-keepclassmembers @kotlinx.parcelize.Parcelize class * { *; }

# ---------- Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ---------- Room Database ----------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.**

# ---------- Moshi / Retrofit ----------
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonClass class *
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ---------- Coil (Image loading) ----------
-dontwarn coil.**

# ---------- Anti-decompilation tricks ----------
# Remove all logging (Log.d, Log.v, Log.i, Log.w) from release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static boolean isLoggable(...);
}
# Remove Timber logs
-assumenosideeffects class timber.log.Timber {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove all toString() overrides (stops class name inspection)
-assumenosideeffects class java.lang.StringBuilder {
    public java.lang.String toString();
}

# ---------- Keep enums intact (needed for when-expression) ----------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
    public *;
}

# ---------- Parcelable ----------
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ---------- Serializable ----------
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ---------- Suppress warnings for optional deps ----------
-dontwarn javax.annotation.**
-dontwarn org.codehaus.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.**
