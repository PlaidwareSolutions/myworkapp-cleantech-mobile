package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityDeviceSettingsBinding
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.PreferenceManager
import com.example.rfidapp.util.constants.Constants


class DeviceSettingsActivity : ActBase<ActivityDeviceSettingsBinding>() {

    private var audioManager: AudioManager? = null
    private var power: Int = 6
    private var volume: Int = 0

    override fun setViewBinding() = ActivityDeviceSettingsBinding.inflate(layoutInflater)

    override fun bindObjects() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager?

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.device_settings)

                btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    override fun bindMethods() {
        setPower()
        setVolume()
    }

    @SuppressLint("SetTextI18n")
    fun setPower() {
        if (isC5Device) {
            power = mReader.power
        } else if (!isBTDevice) {
            highlightToast("Please Use Any RFID Device", 2)
        } else if (isBtConnect) {
            power = mBtReader.power
        } else {
            highlightToast("Please Connect Device First..", 2)
        }
        val safePower = power.coerceIn(6, 30)
        binding.slPwr.value = safePower.toFloat()
        binding.tvPwr.text = "$safePower dBm"

        binding.slPwr.addOnChangeListener { _, f, _ ->
            if ((!PreferenceManager.getStringValue(Constants.GET_DEVICE)
                    .equals(
                        ExifInterface.GPS_MEASUREMENT_2D,
                        ignoreCase = true
                    ) || !mReader.isWorking) && (!PreferenceManager.getStringValue(
                    Constants.GET_DEVICE
                ).equals(
                    "1",
                    ignoreCase = true
                ) || !isBtConnect || !mBtReader.isWorking)
            ) {
                val i = f.toInt()
                power = i
                binding.tvPwr.text = "$i dBm"
                return@addOnChangeListener
            }
            highlightToast("Please Stop Reading First", 2)
        }

        binding.btSetPwr.setOnClickListener {
//            if (isC5Device) {
            if (true) {
                if (mReader.setPower(power)) {
                    highlightToast("Power Set Successfully", 1)
                } else {
                    highlightToast("Power Set Failure", 2)
                }
            } else if (!isBTDevice) {
                highlightToast("Please Use Any RFID Device", 2)
            } else if (!isBtConnect) {
                highlightToast("Please Connect Device First..", 2)
            } else if (mBtReader.setPower(power)) {
                highlightToast("Power Set Successfully", 1)
            } else {
                highlightToast("Power Set Failure", 2)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setVolume() {
        binding.slVol.setValueTo(audioManager!!.getStreamMaxVolume(3).toFloat())
        volume = audioManager!!.getStreamVolume(3)
        binding.slVol.value = volume.toFloat()
        binding.tvVol.text = "$volume Unit"
        binding.slVol.addOnChangeListener { _, f, _ ->
            volume = f.toInt()
            binding.tvVol.text = "$volume Unit"
        }
        binding.btSetVol.setOnClickListener {
            audioManager!!.setStreamVolume(3, volume, 0)
            highlightToast("Volume Set Successfully", 1)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == -1 && requestCode == 123 && data != null) {
            openCsvFile(data.data)
        }
    }

    private fun openCsvFile(uri: Uri?) {
        try {
            val intent = Intent("android.intent.action.VIEW")
            intent.setDataAndType(uri, "*/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                showToast("No app available to open the CSV file")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error opening CSV file")
        }
    }

    override fun onResume() {
        checkBTConnect()
        super.onResume()
    }
}