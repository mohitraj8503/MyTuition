package com.example.mytuition.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import java.io.File
import java.security.MessageDigest

/**
 * AppGuard — Runtime anti-tamper & anti-reverse-engineering checks.
 *
 * Runs at app startup. Kills the process if the APK has been repackaged,
 * signed with a different key, or is running in a suspicious environment.
 */
object AppGuard {

    // SHA-256 of the ORIGINAL release signing certificate.
    // To get this value run:
    //   keytool -printcert -jarfile app-release.apk
    // and paste the SHA-256 fingerprint here (no colons, uppercase).
    // For debug builds set ENFORCE_SIGNATURE = false.
    private const val EXPECTED_CERT_SHA256 =
        "REPLACE_WITH_YOUR_RELEASE_CERT_SHA256_FINGERPRINT"
    private const val ENFORCE_SIGNATURE = false // set true after you have release cert

    /**
     * Call this from Application.onCreate() BEFORE anything else.
     */
    fun init(context: Context) {
        if (isDebuggerAttached()) die("Debugger detected")
        if (isEmulatorSuspicious()) die("Emulator detected")
        if (isRooted()) die("Rooted device")
        if (ENFORCE_SIGNATURE && !isSignatureValid(context)) die("Tampered APK")
    }

    // ── Debugger ──────────────────────────────────────────────────────────────

    private fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    // ── Emulator ─────────────────────────────────────────────────────────────

    private fun isEmulatorSuspicious(): Boolean {
        val build = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val product = Build.PRODUCT.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val hardware = Build.HARDWARE.lowercase()

        return (build.contains("generic")
                || build.contains("unknown")
                || model.contains("google_sdk")
                || model.contains("emulator")
                || model.contains("android sdk built for x86")
                || manufacturer.contains("genymotion")
                || product.contains("sdk")
                || product.contains("vbox")
                || product.contains("emulator")
                || hardware.contains("goldfish")
                || hardware.contains("ranchu"))
    }

    // ── Root Detection ────────────────────────────────────────────────────────

    private val ROOT_PATHS = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/system/bin/.ext/.su",
        "/system/usr/we-need-root/su-backup"
    )

    private val ROOT_PACKAGES = arrayOf(
        "com.noshufou.android.su",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.topjohnwu.magisk",
        "io.github.huskydg.magisk",
        "com.kingroot.kinguser"
    )

    private fun isRooted(): Boolean {
        // Check for su binaries
        if (ROOT_PATHS.any { File(it).exists() }) return true

        // Check for root management apps
        try {
            val pm = android.app.ActivityThread.currentApplication()?.packageManager
            if (pm != null) {
                for (pkg in ROOT_PACKAGES) {
                    try {
                        pm.getPackageInfo(pkg, 0)
                        return true // package found
                    } catch (_: PackageManager.NameNotFoundException) { }
                }
            }
        } catch (_: Exception) { }

        // Try executing su
        return try {
            Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            true
        } catch (_: Exception) {
            false
        }
    }

    // ── Signature Verification ────────────────────────────────────────────────

    private fun isSignatureValid(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pm.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                info.signatures
            } ?: return false

            val md = MessageDigest.getInstance("SHA-256")
            val certHash = signatures.map { sig ->
                md.digest(sig.toByteArray())
                    .joinToString("") { "%02X".format(it) }
            }

            certHash.any { it == EXPECTED_CERT_SHA256 }
        } catch (e: Exception) {
            false
        }
    }

    // ── Die ───────────────────────────────────────────────────────────────────

    private fun die(reason: String) {
        // No toast, no dialog — silent kill to confuse attackers
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
