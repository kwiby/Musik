package com.example.musik.crash_handling

import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.example.musik.MainActivity
import kotlin.system.exitProcess

class CrashHandler(
	private val context: Context
) : Thread.UncaughtExceptionHandler {
	override fun uncaughtException(t: Thread, e: Throwable) {
		val stackTrace = Log.getStackTraceString(e)

		val intent = Intent(context, MainActivity::class.java).apply {
			putExtra("crash_log", stackTrace)
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		context.startActivity(intent)

		Process.killProcess(Process.myPid())
		exitProcess(1)
	}
}