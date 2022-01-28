package com.team1.trivia

import android.content.Intent

class SplashActivity : TriviaActivity() {
    /** Called when the activity is first created.  */
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a background gradient
        val grad = GradientDrawable(Orientation.TOP_BOTTOM, intArrayOf(Color.WHITE, Color.BLUE))
        this.getWindow().setBackgroundDrawable(grad)
        setContentView(R.layout.splash)
        startAnimating()
    }

    /**
     * Helper method to start the animation on the splash screen
     */
    private fun startAnimating() {
        // Fade in top title
        val logo1: TextView? = findViewById(R.id.splashTitle1) as TextView?
        val fade1: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo1.startAnimation(fade1)

        // Spin in the logo
        val image: ImageView? = findViewById(R.id.logoImageView) as ImageView?
        val spin: Animation = AnimationUtils.loadAnimation(this, R.anim.custom_anim)
        image.startAnimation(spin)

        // Fade in bottom title after a built-in delay.
        val logo2: TextView? = findViewById(R.id.splashTitle2) as TextView?
        val fade2: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in2)
        logo2.startAnimation(fade2)

        // Transition to Main Menu when bottom title finishes animating
        fade2.setAnimationListener(object : AnimationListener() {
            fun onAnimationEnd(animation: Animation?) {

                // The animation has ended, transition to the Main Menu screen
                startActivity(
                    Intent(
                        this@SplashActivity,
                        OpeningActivity::class.java
                    )
                )
                this@SplashActivity.finish()
            }

            // Required method from Abstract
            fun onAnimationRepeat(animation: Animation?) {}

            // Required method from Abstract
            fun onAnimationStart(animation: Animation?) {}
        })
    }

    protected fun onPause() { // Called when phone is switched to another app (i.e. phone call)
        super.onPause()

        // Stop the animation
        val logo1: TextView? = findViewById(R.id.splashTitle1) as TextView?
        logo1.clearAnimation()
        val logo2: TextView? = findViewById(R.id.splashTitle2) as TextView?
        logo2.clearAnimation()
    }

    protected fun onResume() {
        super.onResume()

        // Start animating at the beginning so we get the full splash screen experience
        startAnimating()
    }
}