package exception.loginexception

class NoCSRFTokenException: RuntimeException("No CLRF token found on page")