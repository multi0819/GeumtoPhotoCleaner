package com.example.geumtophotocleaner

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
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

    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) runScan() else status.text = "사진 접근 권한이 필요합니다." }
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
        val p = Manifest.permission.READ_MEDIA_IMAGES
        if (android.os.Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) permission.launch(p)
        else runScan()
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
