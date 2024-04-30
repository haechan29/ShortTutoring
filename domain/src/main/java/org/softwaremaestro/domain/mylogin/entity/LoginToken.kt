package org.softwaremaestro.domain.mylogin.entity


abstract class LoginToken(val content: String, var isValid: () -> Boolean)

class FakeLoginToken(content: String, isValid: () -> Boolean): LoginToken(content, isValid)
