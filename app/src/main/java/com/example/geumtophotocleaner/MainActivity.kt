package com.example.geumtophotocleaner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val items = mutableListOf<PhotoItem>()
    private lateinit var adapter: PhotoAdapter
    private lateinit var status: TextView
    private lateinit var progress: ProgressBar

    private val deleteResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { r ->
        if (r.resultCode == Activity.RESULT_OK) { status.text = "삭제가 완료되었습니다."; runScan() }
        else status.text = "삭제가 취소되었습니다."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        status = findViewById(R.id.status); progress = findViewById(R.id.progress)
        adapter = PhotoAdapter(items)
        findViewById<RecyclerView>(R.id.list).apply { layoutManager = LinearLayoutManager(this@MainActivity); adapter = this@MainActivity.adapter }
        findViewById<Button>(R.id.scan).setOnClickListener { ensurePermissionAndScan() }
        findViewById<Button>(R.id.selectAll).setOnClickListener { adapter.selectAll(); status.text = "${items.size}장 전체 선택됨" }
        findViewById<Button>(R.id.delete).setOnClickListener { deleteSelected() }
    }

    private fun ensurePermissionAndScan() {
        when (currentMediaAccess()) {
            MediaAccess.FULL -> runScan()
            MediaAccess.PARTIAL -> showFullAccessDialog()
            MediaAccess.DENIED -> showFullAccessDialog()
        }
    }

    private fun currentMediaAccess(): MediaAccess {
        val fullPermission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val fullGranted = ContextCompat.checkSelfPermission(this, fullPermission) == PackageManager.PERMISSION_GRANTED
        val partialGranted = Build.VERSION.SDK_INT >= 34 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
        return PhotoAccess.resolve(fullGranted, partialGranted)
    }

    private fun showFullAccessDialog() {
        status.text = "전체 사진 권한을 확인해 주세요."
        AlertDialog.Builder(this)
            .setTitle("모든 사진 허용 필요")
            .setMessage("사진 선택창은 열지 않습니다. 설정에서 사진 및 동영상 권한을 '항상 모두 허용'으로 확인한 뒤 앱으로 돌아와 사진 찾기를 다시 눌러 주세요.")
            .setNegativeButton("취소", null)
            .setPositiveButton("설정 열기") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
            }
            .show()
    }

    private fun runScan() {
        progress.visibility = View.VISIBLE; status.text = "금토동 사진을 확인하는 중…"
        thread {
            val found = PhotoScanner(this).scan { n -> runOnUiThread { status.text = "사진 ${n}장 확인 중…" } }
            runOnUiThread { items.clear(); items.addAll(found); adapter.notifyDataSetChanged(); progress.visibility = View.GONE; status.text = "어제까지 촬영한 금토동 사진 ${items.size}장을 찾았습니다." }
        }
    }

    private fun deleteSelected() {
        val uris = items.filter { it.selected }.map { it.uri }
        if (uris.isEmpty()) { status.text = "삭제할 사진을 선택하세요."; return }
        val pi: PendingIntent = MediaStore.createDeleteRequest(contentResolver, uris)
        deleteResult.launch(androidx.activity.result.IntentSenderRequest.Builder(pi.intentSender).build())
    }
}
