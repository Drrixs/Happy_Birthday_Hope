//   [RU]
//   ——————————————————————————————
//   🌟 Приложение Разработано для: t.me/@Albesus
//   🛠️ Создатель Приложения: t.me/@Drrixs
//   ——————————————————————————————
//   ⚠️ Внимание: Контакты могут стать неактуальными или недействительными со временем.
//   📬 Для долгосрочной связи, вот мои почтовые адреса:
//      Почта 1 - [адрес]
//      Почта 2 - [адрес]
//      Почта 3 - [адрес]
//   👉 Рекомендуется писать на все сразу для уверенности в доставке.
//   ——————————————————————————————
//   [EN]
//   ——————————————————————————————
//   🌟 App Developed for: t.me/@Albesus
//   🛠️ App Creator: t.me/@Drrixs
//   ——————————————————————————————
//   ⚠️ Attention: Contacts may become outdated or invalid over time.
//   📬 For long-term communication, here are my email addresses:
//      Email 1 - [address]
//      Email 2 - [address]
//      Email 3 - [address]
//   👉 It's recommended to write to all simultaneously to ensure delivery.
//   ——————————————————————————————






package com.drrixs.happybirthdaynadezhda

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.drrixs.happybirthdaynadezhda.databinding.CountBinding

class CountActivity : AppCompatActivity() {



    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: CountBinding
    private val prefs by lazy { getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE) }

    override fun onResume() {
        super.onResume()
        val currentTime = System.currentTimeMillis()
        val endTime = prefs.getLong("END_TIME", 0)

        if (currentTime < endTime) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.alan_walker)

        mediaPlayer.start()
        setupAnim()
        parade()
    }

    private fun setupAnim() {
        binding.animationView.apply {
            setAnimation("animloveyou.json")
            repeatCount = 0
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {

                    binding.animationView.visibility = View.GONE
                    showAndFadeOutImageView()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            playAnimation()
        }
    }

    private fun showAndFadeOutImageView() {
        binding.imageView2.apply {
            visibility = View.VISIBLE
            alpha = 0f


            animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {

                        animate()
                            .setStartDelay(30000)
                            .alpha(0f)
                            .setDuration(2000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    visibility = View.GONE
                                    byebye()
                                }
                            })
                    }
                })
        }
    }

    private fun byebye() {
        binding.imageViewByeBye.apply {
            visibility = View.VISIBLE
            alpha = 0f

            animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animate()
                            .setStartDelay(500)
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    visibility = View.GONE
                                    finish()
                                }
                            })
                    }
                })
        }
    }
    
    private fun parade() {
        binding.konfettiView.start(Presets.parade())
    }
}
