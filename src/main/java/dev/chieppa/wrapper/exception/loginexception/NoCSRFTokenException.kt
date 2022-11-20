package dev.chieppa.wrapper.exception.loginexception

class NoCSRFTokenException: RuntimeException("No CLRF token found on page")