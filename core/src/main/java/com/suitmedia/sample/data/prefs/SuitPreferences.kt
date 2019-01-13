package com.suitmedia.sample.data.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.common.reflect.TypeParameter
import com.google.gson.Gson
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by dodydmw19 on 7/18/18.
 */

open class SuitPreferences {

    companion object {

        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding"
        private const val SECRET_KEY_HASH_TRANSFORMATION = "SHA-256"
        private const val CHARSET = "UTF-8"

        private var encryptKeys: Boolean = false
        private var writer: Cipher? = null
        private var reader: Cipher? = null
        private var keyWriter: Cipher? = null
        private var instance: SuitPreferences? = null
        private var mSharedPreferences: SharedPreferences? = null

        @SuppressLint("GetInstance")
        @Throws(SecurePreferencesException::class)
        fun init(context: Context, secureKey: String, encryptKeys: Boolean) {

            try {
                this.writer = Cipher.getInstance(TRANSFORMATION)
                this.reader = Cipher.getInstance(TRANSFORMATION)
                this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION)

                initCiphers(secureKey)

                mSharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

                this.encryptKeys = encryptKeys
            } catch (e: GeneralSecurityException) {
                throw SecurePreferencesException(e)
            } catch (e: UnsupportedEncodingException) {
                throw SecurePreferencesException(e)
            }
        }

        fun instance(): SuitPreferences? {
            if (instance == null) {
                validateInitialization()
                synchronized(SuitPreferences::class.java) {
                    if (instance == null) {
                        instance = SuitPreferences()
                    }
                }
            }
            return instance
        }

        private fun validateInitialization() {
            if (mSharedPreferences == null)
                throw SuitException("SuitPreferences Library must be initialized inside your application class by calling SuitPreferences.init(getApplicationContext)")
        }

        @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class)
        protected fun initCiphers(secureKey: String) {
            val ivSpec = getIv()
            val secretKey = getSecretKey(secureKey)

            writer?.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            reader?.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            keyWriter?.init(Cipher.ENCRYPT_MODE, secretKey)
        }

        private fun getIv(): IvParameterSpec {
            val iv = writer?.blockSize?.let { ByteArray(it) }
            writer?.blockSize?.let { System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".toByteArray(), 0, iv, 0, it) }
            return IvParameterSpec(iv)
        }

        @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
        private fun getSecretKey(key: String): SecretKeySpec {
            val keyBytes = createKeyBytes(key)
            return SecretKeySpec(keyBytes, TRANSFORMATION)
        }

        @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
        protected fun createKeyBytes(key: String?): ByteArray {
            val md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION)
            md.reset()
            return md.digest(key?.toByteArray(Charset.forName(CHARSET)))
        }

        @Throws(SecurePreferencesException::class)
        protected fun encrypt(value: String, writer: Cipher): String {
            val secureValue: ByteArray
            try {
                secureValue = convert(writer, value.toByteArray(Charset.forName(CHARSET)))
            } catch (e: UnsupportedEncodingException) {
                throw SecurePreferencesException(e)
            }

            return Base64.encodeToString(secureValue, Base64.NO_WRAP)
        }

        protected fun decrypt(securedEncodedValue: String): String {
            val securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP)
            val value = reader?.let { convert(it, securedValue) }
            try {
                return value?.let { String(it, Charset.forName(CHARSET)) }.toString()
            } catch (e: UnsupportedEncodingException) {
                throw SecurePreferencesException(e)
            }
        }

        @Throws(SecurePreferencesException::class)
        private fun convert(cipher: Cipher, bs: ByteArray): ByteArray {
            try {
                return cipher.doFinal(bs)
            } catch (e: Exception) {
                throw SecurePreferencesException(e)
            }
        }
    }

    fun saveInt(key: String, value: Int?) {
        if (value != null) {
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(value.toString(), it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun getInt(key: String): Int? {
        return if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String = mSharedPreferences?.getString(toKey(key), "").toString()
            decrypt(securedEncodedValue).toInt()
        } else 0
    }

    fun saveBoolean(key: String, value: Boolean?) {
        if (value != null) {
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(value.toString(), it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun getBoolean(key: String): Boolean? {
        return if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String = mSharedPreferences?.getString(toKey(key), "").toString()
            decrypt(securedEncodedValue).toBoolean()
        } else false
    }

    fun saveFloat(key: String, value: Float?) {
        if (value != null) {
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(value.toString(), it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun getFloat(key: String): Float? {
        return if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String = mSharedPreferences?.getString(toKey(key), "").toString()
            decrypt(securedEncodedValue).toFloat()
        } else 0.0f
    }

    fun saveLong(key: String, value: Long?) {
        if (value != null) {
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(value.toString(), it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun getLong(key: String): Long? {
        return if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String = mSharedPreferences?.getString(toKey(key), "").toString()
            decrypt(securedEncodedValue).toLong()
        } else 0
    }

    fun saveString(key: String, value: String?) {
        if (value != null) {
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(value, it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun getString(key: String): String? {
        return if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String = mSharedPreferences?.getString(toKey(key), "").toString()
            decrypt(securedEncodedValue)
        } else ""
    }

    fun <T> saveObject(key: String, `object`: T) {
        if (`object` != null) {
            val objectString = Gson().toJson(`object`)
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(objectString, it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun <T> getObject(key: String, classType: Class<T>): T? {
        if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String? = mSharedPreferences?.getString(toKey(key), "").toString()
            if (securedEncodedValue != null) {
                return Gson().fromJson(decrypt(securedEncodedValue.toString()), classType)
            }
        }
        return null
    }

    fun <T> saveObjectsList(key: String, objectList: List<T>?) {
        if (objectList != null && objectList.isNotEmpty()) {
            val objectString = Gson().toJson(objectList)
            val editor = mSharedPreferences?.edit()
            val secureValueEncoded = writer?.let { encrypt(objectString, it) }
            editor?.putString(toKey(key), secureValueEncoded)
            editor?.apply()
        }
    }

    fun <T> getObjectsList(key: String, classType: Class<T>): List<T>? {
        if (mSharedPreferences?.contains(toKey(key))!!) {
            val securedEncodedValue: String? = mSharedPreferences?.getString(toKey(key), "").toString()

            //val objectString = mSharedPreferences?.getString(key, null)
            if (securedEncodedValue?.let { decrypt(it) } != null) {
                return Gson().fromJson<List<T>>(decrypt(securedEncodedValue), object : com.google.common.reflect.TypeToken<List<T>>() {

                }
                        .where(object : TypeParameter<T>() {

                        }, classType)
                        .type)
            }
        }

        return null
    }

    fun clearSession() {
        val editor = mSharedPreferences?.edit()
        editor?.clear()
        editor?.apply()
    }

    fun deleteValue(key: String): Boolean? {
        if (this.isKeyExists(key)) {
            val editor = mSharedPreferences?.edit()
            editor?.remove(key)
            editor?.apply()
            return true
        }
        return false
    }

    private fun isKeyExists(key: String): Boolean {
        val map = mSharedPreferences?.all
        return map != null && map.containsKey(key)
    }

    private fun toKey(key: String): String {
        return if (encryptKeys)
            keyWriter?.let { encrypt(key, it) }.toString()
        else
            key
    }

    class SecurePreferencesException(e: Throwable) : RuntimeException(e)

}