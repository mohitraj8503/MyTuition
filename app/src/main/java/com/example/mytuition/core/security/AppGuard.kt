package com.example.mytuition.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import java.io.File
import java.security.MessageDigest

object AppGuard {

    private const val EXPECTED_CERT_SHA256 =
        "REPLACE_WITH_YOUR_RELEASE_CERT_SHA256_FINGERPRINT"
    private const val ENFORCE_SIGNATURE = false

    fun init(context: Context) {
        if (com.example.BuildConfig.DEBUG) return
        if (isDebuggerAttached()) die()
        if (isEmulatorSuspicious()) die()
        if (isRooted(context)) die()
        if (ENFORCE_SIGNATURE && !isSignatureValid(context)) die()
    }

    private fun isDebuggerAttached(): Boolean =
        Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    private fun isEmulatorSuspicious(): Boolean {
        val b = Build.FINGERPRINT.lowercase()
        val m = Build.MODEL.lowercase()
        val p = Build.PRODUCT.lowercase()
        val mfr = Build.MANUFACTURER.lowercase()
        val hw = Build.HARDWARE.lowercase()
        return b.contains("generic") || b.contains("unknown")
                || m.contains("google_sdk") || m.contains("emulator")
                || m.contains("android sdk built for x86")
                || mfr.contains("genymotion")
                || p.contains("sdk") || p.contains("vbox") || p.contains("emulator")
                || hw.contains("goldfish") || hw.contains("ranchu")
    }

    private val ROOT_PATHS = arrayOf(
        "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
        "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
        "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
    )

    private val ROOT_PACKAGES = arrayOf(
        "com.noshufou.android.su", "eu.chainfire.supersu",
        "com.koushikdutta.superuser", "com.topjohnwu.magisk",
        "io.github.huskydg.magisk", "com.kingroot.kinguser"
    )

    private fun isRooted(context: Context): Boolean {
        if (ROOT_PATHS.any { File(it).exists() }) return true
        val pm = context.packageManager
        for (pkg in ROOT_PACKAGES) {
            try { pm.getPackageInfo(pkg, 0); return true }
            catch (_: PackageManager.NameNotFoundException) { }
        }
        return try {
            Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            true
        } catch (_: Exception) { false }
    }

    private fun isSignatureValid(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }
            val sigs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                info.signingInfo?.apkContentsSigners
            else {
                @Suppress("DEPRECATION")
                info.signatures
            } ?: return false
            val md = MessageDigest.getInstance("SHA-256")
            sigs.map { md.digest(it.toByteArray()).joinToString("") { b -> "%02X".format(b) } }
                .any { it == EXPECTED_CERT_SHA256 }
        } catch (_: Exception) { false }
    }

    private fun die() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
