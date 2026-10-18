package com.nestcheck.app.data.remote

import android.content.Context
import android.util.Base64
import android.util.Log
import com.nestcheck.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

object EmailService {
    private const val TAG = "EmailService"
    private const val PREFS_NAME = "nestcheck_secure_email_prefs"
    private const val KEY_SENDER = "gmail_sender"
    private const val KEY_SECRET = "gmail_secret_key"

    fun getSenderEmail(context: Context): String {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = sp.getString(KEY_SENDER, null)
        if (!saved.isNullOrBlank()) return saved
        return if (BuildConfig.GMAIL_SENDER.isNotBlank()) BuildConfig.GMAIL_SENDER else "workwithdevraj@gmail.com"
    }

    fun getSecretKey(context: Context): String {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = sp.getString(KEY_SECRET, null)
        if (!saved.isNullOrBlank()) return saved
        return BuildConfig.GMAIL_API_KEY
    }

    fun saveCredentials(context: Context, sender: String, secretKey: String) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_SENDER, sender.trim())
            .putString(KEY_SECRET, secretKey.trim())
            .apply()
    }

    /**
     * Sends a real verification OTP email to recipient.
     * Uses SSL socket to smtp.gmail.com:465 with AUTH LOGIN if a 16-character or standard password/App Password is provided.
     * Falls back to Gmail REST API if a Google API Key / Access Token is provided.
     */
    suspend fun sendOtpEmail(
        context: Context,
        recipient: String,
        otpCode: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val sender = getSenderEmail(context)
        val secret = getSecretKey(context)

        if (secret.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Gmail key is missing. Please configure your Gmail App Password or API Key in settings.")
            )
        }

        // Try SMTP over SSL first (most reliable for Gmail App Passwords)
        try {
            sendViaSmtp(
                senderEmail = sender,
                passwordOrAppPassword = secret.replace(" ", ""),
                recipientEmail = recipient,
                otpCode = otpCode
            )
            Log.d(TAG, "OTP email successfully delivered via Gmail SMTP to $recipient")
            return@withContext Result.success(Unit)
        } catch (smtpEx: Exception) {
            Log.w(TAG, "SMTP send failed, trying HTTP REST API: ${smtpEx.message}")
            // Fallback: Attempt Google Gmail REST API if it's an API token
            try {
                sendViaGmailRestApi(secret, sender, recipient, otpCode)
                Log.d(TAG, "OTP email successfully delivered via Gmail REST to $recipient")
                return@withContext Result.success(Unit)
            } catch (restEx: Exception) {
                Log.e(TAG, "Both SMTP and REST failed", restEx)
                return@withContext Result.failure(
                    Exception("Failed to send OTP to $recipient: ${smtpEx.message}")
                )
            }
        }
    }

    private fun sendViaSmtp(
        senderEmail: String,
        passwordOrAppPassword: String,
        recipientEmail: String,
        otpCode: String
    ) {
        val host = "smtp.gmail.com"
        val port = 465

        val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
        val socket = factory.createSocket(host, port) as SSLSocket
        socket.soTimeout = 15000

        val reader = BufferedReader(InputStreamReader(socket.inputStream, Charsets.UTF_8))
        val writer = BufferedWriter(OutputStreamWriter(socket.outputStream, Charsets.UTF_8))

        fun readLineExpected(expectedCodePrefix: String): String {
            val line = reader.readLine() ?: throw IllegalStateException("Connection closed unexpectedly by SMTP server")
            Log.d(TAG, "SMTP << $line")
            if (!line.startsWith(expectedCodePrefix)) {
                throw IllegalStateException("SMTP Error: expected $expectedCodePrefix, received: $line")
            }
            return line
        }

        fun sendCommand(cmd: String) {
            Log.d(TAG, "SMTP >> $cmd")
            writer.write(cmd + "\r\n")
            writer.flush()
        }

        // 1. Initial greeting
        readLineExpected("220")

        // 2. EHLO
        sendCommand("EHLO localhost")
        while (true) {
            val ehloLine = reader.readLine() ?: break
            Log.d(TAG, "SMTP << $ehloLine")
            if (ehloLine.startsWith("250 ")) break
        }

        // 3. AUTH LOGIN
        sendCommand("AUTH LOGIN")
        readLineExpected("334")

        // 4. Send base64 username
        val b64User = Base64.encodeToString(senderEmail.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        sendCommand(b64User)
        readLineExpected("334")

        // 5. Send base64 password
        val b64Pass = Base64.encodeToString(passwordOrAppPassword.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        sendCommand(b64Pass)
        readLineExpected("235") // 235 2.7.0 Accepted

        // 6. MAIL FROM
        sendCommand("MAIL FROM:<$senderEmail>")
        readLineExpected("250")

        // 7. RCPT TO
        sendCommand("RCPT TO:<$recipientEmail>")
        readLineExpected("250")

        // 8. DATA
        sendCommand("DATA")
        readLineExpected("354")

        // 9. Send headers and message body
        val messageBody = buildString {
            append("From: NestCheck Family <$senderEmail>\r\n")
            append("To: <$recipientEmail>\r\n")
            append("Subject: NestCheck Verification Code: $otpCode\r\n")
            append("MIME-Version: 1.0\r\n")
            append("Content-Type: text/html; charset=UTF-8\r\n")
            append("\r\n")
            append("""
                <!DOCTYPE html>
                <html>
                <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; padding: 24px;">
                    <div style="max-width: 480px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; padding: 32px; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);">
                        <div style="text-align: center; margin-bottom: 24px;">
                            <h2 style="color: #0b57d0; margin: 0; font-size: 24px;">NestCheck Family</h2>
                            <p style="color: #64748b; font-size: 14px; margin-top: 4px;">Smart Parental Controls & Safety</p>
                        </div>
                        <div style="text-align: center; padding: 24px 0; border-top: 1px solid #f1f5f9; border-bottom: 1px solid #f1f5f9;">
                            <p style="color: #0f172a; font-size: 16px; margin-bottom: 12px; font-weight: 500;">Your 4-Digit Verification Code:</p>
                            <div style="font-size: 38px; font-weight: 800; letter-spacing: 8px; color: #0b57d0; background-color: #eff6ff; padding: 16px 24px; border-radius: 12px; display: inline-block;">
                                $otpCode
                            </div>
                            <p style="color: #94a3b8; font-size: 13px; margin-top: 14px;">This code will expire in 10 minutes. Do not share it with anyone.</p>
                        </div>
                        <p style="color: #64748b; font-size: 12px; text-align: center; margin-top: 24px;">
                            If you did not request this code, someone may be attempting to access your NestCheck account.
                        </p>
                    </div>
                </body>
                </html>
            """.trimIndent())
            append("\r\n.\r\n")
        }

        writer.write(messageBody)
        writer.flush()
        readLineExpected("250")

        // 10. QUIT
        try {
            sendCommand("QUIT")
        } catch (_: Exception) {}

        socket.close()
    }

    private fun sendViaGmailRestApi(
        apiKeyOrToken: String,
        senderEmail: String,
        recipientEmail: String,
        otpCode: String
    ) {
        val rawMessage = "From: $senderEmail\r\nTo: $recipientEmail\r\nSubject: NestCheck Code: $otpCode\r\n\r\nYour NestCheck code is: $otpCode"
        val encodedMessage = Base64.encodeToString(rawMessage.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)

        val url = URL("https://gmail.googleapis.com/upload/gmail/v1/users/me/messages/send?key=$apiKeyOrToken")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "message/rfc822")
        conn.setRequestProperty("Authorization", "Bearer $apiKeyOrToken")
        conn.doOutput = true

        conn.outputStream.use { os ->
            os.write(rawMessage.toByteArray(Charsets.UTF_8))
        }

        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "HTTP $code"
            throw IllegalStateException("Gmail REST Error ($code): $err")
        }
    }
}
