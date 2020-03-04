package com.example.elshad.camerax

import android.graphics.SurfaceTexture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textureView = TextureView(this)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            bindPreview(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .setTargetName("Preview")
            .build()
        preview.setSurfaceProvider()
        val cameraSelector = buildCameraSelector()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }

    private fun Preview.setSurfaceProvider() {
        setSurfaceProvider {
            val surfaceTexture = SurfaceTexture(0)
            surfaceTexture.setDefaultBufferSize(it.resolution.width, it.resolution.height)
            val surface = Surface(surfaceTexture)
            textureView.surfaceTexture = surfaceTexture
            reAppendTextureView()
            it.provideSurface(surface, {
            }, {
                surface.release()
                surfaceTexture.release()
            })
        }
    }

    private fun reAppendTextureView() {
        val parent = container as ViewGroup
        parent.removeView(textureView)
        parent.addView(textureView)
    }

    private fun buildCameraSelector() = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

}
