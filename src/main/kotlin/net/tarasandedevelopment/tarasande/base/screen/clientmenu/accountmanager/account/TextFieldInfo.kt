package net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TextFieldInfo(val name: String, val hidden: Boolean, val default: String = "")