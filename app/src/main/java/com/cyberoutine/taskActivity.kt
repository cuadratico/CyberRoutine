package com.cyberoutine

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.workDataOf
import com.cyberoutine.biochips.noti_chip
import com.cyberoutine.db.db_task
import com.cyberoutine.recy_task.adapter_task
import com.cyberoutine.recy_task.data_task
import com.cyberoutine.recy_task.task_list
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.random.nextInt

var update_recy = true
class taskActivity : AppCompatActivity() {
    private lateinit var adapter: adapter_task
    private lateinit var task_corrutine: Job
    private var space = 0
    private val db_task = db_task(this)
    private lateinit var secure_space: ConstraintLayout
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val mk = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val pref = EncryptedSharedPreferences.create(this, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        adapter = adapter_task(task_list)

        val configuration = findViewById<ConstraintLayout>(R.id.configuration)
        val image_configuration = findViewById<ImageView>(R.id.image_configuration)
        val name_configuration = findViewById<TextView>(R.id.name_configuration)
        val money_configuration = findViewById<TextView>(R.id.money_configuration)
        val edit_money = findViewById<ShapeableImageView>(R.id.edit_money)
        secure_space = findViewById<ConstraintLayout>(R.id.secure_space)

        if (pref.getBoolean("NeuroMoney-Infinite", false)){
            edit_money.visibility = View.VISIBLE
        }else {
            edit_money.visibility = View.INVISIBLE
        }
        if (pref.getBoolean("NeuroSecure-S001", false)){
            secure_space.visibility = View.VISIBLE
        }else {
            secure_space.visibility = View.INVISIBLE
        }

        edit_money.setOnClickListener {
            val dilaog_money_edit = Dialog(this)
            val view_money_edit = LayoutInflater.from(this).inflate(R.layout.dialog_money_biochip, null)

            val add_change = view_money_edit.findViewById<AppCompatButton>(R.id.add_change)
            val money_edit = view_money_edit.findViewById<EditText>(R.id.money)

            money_edit.setText(money_configuration.text.toString())

            add_change.setOnClickListener {
                pref.edit().putInt("money", money_edit.text.toString().toInt()).commit()
                money_configuration.text = money_edit.text.toString()
                dilaog_money_edit.dismiss()
            }

            dilaog_money_edit.setContentView(view_money_edit)
            dilaog_money_edit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dilaog_money_edit.setCancelable(false)
            dilaog_money_edit.show()
        }

        val recy = findViewById<RecyclerView>(R.id.recy)

        val chip_store = findViewById<ConstraintLayout>(R.id.chip_store)
        val add = findViewById<ConstraintLayout>(R.id.add)

        money_configuration.text = pref.getInt("money", 0).toString()
        hability_points(pref)

        if (!pref.getBoolean("start", false)){
            finish()
            startActivity(Intent(this, SelectorActivity::class.java))
        }

        task_corrutine = lifecycleScope.launch (Dispatchers.IO, start = CoroutineStart.LAZY){
            while (true){
                if (update_recy){
                    withContext (Dispatchers.Main){
                        adapter.update()
                        money_configuration.text = pref.getInt("money", 0).toString()
                    }
                    update_recy = false
                }
            }
            delay(500)
        }
        task_corrutine.start()
        chip_store.setOnClickListener {
            startActivity(Intent(this, chipActivity::class.java))
            finish()
        }

        configuration.setOnClickListener {
            startActivity(Intent(this, CharacterActivity::class.java))
        }

        val (image, name) = characters[pref.getInt("character", 0)]
        image_configuration.setImageResource(image)
        name_configuration.text = name

        recy.adapter = adapter
        recy.layoutManager = LinearLayoutManager(this)



        if (db_task.extraccion(0)){
            adapter.update()
        }

        add.setOnClickListener {
            val random_number = Random.nextInt(0, 4)
            var habi = list_habilidad[random_number]

            val dialog_add_task = Dialog(this)
            val view_add_task = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)

            val selector_ha = view_add_task.findViewById<ShapeableImageView>(R.id.habilidad_selector)
            val input_task = view_add_task.findViewById<EditText>(R.id.task)
            val add_task = view_add_task.findViewById<AppCompatButton>(R.id.add_task)

            selector_ha.setImageResource(habilidades[habi]!!.imagen)
            selector_ha.setOnClickListener {
                val selector_dialog = AlertDialog.Builder(this)
                    .setSingleChoiceItems (list_habilidad, random_number){_, position ->
                        habi = list_habilidad[position]
                        selector_ha.setImageResource(habilidades[habi]!!.imagen)
                    }
                selector_dialog.show()
            }

            add_task.setOnClickListener {
                var texto_final = input_task.text.toString()
                var iv = ""
                if (space == 1) {
                    val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                    val c = Cipher.getInstance("AES/GCM/NoPadding")
                    c.init(Cipher.ENCRYPT_MODE, ks.getKey("key", null))
                    texto_final = java.util.Base64.getEncoder().encodeToString(c.doFinal(input_task.text.toString().toByteArray()))
                    iv = java.util.Base64.getEncoder().encodeToString(c.iv)
                }
                var money = Random.nextInt(200, 1000)
                if(pref.getBoolean("NeuroMoney-Simple", false)){
                    money = 1000
                }
                db_task.add(pref, texto_final, habi, money, space, iv)
                task_list.add(data_task(task_list.size, input_task.text.toString(), habi, money, iv))
                dialog_add_task.dismiss()
                adapter.update()
            }

            dialog_add_task.setContentView(view_add_task)
            dialog_add_task.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_add_task.show()
        }

        secure_space.setOnClickListener {
            space = 1
            if (db_task.extraccion(space) && BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS){
                        val promt = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Authenticate yourself")
                            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                            .build()

                        BiometricPrompt(this, ContextCompat.getMainExecutor(this), object: BiometricPrompt.AuthenticationCallback(){
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)

                                for (task in task_list){
                                    val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

                                    val c = Cipher.getInstance("AES/GCM/NoPadding")
                                    c.init(Cipher.DECRYPT_MODE, ks.getKey("key", null), GCMParameterSpec(128, java.util.Base64.getDecoder().decode(task.iv)))

                                    task.task = String(c.doFinal(java.util.Base64.getDecoder().decode(task.task.toByteArray())))
                                }
                                adapter.update()
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()

                            }
                        }).authenticate(promt)
                }else {
                    Toast.makeText(this, "You are in safe mode", Toast.LENGTH_SHORT).show()
                    adapter.update()
                }
            secure_space.visibility = View.INVISIBLE


        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onBackPressed() {

        if (space == 1){
            secure_space.visibility = View.VISIBLE
            space = 0
            if (db_task.extraccion(space)){}
            adapter.update()
        }else {
            super.onBackPressed()
        }

    }

}