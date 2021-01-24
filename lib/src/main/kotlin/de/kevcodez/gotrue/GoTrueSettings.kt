package de.kevcodez.gotrue

data class GoTrueSettings(
        val external: External,
        val disableSignup: Boolean,
        val autoconfirm: Boolean
) {

    data class External(
            val bitbucket: Boolean,
            val github: Boolean,
            val gitlab: Boolean,
            val google: Boolean
    )
}
